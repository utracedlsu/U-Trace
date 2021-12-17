package com.capstone.app.utrace_cts.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.app.utrace_cts.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import org.w3c.dom.Text

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var ll_logout: LinearLayout
    private lateinit var builder: MaterialAlertDialogBuilder
    private lateinit var tvProfileName: TextView
    private lateinit var tvProfileAddress: TextView
    private lateinit var tv_vaxID : TextView
    private lateinit var tv_vaxBrand : TextView
    private lateinit var tv_firstDose : TextView
    private lateinit var tv_secondDose : TextView
    private lateinit var rv_boosters: RecyclerView
    private lateinit var btn_verifyAcc : Button
    private lateinit var btn_deleteAcc : Button

    private lateinit var boosterList : ArrayList<Booster>

    lateinit var dates : Array<String>
    lateinit var brands : Array<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // connect
        tvProfileName = view.findViewById(R.id.tv_profile_fullname)
        tvProfileAddress = view.findViewById(R.id.tv_profile_address)
        tv_vaxID = view.findViewById(R.id.tv_vaxID)
        tv_vaxBrand = view.findViewById(R.id.tv_vaxBrand)
        tv_firstDose = view.findViewById(R.id.tv_firstDose)
        tv_secondDose = view.findViewById(R.id.tv_secondDose)
        rv_boosters = view.findViewById(R.id.rv_boosters)
        btn_verifyAcc = view.findViewById(R.id.btn_verifyAcc)
        btn_deleteAcc = view.findViewById(R.id.btn_deleteAcc)
        ll_logout = view.findViewById(R.id.ll_logout)

        // set field data
        tvProfileName.text = Preference.getFullName(requireContext())
        tvProfileAddress.text = (Preference.getFullAddress(requireContext()))

        // boosters recyclerview init
        rv_boosters.layoutManager = LinearLayoutManager(this.context)
        rv_boosters.setHasFixedSize(true)
        boosterList = arrayListOf<Booster>()
            // TESTER: FOR TESTING BOOSTERS RECYCLER VIEW
            dates = arrayOf(
                "01-01-2021",
                "01-02-2021",
                "01-03-2021",
                "01-04-2021",
            )
            brands = arrayOf(
                "Moderna",
                "Sinovac",
                "Sinopharm",
                "Pfizer",
            )
        getTesterData() // TESTER

        // initialize Logout Dialog
        initLogoutDialog()

        // verify account -- go to <?>
        btn_verifyAcc.setOnClickListener {
            // TODO: maybe go to OtpActivationActivity again?
        }

        // delete account -- open confirm deletion dialog
        btn_deleteAcc.setOnClickListener {
            val confirmDeleteDialog = ConfirmAccDeletionFragment()
            confirmDeleteDialog.show(parentFragmentManager, "deleteAccDialog")
        }

        // logout -- go to login activity
        ll_logout.setOnClickListener {
            builder.show()
        }
    }

    // POPULATE TESTER ARRAY
    private fun getTesterData() {
        for (i in dates.indices) {
            val booster = Booster(dates[i], brands[i])
            boosterList.add(booster)
        }
        // Note: Only after the data is collected should the recycler view be triggered
        rv_boosters.adapter = BoosterAdapter(boosterList)
    }

    // initialize logout alert dialog
    private fun initLogoutDialog() {

        builder = MaterialAlertDialogBuilder(requireContext())
        builder.setTitle("Logout Confirmation")
        builder.setMessage("Are you sure you want to logout?")

        // Yes — finish all activities, go to LoginActivity
        builder.setPositiveButton("Yes") {dialog, which ->
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(context, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("EXIT", true)
            startActivity(intent)
            activity?.finish()
        }

        // No — do nothing
        builder.setNegativeButton("Cancel") {dialog, which ->
            dialog.dismiss()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}