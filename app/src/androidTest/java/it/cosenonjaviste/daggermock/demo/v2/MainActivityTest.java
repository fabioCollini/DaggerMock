package it.cosenonjaviste.daggermock.demo.v2;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;

import it.cosenonjaviste.daggermock.DaggerMockRule;
import it.cosenonjaviste.daggermock.demo.App;
import it.cosenonjaviste.daggermock.demo.MainActivity;
import it.cosenonjaviste.daggermock.demo.MyComponent;
import it.cosenonjaviste.daggermock.demo.MyModule;
import it.cosenonjaviste.daggermock.demo.MyPrinter;
import it.cosenonjaviste.daggermock.demo.RestService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MainActivityTest {

    @Rule public final DaggerMockRule<MyComponent> daggerRule = new DaggerMockRule<>(MyComponent.class, new MyModule())
            .set(new DaggerMockRule.ComponentSetter<MyComponent>() {
                @Override public void setComponent(MyComponent component) {
                    App app = (App) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
                    app.setComponent(component);
                }
            });

    @Rule public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class, false, false);

    @Mock RestService restService;

    @Mock MyPrinter myPrinter;

    @Test
    public void testCreateActivity() {
        when(restService.doSomething()).thenReturn("abc");

        activityRule.launchActivity(null);

        verify(myPrinter).print("ABC");
    }
}