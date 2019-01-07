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

package it.cosenonjaviste.daggermock.realworldapp.main;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import androidx.test.rule.ActivityTestRule;
import it.cosenonjaviste.daggermock.realworldapp.EspressoDaggerMockRule;
import it.cosenonjaviste.daggermock.realworldapp.services.SnackBarManager;
import it.cosenonjaviste.daggeroverride.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

public class MainActivityMockPresenterTest {

    @Rule public EspressoDaggerMockRule rule = new EspressoDaggerMockRule();

    @Rule public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class, false, false);

    @Mock MainPresenter presenter;

    @Mock SnackBarManager snackBarManager;

    @Test
    public void testOnCreate() {
        final MainActivity activity = activityRule.launchActivity(null);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                activity.showText("Hello world");
                return null;
            }
        }).when(presenter).loadData();

        onView(withId(R.id.reload)).perform(click());

        onView(withId(R.id.text)).check(matches(withText("Hello world")));
    }

    @Test
    public void testErrorOnCreate() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                snackBarManager.showMessage("Error!");
                return null;
            }
        }).when(presenter).loadData();

        activityRule.launchActivity(null);
        onView(withId(R.id.reload)).perform(click());

        onView(withId(R.id.text)).check(matches(withText("")));

        verify(snackBarManager).showMessage("Error!");
    }
}