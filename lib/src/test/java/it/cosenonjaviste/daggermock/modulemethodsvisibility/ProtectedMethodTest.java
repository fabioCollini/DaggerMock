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

package it.cosenonjaviste.daggermock.modulemethodsvisibility;

import org.junit.Rule;
import org.junit.Test;

import javax.inject.Provider;
import javax.inject.Singleton;

import dagger.Component;
import dagger.Module;
import dagger.Provides;
import it.cosenonjaviste.daggermock.DaggerMockRule;

import static org.assertj.core.api.Assertions.assertThat;

public class ProtectedMethodTest {

    private MainService mainService;

    @Rule public final DaggerMockRule<MyComponent> mockitoRule = new DaggerMockRule<>(MyComponent.class, new MyModule())
            .provides(MyService.class, new Provider<MyService>() {
                @Override public MyService get() {
                    return new MyService();
                }
            })
            .set(new DaggerMockRule.ComponentSetter<MyComponent>() {
                @Override public void setComponent(MyComponent component) {
                    mainService = component.mainService();
                }
            });

    @Test
    public void testErrorOnPackageMethods() throws Throwable {
        assertThat(mainService.getMyService()).isNotNull();
    }

    @Module
    public static class MyModule {
        @Provides protected MyService provideMyService() {
            return new MyService();
        }

        private void privateMethod() {
        }
    }

    @Singleton
    @Component(modules = MyModule.class)
    public interface MyComponent {
        MainService mainService();
    }
}
