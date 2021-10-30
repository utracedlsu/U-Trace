package com.capstone.app.utrace_cts.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.capstone.app.utrace_cts.R
import com.capstone.app.utrace_cts.TestStatusActivity
import com.capstone.app.utrace_cts.UploadDataActivity
import org.eazegraph.lib.charts.BarChart
import org.eazegraph.lib.models.BarModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ContactTracingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ContactTracingFragment : Fragment(R.layout.fragment_contact_tracing) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // initialize, get appropriate color
        var cthChart: BarChart = view.findViewById(R.id.bc_cthChart)
        var green: Int = Color.parseColor("#428E5C")

        // sample values
        cthChart.addBar(BarModel("S", 3f,green))
        cthChart.addBar(BarModel("M", 2f, green))
        cthChart.addBar(BarModel("T", 4f,green))
        cthChart.addBar(BarModel("W", 8f,green))
        cthChart.addBar(BarModel("H", 6f,green))
        cthChart.addBar(BarModel("F", 1f,green))
        cthChart.addBar(BarModel("S", 2f,green))

        // start chart animation
        cthChart.startAnimation()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ContactTracingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ContactTracingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}