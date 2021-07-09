package com.ujjallamichhane.ridesharing

import com.ujjallamichhane.ridesharing.repository.CustomerRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class UnitTestingVerifyOTP {
    private lateinit var customerRepository: CustomerRepository

    @Test
    fun checkCustomerLogin() = runBlocking {
        customerRepository = CustomerRepository()
        val otp = "791909"
        val response = customerRepository.verifyOtp(otp)
        val expectedResult = true
        val actualResult = response.success
        Assert.assertEquals(expectedResult, actualResult)
    }
}