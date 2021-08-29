package com.ujjallamichhane.ridesharing.api

import com.ujjallamichhane.ridesharing.entity.Rides
import com.ujjallamichhane.ridesharing.response.ScheduleRideResponse
import retrofit2.Response
import retrofit2.http.*

interface ScheduleApi {
    @POST("customer/booking")
    suspend fun scheduleRide(
        @Body rides: Rides
    ): Response<ScheduleRideResponse>

//    @GET("booking/single/{id}")
//    suspend fun getApptByName(
//        @Header("Authorization") token: String,
//        @Path("username") username: String
//    ):Response<ScheduleRideResponse>
}