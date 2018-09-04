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

package it.cosenonjaviste.daggermock.injectfromcomponent;

import org.junit.Rule;
import org.junit.Test;

import dagger.Component;
import dagger.Module;
import dagger.Provides;
import it.cosenonjaviste.daggermock.DaggerMockRule;
import it.cosenonjaviste.daggermock.InjectFromComponent;
import it.cosenonjaviste.daggermock.Qualifier1;
import it.cosenonjaviste.daggermock.Qualifier2;

import static org.assertj.core.api.Assertions.assertThat;

public class InjectFromComponentWithQualifierAnnotationsTest {
    @Rule public final DaggerMockRule<MyComponent> rule = new DaggerMockRule<>(MyComponent.class, new MyModule());

    @InjectFromComponent @Qualifier1 String s1;
    @InjectFromComponent @Qualifier2 String s2;

    @Test
    public void testInjectFromComponentWithQualifiers() {
        assertThat(s1).isEqualTo("s1");
        assertThat(s2).isEqualTo("s2");
    }

    @Module
    public static class MyModule {
        @Qualifier1 @Provides public String provideS1() {
            return "s1";
        }

        @Qualifier2 @Provides public String provideS2() {
            return "s2";
        }
    }

    @Component(modules = MyModule.class)
    public interface MyComponent {
        @Qualifier1 String s1();
        @Qualifier2 String s2();
    }
}
