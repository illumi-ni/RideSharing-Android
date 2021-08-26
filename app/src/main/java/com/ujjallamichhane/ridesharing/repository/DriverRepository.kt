package com.ujjallamichhane.ridesharing.repository

import com.ujjallamichhane.ridesharing.api.DriverApi
import com.ujjallamichhane.ridesharing.api.RideSharingApiRequest
import com.ujjallamichhane.ridesharing.api.ServiceBuilder
import com.ujjallamichhane.ridesharing.entity.Driver
import com.ujjallamichhane.ridesharing.response.DriverLoginResponse
import com.ujjallamichhane.ridesharing.response.UpdateDriverResponse
import okhttp3.MultipartBody

class DriverRepository: RideSharingApiRequest() {
    val rideshareApi =
        ServiceBuilder.buildService(DriverApi::class.java)

    //driver login
    suspend fun checkDriver(email: String, password: String): DriverLoginResponse {
        return apiRequest {
            rideshareApi.checkDriver(email, password)
        }
    }

    suspend fun getDriverDetails(): UpdateDriverResponse {
        return apiRequest {
            rideshareApi.getDriverDetails(ServiceBuilder.token!!)
        }
    }

    suspend fun updateDriver(id: String, driver: Driver) : UpdateDriverResponse {
        return apiRequest {
            rideshareApi.updateDriver(ServiceBuilder.token!!,id, driver)
        }
    }

    suspend fun uploadImage(body: MultipartBody.Part)
            : UpdateDriverResponse {
        return apiRequest {
            rideshareApi.uploadImage(ServiceBuilder.token!!, body)
        }
    }
}