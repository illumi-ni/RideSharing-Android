package com.ujjallamichhane.ridesharing.entity

import java.io.Serializable

data class Rides(
    val _id: String? = null,
    val id: String? = null,
    val email: String? = null,
    val contact: String? = null,
    val from: String? = null,
    val to: String? = null,
    val date: String? = null,
    val time: String? = null,
    val distance: String? = null,
    val price: String? = null,
): Serializable

