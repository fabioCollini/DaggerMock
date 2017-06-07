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

package it.cosenonjaviste.daggermock.injectfromcomponentwithparams;

import org.junit.Rule;
import org.junit.Test;

import dagger.Component;
import dagger.Module;
import dagger.Provides;
import it.cosenonjaviste.daggermock.DaggerMockRule;
import it.cosenonjaviste.daggermock.InjectFromComponent;

import static org.assertj.core.api.Assertions.assertThat;

public class InjectFromComponentWithSingleParameterTest {
    @Rule public final DaggerMockRule<MyComponent> rule = new DaggerMockRule<>(MyComponent.class, new MyModule());

    String s1 = "test1";

    @InjectFromComponent(MyActivity.class)
    MainService mainService;

    @Test
    public void testInjectFromComponentWithSingleParameter() {
        assertThat(mainService).isNotNull();
        assertThat(mainService.get()).isEqualTo("test1");
    }

    @Module
    public static class MyModule {

        public static final String MSG_S1 = "s1";

        @Provides
        public String provideS1() {
            return MSG_S1;
        }
    }

    @Component(modules = MyModule.class)
    public interface MyComponent {
        void inject(MyActivity myActivity);
    }
}
