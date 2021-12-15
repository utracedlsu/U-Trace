package com.capstone.app.utrace_cts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class TestStatusActivity : AppCompatActivity() {

    private lateinit var btn_back_TestStatus : Button
    private lateinit var tvTestStatus: TextView
    private lateinit var tvTestDateHeader: TextView
    private lateinit var tvTestDate: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_status)

        //set textviews
        tvTestStatus = findViewById(R.id.tv_covidTestStatus)
        tvTestDateHeader = findViewById(R.id.tv_testDateHeader)
        tvTestDate = findViewById(R.id.tv_details_dateOfTest)
        //another random comment
        if(!Preference.getTestStatus(applicationContext).equals("")){
            val testDate = Preference.getLastTestDate(applicationContext)
            tvTestDateHeader.setText("As of $testDate")
            tvTestDate.setText("$testDate")
            //change true to positive eventually
            if(Preference.getTestStatus(applicationContext).equals("true")){
                tvTestStatus.setText("You have been tested POSTIVE for COVID-19.")
            } else {
                tvTestStatus.setText("You have been tested NEGATIVE for COVID-19.")
            }
        }

        // connect
        btn_back_TestStatus = findViewById(R.id.btn_back_TestStatus)

        // go back to home
        btn_back_TestStatus.setOnClickListener { finish() }
    }
}