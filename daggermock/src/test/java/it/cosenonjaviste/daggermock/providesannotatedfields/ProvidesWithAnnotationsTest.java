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

package it.cosenonjaviste.daggermock.providesannotatedfields;

import org.junit.Rule;
import org.junit.Test;

import it.cosenonjaviste.daggermock.DaggerMockRule;
import it.cosenonjaviste.daggermock.InjectFromComponent;
import it.cosenonjaviste.daggermock.Qualifier1;
import it.cosenonjaviste.daggermock.Qualifier2;
import it.cosenonjaviste.daggermock.simple.MyService;

import static org.assertj.core.api.Assertions.assertThat;

public class ProvidesWithAnnotationsTest {
    @Rule public final DaggerMockRule<MyComponent> rule = new DaggerMockRule<>(MyComponent.class, new MyModule())
            .provides(MyService.class, "service1", new MyServiceImpl("impl1"))
            .provides(MyService.class, "service2", new MyServiceImpl("impl2"))
            .provides(MyService.class, Qualifier1.class, new MyServiceImpl("impl3"))
            .provides(MyService.class, Qualifier2.class, new MyServiceImpl("impl4"));

    @InjectFromComponent
    MainServiceWithAnnotatedFields mainService;

    @Test
    public void testOverride() {
        assertThat(mainService).isNotNull();

        assertThat(((MyServiceImpl) mainService.myService).name).isEqualTo("impl1");
        assertThat(((MyServiceImpl) mainService.myService2).name).isEqualTo("impl2");
        assertThat(((MyServiceImpl) mainService.myService3).name).isEqualTo("impl3");
        assertThat(((MyServiceImpl) mainService.myService4).name).isEqualTo("impl4");
    }
}
