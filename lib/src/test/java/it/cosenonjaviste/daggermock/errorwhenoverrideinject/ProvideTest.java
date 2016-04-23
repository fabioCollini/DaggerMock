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

package it.cosenonjaviste.daggermock.errorwhenoverrideinject;

import org.junit.Test;

import it.cosenonjaviste.daggermock.DaggerMockRule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

public class ProvideTest {
    @Test
    public void testErrorWhenDefiningUsingProvidesMockAFieldThatDoesntOverride() {
        try {
            DaggerMockRule<MyComponent> rule = new DaggerMockRule<>(MyComponent.class, new MyModule())
                    .providesMock(MyService.class, MyService2.class);
            rule.apply(null, null, this).evaluate();

            fail();
        } catch (Throwable e) {
            assertThat(e.getMessage())
                    .contains("Error while trying to override objects")
                    .contains(MyService2.class.getName());
        }
    }

    @Test
    public void testErrorWhenDefiningUsingProvidesAFieldThatDoesntOverride() {
        try {
            DaggerMockRule<MyComponent> rule = new DaggerMockRule<>(MyComponent.class, new MyModule())
                    .provides(MyService.class, new MyService())
                    .provides(MyService2.class, new MyService2());
            rule.apply(null, null, this).evaluate();

            fail();
        } catch (Throwable e) {
            assertThat(e.getMessage())
                    .contains("Error while trying to override objects")
                    .contains(MyService2.class.getName());
        }
    }
}
