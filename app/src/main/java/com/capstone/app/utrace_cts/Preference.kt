package com.capstone.app.utrace_cts

import android.content.Context
import android.content.SharedPreferences

object Preference {

    private const val PREF_ID = "Tracer_pref"
    private const val IS_ONBOARDED = "IS_ONBOARDED"
    private const val PHONE_NUMBER = "PHONE_NUMBER"
    private const val FIREBASE_ID = "FIREBASE_ID"
    private const val FULL_NAME = "FULL_NAME"
    private const val FULL_ADDRESS = "FULL_ADDRESS"

    //vaccination data
    private const val VAX_ID = "VAX_ID"
    private const val VAX_MANUFACTURER = "VAX_MANUFACTURER"
    private const val VAX_CATEGORY = "VAX_CATEGORY"
    private const val VAX_FACILITY = "VAX_FACILITY"

    private const val VAX_1STDOSE = "VAX_1STDOSE"
    private const val VAX_2NDDOSE = "VAX_2NDDOSE"
    private const val VAX_1STLOTNO = "VAX_1STLOTNO"
    private const val VAX_2NDLOTNO = "VAX_2NDLOTNO"
    private const val VAX_1STBATCHNO = "VAX_1STBATCHNO"
    private const val VAX_2NDBATCHNO = "VAX_2NDBATCHNO"
    private const val VAX_1STVACCINATOR = "VAX_1STVACCINATOR"
    private const val VAX_2NDVACCINATOR = "VAX_2NDVACCINATOR"


    //test data
    private const val LATEST_TESTSTATUS = "LATEST_TESTSTATUS"
    private const val LAST_TESTDATE = "LAST_TESTDATE"
    private const val LAST_TESTID = "LAST_TESTID"
    private const val LAST_TESTFAC = "LAST_TESTFAC"
    private const val LAST_TESTMETHOD = "LAST_TESTMETHOD"

    private const val VERIFICATION = "VERIFICATION"
    private const val ISLOGGEDIN = "ISLOGGEDIN"

    private const val CHECK_POINT = "CHECK_POINT"
    private const val HANDSHAKE_PIN = "HANDSHAKE_PIN"
    private const val FCM_TOKEN = "FCM_TOKEN"
    private const val TOKEN_UPLOADED = "TOKEN_UPLOADED"

    private const val NEXT_FETCH_TIME = "NEXT_FETCH_TIME"
    private const val EXPIRY_TIME = "EXPIRY_TIME"
    private const val LAST_FETCH_TIME = "LAST_FETCH_TIME"

    private const val LAST_PURGE_TIME = "LAST_PURGE_TIME"

    private const val ANNOUNCEMENT = "ANNOUNCEMENT"


    //Offline method for logging in
    fun putLoggedIn(context: Context, value: Boolean) {
        context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
            .edit().putBoolean(ISLOGGEDIN, value).apply()
    }

    fun getLoggedIn(context: Context): Boolean {
        return context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
            .getBoolean(ISLOGGEDIN, false)
    }

    fun putHandShakePin(context: Context, value: String) {
        context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
                .edit().putString(HANDSHAKE_PIN, value).apply()
    }

    fun getHandShakePin(context: Context): String {
        return context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
                .getString(HANDSHAKE_PIN, "AERTVC") ?: "AERTVC"
    }

    fun putIsOnBoarded(context: Context, value: Boolean) {
        context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
                .edit().putBoolean(IS_ONBOARDED, value).apply()
    }

    fun isOnBoarded(context: Context): Boolean {
        return context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
                .getBoolean(IS_ONBOARDED, false)
    }

    fun putFirebaseId(context: Context, value: String) {
        context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
            .edit().putString(FIREBASE_ID, value).apply()
    }

    fun getFirebaseId(context: Context): String {
        return context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
            .getString(FIREBASE_ID, "") ?: ""
    }

    fun putFullName(context: Context, value: String) {
        context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
            .edit().putString(FULL_NAME, value).apply()
    }

    fun getFullName(context: Context): String {
        return context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
            .getString(FULL_NAME, "") ?: ""
    }

    fun putFullAddress(context: Context, value: String) {
        context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
            .edit().putString(FULL_ADDRESS, value).apply()
    }

    fun getFullAddress(context: Context): String {
        return context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
            .getString(FULL_ADDRESS, "") ?: ""
    }

    fun putVaxID(context: Context, value: String) {
        context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
            .edit().putString(VAX_ID, value).apply()
    }

    fun getVaxID(context: Context): String {
        return context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
            .getString(VAX_ID, "") ?: ""
    }

    fun putVaxManufacturer(context: Context, value: String) {
        context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
            .edit().putString(VAX_MANUFACTURER, value).apply()
    }

    fun getVaxManufacturer(context: Context): String {
        return context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
            .getString(VAX_MANUFACTURER, "") ?: ""
    }

    fun putVaxCategory(context: Context, value: String) {
        context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
            .edit().putString(VAX_CATEGORY, value).apply()
    }

    fun getVaxCategory(context: Context): String {
        return context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
            .getString(VAX_CATEGORY, "") ?: ""
    }

    fun putVaxDose(context: Context, value: String, doseNum: Int){
        if(doseNum == 1) {
            context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
                .edit().putString(VAX_1STDOSE, value).apply()
        } else {
            context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
                .edit().putString(VAX_2NDDOSE, value).apply()
        }
    }

    fun getVaxDose(context: Context, doseNum: Int): String {
        if(doseNum == 1) {
            return context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
                .getString(VAX_1STDOSE, "") ?: ""
        } else {
            return context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
                .getString(VAX_2NDDOSE, "") ?: ""
        }
    }

    fun putVaxLotNo(context: Context, value: String, doseNum: Int) {
        if(doseNum == 1) {
            context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
                .edit().putString(VAX_1STLOTNO, value).apply()
        } else {
            context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
                .edit().putString(VAX_2NDLOTNO, value).apply()
        }
    }

    fun getVaxLotNo(context: Context, doseNum: Int): String {
        if(doseNum == 1) {
            return context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
                .getString(VAX_1STLOTNO, "") ?: ""
        } else {
            return context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
                .getString(VAX_2NDLOTNO, "") ?: ""
        }
    }

    fun putVaxBatchNo(context: Context, value: String, doseNum: Int) {
        if(doseNum == 1) {
            context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
                .edit().putString(VAX_1STBATCHNO, value).apply()
        } else {
            context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
                .edit().putString(VAX_2NDBATCHNO, value).apply()
        }
    }

    fun getVaxBatchNo(context: Context, doseNum: Int): String {
        if(doseNum == 1) {
            return context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
                .getString(VAX_1STBATCHNO, "") ?: ""
        } else {
            return context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
                .getString(VAX_2NDBATCHNO, "") ?: ""
        }
    }

    fun putVaxVaccinator(context: Context, value: String, doseNum: Int) {
        if(doseNum == 1) {
            context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
                .edit().putString(VAX_1STVACCINATOR, value).apply()
        } else {
            context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
                .edit().putString(VAX_2NDVACCINATOR, value).apply()
        }
    }

    fun getVaxVaccinator(context: Context, doseNum: Int): String {
        if(doseNum == 1) {
            return context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
                .getString(VAX_1STVACCINATOR, "") ?: ""
        } else {
            return context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
                .getString(VAX_2NDVACCINATOR, "") ?: ""
        }
    }

    fun putVaxFacility(context: Context, value: String) {
        context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
            .edit().putString(VAX_FACILITY, value).apply()
    }

    fun getVaxFacility(context: Context): String {
        return context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
            .getString(VAX_FACILITY, "") ?: ""
    }

    fun putCloudMessagingToken(context: Context, value: String){
        context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
            .edit().putString(FCM_TOKEN, value).apply()
    }

    fun getCloudMessagingToken(context: Context): String {
        return context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
            .getString(FCM_TOKEN, "") ?: ""
    }

    fun putTokenUploadStatus(context: Context, value: String){
        context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
            .edit().putString(TOKEN_UPLOADED, value).apply()
    }

    fun getTokenUploadStatus(context: Context): String {
        return context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
            .getString(TOKEN_UPLOADED, "") ?: ""
    }

    fun putTestStatus(context: Context, value: String) {
        context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
            .edit().putString(LATEST_TESTSTATUS, value).apply()
    }

    fun getTestStatus(context: Context): String {
        return context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
            .getString(LATEST_TESTSTATUS, "") ?: ""
    }

    fun putLastTestDate(context: Context, value: String) {
        context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
            .edit().putString(LAST_TESTDATE, value).apply()
    }

    fun getLastTestDate(context: Context): String {
        return context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
            .getString(LAST_TESTDATE, "") ?: ""
    }

    fun putLastTestID(context: Context, value: String) {
        context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
            .edit().putString(LAST_TESTID, value).apply()
    }

    fun getLastTestID(context: Context): String {
        return context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
            .getString(LAST_TESTID, "") ?: ""
    }

    fun putLastTestFac(context: Context, value: String) {
        context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
            .edit().putString(LAST_TESTFAC, value).apply()
    }

    fun getLastTestFac(context: Context): String {
        return context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
            .getString(LAST_TESTFAC, "") ?: ""
    }

    fun putLastTestMethod(context: Context, value: String) {
        context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
            .edit().putString(LAST_TESTMETHOD, value).apply()
    }

    fun getLastTestMethod(context: Context): String {
        return context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
            .getString(LAST_TESTMETHOD, "") ?: ""
    }

    fun putVerification(context: Context, value: String) {
        context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
            .edit().putString(VERIFICATION, value).apply()
    }

    fun getVerification(context: Context): String {
        return context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
            .getString(VERIFICATION, "false") ?: ""
    }

    fun putPhoneNumber(context: Context, value: String) {
        context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
                .edit().putString(PHONE_NUMBER, value).apply()
    }

    fun getPhoneNumber(context: Context): String {
        return context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
                .getString(PHONE_NUMBER, "") ?: ""
    }

    fun putCheckpoint(context: Context, value: Int) {
        context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
                .edit().putInt(CHECK_POINT, value).apply()
    }

    fun getCheckpoint(context: Context): Int {
        return context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
                .getInt(CHECK_POINT, 0)
    }

    fun getLastFetchTimeInMillis(context: Context): Long {
        return context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
                .getLong(
                        LAST_FETCH_TIME, 0
                )
    }

    fun putLastFetchTimeInMillis(context: Context, time: Long) {
        context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
                .edit().putLong(LAST_FETCH_TIME, time).apply()
    }

    fun putNextFetchTimeInMillis(context: Context, time: Long) {
        context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
                .edit().putLong(NEXT_FETCH_TIME, time).apply()
    }

    fun getNextFetchTimeInMillis(context: Context): Long {
        return context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
                .getLong(
                        NEXT_FETCH_TIME, 0
                )
    }

    fun putExpiryTimeInMillis(context: Context, time: Long) {
        context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
                .edit().putLong(EXPIRY_TIME, time).apply()
    }

    fun getExpiryTimeInMillis(context: Context): Long {
        return context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
                .getLong(
                        EXPIRY_TIME, 0
                )
    }

    fun putAnnouncement(context: Context, announcement: String) {
        context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
                .edit().putString(ANNOUNCEMENT, announcement).apply()
    }

    fun getAnnouncement(context: Context): String {
        return context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
                .getString(ANNOUNCEMENT, "") ?: ""
    }

    fun putLastPurgeTime(context: Context, lastPurgeTime: Long) {
        context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
                .edit().putLong(LAST_PURGE_TIME, lastPurgeTime).apply()
    }

    fun getLastPurgeTime(context: Context): Long {
        return context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
                .getLong(LAST_PURGE_TIME, 0)
    }

    fun nukePreferences(context: Context){
        context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
            .edit().clear().apply()
    }

    fun registerListener(
            context: Context,
            listener: SharedPreferences.OnSharedPreferenceChangeListener
    ) {
        context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
                .registerOnSharedPreferenceChangeListener(listener)
    }

    fun unregisterListener(
            context: Context,
            listener: SharedPreferences.OnSharedPreferenceChangeListener
    ) {
        context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
                .unregisterOnSharedPreferenceChangeListener(listener)
    }
}