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

    private lateinit var builder: MaterialAlertDialogBuilder
    private lateinit var tvProfileName: TextView
    private lateinit var tvProfileAddress: TextView
    private lateinit var tv_vaxID : TextView
    private lateinit var tv_vaxBrand : TextView
    private lateinit var tv_firstDose : TextView
    private lateinit var tv_secondDose : TextView
    private lateinit var tv_vaxFacility : TextView
    private lateinit var tv_vaxCategory : TextView
    private lateinit var rv_boosters: RecyclerView
    private lateinit var btn_verifyAcc : Button
    private lateinit var btn_deleteAcc : Button
    private lateinit var btn_logout: Button
    private lateinit var ll_firstDose : LinearLayout
    private lateinit var ll_secondDose : LinearLayout

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

        tv_vaxFacility = view.findViewById(R.id.tv_vaxFacility)
        tv_vaxCategory = view.findViewById(R.id.tv_vaxCategory)
        ll_firstDose = view.findViewById(R.id.ll_firstDose)
        ll_secondDose = view.findViewById(R.id.ll_secondDose)

        rv_boosters = view.findViewById(R.id.rv_boosters)
        btn_verifyAcc = view.findViewById(R.id.btn_verifyAcc)
        btn_deleteAcc = view.findViewById(R.id.btn_deleteAcc)
        btn_logout = view.findViewById(R.id.btn_logout)

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
        btn_logout.setOnClickListener {
            builder.show()
        }


        if (!Preference.getVaxDose(requireContext(), 1).equals("")) {
            // when user clicks first dose, show dialog popup containing details of first dose (Date, Batch #, Lot#, Vaccinator)
            ll_firstDose.setOnClickListener {
                val firstDoseDialog = VaccinationDetailsFragment()
                val bundle = Bundle()
                bundle.putString("vax_dose", "First Dose Details")
                bundle.putString("vax_date", Preference.getVaxDose(requireContext(), 1))
                bundle.putString("vax_batchno", Preference.getVaxBatchNo(requireContext(), 1))
                bundle.putString("vax_lotno", Preference.getVaxLotNo(requireContext(), 1))
                bundle.putString("vax_vaccinator", Preference.getVaxVaccinator(requireContext(), 1))
                firstDoseDialog.arguments = bundle
                firstDoseDialog.show(parentFragmentManager, "firstDoseDialog")
            }
        }

        if (!Preference.getVaxDose(requireContext(), 2).equals("")) {
            // when user clicks second dose, show dialog popup containing details of second dose (Date, Batch #, Lot#, Vaccinator)
            ll_secondDose.setOnClickListener {
                val secondDoseDialog = VaccinationDetailsFragment()
                val bundle = Bundle()
                bundle.putString("vax_dose", "Second Dose Details")
                bundle.putString("vax_date", Preference.getVaxDose(requireContext(), 2))
                bundle.putString("vax_batchno", Preference.getVaxBatchNo(requireContext(), 2))
                bundle.putString("vax_lotno", Preference.getVaxLotNo(requireContext(), 2))
                bundle.putString("vax_vaccinator", Preference.getVaxVaccinator(requireContext(), 2))
                secondDoseDialog.arguments = bundle
                secondDoseDialog.show(parentFragmentManager, "secondDoseDialog")
            }
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
            tv_vaxFacility.setText(Preference.getVaxFacility(requireContext()))
            tv_vaxCategory.setText(Preference.getVaxCategory(requireContext()))
        } else {
            tv_vaxFacility.setText("No vaccine facility set.")
            tv_vaxCategory.setText("No vaccine category set.")
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
                        boosterList.add(Booster(
                            booster.date,
                            booster.vaxbrand,
                            booster.lotno,
                            booster.blockno,
                            booster.vaccinator,
                            booster.category,
                            booster.facility
                            ))
                    }

                    // PREVIOUS CODE: rv_boosters.adapter = BoosterAdapter(boosterList)
                    // just one line -- this one line is replaced by the several lines below
                    // pag di gumana, use the above code ^

                    var adapter = BoosterAdapter(boosterList)
                    rv_boosters.adapter = adapter
                    adapter.setOnItemClickListener(object: BoosterAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {

                            val boosterDetailsDialog = BoosterDetailsFragment()
                            val bundle = Bundle()

                            bundle.putString("booster_date", boosterList.get(position).date)
                            bundle.putString("booster_brand", boosterList.get(position).brand)
                            bundle.putString("booster_facility", boosterList.get(position).facility)
                            bundle.putString("booster_blockno", boosterList.get(position).blockno) //batch no
                            bundle.putString("booster_lotno", boosterList.get(position).lotno)
                            bundle.putString("booster_vaccinator", boosterList.get(position).vaccinator)
                            bundle.putString("booster_category", boosterList.get(position).category)

                            boosterDetailsDialog.arguments = bundle
                            boosterDetailsDialog.show(parentFragmentManager, "boosterDetailsDialog")
                        }
                    })
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
            Preference.putLoggedIn(requireContext(), false)
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