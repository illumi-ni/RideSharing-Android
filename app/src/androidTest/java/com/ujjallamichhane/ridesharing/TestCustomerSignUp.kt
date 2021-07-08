package com.ujjallamichhane.ridesharing

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@LargeTest
@RunWith(JUnit4::class)
class TestCustomerSignUp {
    @get: Rule
    val testRule = ActivityScenarioRule(SignUpActivity::class.java)

    @Test
    fun testCustomerSignUp(){
        Espresso.onView(ViewMatchers.withId(R.id.etFullName))
                .perform(ViewActions.typeText("Emma Watson"))
        Thread.sleep(1000)
        Espresso.onView(ViewMatchers.withId(R.id.etEmail))
                .perform(ViewActions.typeText("emma@gmail.com"))
        Thread.sleep(1000)
        Espresso.onView(ViewMatchers.withId(R.id.etContact))
                .perform(ViewActions.typeText("9828991002"))
        Thread.sleep(1000)
        Espresso.closeSoftKeyboard()
        Espresso.onView(ViewMatchers.withId(R.id.rbFemale))
                .perform(click())
        Thread.sleep(1000)
        Espresso.onView(ViewMatchers.withId(R.id.rbFemale))
                .check(matches(isChecked()));

        Espresso.onView(ViewMatchers.withId(R.id.rbMale))
                .check(matches(isNotChecked()));

        Espresso.onView(ViewMatchers.withId(R.id.rbOthers))
                .check(matches(isNotChecked()));

        Espresso.onView(ViewMatchers.withId(R.id.btnSignUp))
                .perform(ViewActions.click())
        Thread.sleep(2000)
    }


}