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
    private var chartVals = mapOf("Sun" to 0f, "Mon" to 0f, "Tue" to 0f,
        "Wed" to 0f, "Thu" to 0f, "Fri" to 0f, "Sat" to 0f)

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
                    //use filter method to get size of contacts depending on day of the week
                    cthChart.addBar(BarModel("S", filteredRecords.filter { getDayOfTheWeek(it.timestamp).equals("Sun") }.size.toFloat(),green))
                    cthChart.addBar(BarModel("M", filteredRecords.filter { getDayOfTheWeek(it.timestamp).equals("Mon") }.size.toFloat(),green))
                    cthChart.addBar(BarModel("T", filteredRecords.filter { getDayOfTheWeek(it.timestamp).equals("Tue") }.size.toFloat(),green))
                    cthChart.addBar(BarModel("W", filteredRecords.filter { getDayOfTheWeek(it.timestamp).equals("Wed") }.size.toFloat(),green))
                    cthChart.addBar(BarModel("H", filteredRecords.filter { getDayOfTheWeek(it.timestamp).equals("Thu") }.size.toFloat(),green))
                    cthChart.addBar(BarModel("F", filteredRecords.filter { getDayOfTheWeek(it.timestamp).equals("Fri") }.size.toFloat(),green))
                    cthChart.addBar(BarModel("S", filteredRecords.filter { getDayOfTheWeek(it.timestamp).equals("Sat") }.size.toFloat(),green))
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

    //gets day of the week in String form (MON, TUE, WED, etc)
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