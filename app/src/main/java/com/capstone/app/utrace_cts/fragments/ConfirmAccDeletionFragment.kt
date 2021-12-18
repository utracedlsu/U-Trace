package com.capstone.app.utrace_cts.fragments

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.capstone.app.utrace_cts.LoginActivity
import com.capstone.app.utrace_cts.Preference
import com.capstone.app.utrace_cts.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_confirm_account_deletion.*


class ConfirmAccDeletionFragment: DialogFragment() {

    private lateinit var et_typeDelete : EditText
    private lateinit var btn_confirmDeletion : Button
    private lateinit var btn_cancelDeletion : Button

    private var enabledColor = Color.parseColor("#428E5C")
    private var disabledColor = Color.parseColor("#919191")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // initialize, set bg to transparent
        var content: View = inflater.inflate(R.layout.fragment_confirm_account_deletion, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // connect
        et_typeDelete = content.findViewById(R.id.et_typeDelete)
        btn_confirmDeletion = content.findViewById(R.id.btn_confirmDeletion)
        btn_cancelDeletion = content.findViewById(R.id.btn_cancelDeletion)

        // set text watcher (to look for the word "delete") for the editable text
        et_typeDelete.addTextChangedListener(deleteTextWatcher)

        // confirm delete button logic
        btn_confirmDeletion.setOnClickListener {
            // TODO: account deletion logic
            //might throw an exception where user has not reauthenticated for a long time, check na lang
            deleteAccContents()
        }

        // remove this popup when the cancel button is pressed
        btn_cancelDeletion.setOnClickListener{ dialog?.dismiss() }

        return content
    }

    // enables the confirm deletion button if edit text string is "DELETE"; disables it otherwise
    private val deleteTextWatcher = object : TextWatcher {

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (et_typeDelete.text.toString() == "DELETE") {
                btn_confirmDeletion.setBackgroundColor(enabledColor)
                btn_confirmDeletion.isEnabled = true
            } else {
                btn_confirmDeletion.setBackgroundColor(disabledColor)
                btn_confirmDeletion.isEnabled = false
            }


        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun afterTextChanged(s: Editable?) {}
    }

    //delete firebase and preferences data
    //this deletes everything rn, might have to change it so that user is just unpublished / disabled
    private fun deleteAccContents(){
        val fStoreUserID = Preference.getFirebaseId(requireContext())
        val userAuth = FirebaseAuth.getInstance().currentUser
        val fStoreInstance = FirebaseFirestore.getInstance()
        val userFstore = fStoreInstance.collection("users")
            .document(fStoreUserID)
        val userContacts = fStoreInstance.collection("filtered_contact_records")
            .document(fStoreUserID)

        userAuth?.let { fbUser ->
           userFstore.delete().addOnCompleteListener { task ->
               if(task.isSuccessful){
                   Log.i("ConfirmDelete", "Deleted user FStore data, proceeding to user contact data..")
                   userContacts.delete().addOnCompleteListener { task ->
                       if(task.isSuccessful){
                           Log.i("ConfirmDelete", "Deleted contact data, proceeding to user auth data..")
                           //Delete user auth last, we won't have privileges to delete fstore data if it was deleted first
                           userAuth.delete().addOnCompleteListener { task ->
                               if(task.isSuccessful){
                                   Log.i("ConfirmDelete", "Deleted auth data, proceeding to preferences..")
                                   Preference.nukePreferences(requireContext())

                                   Log.i("ConfirmDelete", "Deleted everything, logging out")
                                   FirebaseAuth.getInstance().signOut()
                                   val intent = Intent(requireContext(), LoginActivity::class.java)
                                   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                   intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                   intent.putExtra("EXIT", true)
                                   startActivity(intent)
                                   activity?.finish()
                               } else {
                                   Log.e("ConfirmDelete", "Failed to delete contacts: ${task.exception?.message}")
                               }
                           }
                       } else {
                           Log.e("ConfirmDelete", "Failed to delete FStore: ${task.exception?.message}")
                       }
                   }
               } else {
                   Log.e("ConfirmDelete", "Failed to delete auth: ${task.exception?.message}")
                   Toast.makeText(requireContext(), "Unable to delete account right now, please try again later.", Toast.LENGTH_SHORT).show()
               }
           }
        }
    }
}