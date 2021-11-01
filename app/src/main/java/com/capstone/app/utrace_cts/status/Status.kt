package com.capstone.app.utrace_cts.status

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Status(
        val msg: String
) : Parcelable