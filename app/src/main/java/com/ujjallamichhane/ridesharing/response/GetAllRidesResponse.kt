package com.ujjallamichhane.ridesharing.response

import com.ujjallamichhane.ridesharing.entity.RideRequest

data class GetAllRidesResponse(
    val success: Boolean? = null,
    val data: ArrayList<RideRequest>? = null,
)