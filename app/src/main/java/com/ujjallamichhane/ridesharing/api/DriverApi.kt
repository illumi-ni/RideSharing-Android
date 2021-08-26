package com.ujjallamichhane.ridesharing.api

import com.ujjallamichhane.ridesharing.entity.Driver
import com.ujjallamichhane.ridesharing.response.DriverLoginResponse
import com.ujjallamichhane.ridesharing.response.UpdateDriverResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface DriverApi {

    //Driver login
    @FormUrlEncoded
    @POST("driver/login")
    suspend fun checkDriver(
        @Field("email") email: String,
        @Field("password") password: String
    ) : Response<DriverLoginResponse>

    @GET("driver/single")
    suspend fun getDriverDetails(
        @Header("Authorization") token: String
    ): Response<UpdateDriverResponse>

    @PUT("driver/update/{id}")
    suspend fun updateDriver(
        @Header("Authorization") token: String,
        @Path ("id")id: String,
        @Body driver:Driver
    ):Response<UpdateDriverResponse>

    @Multipart
    @PUT("driver/updateImage")
    suspend fun uploadImage(
        @Header("Authorization") token: String,
        @Part photo: MultipartBody.Part
    ):Response<UpdateDriverResponse>

}