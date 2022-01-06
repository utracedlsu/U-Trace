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
        btn_back_TestStatus = findViewById(R.id.btn_back_TestStatus)
        btn_updateTestStatus = findViewById(R.id.btn_updateTestStatus)
        testID = Preference.getLastTestID(applicationContext)

        // set TextViews
        if(!Preference.getTestStatus(applicationContext).equals("")){
            val testDate = Preference.getLastTestDate(applicationContext)
            tvTestDateHeader.setText("As of $testDate")
            tvTestDate.setText("$testDate")
            tvTestID.setText("$testID")

            //change true to positive eventually
            if(Preference.getTestStatus(applicationContext).equals("true")){
                tvTestStatus.setText("You have been tested POSTIVE for COVID-19.")
            } else {
                tvTestStatus.setText("You have been tested NEGATIVE for COVID-19.")
            }
        } else {
            //set header to blank in order to hide it if wala pang test status at all
            tvTestDateHeader.setText("")
            tvTestDate.setText("No test date set.")
            tvTestID.setText("No test ID set.")
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

                            //refresh textViews
                            val testDate = Preference.getLastTestDate(applicationContext)
                            tvTestDateHeader.setText("As of $testDate")
                            tvTestDate.setText("$testDate")
                            tvTestID.setText("${Preference.getLastTestID(applicationContext)}")

                            //change true to positive eventually
                            if(Preference.getTestStatus(applicationContext).equals("true")){
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