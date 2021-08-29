package com.ujjallamichhane.ridesharing.repository

import com.ujjallamichhane.ridesharing.api.DriverApi
import com.ujjallamichhane.ridesharing.api.RideSharingApiRequest
import com.ujjallamichhane.ridesharing.api.ScheduleApi
import com.ujjallamichhane.ridesharing.api.ServiceBuilder
import com.ujjallamichhane.ridesharing.entity.Rides
import com.ujjallamichhane.ridesharing.response.DriverLoginResponse
import com.ujjallamichhane.ridesharing.response.ScheduleRideResponse
import com.ujjallamichhane.ridesharing.response.UpdateDriverResponse

class ScheduleRepository: RideSharingApiRequest() {
    val rideshareApi =
        ServiceBuilder.buildService(ScheduleApi::class.java)

    suspend fun scheduleRide(rides: Rides): ScheduleRideResponse{
        return apiRequest {
            rideshareApi.scheduleRide(rides)
        }
    }
//    suspend fun getScheduledRide(): ScheduleRideResponse {
//        return apiRequest {
//            rideshareApi.getScheduledRide(
//                ServiceBuilder.token!!,
//                ServiceBuilder.id!!
//            )
//        }
//    }

}