package com.ujjallamichhane.ridesharing.api

import com.ujjallamichhane.ridesharing.entity.Customer
import com.ujjallamichhane.ridesharing.response.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface CustomerApi {
    //Register Customer
    @POST("customer/insert")
    suspend fun registerCustomer(
        @Body customer: Customer
    ): Response<LoginResponse>

    @FormUrlEncoded
    @POST("sendotp")
    suspend fun sendOTP(
            @Field("email") email: String
    ): Response<LoginResponse>
    
    @FormUrlEncoded
    @POST("verifyotp")
    suspend fun verifyOtp(
            @Field("otp") otp: String
    ): Response<LoginResponse>

}