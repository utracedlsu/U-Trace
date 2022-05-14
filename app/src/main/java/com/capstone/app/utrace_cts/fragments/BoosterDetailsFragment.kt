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
        //TODO: get data from bundle

        // set data
        //TODO: set data (retrieved from bundle)
        tv_boosterDate.text = ""
        tv_boosterBrand.text = ""
        tv_boosterFacility.text = ""
        tv_boosterBatchNo.text = ""
        tv_boosterLotNo.text = ""
        tv_boosterVaccinator.text = ""
        tv_boosterCategory.text = ""

        return content
    }

}