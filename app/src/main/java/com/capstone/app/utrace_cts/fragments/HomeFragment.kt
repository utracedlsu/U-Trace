package com.capstone.app.utrace_cts.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.capstone.app.utrace_cts.R
import com.capstone.app.utrace_cts.TestStatusActivity
import com.capstone.app.utrace_cts.UploadDataActivity
import com.capstone.app.utrace_cts.Utils
import com.google.android.material.bottomnavigation.BottomNavigationView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var ivUpload: ImageView
    private lateinit var ivTestStatus : ImageView
    private var btState = true // TRUE: Bluetooth is ON ; FALSE: Bluetooth is OFF
    private lateinit var iv_bluetooth: ImageView
    private lateinit var tv_bluetooth: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // connect
        ivUpload = view.findViewById(R.id.iv_upload)
        ivTestStatus = view.findViewById(R.id.iv_testStatus)
        iv_bluetooth = view.findViewById(R.id.iv_bluetooth)
        tv_bluetooth = view.findViewById(R.id.tv_bluetooth)

        // Go to UploadDataActivity
        ivUpload.setOnClickListener {
            val intent = Intent(activity, UploadDataActivity::class.java)
            startActivity(intent)
        }

        // Go to TestStatusActivity
        ivTestStatus.setOnClickListener {
            val intent = Intent(activity, TestStatusActivity::class.java)
            startActivity(intent)
        }

        // set Bluetooth button state
        if (btState) turnOffBt()
        else turnOnBt()

        // Bluetooth button functionality
        iv_bluetooth.setOnClickListener {
            if (btState) turnOffBt()
            else turnOnBt()
        }
    }

    private fun turnOnBt() {
        iv_bluetooth.setImageResource(R.drawable.bton)
        tv_bluetooth.text = "BLUETOOTH IS RUNNING "
    }

    private fun turnOffBt() {
        iv_bluetooth.setImageResource(R.drawable.btoff)
        tv_bluetooth.text = "BLUETOOTH IS OFF "
    }

    // TODO: set override back button confirming to exit app

//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment HomeFragment.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            HomeFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
}