package com.capstone.app.utrace_cts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class TestStatusActivity : AppCompatActivity() {

    private lateinit var btn_back_TestStatus : Button
    private lateinit var tvTestStatus: TextView
    private lateinit var tvTestDateHeader: TextView
    private lateinit var tvTestDate: TextView
    private lateinit var tvTestID: TextView
    private lateinit var tvTestFacility: TextView
    private lateinit var tvTestMethod: TextView
    private lateinit var btn_updateTestStatus : Button
    private lateinit var testID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_status)

        // connect
        tvTestStatus = findViewById(R.id.tv_covidTestStatus)
        tvTestDateHeader = findViewById(R.id.tv_testDateHeader)
        tvTestDate = findViewById(R.id.tv_details_dateOfTest)
        tvTestID = findViewById(R.id.tv_details_testID)
        tvTestFacility = findViewById(R.id.tv_details_facility)
        tvTestMethod = findViewById(R.id.tv_details_method)

        btn_back_TestStatus = findViewById(R.id.btn_back_TestStatus)
        btn_updateTestStatus = findViewById(R.id.btn_updateTestStatus)
        testID = Preference.getLastTestID(applicationContext)

        // set TextViews
        if(!Preference.getTestStatus(applicationContext).equals("") &&
            !Preference.getTestStatus(applicationContext).equals("Untested")){
            val testDate = Preference.getLastTestDate(applicationContext)
            tvTestDateHeader.setText("As of $testDate")
            tvTestDate.setText("$testDate")
            tvTestID.setText("$testID")
            tvTestFacility.setText("${Preference.getLastTestFac(applicationContext)}")
            tvTestMethod.setText("${Preference.getLastTestMethod(applicationContext)}")

            if(Preference.getTestStatus(applicationContext).equals("true")
                || Preference.getTestStatus(applicationContext).equals("Positive", true)){
                tvTestStatus.setText("You have been tested POSTIVE for COVID-19.")
            } else {
                tvTestStatus.setText("You have been tested NEGATIVE for COVID-19.")
            }
        } else {
            tvTestDateHeader.setText("")
            tvTestDate.setText("N/A")
            tvTestID.setText("N/A")
            tvTestFacility.setText("N/A")
            tvTestMethod.setText("N/A")
        }

        // go back to home
        btn_back_TestStatus.setOnClickListener { finish() }

        btn_updateTestStatus.setOnClickListener {
            refreshTestStatus()
        }
    }

    private fun refreshTestStatus(){
        FirebaseFirestore.getInstance().collection("users").document(Preference.getFirebaseId(applicationContext))
            .get().addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val result = task.result
                    val covidTests = result?.get("covid_tests") as ArrayList<HashMap<String, Object>>

                    if(covidTests.size > 0){
                        if(covidTests.last().get("testID").toString().equals(testID)){
                            Toast.makeText(applicationContext, "No new test status was found at this time, please try again later.", Toast.LENGTH_SHORT).show()
                        } else {
                            Preference.putLastTestID(applicationContext, covidTests.last().get("testID").toString())
                            Preference.putTestStatus(applicationContext, "${result?.get("covid_positive").toString()}")
                            Preference.putLastTestDate(applicationContext, "${result?.getString("last_testdate")}")

                            val retrievedFac = covidTests.last().get("facility")
                            val retrievedMeth = covidTests.last().get("method")

                            if(retrievedFac != null && retrievedMeth != null){
                                Preference.putLastTestFac(applicationContext, retrievedFac.toString())
                                Preference.putLastTestMethod(applicationContext, retrievedMeth.toString())
                            }

                            //refresh textViews
                            val testDate = Preference.getLastTestDate(applicationContext)
                            tvTestDateHeader.setText("As of $testDate")
                            tvTestDate.setText("$testDate")
                            tvTestID.setText("${Preference.getLastTestID(applicationContext)}")
                            tvTestFacility.setText("${Preference.getLastTestFac(applicationContext)}")
                            tvTestMethod.setText("${Preference.getLastTestMethod(applicationContext)}")

                            //change true to positive eventually
                            if(Preference.getTestStatus(applicationContext).equals("true")
                                || Preference.getTestStatus(applicationContext).equals("Positive", true)){
                                tvTestStatus.setText("You have been tested POSTIVE for COVID-19.")
                            } else {
                                tvTestStatus.setText("You have been tested NEGATIVE for COVID-19.")
                            }

                            Toast.makeText(applicationContext, "Test status has been refreshed.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(applicationContext, "Unable to check for test status at this time, please try again later.", Toast.LENGTH_SHORT).show()
                    Log.d("TestStatusLog", "Unable to retrieve records at this time. ${task.exception?.message}")
                }
            }
    }
}