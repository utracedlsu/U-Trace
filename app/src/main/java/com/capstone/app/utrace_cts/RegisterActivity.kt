package com.capstone.app.utrace_cts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class RegisterActivity : AppCompatActivity() {

    private lateinit var et_regFname: EditText
    private lateinit var et_regLname: EditText
    private lateinit var et_regMobileNo: EditText
    private lateinit var et_regPass: EditText
    private lateinit var et_regConfirmPass: EditText
    private lateinit var btn_regConfirm: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // initialize
        et_regFname = findViewById(R.id.et_regFname)
        et_regLname = findViewById(R.id.et_regLname)
        et_regMobileNo = findViewById(R.id.et_regMobileNo)
        et_regPass = findViewById(R.id.et_regPass)
        et_regConfirmPass = findViewById(R.id.et_regConfirmPass)
        btn_regConfirm = findViewById(R.id.btn_regConfirm)

        // go to OTP Activation activity
        btn_regConfirm.setOnClickListener {
            val intent = Intent(this, OtpActivationActivity::class.java)
            startActivity(intent)
        }
    }

    // validate input details (for testing only)
    private fun checkForDiscrepancies(): Boolean {

        // if any given field is empty, throw error
        if (et_regFname.text.isEmpty()) {
            et_regFname.error = "Input your first name."
            return false
        }

        if (et_regLname.text.isEmpty()) {
            et_regLname.error = "Input your last name."
            return false
        }

        if (et_regMobileNo.text.isEmpty()) {
            et_regMobileNo.error = "Input your mobile number."
            return false
        }

        if (et_regPass.text.isEmpty()) {
            et_regPass.error = "Input a password."
            return false
        }

        if (et_regConfirmPass.text.isEmpty()) {
            et_regConfirmPass.error = "Confirm your password."
            return false
        }

        // if password and confirm password isn't the same, throw error
        if (et_regPass.text.toString() != et_regConfirmPass.text.toString()) {
            et_regPass.error = "Passwords do not match."
            et_regConfirmPass.error = "Passwords no not match."
            return false
        }

        return true
    }

}