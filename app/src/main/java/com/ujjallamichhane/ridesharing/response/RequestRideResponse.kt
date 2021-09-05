package com.ujjallamichhane.ridesharing.response

import com.ujjallamichhane.ridesharing.entity.RideRequest

data class RequestRideResponse (
    val success: Boolean? = null,
    val BookingData : RideRequest? = null
)