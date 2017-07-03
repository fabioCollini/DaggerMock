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

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.rule.ActivityTestRule
import it.cosenonjaviste.daggermock.realworldappkotlin.EspressoDaggerMockRule
import it.cosenonjaviste.daggermock.realworldappkotlin.services.SnackBarManager
import it.cosenonjaviste.daggeroverride.R
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.verify

class MainActivityMockPresenterTest {

    @get:Rule val rule = EspressoDaggerMockRule()

    @get:Rule val activityRule = ActivityTestRule(MainActivity::class.java, false, false)

    @Mock lateinit var presenter: MainPresenter

    @Mock lateinit var snackBarManager: SnackBarManager

    @Test
    fun testOnCreate() {
        val activity = activityRule.launchActivity(null)

        doAnswer {
            activity.showText("Hello world")
            null
        }.`when`<MainPresenter>(presenter).loadData()

        onView(withId(R.id.reload)).perform(click())

        onView(withId(R.id.text)).check(matches(withText("Hello world")))
    }

    @Test
    fun testErrorOnCreate() {
        doAnswer {
            snackBarManager.showMessage("Error!")
            null
        }.`when`<MainPresenter>(presenter).loadData()

        activityRule.launchActivity(null)
        onView(withId(R.id.reload)).perform(click())

        onView(withId(R.id.text)).check(matches(withText("")))

        verify(snackBarManager).showMessage("Error!")
    }
}