package com.ujjallamichhane.ridesharing.repository

import com.ujjallamichhane.ridesharing.api.CustomerApi
import com.ujjallamichhane.ridesharing.api.RideSharingApiRequest
import com.ujjallamichhane.ridesharing.api.ServiceBuilder
import com.ujjallamichhane.ridesharing.entity.Customer
import com.ujjallamichhane.ridesharing.response.LoginResponse

class CustomerRepository
    :RideSharingApiRequest(){
        val rideshareApi =
            ServiceBuilder.buildService(CustomerApi::class.java)
    suspend fun registerCustomer(customer: Customer):LoginResponse{
        return apiRequest {
            rideshareApi.registerCustomer(customer)
        }
    }
    suspend fun sendOTP(email: String):LoginResponse{
        return apiRequest {
            rideshareApi.sendOTP(email)
        }
    }
    suspend fun verifyOtp(otp: String):LoginResponse{
        return apiRequest {
            rideshareApi.verifyOtp(otp)
        }
    }
}