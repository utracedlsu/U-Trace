package com.capstone.app.utrace_cts.fragments

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.capstone.app.utrace_cts.EnablingPermissionsActivity
import com.capstone.app.utrace_cts.MainActivity
import com.capstone.app.utrace_cts.R

class ConfirmUploadDataFragment: DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // initialize, set bg to transparent
        var content: View = inflater.inflate(R.layout.fragment_confirm_upload_data, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // connect
        var tv_dataPrivConsent_content: TextView = content.findViewById(R.id.tv_dataPrivConsent_content)
        var btn_agreeConsent: Button = content.findViewById(R.id.btn_agreeConsent)
        var btn_cancelUploadData: Button = content.findViewById(R.id.btn_cancelUploadData)

        tv_dataPrivConsent_content.movementMethod = ScrollingMovementMethod() // allow popup to be scrollable

        // get data -- either from EnablingPermissionsActivity or UploadDataActivity
        val bundle = arguments
        val source: Boolean = bundle!!.getBoolean("source")

        if (source) { // if previous activity was EnablingPermissionsActivity...

            // set appropriate button text for the popup
            btn_agreeConsent.text = "I ACCEPT"

            // btn logic: go to MainActivity
            btn_agreeConsent.setOnClickListener{
                val intent = Intent(context, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("EXIT", true)
                startActivity(intent)
                activity?.finish()
            }
        }
        else if (!source) { // else, previous activity was UploadDataActivity

            // set appropriate button text for the popup
            btn_agreeConsent.text = "AGREE & CONFIRM UPLOAD"

            // btn logic: ???
            btn_agreeConsent.setOnClickListener{
                //TODO: Dito i-lalagay yung logic to upload their data to the servers i guess?
            }
        }
        else {
            Log.i("ConfirmUploadDataFragment.kt", "Unknown source.")
        }

        btn_cancelUploadData.setOnClickListener { dialog?.dismiss() } // close popup window on cancel

        return content
    }
}