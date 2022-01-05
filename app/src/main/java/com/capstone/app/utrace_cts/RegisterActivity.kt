package com.capstone.app.utrace_cts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var et_regFname: EditText
    private lateinit var et_regLname: EditText
    private lateinit var et_regMobileNo: EditText
    private lateinit var sp_region: Spinner
    private lateinit var sp_province: Spinner
    private lateinit var sp_city: Spinner
    private lateinit var sp_barangay: Spinner
    private lateinit var et_street: EditText
    private lateinit var btn_regConfirm: Button

    //Firebase
    private lateinit var mAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    //private lateinit var fStore = Fireba

    //String arraylists for spinner NAMES
    private lateinit var regionsNamesArray: ArrayList<String>
    private lateinit var provincesNamesArray: ArrayList<String>
    private lateinit var citiesNamesArray: ArrayList<String>
    private lateinit var barangaysNamesArray: ArrayList<String>

    //String arraylists for spinner IDs
    private lateinit var regionsIDsArray: ArrayList<String>
    private lateinit var provincesIDsArray: ArrayList<String>
    private lateinit var citiesIDsArray: ArrayList<String>
    private lateinit var barangaysIDsArray: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // initialize
        et_regFname = findViewById(R.id.et_regFname)
        et_regLname = findViewById(R.id.et_regLname)
        et_regMobileNo = findViewById(R.id.et_regMobileNo)
        sp_region = findViewById(R.id.sp_region)
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

        //init spinner data of region first
        initSpinnerDataRegion()

        val provinces = arrayOf("Select Province")
        sp_province.adapter = ArrayAdapter(this, R.layout.style_spinner, provinces)
        sp_province.setEnabled(false)

        val cities = arrayOf("Select City")
        sp_city.adapter = ArrayAdapter(this, R.layout.style_spinner, cities)
        sp_city.setEnabled(false)

        val barangays = arrayOf("Select Barangay")
        sp_barangay.adapter = ArrayAdapter(this, R.layout.style_spinner, barangays)
        sp_barangay.setEnabled(false)

    }

    //initialize spinner data for region
    private fun initSpinnerDataRegion(){
        var spinnerNamesData = ArrayList<String>()
        var spinnerIDsData = ArrayList<String>()

        spinnerNamesData.add("Select Region")
        spinnerIDsData.add("PLACEHOLDER")

        fStore.collection("regions").get().addOnCompleteListener { task ->
            if(task.isSuccessful){
                val result = task.getResult()
                result?.let{
                    for (documentSnapshot: QueryDocumentSnapshot in it){
                        spinnerNamesData.add(documentSnapshot.getString("name").toString())
                        spinnerIDsData.add(documentSnapshot.getString("id").toString())
                    }
                    regionsNamesArray = spinnerNamesData
                    regionsIDsArray = spinnerIDsData
                    sp_region.adapter = ArrayAdapter(this, R.layout.style_spinner, regionsNamesArray)
                    sp_region.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            //do nothing if nothing is selected, leave this as is?
                        }
                        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                            if(regionsNamesArray.get(position).equals("Select Region")){
                                //disable spinners if no item is selected
                                sp_province.setEnabled(false)
                                sp_city.setEnabled(false)
                                sp_barangay.setEnabled(false)
                            } else {
                                //initialize spinner data for province
                                initSpinnerDataExtra("provinces", regionsIDsArray.get(position))
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(applicationContext, "Unable to retrieve records at this time. ${task.exception?.message}",
                    Toast.LENGTH_SHORT).show()
                Log.d("RegisterActivityLog", "Unable to retrieve records at this time. ${task.exception?.message}")
            }
        }
    }

    //initialize spinner data after province, city, or barangay
    private fun initSpinnerDataExtra(collection: String, selectedItem: String){
        var spinnerNamesData = ArrayList<String>()
        var spinnerIDsData = ArrayList<String>()
        var codeString = "" //used for firebase equality condition

        Log.d("RegisterActivityLog", "Collection - $collection, Selected Item - $selectedItem")

        //modify code string to be used as equality condition depending on collection to be retrieved
        when(collection){
            "provinces" -> {
                codeString = "region_code"
                spinnerNamesData.add("Select Province")
                spinnerIDsData.add("PLACEHOLDER") //placeholder to match with displayed item
            }
            "cities" -> {
                codeString = "province_code"
                spinnerNamesData.add("Select City")
                spinnerIDsData.add("PLACEHOLDER")
            }
            "barangays" -> {
                codeString = "city_code"
                spinnerNamesData.add("Select Barangay")
                spinnerIDsData.add("PLACEHOLDER")
            }
        }

        fStore.collection(collection).whereEqualTo(codeString, selectedItem).get().addOnCompleteListener { task ->
            if(task.isSuccessful){
                val result = task.getResult()
                result?.let {
                    for (documentSnapshot: QueryDocumentSnapshot in it){
                        spinnerNamesData.add(documentSnapshot.getString("name").toString())
                        spinnerIDsData.add(documentSnapshot.getString("id").toString())
                    }

                    when(collection){
                        "provinces" -> {
                            provincesNamesArray = spinnerNamesData
                            provincesIDsArray = spinnerIDsData
                            sp_province.adapter = ArrayAdapter(this, R.layout.style_spinner, provincesNamesArray)
                            sp_province.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                    //do nothing if nothing is selected, leave this as is?
                                }
                                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                                    if(provincesNamesArray.get(position).equals("Select Province")){
                                        //disable spinners if no item is selected
                                        sp_city.setEnabled(false)
                                        sp_barangay.setEnabled(false)
                                    } else {
                                        //initialize spinner data for cities from selected province
                                        initSpinnerDataExtra("cities", provincesIDsArray.get(position))
                                    }
                                }
                            }
                            sp_province.setEnabled(true)
                        }
                        "cities" -> {
                            citiesNamesArray = spinnerNamesData
                            citiesIDsArray = spinnerIDsData
                            sp_city.adapter = ArrayAdapter(this, R.layout.style_spinner, citiesNamesArray)
                            sp_city.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                    //do nothing if nothing is selected, leave this as is?
                                }
                                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                                    if(citiesNamesArray.get(position).equals("Select City")){
                                        //disable spinner if no item is selected
                                        sp_barangay.setEnabled(false)
                                    } else {
                                        //initialize spinner data for barangays from selected city
                                        initSpinnerDataExtra("barangays", citiesIDsArray.get(position))
                                    }
                                }
                            }
                            sp_city.setEnabled(true)
                        }
                        "barangays" -> {
                            barangaysNamesArray = spinnerNamesData
                            barangaysIDsArray = spinnerIDsData
                            sp_barangay.adapter = ArrayAdapter(this, R.layout.style_spinner, barangaysNamesArray)
                            sp_barangay.setEnabled(true)
                        }
                    }
                }
            } else {
                Toast.makeText(applicationContext, "Unable to retrieve records at this time. ${task.exception?.message}",
                    Toast.LENGTH_SHORT).show()
                Log.d("RegisterActivityLog", "Unable to retrieve records at this time. ${task.exception?.message}")
            }
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

        if (sp_region.selectedItem.toString() == "Select Region"){
            Toast.makeText(this, "Please select a Region.", Toast.LENGTH_LONG).show()
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
        val phoneno = et_regMobileNo.text.toString()
        val region = sp_region.selectedItem.toString()
        val province = sp_province.selectedItem.toString()
        val city = sp_city.selectedItem.toString()
        val barangay = sp_barangay.selectedItem.toString()
        val street = et_street.text.toString()

        var userDetails = hashMapOf(
            //allows OtpActivationActivity to identify the intent sender
            "activity_source" to "RegisterActivity",
            "firstname" to fname,
            "lastname" to lname,
            "region" to region,
            "province" to province,
            "city" to city,
            "barangay" to barangay,
            "street" to street,
            "phone" to phoneno
        )

        if(checkForDiscrepancies()) {

            val otpIntent = Intent(this, OtpActivationActivity::class.java)
            otpIntent.putExtra("USER_DETAILS", userDetails)
            startActivity(otpIntent)

        } else {
            Toast.makeText(applicationContext, "Please fill up the missing details.", Toast.LENGTH_SHORT).show()
        }
    }


}