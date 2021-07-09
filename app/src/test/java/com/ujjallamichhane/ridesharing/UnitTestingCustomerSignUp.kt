package com.ujjallamichhane.ridesharing

import com.ujjallamichhane.ridesharing.entity.Customer
import com.ujjallamichhane.ridesharing.repository.CustomerRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class UnitTestingCustomerSignUp {
    private lateinit var customerRepository: CustomerRepository

    @Test
    fun checkCustomerSignup() = runBlocking {
        customerRepository = CustomerRepository()
        val customer = Customer(fullname = "Ujjal Lamichhane", contact = "0987654321", email = "123456@gmail.com",
                gender = "Male")
        val response = customerRepository.registerCustomer(customer)
        val expectedResult = true
        val actualResult = response.success
        Assert.assertEquals(expectedResult, actualResult)
    }
}