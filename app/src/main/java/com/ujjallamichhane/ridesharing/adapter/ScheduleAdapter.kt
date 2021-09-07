package com.ujjallamichhane.ridesharing.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ujjallamichhane.ridesharing.R
import com.ujjallamichhane.ridesharing.entity.Rides

class ScheduleAdapter (
    val lstScheduleRides: ArrayList<Rides>,
    val context: Context
): RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    class ScheduleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTime: TextView
        val tvDate: TextView
        val tvPickUp: TextView
        val tvDestination: TextView
        val tvPrice: TextView
        val deleteIcon: ImageView
        val editIcon: ImageView

        init {
            tvTime = view.findViewById(R.id.tvTime)
            tvDate = view.findViewById(R.id.tvDate)
            tvPickUp = view.findViewById(R.id.tvPickup)
            tvDestination = view.findViewById(R.id.tvDestination)
            tvPrice = view.findViewById(R.id.tvPrice)
            deleteIcon = view.findViewById(R.id.deleteIcon)
            editIcon = view.findViewById(R.id.editIcon)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.schedule_ride_layout, parent, false)
        return ScheduleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val rides = lstScheduleRides[position]
        holder.tvDate.text = rides.date
        holder.tvPrice.text = rides.price
        holder.tvPickUp.text = rides.from
        holder.tvDestination.text = rides.to
//        holder.tvRiderName.text = rides.fullname
        holder.deleteIcon.setOnClickListener {

        }
        holder.editIcon.setOnClickListener {

        }
    }

    override fun getItemCount(): Int {
        return lstScheduleRides.size
    }

}