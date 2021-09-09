package com.ujjallamichhane.ridesharing.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.ujjallamichhane.ridesharing.R
import com.ujjallamichhane.ridesharing.entity.RideRequest
import com.ujjallamichhane.ridesharing.ui.customer.home.MapsFragment

class DriverRideHistoryAdapter (
    val lstRideHistory: ArrayList<RideRequest>,
    val context: Context
): RecyclerView.Adapter<DriverRideHistoryAdapter.HistoryViewHolder>(){

    class HistoryViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val tvRideDate: TextView
        val tvHistoryPrice: TextView
        val tvPickupLoc: TextView
        val tvDestinationLoc: TextView

        init {
            tvRideDate = view.findViewById(R.id.tvRideDate)
            tvHistoryPrice = view.findViewById(R.id.tvHistoryPrice)
            tvPickupLoc = view.findViewById(R.id.tvPickupLoc)
            tvDestinationLoc = view.findViewById(R.id.tvDestinationLoc)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.driver_ride_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val rideRequest = lstRideHistory[position]
        holder.tvRideDate.text = rideRequest.date
        holder.tvHistoryPrice.text = rideRequest.price
        holder.tvPickupLoc.text = rideRequest.from
        holder.tvDestinationLoc.text = rideRequest.to

    }


    override fun getItemCount(): Int {
        return lstRideHistory.size
    }


}