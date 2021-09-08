package com.ujjallamichhane.ridesharing.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.ujjallamichhane.ridesharing.R
import com.ujjallamichhane.ridesharing.SignInActivity
import com.ujjallamichhane.ridesharing.entity.Rides
import com.ujjallamichhane.ridesharing.fragments.UpdateRideFragment
import com.ujjallamichhane.ridesharing.repository.ScheduleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScheduleAdapter(
    val lstScheduleRides: ArrayList<Rides>,
    val context: Context
) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

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
        holder.deleteIcon.setOnClickListener {
            deleteRides(rides)
        }
        holder.editIcon.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val fragment = UpdateRideFragment()
                val appCompatActivity = v!!.context as AppCompatActivity
                val args = Bundle()
                args.putSerializable("rides", rides)
                fragment.arguments = args

                appCompatActivity.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment, fragment)
                    .commit()

            }
        })
    }

    override fun getItemCount(): Int {
        return lstScheduleRides.size
    }

    private fun deleteRides(rides: Rides) {

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Are you sure you want to cancel rides?")
        builder.setIcon(R.drawable.ic_info)
        builder.setPositiveButton("Yes") { _, _ ->
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val scheduleRepository = ScheduleRepository()
                    val response = scheduleRepository.deleteBooking(rides._id!!)
                    if (response.success == true) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "Ride canceled",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        withContext(Dispatchers.Main) {
                            lstScheduleRides.remove(rides)
                            notifyDataSetChanged()

                        }
                    }
                } catch (ex: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            ex.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

        builder.setNegativeButton("No") { _, _ ->

        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

}