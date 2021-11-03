package com.capstone.app.utrace_cts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.capstone.app.utrace_cts.fragments.ConfirmUploadDataFragment

class EnablingPermissionsActivity : AppCompatActivity() {

    /*  Note:
    *   Use ConfirmUploadDataFragment; fragment_confirm_upload_data.xml
    *   for terms & conditions confirmation
    */

    private lateinit var btn_enablePerm: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enabling_permissions)

        btn_enablePerm = findViewById(R.id.btn_enablePerm)

        // data privacy consent/terms and conditions popup window
        btn_enablePerm.setOnClickListener {
            val confirmUploadDialog = ConfirmUploadDataFragment()

            // add to bundle: source = EnablePermissionsActivity
            val bundle = Bundle()
            bundle.putBoolean("source", true)
            confirmUploadDialog.arguments = bundle

            confirmUploadDialog.show(supportFragmentManager, "confirmUploadDialog")
        }
    }


}