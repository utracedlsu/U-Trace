package com.capstone.app.utrace_cts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var et_regFname: EditText
    private lateinit var et_regLname: EditText
    private lateinit var et_regMobileNo: EditText
    private lateinit var et_regEmail: EditText
    private lateinit var et_regPass: EditText
    private lateinit var et_regConfirmPass: EditText
    private lateinit var btn_regConfirm: Button

    //Firebase
    private lateinit var mAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    //private lateinit var fStore = Fireba

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // initialize
        et_regFname = findViewById(R.id.et_regFname)
        et_regLname = findViewById(R.id.et_regLname)
        et_regMobileNo = findViewById(R.id.et_regMobileNo)
        et_regEmail = findViewById(R.id.et_regEmail)
        et_regPass = findViewById(R.id.et_regPass)
        et_regConfirmPass = findViewById(R.id.et_regConfirmPass)
        btn_regConfirm = findViewById(R.id.btn_regConfirm)

        //get firebaseAuth instance
        mAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        // go to OTP Activation activity
        btn_regConfirm.setOnClickListener {
            registerNumberFStore()
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

        if (et_regEmail.text.isEmpty()) {
            et_regEmail.error = "Input your email."
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

    private fun registerNumberFStore(){
        //get values from editTexts
        val fname = et_regFname.text.toString()
        val lname = et_regLname.text.toString()
        val email = et_regEmail.text.toString()
        val pword = et_regPass.text.toString()
        val phoneno = et_regMobileNo.text.toString()

        var userDetails = hashMapOf(
            //allows OtpActivationActivity to identify the intent sender
            "activity_source" to "RegisterActivity",
            "firstname" to fname,
            "lastname" to lname,
            "email" to email,
            "pword" to pword,
            "phone" to phoneno
        )


        if(checkForDiscrepancies()) {

            val otpIntent = Intent(this, OtpActivationActivity::class.java)

            otpIntent.putExtra("USER_DETAILS", userDetails)

            startActivity(otpIntent)
        } else {
            Toast.makeText(applicationContext, "lorem ipsum", Toast.LENGTH_SHORT).show()
        }
    }


}