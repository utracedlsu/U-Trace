package com.capstone.app.utrace_cts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var et_regFname: EditText
    private lateinit var et_regLname: EditText
    private lateinit var et_regMobileNo: EditText
    private lateinit var sp_province: Spinner
    private lateinit var sp_city: Spinner
    private lateinit var sp_barangay: Spinner
    private lateinit var et_street: EditText
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
        sp_province = findViewById(R.id.sp_province)
        sp_city = findViewById(R.id.sp_city)
        sp_barangay = findViewById(R.id.sp_barangay)
        et_street = findViewById(R.id.et_street)
        btn_regConfirm = findViewById(R.id.btn_regConfirm)

        //get firebaseAuth instance
        mAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        // go to OTP Activation activity
        btn_regConfirm.setOnClickListener {
            registerNumberFStore()
        }

        // temp spinner values for province, city, barangay
        val provinces = arrayOf("Select Province", "Province1", "Province2", "Province3")
        sp_province.adapter = ArrayAdapter(this, R.layout.style_spinner, provinces)

        val cities = arrayOf("Select City", "City1", "City2", "City3")
        sp_city.adapter = ArrayAdapter(this, R.layout.style_spinner, cities)

        val barangays = arrayOf("Select Barangay", "Barangay1", "Barangay2", "Barangay3")
        sp_barangay.adapter = ArrayAdapter(this, R.layout.style_spinner, barangays)

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

        if (sp_province.selectedItem.toString() == "Select Province"){
            Toast.makeText(this, "Please select a province.", Toast.LENGTH_LONG).show()
            return false
        }

        if (sp_city.selectedItem.toString() == "Select City"){
            Toast.makeText(this, "Please select a city.", Toast.LENGTH_LONG).show()
            return false
        }

        if (sp_barangay.selectedItem.toString() == "Select Barangay"){
            Toast.makeText(this, "Please select a barangay.", Toast.LENGTH_LONG).show()
            return false
        }

        if (et_street.text.isEmpty()) {
            et_street.error = "Input your street address."
            return false
        }

        return true
    }

    private fun registerNumberFStore(){
        //get values from editTexts
        val fname = et_regFname.text.toString()
        val lname = et_regLname.text.toString()
        //al email = et_regEmail.text.toString()
        //val pword = et_regPass.text.toString()
        val phoneno = et_regMobileNo.text.toString()

        var userDetails = hashMapOf(
            //allows OtpActivationActivity to identify the intent sender
            "activity_source" to "RegisterActivity",
            "firstname" to fname,
            "lastname" to lname,
            //"email" to email,
            //"pword" to pword,
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