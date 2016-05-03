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

package it.cosenonjaviste.daggermock.realworldapp.services;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;

import it.cosenonjaviste.daggermock.InjectFromComponent;
import it.cosenonjaviste.daggermock.realworldapp.JUnitDaggerMockRule;
import it.cosenonjaviste.daggermock.realworldapp.main.MainActivity;
import it.cosenonjaviste.daggermock.realworldapp.main.MainPresenter;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;

public class MainServiceTest {
    @Rule public JUnitDaggerMockRule rule = new JUnitDaggerMockRule();

    @Mock RestService restService;

    @InjectFromComponent({MainActivity.class, MainPresenter.class})
    MainService mainService;

    @Test
    public void testDoSomething() {
        when(restService.executeServerCall()).thenReturn(true);

        String s = mainService.doSomething();

        assertThat(s).isEqualTo("Hello world");
    }

    @Test(expected = RuntimeException.class)
    public void testErrorOnDoSomething() {
        when(restService.executeServerCall()).thenReturn(false);

        mainService.doSomething();
    }
}