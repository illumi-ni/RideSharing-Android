package com.ujjallamichhane.ridesharing

import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test

@LargeTest
class TestVerifyOTP {
    @get: Rule
    val testRule = ActivityScenarioRule(SignInOTP::class.java)

    @Test
    fun testVerifyOTP() {
        onView(ViewMatchers.withId(R.id.etOTP))
            .perform(ViewActions.typeText("655499"))
        Thread.sleep(1000)

        closeSoftKeyboard()

        onView(ViewMatchers.withId(R.id.btnVerify))
            .perform(ViewActions.click())
        Thread.sleep(3000)

        onView(ViewMatchers.withId(R.id.dashboardContainer))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}