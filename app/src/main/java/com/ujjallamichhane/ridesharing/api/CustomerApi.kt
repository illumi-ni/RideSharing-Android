package com.ujjallamichhane.ridesharing.api

import com.ujjallamichhane.ridesharing.entity.Customer
import com.ujjallamichhane.ridesharing.response.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface CustomerApi {
    //Register Customer
    @POST("customer/insert")
    suspend fun registerCustomer(
        @Body customer: Customer
    ): Response<LoginResponse>
}