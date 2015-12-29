/*
 *  Copyright 2016 Fabio Collini.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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