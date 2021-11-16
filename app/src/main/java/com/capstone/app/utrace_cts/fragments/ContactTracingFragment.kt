package com.capstone.app.utrace_cts.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

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
    private lateinit var tvBTExchanges: TextView
    private lateinit var tvDateRange: TextView

    private var green: Int = 0
    private var chartVals: ArrayList<Float> = arrayListOf(0f, 0f, 0f, 0f, 0f, 0f, 0f)
    private var daysOfTheWeek: ArrayList<String> = arrayListOf("SU", "MO", "TU", "WE", "TH", "FR", "SA")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // initialize, get appropriate color
        cthChart = view.findViewById(R.id.bc_cthChart)
        tvBTExchanges = view.findViewById(R.id.tv_btExchanges)
        tvDateRange = view.findViewById(R.id.tv_dateRange)
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

                //set exchanges for the past 24 hours
                val pastDayCount = exportedData.recordList.filter {
                    it.timestamp >= (System.currentTimeMillis() - 86400000) && it.timestamp <= System.currentTimeMillis()
                }.size
                //for testing purposes
                val pastWeekCount = exportedData.recordList.filter {
                    it.timestamp >= (System.currentTimeMillis() - 604800000) && it.timestamp <= System.currentTimeMillis()
                }.size
                //tvBTExchanges.setText("You have had ${pastDayCount} Bluetooth exchange(s) in the past 24 hours.")
                //for testing purposes
                tvBTExchanges.setText("You have had ${pastWeekCount} Bluetooth exchange(s) in the past week.")

                //Make changes to bar data here?
                if(exportedData.recordList.size > 0){
                    cthChart.clearChart()
                    val weekBefore: Long = System.currentTimeMillis() - 604800000
                    //new solution?
                    val filteredRecords = exportedData.recordList.filter {
                        it.timestamp >= weekBefore && it.timestamp <= System.currentTimeMillis()
                    }
                    //starting time and ending times
                    val startTime: Long = filteredRecords.sortedBy { it.timestamp }[0].timestamp
                    val endTime: Long = filteredRecords.sortedByDescending { it.timestamp }[0].timestamp

                    tvDateRange.setText("${convertLongToTime(startTime)} - ${convertLongToTime(endTime)}")
                    for(i in 0..(filteredRecords.size-1)) {
                        when (getDayOfTheWeek(filteredRecords.sortedByDescending{ it.timestamp }[i].timestamp)) {
                            "Sun" -> chartVals.set(0, (chartVals.get(0) + 1f))
                            "Mon" -> chartVals.set(1, (chartVals.get(1) + 1f))
                            "Tue" -> chartVals.set(2, (chartVals.get(2) + 1f))
                            "Wed" -> chartVals.set(3, (chartVals.get(3) + 1f))
                            "Thu" -> chartVals.set(4, (chartVals.get(4) + 1f))
                            "Fri" -> chartVals.set(5, (chartVals.get(5) + 1f))
                            "Sat" -> chartVals.set(6, (chartVals.get(6) + 1f))
                        }
                    }
                    cthChart.addBar(BarModel("S", chartVals.get(0),green))
                    cthChart.addBar(BarModel("M", chartVals.get(1),green))
                    cthChart.addBar(BarModel("T", chartVals.get(2),green))
                    cthChart.addBar(BarModel("W", chartVals.get(3),green))
                    cthChart.addBar(BarModel("H", chartVals.get(4),green))
                    cthChart.addBar(BarModel("F", chartVals.get(5),green))
                    cthChart.addBar(BarModel("S", chartVals.get(6),green))
                    // start chart animation
                    cthChart.startAnimation()
                } else {
                    tvDateRange.setText("No exchanges have been made in the past week.")
                    // sample values
                    cthChart.addBar(BarModel("S", 0f,green))
                    cthChart.addBar(BarModel("M", 0f,green))
                    cthChart.addBar(BarModel("T", 0f,green))
                    cthChart.addBar(BarModel("W", 0f,green))
                    cthChart.addBar(BarModel("H", 0f,green))
                    cthChart.addBar(BarModel("F", 0f,green))
                    cthChart.addBar(BarModel("S", 0f,green))
                    // start chart animation
                    cthChart.startAnimation()
                }
            }
    }

    fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("MMM dd, yyyy")
        return format.format(date)
    }

    fun getDayOfTheWeek(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("EEE")
        return format.format(date)
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