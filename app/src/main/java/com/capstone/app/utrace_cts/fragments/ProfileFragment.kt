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
import com.capstone.app.utrace_cts.vaxboosters.persistence.VaxBoosterRecord
import com.capstone.app.utrace_cts.vaxboosters.persistence.VaxBoosterRecordStorage
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.w3c.dom.Text
import java.util.*
import kotlin.collections.ArrayList

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

    private var disposableObj: Disposable? = null //used to read SQLite Records

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
        setVaxTVs()

        // boosters recyclerview init
        rv_boosters.layoutManager = LinearLayoutManager(this.context)
        rv_boosters.setHasFixedSize(true)
        boosterList = arrayListOf<Booster>()
        getBoosterData() //retrieve boosters from DB

        // initialize Logout Dialog
        initLogoutDialog()

        if(Preference.getVerification(requireContext()).equals("false")){
            btn_verifyAcc.setOnClickListener {
                goToOTP("UserVerification")
            }
        } else {
            //disable verification button if already verified
            btn_verifyAcc.setEnabled(false)
            btn_verifyAcc.setText("ACCOUNT VERIFIED")
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

    //activity source should be UserVerification or UserDeletion
    private fun goToOTP(activitySource: String){
        var phoneNo = Preference.getPhoneNumber(requireContext())

        var intentDetails = hashMapOf(
            //remove the '+63' in the saved preference phone number
            "activity_source" to activitySource,
            "phone" to phoneNo.drop(3)
        )

        val otpIntent = Intent(requireContext(), OtpActivationActivity::class.java)

        otpIntent.putExtra("USER_DETAILS", intentDetails)

        startActivity(otpIntent)
    }

    //sets the vaccination text views
    private fun setVaxTVs(){
        if(!Preference.getVaxID(requireContext()).equals("")){
            tv_vaxID.setText(Preference.getVaxID(requireContext()))
        } else {
            tv_vaxID.setText("No vaccine ID set.")
        }

        if(!Preference.getVaxManufacturer(requireContext()).equals("")){
            tv_vaxBrand.setText(Preference.getVaxManufacturer(requireContext()))
        } else {
            tv_vaxBrand.setText("No vaccine brand set.")
        }

        if(!Preference.getVaxDose(requireContext(), 1).equals("")){
            tv_firstDose.setText(Preference.getVaxDose(requireContext(), 1))
        } else {
            tv_firstDose.setText("No first dose date set.")
        }

        if(!Preference.getVaxDose(requireContext(), 2).equals("")){
            tv_secondDose.setText(Preference.getVaxDose(requireContext(), 2))
        } else {
            tv_secondDose.setText("No second dose date set.")
        }
    }

    private fun getBoosterData(){
        var observableBoosters = Observable.create<List<VaxBoosterRecord>>{
            val result = VaxBoosterRecordStorage(requireContext()).getAllBoosters()
            it.onNext(result)
        }

        disposableObj = observableBoosters.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
            .subscribe { retrievedBoosters ->
                if(retrievedBoosters.size > 0){
                    for(booster in retrievedBoosters){
                        boosterList.add(Booster(booster.date, booster.vaxbrand))
                    }
                    rv_boosters.adapter = BoosterAdapter(boosterList)
                } else {
                    //TODO: Display 'No boosters' textview or something
                }
            }
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

    override fun onDestroy() {
        super.onDestroy()
        disposableObj?.dispose()
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