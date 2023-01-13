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
import com.capstone.app.utrace_cts.OtpActivationActivity
import com.capstone.app.utrace_cts.Preference
import com.capstone.app.utrace_cts.R
import com.capstone.app.utrace_cts.notifications.persistence.NotificationRecordStorage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_confirm_account_deletion.*
import java.util.*


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
            //go to otp for deletion
            goToOTP("UserDeletion")
        }
        btn_confirmDeletion.setEnabled(false)

        // remove this popup when the cancel button is pressed
        btn_cancelDeletion.setOnClickListener{ dialog?.dismiss() }

        return content
    }

    // enables the confirm deletion button if edit text string is "DELETE"; disables it otherwise
    private val deleteTextWatcher = object : TextWatcher {

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (et_typeDelete.text.toString() == "DELETE") {
                btn_confirmDeletion.setBackgroundColor(enabledColor)
                btn_confirmDeletion.setEnabled(true)
            } else {
                btn_confirmDeletion.setBackgroundColor(disabledColor)
                btn_confirmDeletion.setEnabled(false)
            }


        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun afterTextChanged(s: Editable?) {}
    }

    //activity source should be UserDeletion
    private fun goToOTP(activitySource: String){
        var phoneNo = Preference.getPhoneNumber(requireContext())

        var intentDetails = hashMapOf(
            //remove the '+63' in the saved preference phone number
            "activity_source" to activitySource,
            "phone" to phoneNo.drop(3)
        )

        val otpIntent = Intent(requireContext(), OtpActivationActivity::class.java)

        otpIntent.putExtra("USER_DETAILS", intentDetails)

        startActivity(otpIntent)
    }
}