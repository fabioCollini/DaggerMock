/*
 *   Copyright 2016 Fabio Collini.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package it.cosenonjaviste.daggermock.realworldappkotlin.main

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import it.cosenonjaviste.daggermock.realworldappkotlin.espressoDaggerMockRule
import it.cosenonjaviste.daggermock.realworldappkotlin.services.RestService
import it.cosenonjaviste.daggermock.realworldappkotlin.services.SnackBarManager
import it.cosenonjaviste.daggeroverride.R
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify

class MainActivityTest {

    @get:Rule val rule = espressoDaggerMockRule()

    @get:Rule val activityRule = ActivityTestRule(MainActivity::class.java, false, false)

    @Mock lateinit var restService: RestService

    @Mock lateinit var snackBarManager: SnackBarManager

    @Test
    fun testOnCreate() {
        `when`(restService.executeServerCall()).thenReturn(true)

        activityRule.launchActivity(null)
        onView(withId(R.id.reload)).perform(click())

        onView(withId(R.id.text)).check(matches(withText("Hello world")))
    }

    @Test
    fun testErrorOnCreate() {
        `when`(restService.executeServerCall()).thenReturn(false)

        activityRule.launchActivity(null)
        onView(withId(R.id.reload)).perform(click())

        onView(withId(R.id.text)).check(matches(withText("")))
        verify<SnackBarManager>(snackBarManager).showMessage("Error!")
    }
}