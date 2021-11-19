package com.capstone.app.utrace_cts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import java.io.Serializable
import java.util.concurrent.TimeUnit

class OtpActivationActivity : AppCompatActivity() {

    private lateinit var btn_otpConfirm: Button
    private var regMap: Serializable? = null
    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    private lateinit var phoneNum: String
    private lateinit var intentSource: String //should be either Login or Register activities
    private lateinit var signInMap: HashMap<String, Object>
    private lateinit var authCredential: PhoneAuthCredential
    private lateinit var etOTP: EditText


    private var callbacks = object: PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
        override fun onVerificationCompleted(credential: PhoneAuthCredential){
            Log.d("OTPActivation", "onVerificationCompleted")

            etOTP.setText(credential.smsCode)
            authCredential = credential
            Log.d("OTPActivation", "SMS Code: ${credential.smsCode}")
            Log.d("OTPActivation", "authCredential: ${authCredential.toString()}")
        }

        override fun onVerificationFailed(fe: FirebaseException) {
            Log.d("OTPActivation", "onVericationFailed")
            Toast.makeText(applicationContext, "Error: ${fe.message}", Toast.LENGTH_SHORT).show()
            //do stuff
        }

        override fun onCodeSent(verificationID: String, token: PhoneAuthProvider.ForceResendingToken) {
            Log.d("OTPActivation", "onCodeSent")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_activation)

        //init firebaseAuth
        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        regMap = intent.getSerializableExtra("USER_DETAILS")

        etOTP = findViewById(R.id.et_otp)
        btn_otpConfirm = findViewById(R.id.btn_otpConfirm)

        signInMap = regMap as HashMap<String, Object>
        phoneNum = signInMap.get("phone") as String
        intentSource = signInMap.get("activity_source") as String

        Log.d("OTPActivity", "+63"+phoneNum)

        requestOTP(phoneNum)

        // go to Enable Permissions activity
        btn_otpConfirm.setOnClickListener {
            validateOTP()
        }
    }

    private fun validateOTP(){
        val enteredOTP = etOTP.text.toString()
        if(enteredOTP.equals(authCredential.smsCode)){
            Preference.putPhoneNumber(applicationContext, "+63${phoneNum}")
            if(intentSource.equals("RegisterActivity")) {
               registerWithPhoneCredential(authCredential)
            } else if (intentSource.equals("LoginActivity")){
                logInWithPhoneCredential(authCredential)
            } else {
                Toast.makeText(applicationContext, "Error validating OTP!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(applicationContext, "OTP is incorrect. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestOTP(phoneNum: String){
        val options = PhoneAuthOptions.newBuilder(fAuth)
            .setPhoneNumber("+63" + phoneNum)
            .setActivity(this)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    //upon LOGGING IN
    private fun logInWithPhoneCredential(credential: PhoneAuthCredential){
        Log.d("OTPActivation", "Entering logInWithPhoneCredential")
        Log.d("OTPActivation", "credential: ${credential.toString()}")

        fAuth.signInWithCredential(credential).addOnCompleteListener{ task ->
            if(task.isSuccessful) {
                Toast.makeText(applicationContext, "Successfully logged in! ${Preference.getPhoneNumber(applicationContext)}", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(applicationContext, "Error! ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                Log.d("OTPActivation", "Error! ${task.exception?.message}")
            }
        }
    }

    //upon REGISTERING, should be renamed in order to avoid confusion
    private fun registerWithPhoneCredential(credential: PhoneAuthCredential){
        Log.d("OTPActivation", "Entering registerWithPhoneCredential")
        Log.d("OTPActivation", "credential: ${credential.toString()}")


        fAuth.signInWithCredential(credential).addOnCompleteListener{ task ->
            if(task.isSuccessful){
                val fname = signInMap.get("firstname") as String
                val lname = signInMap.get("lastname") as String
                val province = signInMap.get("province") as String
                val city = signInMap.get("city") as String
                val barangay = signInMap.get("barangay") as String
                val street = signInMap.get("street") as String
                val vaxID = ""
                val vax1stDose = ""
                val vax2ndDose = ""
                val lastTestDate = ""
                val isPositive = false
                val isVerified = false

                val fStoreInsertMap = hashMapOf(
                    "firstname" to fname,
                    "lastname" to lname,
                    "province" to province,
                    "city" to city,
                    "barangay" to barangay,
                    "street" to street,
                    "phone" to phoneNum,
                    "vax_ID" to vaxID,
                    "vax_1stdose" to vax1stDose,
                    "vax_2nddose" to vax2ndDose,
                    "last_testdate" to lastTestDate,
                    "covid_positive" to isPositive,
                    "verification" to isVerified
                )

                Log.d("OTPActivation", "{$fname, $lname, $phoneNum")

                val newUser = fAuth.currentUser
                val newUserID = newUser?.uid as String

                fStore.collection("users").document(newUserID).set(fStoreInsertMap)
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful) {
                            Preference.putFullName(applicationContext, "$fname $lname")
                            Preference.putFullAddress(applicationContext,"$street, $barangay, $city, $province")
                            Toast.makeText(
                                applicationContext,
                                "Successfully Added! ${Preference.getPhoneNumber(applicationContext)}",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(applicationContext, "Error! ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            Log.d("OTPActivation", "Error! ${task.exception?.message}")
                        }
                    }
            } else {
                Toast.makeText(applicationContext, "Error! ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                Log.d("OTPActivation", "Error! ${task.exception?.message}")
            }
        }
    }
}