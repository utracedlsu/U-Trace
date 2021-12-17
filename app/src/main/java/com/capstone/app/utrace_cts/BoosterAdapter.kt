package com.capstone.app.utrace_cts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BoosterAdapter (private val boosterList: ArrayList<Booster>) : RecyclerView.Adapter<BoosterAdapter.BoosterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoosterViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.boosters_list, parent, false)
        return BoosterViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BoosterViewHolder, position: Int) {
        val currentItem = boosterList[position]
        holder.boosterShotDate.text = currentItem.date
        holder.boosterShotBrand.text = currentItem.brand
    }

    override fun getItemCount(): Int {
        return boosterList.size
    }

    class BoosterViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val boosterShotDate  : TextView = itemView.findViewById(R.id.tv_boosterShotDate)
        val boosterShotBrand : TextView = itemView.findViewById(R.id.tv_boosterShotBrand)
    }
}