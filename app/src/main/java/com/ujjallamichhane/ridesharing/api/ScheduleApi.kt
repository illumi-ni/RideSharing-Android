package com.ujjallamichhane.ridesharing.api

import com.ujjallamichhane.ridesharing.entity.Rides
import com.ujjallamichhane.ridesharing.response.GetAllRidesResponse
import com.ujjallamichhane.ridesharing.response.GetAllScheduleRidesResponse
import com.ujjallamichhane.ridesharing.response.ScheduleRideResponse
import retrofit2.Response
import retrofit2.http.*

interface ScheduleApi {
    @POST("customer/booking")
    suspend fun scheduleRide(
        @Body rides: Rides
    ): Response<ScheduleRideResponse>

    @GET("booking/single")
    suspend fun getAllScheduleRides(
        @Header("Authorization") token: String,
    ): Response<GetAllScheduleRidesResponse>

    @PUT("update/booking")
    suspend fun updateBooking(
        @Header("Authorization") token: String,
        @Body rides: Rides
    ): Response<ScheduleRideResponse>

    @DELETE("delete/booking/{bid}")
    suspend fun deleteBooking(
        @Header("Authorization") token: String,
        @Path("bid") id: String
    ): Response<ScheduleRideResponse>
}