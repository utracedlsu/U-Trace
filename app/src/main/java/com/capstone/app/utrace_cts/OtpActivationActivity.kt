package com.capstone.app.utrace_cts

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.capstone.app.utrace_cts.notifications.persistence.NotificationRecordStorage
import com.capstone.app.utrace_cts.vaxboosters.persistence.VaxBoosterRecord
import com.capstone.app.utrace_cts.vaxboosters.persistence.VaxBoosterRecordStorage
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
    private lateinit var tvResendOTP: TextView
    private lateinit var tvOTPText: TextView
    private lateinit var resendOTPTimer: CountDownTimer
    private lateinit var vaxBoosterRecordStorage: VaxBoosterRecordStorage

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
            startResendTimer()
        }
    }

    //starts a timer for resending otp
    fun startResendTimer(){
        resendOTPTimer = object: CountDownTimer(61000, 1000){
            override fun onTick(millisUntilFinished: Long){
                tvResendOTP.setText("Resend OTP (${millisUntilFinished/1000})")
            }
            override fun onFinish(){
                tvResendOTP.setEnabled(true)
                tvResendOTP.setTextColor(Color.parseColor("#428E5C"))
                tvResendOTP.setText("Resend OTP")
            }
        }.start()
    }

    fun cancelTimer(){
        if(resendOTPTimer != null){
            resendOTPTimer.cancel()
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
        tvResendOTP = findViewById(R.id.tv_resendOTP)
        btn_otpConfirm = findViewById(R.id.btn_otpConfirm)

        tvResendOTP.setEnabled(false)

        signInMap = regMap as HashMap<String, Object>
        phoneNum = signInMap.get("phone") as String
        intentSource = signInMap.get("activity_source") as String

        Log.d("OTPActivity", "+63"+phoneNum)

        setOTPText()
        requestOTP(phoneNum)

        // go to Enable Permissions activity
        btn_otpConfirm.setOnClickListener {
            validateOTP()
        }

        //there should be a timer to disable this for a specified time
        tvResendOTP.setOnClickListener{
            requestOTP(phoneNum)
        }
    }

    //set OTP instructions here based on the intent source
    private fun setOTPText(){
        //connect text view
        tvOTPText = findViewById(R.id.tv_OTPtext)
        when(intentSource){
            "RegisterActivity"->{
                tvOTPText.setText(
                    "An OTP has been sent to your device. Please enter the correct OTP in order to complete" +
                            " the user registration."
                )
            }
            "LoginActivity"->{
                tvOTPText.setText(
                    "An OTP has been sent to your device. Please enter the correct OTP in order to complete" +
                            " user log in."
                )
            }
            "UserVerification"->{
                tvOTPText.setText(
                    "An OTP has been sent to your device. Please enter the correct OTP in order to complete" +
                            " user verification."
                )
            }
            "UserDeletion"->{
                tvOTPText.setText(
                    "An OTP has been sent to your device. Please enter the correct OTP in order to complete" +
                            " user deletion."
                )
            }
            else -> {
                Toast.makeText(applicationContext, "Can't identify cause of OTP, please return to home.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateOTP(){
        val enteredOTP = etOTP.text.toString()
        //check if authCredential is initialized in the first place
        if(this::authCredential.isInitialized){
            if(enteredOTP.equals(authCredential.smsCode)){
                Preference.putPhoneNumber(applicationContext, "+63${phoneNum}")

                when(intentSource){
                    "RegisterActivity"->{
                        registerWithPhoneCredential(authCredential)
                    }
                    "LoginActivity"->{
                        logInWithPhoneCredential(authCredential)
                    }
                    "UserVerification"->{
                        verifyUser()
                    }
                    "UserDeletion"->{
                        deleteAccContents(authCredential)
                    }
                    else -> {
                        Toast.makeText(applicationContext, "Can't identify cause of OTP, please try again later.", Toast.LENGTH_SHORT).show()
                        //TODO: Return to Main Activity (?)
                    }
                }
            } else {
                Toast.makeText(applicationContext, "OTP is incorrect. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }
        else {
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
                val loggedUser = fAuth.currentUser
                val loggedUserId = loggedUser?.uid as String
                Preference.putFirebaseId(applicationContext, loggedUserId)
                updatePrefsLogin(loggedUserId)
            } else {
                Toast.makeText(applicationContext, "Error! ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                Log.d("OTPActivation", "Error! ${task.exception?.message}")
            }
        }
    }

    //refreshes preferences of user upon logging in
    private fun updatePrefsLogin(firebaseID: String){
        Log.i("OTPActivation", "Entered updatePrefsLogin for $firebaseID")
        fStore.collection("users").document(firebaseID).get().addOnCompleteListener { task ->
            if(task.isSuccessful){
                Log.i("OTPActivation", "Retrieving data from firestore and saving to preferences...")
                val result = task.result

                val boosterArray = result?.get("vax_booster") as ArrayList<HashMap<String, Object>>
                val covidTests = result?.get("covid_tests") as ArrayList<HashMap<String, Object>>

                //array of booster sqlite entities
                var sqliteBoosters = ArrayList<VaxBoosterRecord>()
                vaxBoosterRecordStorage = VaxBoosterRecordStorage(this.applicationContext)

                Preference.putFirebaseId(applicationContext, firebaseID)
                Preference.putFullName(applicationContext,
                    "${result?.getString("firstname")} ${result?.getString("lastname")}")
                Preference.putFullAddress(applicationContext,
                "${result?.getString("street")}, ${result?.getString("barangay")}" +
                        ", ${result?.getString("city")}, ${result?.getString("province")}"
                    )
                Preference.putVerification(applicationContext, "${result?.getBoolean("verification")}")
                Preference.putTestStatus(applicationContext, "${result?.get("covid_positive").toString()}")
                Preference.putLastTestDate(applicationContext, "${result?.getString("last_testdate")}")
                Preference.putVaxID(applicationContext, "${result?.getString("vax_ID")}")
                Preference.putVaxManufacturer(applicationContext, "${result?.getString("vax_manufacturer")}")
                Preference.putVaxCategory(applicationContext, "${result?.getString("vax_category")}")
                Preference.putVaxFacility(applicationContext, "${result?.getString("vax_facility")}")

                // 5/13/2022 - Vaccination Data Redesign
                val vax1Map = result?.get("vax_1stdose") as HashMap<String, Object>
                if(!vax1Map.isEmpty()){
                    Preference.putVaxDose(applicationContext, "${vax1Map.get("date").toString()}", 1)
                    Preference.putVaxLotNo(applicationContext, "${vax1Map.get("lot_no").toString()}", 1)
                    Preference.putVaxBatchNo(applicationContext, "${vax1Map.get("batch_no").toString()}", 1)
                    Preference.putVaxVaccinator(applicationContext, "${vax1Map.get("lot_no").toString()}", 1)
                }

                val vax2Map = result?.get("vax_2nddose") as HashMap<String, Object>
                if(!vax2Map.isEmpty()){
                    Preference.putVaxDose(applicationContext, "${vax2Map.get("date").toString()}", 2)
                    Preference.putVaxLotNo(applicationContext, "${vax2Map.get("lot_no").toString()}", 2)
                    Preference.putVaxBatchNo(applicationContext, "${vax2Map.get("batch_no").toString()}", 2)
                    Preference.putVaxVaccinator(applicationContext, "${vax2Map.get("lot_no").toString()}", 2)
                }

                //covid tests insert
                if(covidTests.size > 0){
                    Preference.putLastTestID(applicationContext, covidTests.last().get("testID").toString())
                    val retrievedFac = covidTests.last().get("facility")
                    val retrievedMeth = covidTests.last().get("method")

                    if(retrievedFac != null && retrievedMeth != null){
                        Preference.putLastTestFac(applicationContext, retrievedFac.toString())
                        Preference.putLastTestMethod(applicationContext, retrievedMeth.toString())
                    }
                }

                //booster insert loop
                //nuke db then reinsert everything
                if(boosterArray.size > 0){
                    for (fsBooster in boosterArray){
                        sqliteBoosters.add(VaxBoosterRecord(
                            vaxbrand = fsBooster.get("vax_manufacturer").toString(),
                            date = fsBooster.get("date").toString(),
                            blockno = fsBooster.get("blockno").toString(),
                            lotno = fsBooster.get("lotno").toString(),
                            vaccinator = fsBooster.get("vaccinator").toString(),
                            category = fsBooster.get("category").toString(),
                            facility = fsBooster.get("facility").toString()
                            ))
                    }

                    //delete notifs records
                    Observable.create<Boolean>{
                        vaxBoosterRecordStorage.nukeDb()
                        it.onNext(true)
                    }.observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe{ result ->
                            if(result){
                                GlobalScope.launch {
                                    vaxBoosterRecordStorage.saveMultipleBoosters(sqliteBoosters)
                                }
                            } else {
                                Log.e("OTPActivation", "Unable to delete boosters")
                            }
                        }
                }

                Preference.putLoggedIn(applicationContext, true)
                Toast.makeText(applicationContext, "Successfully logged in! ${Preference.getPhoneNumber(applicationContext)}", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
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
                val region = signInMap.get("region") as String
                val province = signInMap.get("province") as String
                val city = signInMap.get("city") as String
                val barangay = signInMap.get("barangay") as String
                val street = signInMap.get("street") as String
                val emptyRegMap = HashMap<String, Object>()
                val emptyRegArray = ArrayList<String>() //just an empty array for registering
                val isVerified = false

                val fStoreInsertMap = hashMapOf(
                    "firstname" to fname,
                    "lastname" to lname,
                    "region" to region,
                    "province" to province,
                    "city" to city,
                    "barangay" to barangay,
                    "street" to street,
                    "phone" to phoneNum,
                    "vax_ID" to "",
                    "vax_manufacturer" to "",
                    "vax_category" to "",
                    "vax_1stdose" to emptyRegMap, //map which will contain date, lot, batch, facility, vaccinator
                    "vax_2nddose" to emptyRegMap,
                    "vax_facility" to "", //might be removed later
                    "vax_booster" to emptyRegArray, //just an empty array, so use the same var
                    "covid_tests" to emptyRegArray,
                    "covid_positive" to "Untested", //untested, positive, or negative ONLY
                    "last_testdate" to "",
                    "verification" to isVerified,
                    "fcm_token" to Preference.getCloudMessagingToken(applicationContext),
                    "document_status" to "published"
                )

                Log.d("OTPActivation", "{$fname, $lname, $phoneNum")

                val newUser = fAuth.currentUser
                val newUserID = newUser?.uid as String

                fStore.collection("users").document(newUserID).set(fStoreInsertMap)
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful) {
                            Preference.putFirebaseId(applicationContext, newUserID)
                            Preference.putFullName(applicationContext, "$fname $lname")
                            Preference.putFullAddress(applicationContext,"$street, $barangay, $city, $province")
                            Preference.putTokenUploadStatus(applicationContext, "Uploaded")
                            Toast.makeText(
                                applicationContext,
                                "Successfully Added! ${Preference.getPhoneNumber(applicationContext)}",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
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

    //upon deletion
    //deletes preferences and sets firebase data to 'deleted'
    private fun deleteAccContents(credential: PhoneAuthCredential){
        val fStoreUserID = Preference.getFirebaseId(applicationContext)
        val userAuth = FirebaseAuth.getInstance().currentUser
        val fStoreInstance = FirebaseFirestore.getInstance()
        val userFstore = fStoreInstance.collection("users")
            .document(fStoreUserID)
        val userContacts = fStoreInstance.collection("filtered_contact_records")
            .document(fStoreUserID)

        //reauthenticate user
        userAuth?.let { fbUser ->
            fbUser.reauthenticate(credential).addOnCompleteListener { zerotask ->
                if(zerotask.isSuccessful){
                    userFstore.update("document_status", "deleted").addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            Log.i("ConfirmDelete", "Set FStore data to 'deleted', checking contact data..")
                            userContacts.get().addOnCompleteListener { recordCheck ->
                                if(recordCheck.isSuccessful){
                                    val result = recordCheck.result
                                    result?.let { resultCheck ->
                                        if(resultCheck.exists()){
                                            Log.i("ConfirmDelete", "Contact record exists, setting to 'deleted'..")
                                            userContacts.update("document_status", "deleted").addOnCompleteListener { firsttask ->
                                                if(firsttask.isSuccessful){
                                                    Log.i("ConfirmDelete", "Set contact data to 'deleted', proceeding to user auth data..")
                                                    //Delete user auth last
                                                    userAuth.delete().addOnCompleteListener { secondtask ->
                                                        if(secondtask.isSuccessful){
                                                            Log.i("ConfirmDelete", "Deleted auth data, proceeding to preferences..")
                                                            Preference.nukePreferences(applicationContext)

                                                            //delete notifs records
                                                            Observable.create<Boolean>{
                                                                NotificationRecordStorage(applicationContext).nukeDb()
                                                                it.onNext(true)
                                                            }.observeOn(AndroidSchedulers.mainThread())
                                                                .subscribeOn(Schedulers.io())
                                                                .subscribe{ result ->
                                                                    Log.i("ConfirmDelete", "Deleted notif records: $result")
                                                                }

                                                            Log.i("ConfirmDelete", "Deleted everything, logging out")
                                                            FirebaseAuth.getInstance().signOut()
                                                            val intent = Intent(applicationContext, LoginActivity::class.java)
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                            intent.putExtra("EXIT", true)
                                                            startActivity(intent)
                                                            finish() //monitor this, idk what happens pag finish lang
                                                        } else {
                                                            Log.e("ConfirmDelete", "Failed to delete auth: ${secondtask.exception?.message}")
                                                        }
                                                    }
                                                } else {
                                                    Log.e("ConfirmDelete", "Failed to delete contacts: ${firsttask.exception?.message}")
                                                }
                                            }
                                        } else {
                                            Log.i("ConfirmDelete", "Contact doesn't exist, proceeding to delete auth...")
                                            userAuth.delete().addOnCompleteListener { secondtask ->
                                                if(secondtask.isSuccessful){
                                                    Log.i("ConfirmDelete", "Deleted auth data, proceeding to preferences..")
                                                    Preference.nukePreferences(applicationContext)

                                                    //delete notifs records
                                                    Observable.create<Boolean>{
                                                        NotificationRecordStorage(applicationContext).nukeDb()
                                                        it.onNext(true)
                                                    }.observeOn(AndroidSchedulers.mainThread())
                                                        .subscribeOn(Schedulers.io())
                                                        .subscribe{ result ->
                                                            Log.i("ConfirmDelete", "Deleted notif records: $result")
                                                        }

                                                    Log.i("ConfirmDelete", "Deleted everything, logging out")
                                                    Toast.makeText(applicationContext, "Successfully deleted user account.", Toast.LENGTH_SHORT).show()
                                                    FirebaseAuth.getInstance().signOut()
                                                    val intent = Intent(applicationContext, LoginActivity::class.java)
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                    intent.putExtra("EXIT", true)
                                                    startActivity(intent)
                                                    finish() //monitor this, idk what happens pag finish lang
                                                } else {
                                                    Log.e("ConfirmDelete", "Failed to delete auth: ${secondtask.exception?.message}")
                                                    finish()
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    Log.i("ConfirmDelete", "Unable to get record: ${recordCheck.exception?.message}")
                                    finish()
                                }
                            }
                        } else {
                            Log.e("ConfirmDelete", "Failed to delete FStore: ${task.exception?.message}")
                            Toast.makeText(applicationContext, "Unable to delete account right now, please try again later.", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                } else {
                    Log.e("ConfirmDelete", "Failed to delete auth: ${zerotask.exception?.message}")
                    Toast.makeText(applicationContext, "Unable to delete user at this time, please try again later.", Toast.LENGTH_SHORT).show()
                    //TODO: Return to mainActivity / Home
                    finish()
                }
            }
        }
    }

    //upon user verification
    private fun verifyUser(){
        val currUserID = Preference.getFirebaseId(applicationContext)

        fStore.collection("users").document(currUserID).update("verification", true)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    Preference.putVerification(applicationContext, "true")

                    Toast.makeText(applicationContext, "Your account has been successfully verified!", Toast.LENGTH_SHORT).show()

                    // 5/22/2022 - DO NOT start a new MainActivity. instead finish() and return to prev
                    //val intent = Intent(this, MainActivity::class.java)
                    //startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(applicationContext, "Error! ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    Log.d("OTPActivation", "Error! ${task.exception?.message}")
                    finish()
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelTimer()
    }
}