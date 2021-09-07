package com.ujjallamichhane.ridesharing.api

import com.ujjallamichhane.ridesharing.entity.RideRequest
import com.ujjallamichhane.ridesharing.entity.Rides
import com.ujjallamichhane.ridesharing.response.GetAllRidesResponse
import com.ujjallamichhane.ridesharing.response.RequestRideResponse
import com.ujjallamichhane.ridesharing.response.ScheduleRideResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface RideApi {
    @POST("/insert/Ride")
    suspend fun insertRide(
        @Body ride: RideRequest
    ): Response<RequestRideResponse>

    @GET("get/myBookings")
    suspend fun getAllBookings(
        @Header("Authorization") token: String
    ): Response<GetAllRidesResponse>
}