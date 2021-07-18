package com.ujjallamichhane.ridesharing.api

import com.ujjallamichhane.ridesharing.entity.Customer
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceBuilder {
//    const val BASE_URL =
//        "http://192.168.137.169:90"

        const val BASE_URL =
        "http://10.0.2.2:90/"

    var token: String? = null
    var customer: Customer? = null
    var email: String? = null

    private val okHttp = OkHttpClient.Builder()
    private val retrofitBuilder = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttp.build())
    //Creating retrofit instance
    private val retrofit = retrofitBuilder.build()
    //Generic function
    fun <T> buildService(serviceType: Class<T>): T{
        return  retrofit.create(serviceType)
    }
}