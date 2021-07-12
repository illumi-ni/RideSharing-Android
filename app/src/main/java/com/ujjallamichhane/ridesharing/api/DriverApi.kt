package com.ujjallamichhane.ridesharing.api

import com.ujjallamichhane.ridesharing.entity.Customer
import com.ujjallamichhane.ridesharing.entity.Driver
import com.ujjallamichhane.ridesharing.response.DriverLoginResponse
import com.ujjallamichhane.ridesharing.response.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface DriverApi {

    //Driver login
    @FormUrlEncoded
    @POST("driver/login")
    suspend fun checkDriver(
        @Field("email") email: String,
        @Field("password") password: String
    ) : Response<DriverLoginResponse>
}