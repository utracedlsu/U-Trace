package com.capstone.app.utrace_cts

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PowerManager
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.capstone.app.utrace_cts.fragments.ContactTracingFragment
import com.capstone.app.utrace_cts.fragments.HomeFragment
import com.capstone.app.utrace_cts.fragments.NotificationsFragment
import com.capstone.app.utrace_cts.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

private const val LOCATION_PERMISSION_REQUEST_CODE = 2
private const val REQUEST_ENABLE_BT = 123
private const val BATTERY_OPTIMISER = 789

class MainActivity : AppCompatActivity() {

    private val homeFragment = HomeFragment()
    private val profileFragment = ProfileFragment()
    private val notificationsFragment = NotificationsFragment()
    private val contactTracingFragment = ContactTracingFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Navigation Bar initialization
        val nav: BottomNavigationView = findViewById(R.id.navigationBar)
        nav.itemIconTintList = null // make icon-switching (when clicked) work

        // set HomeFragment as default
        nav.menu[2].isChecked = true
        replaceFragment(homeFragment)

        // Navigation Bar Logic (Try: setOnItemSelectedListener)
        nav.setOnNavigationItemSelectedListener{
            when(it.itemId){
                R.id.homeFrag -> replaceFragment(homeFragment)
                R.id.profileFrag -> replaceFragment(profileFragment)
                R.id.notifFrag -> replaceFragment(notificationsFragment)
                R.id.contactTracingFrag -> replaceFragment(contactTracingFragment)
            }
            true
        }

        if(!isLocationPermissionGranted){
            requestLocationPermission()
        } else {
            Utils.startBluetoothMonitoringService(this)
        }

        //recieveNotifs()
    }

    //UNUSED FOR NOW: receive notifications when app is in background
    private fun recieveNotifs(){
        val fbIntent = intent
        val firebaseUserID = Preference.getFirebaseId(applicationContext)
        fbIntent.let { intent ->
            val extras = intent.extras
            extras?.let { recExtras ->
                val notifFlag = recExtras.getString("notif_flag")

                when (notifFlag){
                    "1" -> {
                        Log.i("FirebaseNotifications", "Attempting to retrieve test data...")
                        FirebaseFirestore.getInstance().collection("users").document(firebaseUserID)
                            .get().addOnCompleteListener { task ->
                                if(task.isSuccessful){
                                    Log.i("FirebaseNotifications", "Task successful, saving to preferences")
                                    val snapshot = task.result
                                    val latestTestResult = snapshot?.getBoolean("covid_positive")
                                    val latestTestDate = snapshot?.getString("last_testdate")

                                    //save latest test data to preferences
                                    Preference.putTestStatus(applicationContext, latestTestResult.toString())
                                    Preference.putLastTestDate(applicationContext, latestTestDate.toString())
                                    Log.i("FirebaseNotifications", "Test Results have been updated")

                                } else {
                                    Log.e("FirebaseNotifications", "Failed to get Test Data: ${task.exception?.message}")
                                }
                            }
                    }
                    "2" -> {

                    }
                    "3" -> {
                        Log.i("FirebaseNotifications", "Attempting to retrieve first dose data...")
                        FirebaseFirestore.getInstance().collection("users").document(firebaseUserID)
                            .get().addOnCompleteListener { task ->
                                if(task.isSuccessful){
                                    Log.i("FirebaseNotifications", "Task successful, saving to preferences")
                                    val snapshot = task.result

                                    val vaxID = snapshot?.getString("vax_ID")
                                    val vax1stDose = snapshot?.getString("vax_1stdose")
                                    val vaxManufacturer = snapshot?.getString("vax_manufacturer")

                                    //save latest vax data to preferences
                                    Preference.putVaxID(applicationContext, vaxID.toString())
                                    Preference.putVaxDose(applicationContext, vax1stDose.toString(), 1)
                                    Preference.putVaxManufacturer(applicationContext, vaxManufacturer.toString())
                                    Log.i("FirebaseNotifications", "Vaccination data has been updated (first dose)")

                                } else {
                                    Log.e("FirebaseNotifications", "Failed to get first dose data: ${task.exception?.message}")
                                }
                            }
                    }
                    "4" -> {
                        Log.i("FirebaseNotifications", "Attempting to retrieve second dose data...")
                        FirebaseFirestore.getInstance().collection("users").document(firebaseUserID)
                            .get().addOnCompleteListener { task ->
                                if(task.isSuccessful){
                                    Log.i("FirebaseNotifications", "Task successful, saving to preferences")
                                    val snapshot = task.result

                                    val vax2ndDose = snapshot?.getString("vax_2nddose")

                                    //save latest vax data to preferences
                                    Preference.putVaxDose(applicationContext, vax2ndDose.toString(), 2)
                                    Log.i("FirebaseNotifications", "Vaccination data has been updated (second dose)")

                                } else {
                                    Log.e("FirebaseNotifications", "Failed to get second dose data: ${task.exception?.message}")
                                }
                            }
                    }
                    else -> {
                        Log.e("HomeFragment", "Unknown notif flag; do nothing")
                    }
                }
            }
        }
    }

    // Check if permissions are granted

    val isLocationPermissionGranted
        get() = hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun Context.hasPermission(permissionType: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permissionType) ==
                PackageManager.PERMISSION_GRANTED
    }
    // Request permission from user
    private fun requestLocationPermission() {
        if (isLocationPermissionGranted) {
            return
        }
        runOnUiThread {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Location permission required")
            builder.setMessage("Starting from Android M (6.0), the system requires apps to be granted " +
                    "location access in order to scan for BLE devices.")
            builder.setCancelable(false)
            //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = ))
            builder.setPositiveButton(android.R.string.ok) {dialog, which
                ->                 requestPermission(
                Manifest.permission.ACCESS_FINE_LOCATION,
                LOCATION_PERMISSION_REQUEST_CODE
            )
            }
            builder.show()
        }
    }

    private fun Activity.requestPermission(permission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.firstOrNull() == PackageManager.PERMISSION_DENIED) {
                    requestLocationPermission()
                    excludeFromBatteryOptimization()
                } else {
                    excludeFromBatteryOptimization() //tentative location
                    Utils.startBluetoothMonitoringService(this)
                }
            }
        }
    }
    //Request Bluetooth Permission
    override fun onResume(){
        super.onResume()
        if(!bluetoothAdapter.isEnabled){
            promptBluetoothEnable()
        }
    }

    private fun promptBluetoothEnable(){
        if(!bluetoothAdapter.isEnabled){
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            getResult.launch(enableIntent)
        }
    }
    private val getResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            val value = it.data?.getStringExtra("input")
        }
    }

    private fun excludeFromBatteryOptimization() {
        Log.d("MainActivityLog", "[excludeFromBatteryOptimization] ")
        val powerManager =
            this.getSystemService(AppCompatActivity.POWER_SERVICE) as PowerManager
        val packageName = this.packageName
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent =
                Utils.getBatteryOptimizerExemptionIntent(
                    packageName
                )

            if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
                Log.d("MainActivityLog", "Not on Battery Optimization whitelist")
                //check if there's any activity that can handle this
                if (Utils.canHandleIntent(
                        intent,
                        packageManager
                    )
                ) {
                    getResult.launch(intent)
                } else {
                    Log.d("MainActivityLog", "No way of handling optimizer")
                }
            } else {
                Log.d("MainActivityLog", "On Battery Optimization whitelist")
            }
        }
    }

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    // updates the fragment holder (view), given a fragment
    private fun replaceFragment(fragment: Fragment){
        if(fragment != null){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentHolder, fragment)
            transaction.commit()
        }
    }

    //check if user is already logged in, if not then send to login page
    override fun onStart() {
        super.onStart()

        if(FirebaseAuth.getInstance().currentUser == null){
            startActivity(Intent(this, LoginActivity::class.java))
        }

        //check if token is empty
        if(Preference.getCloudMessagingToken(applicationContext).equals("")){
            Log.i("FirebaseNotifications", "No FCM Token, retrieving from server")

            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val newToken = task.result
                    val fUserId = FirebaseAuth.getInstance().currentUser?.uid
                    Log.i("FirebaseNotifications", "User ID: $fUserId")
                    Log.i("FirebaseNotifications", "New FCM Token: $newToken")
                    Preference.putCloudMessagingToken(applicationContext, newToken.toString())

                    FirebaseFirestore.getInstance().collection("users")
                        .document(fUserId.toString()).update("fcm_token", newToken.toString()).addOnCompleteListener { task ->
                            if(task.isSuccessful){
                                Log.i("FirebaseNotifications", "Successfully sent token to server")
                            } else {
                                Log.i("FirebaseNotifications", "Unable to send token to server: ${task.exception?.message}")
                            }
                        }
                } else {
                    Log.i("FirebaseNotifications", "Unable to retrieve FCM Token, ${task.exception?.message}")
                }
            }
        } else {
            Log.i("FirebaseNotifications", "FCM Token: ${Preference.getCloudMessagingToken(applicationContext)}")
        }
    }
}