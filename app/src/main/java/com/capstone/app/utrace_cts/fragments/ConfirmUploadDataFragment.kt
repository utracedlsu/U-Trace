package com.capstone.app.utrace_cts.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.capstone.app.utrace_cts.R

class ConfirmUploadDataFragment: DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // init and set bg to transparent
        var content: View = inflater.inflate(R.layout.fragment_confirm_upload_data, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // connect
        var tv_dataPrivConsent_content: TextView = content.findViewById(R.id.tv_dataPrivConsent_content)
        var btn_cancelUploadData: Button = content.findViewById(R.id.btn_cancelUploadData)

        tv_dataPrivConsent_content.movementMethod = ScrollingMovementMethod() // allow scrollable

        btn_cancelUploadData.setOnClickListener { dialog?.dismiss() } // close popup window on cancel

        return content
    }
}