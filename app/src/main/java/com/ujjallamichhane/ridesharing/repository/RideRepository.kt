package com.ujjallamichhane.ridesharing.repository

import android.content.Context
import com.ujjallamichhane.ridesharing.api.RideApi
import com.ujjallamichhane.ridesharing.api.RideSharingApiRequest
import com.ujjallamichhane.ridesharing.api.ScheduleApi
import com.ujjallamichhane.ridesharing.api.ServiceBuilder
import com.ujjallamichhane.ridesharing.entity.RideRequest
import com.ujjallamichhane.ridesharing.entity.Rides
import com.ujjallamichhane.ridesharing.response.GetAllRidesResponse
import com.ujjallamichhane.ridesharing.response.RequestRideResponse
import com.ujjallamichhane.ridesharing.response.ScheduleRideResponse
import retrofit2.Response

class RideRepository: RideSharingApiRequest() {
    val rideshareApi =
        ServiceBuilder.buildService(RideApi::class.java)

    suspend fun insertRide(ride: RideRequest): RequestRideResponse {
        return apiRequest {
            rideshareApi.insertRide(ride)
        }
    }

    suspend fun getAllBookings(token: String): GetAllRidesResponse {
        return apiRequest {
            rideshareApi.getAllBookings(token)
        }

    }
}