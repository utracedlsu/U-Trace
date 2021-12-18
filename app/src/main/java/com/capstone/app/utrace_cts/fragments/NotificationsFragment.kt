package com.capstone.app.utrace_cts.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.app.utrace_cts.Notification
import com.capstone.app.utrace_cts.NotificationsAdapter
import com.capstone.app.utrace_cts.R
import com.capstone.app.utrace_cts.SwipeToDelete
import com.capstone.app.utrace_cts.notifications.persistence.NotificationRecord
import com.capstone.app.utrace_cts.notifications.persistence.NotificationRecordStorage
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executors
import java.util.function.BiFunction
import java.util.function.Function

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class NotificationsFragment : Fragment(R.layout.fragment_notifications) {

    private var disposableObj: Disposable? = null //used to read SQLite Records

    private lateinit var rv_notifications: RecyclerView
    private lateinit var adapter : NotificationsAdapter
    private lateinit var tv_emptyMsg: TextView
    private lateinit var notificationsList : ArrayList<Notification>

    private lateinit var nTypes : Array<String>
    private lateinit var nDates : Array<String>
    private lateinit var nTimes : Array<String>
    private lateinit var nHeaders : Array<String>
    private lateinit var nContents : Array<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getNotifsFromDB()

        // connect & init var
        rv_notifications = view.findViewById(R.id.rv_notifications)
        tv_emptyMsg = view.findViewById(R.id.tv_emptyMsg)
        notificationsList = arrayListOf<Notification>()

        getTestingData() // FOR TESTING
    }

    private fun getNotifsFromDB(){
        var observableNotifs = Observable.create<List<NotificationRecord>> {
            val result = NotificationRecordStorage(requireActivity().applicationContext).getAllNotifs()
            it.onNext(result)
        }

        disposableObj = observableNotifs.observeOn(AndroidSchedulers.mainThread()).subscribeOn(
            Schedulers.io()).subscribe{ retrievedNotifs ->
            Log.d("NotifFragment", "Notifs ${retrievedNotifs}")
            Log.d("NotifFragment", "Notif 1: ${retrievedNotifs.get(0).title} - ${retrievedNotifs.get(0).body} - ${retrievedNotifs.get(0).timestamp}")
            //TODO: Create RecyclerView Here with retrievedNotifs
            //retrievedNotifs: array of notificationRecords (check notifications > persistence > NotificationRecord.kt)

        }
    }

    // POPULATE TESTER ARRAY
    private fun getTestingData() {
        nTypes = arrayOf(
            "BLUETOOTH",
            "WARNING",
            "some other thing (see NotificationsAdapter for more info)",
        )
        nDates = arrayOf(
            "12-16-2021",
            "12-17-2021",
            "12-18-2021",
        )
        nTimes = arrayOf(
            "1:00PM",
            "2:30PM",
            "7:00AM",
        )
        nHeaders = arrayOf(
            "You've had 10 Bluetooth exchanges in the past 24 hours.",
            "One of your close contacts listed themself as positive.",
            "super idol doengmi yoshangmin idk",
        )
        nContents = arrayOf(
            "You should get tested my guy",
            "You should check yourself and see if you're safe or not or something",
            "stop babe youre not super idol",
        )

        for (i in nTypes.indices) {
            val notif = Notification(nTypes[i], nDates[i], nTimes[i], nHeaders[i], nContents[i])
            notificationsList.add(notif)
        }

        // IMPORTANT: Only after the data is collected should the recycler view be initialized
        toggleEmptyMessage()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        rv_notifications.layoutManager = LinearLayoutManager(this.context)
        rv_notifications.setHasFixedSize(true)
        adapter = NotificationsAdapter(notificationsList)
        rv_notifications.adapter = adapter
        var itemTouchHelper = ItemTouchHelper(SwipeToDelete(adapter))
        itemTouchHelper.attachToRecyclerView(rv_notifications)
    }

    private fun toggleEmptyMessage() {
        if (notificationsList.isEmpty()) {
            tv_emptyMsg.visibility = View.VISIBLE
        } else {
            tv_emptyMsg.visibility = View.GONE
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