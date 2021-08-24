package com.ujjallamichhane.ridesharing.response

import com.ujjallamichhane.ridesharing.entity.Customer
import com.ujjallamichhane.ridesharing.entity.Driver

data class UpdateDriverResponse(
    val success : Boolean? = null,
    val driverData : Driver? = null
)