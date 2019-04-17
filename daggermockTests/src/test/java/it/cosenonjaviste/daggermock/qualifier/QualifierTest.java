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

package it.cosenonjaviste.daggermock.qualifier;

import org.junit.Rule;
import org.junit.Test;

import dagger.Component;
import dagger.Module;
import dagger.Provides;
import it.cosenonjaviste.daggermock.DaggerMockRule;
import it.cosenonjaviste.daggermock.Qualifier1;
import it.cosenonjaviste.daggermock.Qualifier2;
import it.cosenonjaviste.daggermock.Qualifier3;

import static org.assertj.core.api.Assertions.assertThat;

public class QualifierTest {
    @Rule public final DaggerMockRule<MyComponent> rule = new DaggerMockRule<>(MyComponent.class, new MyModule())
            .set(new DaggerMockRule.ComponentSetter<MyComponent>() {
                @Override public void setComponent(MyComponent component) {
                    mainService = component.mainService();
                }
            });

    @Qualifier1 String s1 = "test1";

    @Qualifier2 String s2 = "test2";

    String s3 = "test3";

    private MainService mainService;

    @Test
    public void testNamed() {
        assertThat(mainService.get()).isEqualTo("test1test2s3");
    }

    @Module
    public static class MyModule {
        @Provides @Qualifier1 public String provideS1() {
            return "s1";
        }

        @Provides @Qualifier2 public String provideS2() {
            return "s2";
        }

        @Provides @Qualifier3 public String provideS3() {
            return "s3";
        }
    }

    @Component(modules = MyModule.class)
    public interface MyComponent {
        MainService mainService();
    }
}
