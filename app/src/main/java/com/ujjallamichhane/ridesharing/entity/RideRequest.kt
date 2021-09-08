package com.ujjallamichhane.ridesharing.entity

import java.io.Serializable

data class RideRequest(
    val _id: String? = null,
    val from: String? = null,
    val to: String? = null,
    val date: String? = null,
    val distance: String? = null,
    val price: String? = null,
    val customer: Customer? = null,
    val driver: Driver? = null,
    val customerID: String? = null,
    val driverID: String? = null,
//    val fullname: String? = null,
//    val contact: String? = null,
//    val photo: String? = null,
): Serializable