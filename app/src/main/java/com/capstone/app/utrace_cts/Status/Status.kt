package com.capstone.app.utrace_cts.Status

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Status(
        val msg: String
) : Parcelable