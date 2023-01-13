package com.capstone.app.utrace_cts.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.app.utrace_cts.Booster
import com.capstone.app.utrace_cts.Preference
import com.capstone.app.utrace_cts.R
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private lateinit var cv_hot1: MaterialCardView
    private lateinit var cv_hot2: MaterialCardView
    private lateinit var cv_hot3: MaterialCardView
    private lateinit var cv_hot4: MaterialCardView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cv_hot1 = view.findViewById(R.id.cv_hot1)
        cv_hot2 = view.findViewById(R.id.cv_hot2)
        cv_hot3 = view.findViewById(R.id.cv_hot3)
        cv_hot4 = view.findViewById(R.id.cv_hot4)

        cv_hot1.setOnClickListener {

            val cb1: ClipboardManager = activity?.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val c1 : ClipData = ClipData.newPlainText("cv_hot1", "02-894-26843")
            cb1.setPrimaryClip(c1)

            val t1 = Toast.makeText(this.context,"Copied to Clipboard: 02-894-26843",Toast.LENGTH_SHORT)
            t1.show()
        }

        cv_hot2.setOnClickListener {

            val cb2: ClipboardManager = activity?.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val c2 : ClipData = ClipData.newPlainText("cv_hot2", "8-807-2631")
            cb2.setPrimaryClip(c2)

            val t2 = Toast.makeText(this.context,"Copied to Clipboard: 8-807-2631",Toast.LENGTH_SHORT)
            t2.show()
        }

        cv_hot3.setOnClickListener {

            val cb3: ClipboardManager = activity?.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val c3 : ClipData = ClipData.newPlainText("cv_hot3", "8876 3444")
            cb3.setPrimaryClip(c3)

            val t3 = Toast.makeText(this.context,"Copied to Clipboard: 8876 3444",Toast.LENGTH_SHORT)
            t3.show()
        }

        cv_hot4.setOnClickListener {

            val cb4: ClipboardManager = activity?.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val c4 : ClipData = ClipData.newPlainText("cv_hot4", "8925 03430")
            cb4.setPrimaryClip(c4)

            val t4 = Toast.makeText(this.context,"Copied to Clipboard: 8925 03430",Toast.LENGTH_SHORT)
            t4.show()
        }

    }
}