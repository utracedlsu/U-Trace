package com.capstone.app.utrace_cts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.capstone.app.utrace_cts.fragments.ContactTracingFragment
import com.capstone.app.utrace_cts.fragments.HomeFragment
import com.capstone.app.utrace_cts.fragments.NotificationsFragment
import com.capstone.app.utrace_cts.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

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

        // Navigation Bar Logic
        nav.setOnNavigationItemSelectedListener{
            when(it.itemId){
                R.id.homeFrag -> replaceFragment(homeFragment)
                R.id.profileFrag -> replaceFragment(profileFragment)
                R.id.notifFrag -> replaceFragment(notificationsFragment)
                R.id.contactTracingFrag -> replaceFragment(contactTracingFragment)
            }
            true
        }
        Utils.startBluetoothMonitoringService(this)
    }

    // replaceFragment: updates the fragment holder (view) given a fragment
    private fun replaceFragment(fragment: Fragment){
        if(fragment != null){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentHolder, fragment)
            transaction.commit()
        }
    }
}