package com.capstone.app.utrace_cts.ui.util

import android.app.Activity
import android.app.AlertDialog
import com.capstone.app.utrace_cts.R

// reference: https://www.youtube.com/watch?v=VKePyfdSSoQ

class LoadingDialog (val fromActivity: Activity) {

    private lateinit var loadingDialog : AlertDialog

    fun startLoading() {

        // set view
        val inflater = fromActivity.layoutInflater
        val dialogView = inflater.inflate(R.layout.util_loading, null)

        // set dialog
        val builder = AlertDialog.Builder(fromActivity)
        builder.setView(dialogView)
        builder.setCancelable(false)

        // create and show loading
        loadingDialog = builder.create()
        loadingDialog.show()
    }

    fun loadingFinished() {
        loadingDialog.dismiss()
    }
}