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

class VaccinationDetailsFragment: DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // initialize, set bg to transparent
        var content: View = inflater.inflate(R.layout.fragment_vaxaddinfo, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // connect
        var tv_doseNo: TextView = content.findViewById(R.id.tv_doseNo)
        var tv_doseDate: TextView = content.findViewById(R.id.tv_doseDate)
        var tv_doseBatchNo: TextView = content.findViewById(R.id.tv_doseBatchNo)
        var tv_doseLotNo: TextView = content.findViewById(R.id.tv_doseLotNo)
        var tv_doseVaccinator : TextView = content.findViewById(R.id.tv_doseVaccinator)

        // close dialog when user touched outside
        dialog?.setCanceledOnTouchOutside(true)

        // get data
        val bundle = arguments
        //TODO: get data from bundle

        // set data
        //TODO: set data (retrieved from bundle)
        tv_doseNo.text = ""
        tv_doseDate.text = ""
        tv_doseBatchNo.text = ""
        tv_doseLotNo.text = ""
        tv_doseVaccinator.text = ""

        return content
    }

}