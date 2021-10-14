package com.capstone.app.utrace_cts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.capstone.app.utrace_cts.fragments.HomeFragment
import com.capstone.app.utrace_cts.fragments.NotificationsFragment
import com.capstone.app.utrace_cts.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private val homeFragment = HomeFragment()
    private val profileFragment = ProfileFragment()
    private val notificationsFragment = NotificationsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Navigation Bar initialization
        val nav: BottomNavigationView = findViewById(R.id.navigationBar)
        nav.setItemIconTintList(null) // make icon-switching (when clicked) work
        nav.menu.get(2).setChecked(true) // set home (third in the array) as default

        replaceFragment(homeFragment) // set fragment to home on start

        // Navigation Bar : Navigation logic
        nav.setOnNavigationItemSelectedListener{
            when(it.itemId){
                R.id.homeFrag -> replaceFragment(homeFragment)
                R.id.profileFrag -> replaceFragment(profileFragment)
                R.id.notifFrag -> replaceFragment(notificationsFragment)
            }
            true
        }
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