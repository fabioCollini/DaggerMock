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

package it.cosenonjaviste.daggermock.simple;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;

import it.cosenonjaviste.daggermock.DaggerMockRule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

public class ProvidesMockWithInitMethodTest {

    public static class MyRule extends DaggerMockRule<MyComponent> {
        public MyRule() {
            super(MyComponent.class, new MyModule());
            providesMock(MyService.class, new DaggerMockRule.MockInitializer<MyService>() {
                @Override
                public void init(MyService mock) {
                    when(mock.getWithParam(anyInt())).thenReturn("default");
                }
            });
        }
    }

    @Rule public final DaggerMockRule<MyComponent> rule = new MyRule()
            .providesMock(MyService.class, new DaggerMockRule.MockInitializer<MyService>() {
                @Override
                public void init(MyService mock) {
                    when(mock.getWithParam(1)).thenReturn("rule");
                }
            })
            .set(new DaggerMockRule.ComponentSetter<MyComponent>() {
                @Override
                public void setComponent(MyComponent component) {
                    mainService = component.mainService();
                }
            });

    @Mock MyService myService;

    private MainService mainService;

    @Before
    public void setUp() throws Exception {
        when(myService.getWithParam(2)).thenReturn("setUp");
    }

    @Test
    public void testDefaultDefinitionInRule() {
        assertThat(mainService.getWithParam(0)).isEqualTo("default");
    }

    @Test
    public void testDefaultDefinitionInProvide() {
        assertThat(mainService.getWithParam(1)).isEqualTo("rule");
    }

    @Test
    public void testDefinitionInSetup() {
        assertThat(mainService.getWithParam(2)).isEqualTo("setUp");
    }
}
