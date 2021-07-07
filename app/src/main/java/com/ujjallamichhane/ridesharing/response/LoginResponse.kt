package com.ujjallamichhane.ridesharing.response

import com.ujjallamichhane.ridesharing.entity.Customer

data class LoginResponse(
    val success: Boolean? = null,
    val token: String? = null,
    val data : Customer? = null
)
