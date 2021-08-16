package com.ujjallamichhane.ridesharing.api

import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException
import com.ujjallamichhane.ridesharing.entity.Customer
import com.ujjallamichhane.ridesharing.entity.Driver
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceBuilder {
//    const val BASE_URL =
//        "http://192.168.137.233:90"

        const val BASE_URL =
        "http://10.0.2.2:90/"

    var token: String? = null
    var customer: Customer? = null
    var driver: Driver? = null
    var email: String? = null
    var password: String? = null

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

    lateinit var mSocket: Socket
    @Synchronized
    fun setSocket() {
        try {
// "http://10.0.2.2:3000" is the network your Android emulator must use to join the localhost network on your computer
// "http://localhost:3000/" will not work
// If you want to use your physical phone you could use the your ip address plus :3000
// This will allow your Android Emulator and physical device at your home to connect to the server
            mSocket = IO.socket(BASE_URL)
        } catch (e: URISyntaxException) {

        }
    }

    @Synchronized
    fun getSocket(): Socket {
        return mSocket
    }

    @Synchronized
    fun establishConnection() {
        mSocket.connect()
    }

    @Synchronized
    fun closeConnection() {
        mSocket.disconnect()
    }
}