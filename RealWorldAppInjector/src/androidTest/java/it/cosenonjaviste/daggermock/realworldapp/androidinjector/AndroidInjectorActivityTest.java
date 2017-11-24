package it.cosenonjaviste.daggermock.realworldapp.androidinjector;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;

import it.cosenonjaviste.daggermock.realworldapp.EspressoDaggerMockRule;
import it.cosenonjaviste.daggermock.realworldapp.main.MainActivity;
import it.cosenonjaviste.daggermock.realworldapp.services.RestService;
import it.cosenonjaviste.daggermock.realworldapp.services.SnackBarManager;
import it.cosenonjaviste.daggeroverride.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by thomasschmidt on 23/11/2017.
 */

public class AndroidInjectorActivityTest {

    @Rule
    public EspressoDaggerMockRule rule = new EspressoDaggerMockRule();

    @Rule public ActivityTestRule<AndroidInjectorActivity> activityRule = new ActivityTestRule<>(AndroidInjectorActivity.class, false, false);

    @Mock
    RestService restService;

    @Mock
    SnackBarManager snackBarManager;

    @Test
    public void testOnCreate() {
        when(restService.executeServerCall()).thenReturn(true);

        activityRule.launchActivity(null);
        onView(withId(R.id.reload)).perform(click());

        onView(withId(R.id.text)).check(matches(withText("Hello world")));
    }

    @Test
    public void testErrorOnCreate() {
        when(restService.executeServerCall()).thenReturn(false);

        activityRule.launchActivity(null);
        onView(withId(R.id.reload)).perform(click());

        onView(withId(R.id.text)).check(matches(withText("")));
        verify(snackBarManager).showMessage(any(Activity.class), eq("Error!"));
    }
}
