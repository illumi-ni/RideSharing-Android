package com.ujjallamichhane.ridesharing.response

import com.ujjallamichhane.ridesharing.entity.Driver
import com.ujjallamichhane.ridesharing.entity.Rides

data class ScheduleRideResponse(
    val success: Boolean? = null,
    val BookingData : Rides? = null
)
