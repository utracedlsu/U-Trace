package com.capstone.app.utrace_cts.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.capstone.app.utrace_cts.R
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
 * Use the [NotificationsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NotificationsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var disposableObj: Disposable? = null //used to read SQLite Records
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        getNotifsFromDB()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notifications, container, false)
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment NotificationsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NotificationsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}