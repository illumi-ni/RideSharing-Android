package com.ujjallamichhane.ridesharing.repository

import com.ujjallamichhane.ridesharing.api.CustomerApi
import com.ujjallamichhane.ridesharing.api.DriverApi
import com.ujjallamichhane.ridesharing.api.RideSharingApiRequest
import com.ujjallamichhane.ridesharing.api.ServiceBuilder
import com.ujjallamichhane.ridesharing.entity.Customer
import com.ujjallamichhane.ridesharing.response.LoginResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

class DriverRepository: RideSharingApiRequest() {
    val rideshareApi =
        ServiceBuilder.buildService(DriverApi::class.java)

    //driver login
    suspend fun checkDriver(email: String, password: String): LoginResponse {
        return apiRequest {
            rideshareApi.checkDriver(email, password)
        }
    }
}