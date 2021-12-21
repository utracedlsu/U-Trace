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
import com.capstone.app.utrace_cts.*
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

        // connect & init var
        rv_notifications = view.findViewById(R.id.rv_notifications)
        tv_emptyMsg = view.findViewById(R.id.tv_emptyMsg)
        notificationsList = arrayListOf<Notification>()

        //retrieve notifications from database, then initialize
        getNotifsFromDB()
    }

    private fun getNotifsFromDB(){
        var observableNotifs = Observable.create<List<NotificationRecord>> {
            val result = NotificationRecordStorage(requireActivity().applicationContext).getAllNotifs()
            it.onNext(result)
        }

        disposableObj = observableNotifs.observeOn(AndroidSchedulers.mainThread()).subscribeOn(
            Schedulers.io()).subscribe{ retrievedNotifs ->
            if(retrievedNotifs.size > 0){
                for (notif in retrievedNotifs){
                    notificationsList.add(Notification(
                        //TODO: create logic for determining notification type
                        "WARNING",
                        Utils.getDDMMYYY(notif.timestamp),
                        Utils.getTimeInHours(notif.timestamp),
                        notif.title,
                        notif.body
                    ))
                }
                initRecyclerView()
            } else {
                checkIfEmpty()
            }
        }
    }

    private fun initRecyclerView() {

        rv_notifications.layoutManager = LinearLayoutManager(this.context)
        rv_notifications.setHasFixedSize(true)
        adapter = NotificationsAdapter(notificationsList)
        rv_notifications.adapter = adapter
        var itemTouchHelper = ItemTouchHelper(SwipeToDelete(adapter))
        itemTouchHelper.attachToRecyclerView(rv_notifications)

        // What: Instantiate interface from NotificationsAdapter
        // Purpose: Show text: "You've no notifications" when user has no notifications
        class Listener : ChangeListener {
            override fun onChange(data: Int) {
                if (data > 0) tv_emptyMsg.visibility = View.GONE
                else tv_emptyMsg.visibility = View.VISIBLE
            }
        }

        adapter.setOnChangeListener(Listener())
    }

    private fun checkIfEmpty() {
        if (notificationsList.isEmpty()) tv_emptyMsg.visibility = View.VISIBLE
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