package com.ujjallamichhane.ridesharing.response

import com.ujjallamichhane.ridesharing.entity.Customer
import com.ujjallamichhane.ridesharing.entity.Driver

data class DriverLoginResponse(
    val success: Boolean? = null,
    val token: String? = null,
    val driverData : Driver? = null
)
