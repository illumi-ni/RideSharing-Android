package com.ujjallamichhane.ridesharing.response

import com.ujjallamichhane.ridesharing.entity.Driver

data class ScheduleRideResponse(
    val success: Boolean? = null,
    val BookingData : Driver? = null
)
