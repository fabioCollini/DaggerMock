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

package it.cosenonjaviste.daggermock.demo.v2;

import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import it.cosenonjaviste.daggermock.demo.MainService;
import it.cosenonjaviste.daggermock.demo.MyPrinter;
import it.cosenonjaviste.daggermock.demo.RestService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MainServiceDaggerTest {

    @Inject RestService restService;

    @Inject MyPrinter myPrinter;

    @Inject MainService mainService;

    @Before public void setUp() {
        TestComponent component = DaggerTestComponent.builder().myModule(new TestModule()).build();
        component.inject(this);
    }

    @Test public void testDoSomething() {
        when(restService.getSomething()).thenReturn("abc");

        mainService.doSomething();

        verify(myPrinter).print("ABC");
    }
}