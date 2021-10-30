package com.capstone.app.utrace_cts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class EnablingPermissionsActivity : AppCompatActivity() {

    /*  Note:
    *   Use ConfirmUploadDataFragment; fragment_confirm_upload_data.xml
    *   for privacy consent confirmation
    */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enabling_permissions)
    }
}