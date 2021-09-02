package com.ujjallamichhane.ridesharing.api

import com.ujjallamichhane.ridesharing.entity.RideRequest
import com.ujjallamichhane.ridesharing.entity.Rides
import com.ujjallamichhane.ridesharing.response.ScheduleRideResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RideApi {
    @POST("/insertRide")
    suspend fun insertRide(
        @Body ride: RideRequest
    ): Response<ScheduleRideResponse>
}