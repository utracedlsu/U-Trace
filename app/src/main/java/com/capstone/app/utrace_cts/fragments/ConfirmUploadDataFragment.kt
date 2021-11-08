package com.capstone.app.utrace_cts.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import com.capstone.app.utrace_cts.*
import com.capstone.app.utrace_cts.status.persistence.StatusRecord
import com.capstone.app.utrace_cts.status.persistence.StatusRecordStorage
import com.capstone.app.utrace_cts.streetpass.persistence.StreetPassRecord
import com.capstone.app.utrace_cts.streetpass.persistence.StreetPassRecordStorage
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class ConfirmUploadDataFragment: DialogFragment() {

    private var disposableObj: Disposable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // initialize, set bg to transparent
        var content: View = inflater.inflate(R.layout.fragment_confirm_upload_data, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // connect
        var tv_dataPrivConsent_content: TextView = content.findViewById(R.id.tv_dataPrivConsent_content)
        var btn_agreeConsent: Button = content.findViewById(R.id.btn_agreeConsent)
        var btn_cancelUploadData: Button = content.findViewById(R.id.btn_cancelUploadData)

        tv_dataPrivConsent_content.movementMethod = ScrollingMovementMethod() // allow popup to be scrollable

        // get data -- either from EnablingPermissionsActivity or UploadDataActivity
        val bundle = arguments
        val source: Boolean = bundle!!.getBoolean("source")

        if (source) { // if previous activity was EnablingPermissionsActivity...

            // set appropriate button text for the popup
            btn_agreeConsent.text = "I ACCEPT"

            // btn logic: go to MainActivity
            btn_agreeConsent.setOnClickListener{
                val intent = Intent(context, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("EXIT", true)
                startActivity(intent)
                activity?.finish()
            }
        }
        else if (!source) { // else, previous activity was UploadDataActivity

            // set appropriate button text for the popup
            btn_agreeConsent.text = "AGREE & CONFIRM UPLOAD"

            // btn logic: ???
            btn_agreeConsent.setOnClickListener{
                //TODO: Dito i-lalagay yung logic to upload their data to the servers i guess?

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
                        .subscribe { exportedData ->
                            Log.d("UploadFragment", "Records: ${exportedData.recordList}")
                            Log.d("UploadFragment", "${exportedData.statusList}")

                            try {
                                var task = writeToInternalStorageAndUpload(
                                    requireActivity().applicationContext,
                                    exportedData.recordList,
                                    exportedData.statusList
                                )

                                task.addOnSuccessListener {
                                    Log.d("UploadFragment", "Successfully Uploaded Records!")
                                    Toast.makeText(requireActivity().applicationContext, "Successfully Uploaded Records!", Toast.LENGTH_SHORT).show()
                                }
                                task.addOnFailureListener{
                                    Log.d("UploadFragment", "Failed to upload records: ${it.message}")
                                    Toast.makeText(requireActivity().applicationContext, "Failed to upload records: ${it.message}", Toast.LENGTH_SHORT).show()
                                }
                            } catch(e: Exception){
                                Log.d("UploadFragment", "Failed to upload records: ${e.message}")
                                Toast.makeText(requireActivity().applicationContext, "Failed to upload records: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
            }
        }
        else {
            Log.i("ConfirmUploadDataFragment.kt", "Unknown source.")
        }

        btn_cancelUploadData.setOnClickListener { dialog?.dismiss() } // close popup window on cancel

        return content
    }

    private fun writeToInternalStorageAndUpload(context: Context, spRecordsList:
    List<StreetPassRecord>, statusList: List<StatusRecord>): UploadTask{
        var date = Utils.getDateFromUnix(System.currentTimeMillis())
        var gson = Gson()

        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL

        var updatedDeviceList = spRecordsList.map {
            it.timestamp = it.timestamp / 1000
            return@map it
        }

        var updatedStatusList = statusList.map {
            it.timestamp = it.timestamp / 1000
            return@map it
        }

        var map: MutableMap<String, Any> = HashMap()
        map["records"] = updatedDeviceList as Any
        map["events"] = updatedStatusList as Any

        val mapString = gson.toJson(map)

        val fileName = "StreetPassRecord_${manufacturer}_${model}_$date.json"
        val fileOutputStream: FileOutputStream

        val uploadDir = File(context.filesDir, "upload")

        if (uploadDir.exists()) {
            uploadDir.deleteRecursively()
        }

        uploadDir.mkdirs()
        val fileToUpload = File(uploadDir, fileName)

        fileOutputStream = FileOutputStream(fileToUpload)

        fileOutputStream.write(mapString.toByteArray())
        fileOutputStream.close()

        Log.d("UploadFragment", "File wrote: ${fileToUpload.absolutePath}")

        return uploadToFirebase(context, fileToUpload)
    }

    private fun uploadToFirebase(context: Context, fileToUpload: File): UploadTask{
        val storage = FirebaseStorage.getInstance("gs://u-trace-upload")
        val storageRef = storage.getReferenceFromUrl("gs://u-trace-upload")

        //add date folder and name of file
        val dateString = SimpleDateFormat("yyyyMMdd").format(Date())
        var streetPassRecordsRef =
            storageRef.child("streetPassRecords/$dateString/${fileToUpload.name}")
        val fileUri: Uri =
            FileProvider.getUriForFile(
                context,
                "${BuildConfig.APPLICATION_ID}.fileprovider",
                fileToUpload
            )

        var uploadTask = streetPassRecordsRef.putFile(fileUri)

        uploadTask.addOnCompleteListener{
            try{
               fileToUpload.delete()
               Log.d("UploadFragment", "File deleted")
            } catch (e: Exception){
               Log.e("UploadFragment", "Failed to delete file: ${e.message}")
            }
        }

        return uploadTask
    }
}