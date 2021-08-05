package com.ujjallamichhane.ridesharing.entity

data class RideRequest(
    val _id: String? = null,
    val fullname: String? = null,
    val phone: String? = null,
    val from: String? = null,
    val to: String? = null,
    val date: String? = null,
    val time: String? = null,
    val distance: String? = null,
    val price: String? = null
)