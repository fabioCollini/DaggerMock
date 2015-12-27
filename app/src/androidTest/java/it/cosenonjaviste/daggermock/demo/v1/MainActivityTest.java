package it.cosenonjaviste.daggermock.demo.v1;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;

import it.cosenonjaviste.daggermock.demo.App;
import it.cosenonjaviste.daggermock.demo.MainActivity;
import it.cosenonjaviste.daggermock.demo.MyPrinter;
import it.cosenonjaviste.daggermock.demo.RestService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MainActivityTest {

    @Rule public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class, false, false);

    @Inject RestService restService;

    @Inject MyPrinter myPrinter;

    @Before
    public void setUp() throws Exception {
        EspressoTestComponent component = DaggerEspressoTestComponent.builder().myModule(new EspressoTestModule()).build();

        App app = (App) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
        app.setComponent(component);

        component.inject(this);
    }

    @Test
    public void testCreateActivity() {
        when(restService.doSomething()).thenReturn("abc");

        activityRule.launchActivity(null);

        verify(myPrinter).print("ABC");
    }
}