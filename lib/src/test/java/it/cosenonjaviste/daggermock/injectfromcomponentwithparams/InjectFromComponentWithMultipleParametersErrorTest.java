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

package it.cosenonjaviste.daggermock.injectfromcomponentwithparams;

import org.junit.Test;

import dagger.Component;
import dagger.Module;
import dagger.Provides;
import it.cosenonjaviste.daggermock.DaggerMockRule;
import it.cosenonjaviste.daggermock.InjectFromComponent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

public class InjectFromComponentWithMultipleParametersErrorTest {
    String s1 = "test1";

    @InjectFromComponent({MyActivity.class, Service1.class, Service2.class})
    Service3 service3;

    @Test
    public void testInjectFromComponentWithMultipleParameters() {
        try {
            DaggerMockRule<MyComponent> rule = new DaggerMockRule<>(MyComponent.class, new MyModule());
            rule.apply(null, null, this).evaluate();

            fail();
        } catch (Throwable e) {
            assertThat(e.getMessage())
                    .contains("it.cosenonjaviste.daggermock.injectfromcomponentwithparams.Service1 field not found in class " +
                            "it.cosenonjaviste.daggermock.injectfromcomponentwithparams.MyActivity, it's defined as parameter in InjectFromComponent annotation");
        }
    }

    @Module
    public static class MyModule {
        @Provides
        public String provideS1() {
            return "s1";
        }
    }

    @Component(modules = MyModule.class)
    public interface MyComponent {
        void inject(MyActivity myActivity);
    }
}
