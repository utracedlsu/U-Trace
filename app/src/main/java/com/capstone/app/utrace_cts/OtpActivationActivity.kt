package com.capstone.app.utrace_cts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class OtpActivationActivity : AppCompatActivity() {

    private lateinit var btn_otpConfirm: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_activation)

        btn_otpConfirm = findViewById(R.id.btn_otpConfirm)

        // go to Enable Permissions activity
        btn_otpConfirm.setOnClickListener {
            val intent = Intent(this, EnablingPermissionsActivity::class.java)
            startActivity(intent)
        }
    }
}