package com.capstone.app.utrace_cts.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import com.capstone.app.utrace_cts.LoginActivity
import com.capstone.app.utrace_cts.MainActivity
import com.capstone.app.utrace_cts.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var ll_logout: LinearLayout
    private lateinit var builder: MaterialAlertDialogBuilder

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // initialize
        ll_logout = view.findViewById(R.id.ll_logout)
        initLogoutDialog()

        // logout -- go to login activity
        ll_logout.setOnClickListener {
            builder.show()
        }
    }

    // initialize alert dialog
    private fun initLogoutDialog() {

        builder = MaterialAlertDialogBuilder(requireContext())
        builder.setTitle("Logout Confirmation")
        builder.setMessage("Are you sure you want to logout?")

        // Yes — finish all activities, go to LoginActivity
        builder.setPositiveButton("Yes") {dialog, which ->
            val intent = Intent(context, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("EXIT", true)
            startActivity(intent)
            activity?.finish()
        }

        // No — do nothing
        builder.setNegativeButton("Cancel") {dialog, which ->
            dialog.dismiss()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}