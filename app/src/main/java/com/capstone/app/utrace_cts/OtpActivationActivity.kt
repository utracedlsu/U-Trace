package com.capstone.app.utrace_cts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
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
    private lateinit var signInMap: HashMap<String, Object>
    private lateinit var authCredential: PhoneAuthCredential

    private var callbacks = object: PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
        override fun onVerificationCompleted(credential: PhoneAuthCredential){
            Log.d("OTPActivation", "onVericationCompleted")
            authCredential = credential
            Log.d("OTPActivation", "authCredential: ${authCredential.toString()}")
            signInWithPhoneCredential(credential)
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


        btn_otpConfirm = findViewById(R.id.btn_otpConfirm)

        signInMap = regMap as HashMap<String, Object>
        phoneNum = signInMap.get("phone") as String

        Log.d("OTPActivity", "+63"+phoneNum)

        requestOTP(phoneNum)

        // go to Enable Permissions activity
        btn_otpConfirm.setOnClickListener {
            signInWithPhoneCredential(authCredential)
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

    private fun signInWithPhoneCredential(credential: PhoneAuthCredential){
        Log.d("OTPActivation", "Entering signInWithPhoneCredential")
        Log.d("OTPActivation", "credential: ${credential.toString()}")


        fAuth.signInWithCredential(credential).addOnCompleteListener{ task ->
            if(task.isSuccessful){
                val email = signInMap.get("email") as String
                val pword = signInMap.get("pword") as String

                val fname = signInMap.get("fistname") as String
                val lname = signInMap.get("lastname") as String

                val fStoreInsertMap = hashMapOf(
                    "firstname" to fname,
                    "lastname" to lname,
                    "phone" to phoneNum
                )

                Log.d("OTPActivation", "{$fname, $lname, $phoneNum")

                val newUser = fAuth.currentUser
                val newUserID = newUser?.uid as String

                fStore.collection("users").document(newUserID).set(fStoreInsertMap)
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful) {
                            Toast.makeText(
                                applicationContext,
                                "Successfully Added!",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(applicationContext, "Error! ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            Log.d("OTPActivation", "Error! ${task.exception?.message}")
                        }
                    }
                /*
                fAuth.createUserWithEmailAndPassword(email, pword)
                    .addOnCompleteListener(OnCompleteListener { task ->
                        if(task.isSuccessful){
                            val newUser = fAuth.currentUser
                            val newUserID = newUser?.uid as String

                            fStore.collection("users").document(newUserID).set(fStoreInsertMap)
                                .addOnCompleteListener { task ->
                                    Toast.makeText(applicationContext, "Successfully Added!", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(applicationContext, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                 */
            } else {
                Toast.makeText(applicationContext, "Error! ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                Log.d("OTPActivation", "Error! ${task.exception?.message}")
            }
        }
    }
}