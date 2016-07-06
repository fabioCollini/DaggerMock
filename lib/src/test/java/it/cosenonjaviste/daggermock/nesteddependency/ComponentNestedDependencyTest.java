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

package it.cosenonjaviste.daggermock.nesteddependency;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;

import it.cosenonjaviste.daggermock.DaggerMockRule;

import static org.assertj.core.api.Assertions.assertThat;

public class ComponentNestedDependencyTest {

    @Rule
    public final DaggerMockRule<MyComponent> rule = new DaggerMockRule<>(MyComponent.class, new MyModule())
            .addComponentDependency(MyComponent2.class, new MyModule2())
            .addComponentDependency(MyComponent2.class, MyComponent3.class, new MyModule3())
            .addComponentDependency(MyComponent3.class, MyComponent4.class, new MyModule4())
            .set(new DaggerMockRule.ComponentSetter<MyComponent>() {
                @Override public void setComponent(MyComponent component) {
                    mainService = component.mainService();
                }
            });

    @Mock
    MyService myService;
    @Mock
    MyService2 myService2;

    private MainService mainService;

    @Test
    public void testComponentDependencyModulesCanBeOverriden() {
        assertThat(mainService).isNotNull();
        assertThat(mainService.getMyService()).isSameAs(myService);
        assertThat(mainService.getMyService2()).isSameAs(myService2);
    }
}
