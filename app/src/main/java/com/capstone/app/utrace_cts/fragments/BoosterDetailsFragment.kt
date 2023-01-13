package com.capstone.app.utrace_cts.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.capstone.app.utrace_cts.R

class BoosterDetailsFragment: DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // initialize, set bg to transparent
        var content: View = inflater.inflate(R.layout.fragment_boosteraddinfo, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // connect
        var tv_boosterDate: TextView = content.findViewById(R.id.tv_boosterDate)
        var tv_boosterBrand: TextView = content.findViewById(R.id.tv_boosterBrand)
        var tv_boosterFacility: TextView = content.findViewById(R.id.tv_boosterFacility)
        var tv_boosterBatchNo: TextView = content.findViewById(R.id.tv_boosterBatchNo)
        var tv_boosterLotNo: TextView = content.findViewById(R.id.tv_boosterLotNo)
        var tv_boosterVaccinator : TextView = content.findViewById(R.id.tv_boosterVaccinator)
        var tv_boosterCategory : TextView = content.findViewById(R.id.tv_boosterCategory)

        // close dialog when user touched outside
        dialog?.setCanceledOnTouchOutside(true)

        // get data
        val bundle = arguments
        tv_boosterDate.text = bundle?.getString("booster_date")
        tv_boosterBrand.text = bundle?.getString("booster_brand")
        tv_boosterFacility.text = bundle?.getString("booster_facility")
        tv_boosterBatchNo.text = bundle?.getString("booster_blockno")
        tv_boosterLotNo.text = bundle?.getString("booster_lotno")
        tv_boosterVaccinator.text = bundle?.getString("booster_vaccinator")

        return content
    }

}