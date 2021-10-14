package com.capstone.app.utrace_cts

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.capstone.app.utrace_cts.fragments.ConfirmUploadDataFragment

class UploadDataActivity : AppCompatActivity() {

    private lateinit var btn_confirmUploadData: Button
    private lateinit var btn_back_UploadData : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_data)

        // connect
        btn_confirmUploadData = findViewById(R.id.btn_confirmUploadData)
        btn_back_UploadData = findViewById(R.id.btn_back_UploadData)


        // data privacy consent and confirmation popup window
        btn_confirmUploadData.setOnClickListener {
            val confirmUploadDialog = ConfirmUploadDataFragment()
            confirmUploadDialog.show(supportFragmentManager, "confirmUploadDialog")
        }

        // go back to home
        btn_back_UploadData.setOnClickListener {
            finish()
        }


    }
}