package com.ujjallamichhane.ridesharing.response

import com.ujjallamichhane.ridesharing.entity.Customer

data class UpdateCustomerResponse(
    val success : Boolean? = null,
    val CustomerData : Customer? = null
)