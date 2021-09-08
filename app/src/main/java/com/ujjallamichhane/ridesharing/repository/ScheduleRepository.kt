package com.ujjallamichhane.ridesharing.repository

import com.ujjallamichhane.ridesharing.api.DriverApi
import com.ujjallamichhane.ridesharing.api.RideSharingApiRequest
import com.ujjallamichhane.ridesharing.api.ScheduleApi
import com.ujjallamichhane.ridesharing.api.ServiceBuilder
import com.ujjallamichhane.ridesharing.entity.Rides
import com.ujjallamichhane.ridesharing.response.*

class ScheduleRepository: RideSharingApiRequest() {
    val rideshareApi =
        ServiceBuilder.buildService(ScheduleApi::class.java)

    suspend fun scheduleRide(rides: Rides): ScheduleRideResponse{
        return apiRequest {
            rideshareApi.scheduleRide(rides)
        }
    }

    suspend fun getAllScheduleRides(token: String): GetAllScheduleRidesResponse {
        return apiRequest {
            rideshareApi.getAllScheduleRides(token)
        }
    }

    suspend fun updateBooking(rides: Rides): ScheduleRideResponse {
        return apiRequest {
            rideshareApi.updateBooking(ServiceBuilder.token!!, rides)
        }
    }

    suspend fun deleteBooking(id : String) : ScheduleRideResponse{
        return apiRequest {
            rideshareApi.deleteBooking(ServiceBuilder.token!!, id)
        }
    }

}