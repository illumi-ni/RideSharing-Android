package com.ujjallamichhane.ridesharing

import com.ujjallamichhane.ridesharing.repository.CustomerRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class UnitTestingSendOTP {
    private lateinit var customerRepository: CustomerRepository

    @Test
    fun checkCustomerLogin() = runBlocking {
        customerRepository = CustomerRepository()
        val email = "krazyme53@gmail.com"
        val response = customerRepository.sendOTP(email)
        val expectedResult = true
        val actualResult = response.success
        Assert.assertEquals(expectedResult, actualResult)
    }
}