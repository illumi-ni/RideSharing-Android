package com.ujjallamichhane.ridesharing.response

import com.ujjallamichhane.ridesharing.entity.Rides

data class GetAllScheduleRidesResponse (
    val success: Boolean? = null,
    val data: ArrayList<Rides>? = null,
)