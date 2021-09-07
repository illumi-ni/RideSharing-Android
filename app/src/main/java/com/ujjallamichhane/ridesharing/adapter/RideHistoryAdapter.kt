package com.ujjallamichhane.ridesharing.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ujjallamichhane.ridesharing.R
import com.ujjallamichhane.ridesharing.entity.RideRequest
import com.ujjallamichhane.ridesharing.entity.Rides

class RideHistoryAdapter(
    val lstRideHistory: ArrayList<RideRequest>,
    val context: Context
): RecyclerView.Adapter<RideHistoryAdapter.HistoryViewHolder>(){

    class HistoryViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val tvRideDate: TextView
//        val tvRideTime: TextView
        val tvHistoryPrice: TextView
        val tvPickupLoc: TextView
        val tvDestinationLoc: TextView
        val tvRequestAgain: TextView

        init {
            tvRideDate = view.findViewById(R.id.tvRideDate)
//            tvRideTime = view.findViewById(R.id.tvRideTime)
            tvHistoryPrice = view.findViewById(R.id.tvHistoryPrice)
            tvPickupLoc = view.findViewById(R.id.tvPickupLoc)
            tvDestinationLoc = view.findViewById(R.id.tvDestinationLoc)
            tvRequestAgain = view.findViewById(R.id.tvRequestAgain)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.customer_history_layout, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val rides = lstRideHistory[position]
        holder.tvRideDate.text = rides.date
        holder.tvHistoryPrice.text = rides.price
        holder.tvPickupLoc.text = rides.from
        holder.tvDestinationLoc.text = rides.to
        holder.tvRequestAgain.setOnClickListener{

        }
    }

    override fun getItemCount(): Int {
        return lstRideHistory.size
    }

}