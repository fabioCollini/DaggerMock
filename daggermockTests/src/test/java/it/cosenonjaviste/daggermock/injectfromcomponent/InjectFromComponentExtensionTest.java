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

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import java.util.Collection;
import java.util.Collections;

import org.junit.jupiter.api.extension.ExtendWith;

import dagger.Module;
import dagger.Provides;
import dagger.Component;
import it.cosenonjaviste.daggermock.DaggerMockExtension;
import it.cosenonjaviste.daggermock.DaggerMockTest;
import it.cosenonjaviste.daggermock.DaggerMockModules;
import it.cosenonjaviste.daggermock.InjectFromComponent;

@ExtendWith(DaggerMockExtension.class)
@DaggerMockTest(InjectFromComponentExtensionTest.MyComponent.class)
public class InjectFromComponentExtensionTest {

    @DaggerMockModules
    Collection<Object> list = Collections.singleton(new MyModule());

    String s1 = "test1";

    @InjectFromComponent MainService mainService;

    @Test
    public void testInjectFromComponent() {
        assertThat(mainService).isNotNull();
        assertThat(mainService.get()).isEqualTo("test1");
    }

    @Module
    public static class MyModule {
        @Provides public String provideS1() {
            return "s1";
        }
    }

    @Component(modules = MyModule.class)
    public interface MyComponent {
        MainService mainService();
    }
}
