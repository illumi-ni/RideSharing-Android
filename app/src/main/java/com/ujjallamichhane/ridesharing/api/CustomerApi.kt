package com.ujjallamichhane.ridesharing.api

import com.ujjallamichhane.ridesharing.entity.Customer
import com.ujjallamichhane.ridesharing.response.LoginResponse
import com.ujjallamichhane.ridesharing.response.UpdateCustomerResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

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

    @FormUrlEncoded
    @POST("checkEmail")
    suspend fun checkEmail(
        @Field("email") email: String
    ): Response<LoginResponse>

    @GET("customer/single/{email}")
    suspend fun getCustomerDetails(
        @Header("Authorization") token: String,
        @Path("email") email: String
    ): Response<UpdateCustomerResponse>

    @Multipart
    @PUT("user/updateImage")
    suspend fun uploadImage(
        @Header("Authorization") token: String,
        @Part photo: MultipartBody.Part
    ):Response<UpdateCustomerResponse>
}