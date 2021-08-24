package com.ujjallamichhane.ridesharing.entity

data class Rides(
    val _id: String? = null,
    val pickupDate: String? = null,
    val pickupTime: String? = null,
    val pickupLocation: String? = null,
    val dropLocation: String? = null,
    val ridePrice: String? = null,
    val imgCustomer: String? = null
)
