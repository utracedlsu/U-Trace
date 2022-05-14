package com.capstone.app.utrace_cts

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

class BoosterAdapter (private val boosterList: ArrayList<Booster>) : RecyclerView.Adapter<BoosterAdapter.BoosterViewHolder>() {

    private var grey: Int = Color.parseColor("#FDFCF1")
    private lateinit var mListener: onItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoosterViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.boosters_list, parent, false)

        boosterList.sortByDescending { it.date }

        return BoosterViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: BoosterViewHolder, position: Int) {
        val currentItem = boosterList[position]
        holder.boosterShotDate.text = currentItem.date
        holder.boosterShotBrand.text = currentItem.brand
        if (position % 2 == 0)
            holder.boosterLayout.setBackgroundColor(grey)
    }

    override fun getItemCount(): Int {
        return boosterList.size
    }

    class BoosterViewHolder(itemView : View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val boosterShotDate  : TextView = itemView.findViewById(R.id.tv_boosterShotDate)
        val boosterShotBrand : TextView = itemView.findViewById(R.id.tv_boosterShotBrand)
        val boosterLayout : ConstraintLayout = itemView.findViewById(R.id.cl_boosterShot)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }

    }

    interface onItemClickListener {

        fun onItemClick(position: Int)

    }

    fun setOnItemClickListener(listener: onItemClickListener) {

        mListener = listener

    }
}