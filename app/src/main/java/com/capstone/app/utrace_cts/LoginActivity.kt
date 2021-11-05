package com.capstone.app.utrace_cts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var tv_register: TextView
    private lateinit var btnLoginConfirm: Button
    private lateinit var etLoginMobileNo: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etLoginMobileNo = findViewById(R.id.et_loginMobileNo)
        btnLoginConfirm = findViewById(R.id.btn_loginConfirm)
        btnLoginConfirm.setOnClickListener {
            loginOTP()
        }

        tv_register = findViewById(R.id.tv_register)
        // Go to RegisterActivity
        tv_register.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    //invalidates login textbox then sends user to otpActivationActivity
    fun loginOTP(){
        if(!etLoginMobileNo.text.isEmpty()){
            val phoneno = etLoginMobileNo.text.toString()

            var loginDetails = hashMapOf(
                "activity_source" to "LoginActivity",
                "phone" to phoneno
            )

            val otpIntent = Intent(this, OtpActivationActivity::class.java)

            otpIntent.putExtra("USER_DETAILS", loginDetails)

            startActivity(otpIntent)
        } else {
            Toast.makeText(applicationContext, "lorem ipsum", Toast.LENGTH_SHORT).show()
        }
    }

    //check if user is already logged in
    override fun onStart() {
        super.onStart()

        if(FirebaseAuth.getInstance().currentUser != null){
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}