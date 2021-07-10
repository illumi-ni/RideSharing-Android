package com.ujjallamichhane.ridesharing

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.ujjallamichhane.ridesharing.fragments.CustomerSignInFragment
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class TestSendOTP {
    @get: Rule
    val testRule = ActivityScenarioRule(SignInActivity::class.java)

    @Test
    fun testSentOTP() {
        val scenario = launchFragmentInContainer<CustomerSignInFragment>()

        onView(withId(R.id.etEmailSignIn))
            .perform(ViewActions.typeText("krazyme53@gmail.com"))
        Thread.sleep(1000)
        closeSoftKeyboard()

        onView(withId(R.id.btnSignIn))
            .perform(ViewActions.click())
        Thread.sleep(5000)

        onView(withId(R.id.OTPcontainer))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}