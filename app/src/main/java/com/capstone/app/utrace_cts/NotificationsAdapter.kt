package com.capstone.app.utrace_cts

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView

class NotificationsAdapter (private val notifList: ArrayList<Notification>) : RecyclerView.Adapter<NotificationsAdapter.NotifViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotifViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.notif_list, parent, false)
        return NotifViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NotifViewHolder, position: Int) {
        val currentItem = notifList[position]

        holder.notifDate.text = currentItem.date
        holder.notifTime.text = currentItem.time
        holder.notifHeader.text = currentItem.header
        holder.notifContent.text = currentItem.content
        when (currentItem.type) {
            "BLUETOOTH" -> holder.notifSymbol.setImageResource(R.drawable.bluetoothnotif)
            "WARNING" -> holder.notifSymbol.setImageResource(R.drawable.warningnotif)
            else -> holder.notifSymbol.setImageResource(R.drawable.generalnotif)
        }
    }

    override fun getItemCount(): Int {
        return notifList.size
    }

    class NotifViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val notifDate : TextView = itemView.findViewById(R.id.tv_date)
        val notifTime : TextView = itemView.findViewById(R.id.tv_time)
        val notifSymbol : ImageView = itemView.findViewById(R.id.iv_notifSymbol)
        val notifHeader : TextView = itemView.findViewById(R.id.tv_notifHeader)
        val notifContent : TextView = itemView.findViewById(R.id.tv_notifContent)
    }

    fun deleteItem(index: Int) {
        notifList.removeAt(index)
        notifyItemRemoved(index)
    }
}