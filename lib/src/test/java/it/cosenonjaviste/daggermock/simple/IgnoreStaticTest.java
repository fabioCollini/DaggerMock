/*
 *   Copyright 2016 2017 Fabio Collini.
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

package it.cosenonjaviste.daggermock.simple;

import dagger.Component;
import dagger.Module;
import dagger.Provides;
import it.cosenonjaviste.daggermock.DaggerMockRule;
import it.cosenonjaviste.daggermock.InjectFromComponent;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.junit.Rule;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IgnoreStaticTest {
    @Rule public final DaggerMockRule<MyComponent> rule = new DaggerMockRule<>(MyComponent.class, new MyModule());

    public static String MY_TEST_STRING = "MY_TEST_STRING";

    @InjectFromComponent StringHolder stringHolder;

    @Test
    public void testConstructorArgs() {
        assertThat(stringHolder.s).isEqualTo("aaa");
    }

    @Singleton
    @Component(modules = MyModule.class)
    public interface MyComponent {
        StringHolder stringHolder();
    }

    @Module
    public class MyModule {
        @Provides public String provideString() {
            return "aaa";
        }

    }

    public static class StringHolder {
        @Inject String s;

        @Inject public StringHolder() {
        }
    }
}
