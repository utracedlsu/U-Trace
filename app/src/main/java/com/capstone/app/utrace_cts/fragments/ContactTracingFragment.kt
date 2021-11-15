package com.capstone.app.utrace_cts.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import com.capstone.app.utrace_cts.R
import com.capstone.app.utrace_cts.TestStatusActivity
import com.capstone.app.utrace_cts.UploadDataActivity
import com.capstone.app.utrace_cts.status.persistence.StatusRecord
import com.capstone.app.utrace_cts.status.persistence.StatusRecordStorage
import com.capstone.app.utrace_cts.streetpass.persistence.StreetPassRecord
import com.capstone.app.utrace_cts.streetpass.persistence.StreetPassRecordStorage
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_contact_tracing.*
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

    private var disposableObj: Disposable? = null //used to read SQLite Records
    private lateinit var cthChart: BarChart //put cthChart here so that it can be used in different functions
    private var green: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // initialize, get appropriate color
        cthChart = view.findViewById(R.id.bc_cthChart)
        green = Color.parseColor("#428E5C")

        //retrieve database records here and add bars
        getReportsFromDB()

    }

    //function to get data from database
    private fun getReportsFromDB(){
        //Get records here
        var observableStreetRecords = Observable.create<List<StreetPassRecord>> {
            val result = StreetPassRecordStorage(requireActivity().applicationContext).getAllRecords()
            it.onNext(result)
        }
        var observableStatusRecords = Observable.create<List<StatusRecord>> {
            val result = StatusRecordStorage(requireActivity().applicationContext).getAllRecords()
            it.onNext(result)
        }

        disposableObj = Observable.zip(observableStreetRecords, observableStatusRecords,
            BiFunction<List<StreetPassRecord>, List<StatusRecord>, ExportData>{records, status ->
                ExportData(records, status)
            }
        ).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
            .subscribe {    exportedData ->
                Log.d("ContactTracingActivity", "Records: ${exportedData.recordList}")
                Log.d("ContactTracingActivity", "${exportedData.statusList}")

                //Make changes to bar data here?
                if(exportedData.recordList.size > 0){
                    // convert long to dateformat
                    // get day of the week

                } else {
                    // sample values
                    cthChart.addBar(BarModel("S", 1f,green))
                    cthChart.addBar(BarModel("M", 1f, green))
                    cthChart.addBar(BarModel("T", 1f,green))
                    cthChart.addBar(BarModel("W", 1f,green))
                    cthChart.addBar(BarModel("H", 1f,green))
                    cthChart.addBar(BarModel("F", 1f,green))
                    cthChart.addBar(BarModel("S", 1f,green))
                    // start chart animation
                    cthChart.startAnimation()
                }
            }
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