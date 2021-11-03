package com.capstone.app.utrace_cts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class TestStatusActivity : AppCompatActivity() {

    private lateinit var btn_back_TestStatus : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_status)

        // connect
        btn_back_TestStatus = findViewById(R.id.btn_back_TestStatus)

        // go back to home
        btn_back_TestStatus.setOnClickListener { finish() }
    }
}