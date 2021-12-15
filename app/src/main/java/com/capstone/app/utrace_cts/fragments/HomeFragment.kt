package com.capstone.app.utrace_cts.fragments

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.capstone.app.utrace_cts.*
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
    private lateinit var tvTest: TextView
    private lateinit var tvVax: TextView
    private val btReceiver = BluetoothBroadcastReceiver()
    private lateinit var localBroadcastManager: LocalBroadcastManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //localBroadcastManager = LocalBroadcastManager.getInstance(requireActivity().applicationContext)

        val btFilter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        requireActivity().registerReceiver(btReceiver, btFilter)
        Log.d("HomeFragment", "Receiver Registered")

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

        // Retrieve vaccination and test status from preferences
        tvTest = view.findViewById(R.id.tv_test)
        tvVax = view.findViewById(R.id.tv_vaccination)

        // change up na lang yung captions here
        val testStatus = Preference.getTestStatus(requireContext())
        //another random comment
        // either empty string, positive or negative
        if(testStatus.equals("")){
            tvTest.setText("Your test status has not been set.")
        } else if (testStatus.equals("true")) {
            tvTest.setText("You have tested positive for COVID-19. Blah blah blah etc")
        } else {
            tvTest.setText("You have tested negative for COVID-19. Blah blah blah etc")
        }

        val vaxID = Preference.getVaxID(requireContext())
        if(vaxID.equals("")){
            tvVax.setText("Your vaccination status has not yet been set.")
        } else {
            val vaxCount = Preference.getVaxCount(requireContext())
            tvVax.setText("You have been vaccinated $vaxCount times.")
        }
    }

    inner class BluetoothBroadcastReceiver: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent) {
            val action = intent.action
            Log.d("HomeFragment", "Entered Receiver")
            if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                when (state){
                    BluetoothAdapter.STATE_OFF -> {
                        Log.d("HomeFragment", "BT is OFF")
                        iv_bluetooth.setImageResource(R.drawable.bt_base)
                        tv_bluetooth.text = "Bluetooth is off. Turn Bluetooth on to enable contact tracing."
                    }
                    BluetoothAdapter.STATE_ON -> {
                        Log.d("HomeFragment", "BT is ON")
                        iv_bluetooth.setImageResource(R.drawable.bt)
                        tv_bluetooth.text = "Bluetooth is up and running! Remember to leave it on when you're outside."
                    }
                    BluetoothAdapter.ERROR -> {
                        iv_bluetooth.setImageResource(R.drawable.bt_base)
                        tv_bluetooth.text = "Unable to get Bluetooth status at this time. "
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //localBroadcastManager.unregisterReceiver(btReceiver)
        requireActivity().unregisterReceiver(btReceiver)
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