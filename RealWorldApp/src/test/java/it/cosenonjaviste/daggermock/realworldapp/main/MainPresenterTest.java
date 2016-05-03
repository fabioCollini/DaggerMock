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

package it.cosenonjaviste.daggermock.realworldapp.main;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;

import it.cosenonjaviste.daggermock.InjectFromComponent;
import it.cosenonjaviste.daggermock.realworldapp.JUnitDaggerMockRule;
import it.cosenonjaviste.daggermock.realworldapp.services.RestService;
import it.cosenonjaviste.daggermock.realworldapp.services.SnackBarManager;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MainPresenterTest {
    @Rule public JUnitDaggerMockRule rule = new JUnitDaggerMockRule();

    @Mock RestService restService;

    @Mock MainView view;

    @Mock SnackBarManager snackBarManager;

    @InjectFromComponent(MainActivity.class)
    MainPresenter presenter;

    @Test
    public void testLoadData() {
        when(restService.executeServerCall()).thenReturn(true);

        presenter.loadData();

        verify(view).showText("Hello world");
        verify(snackBarManager, never()).showMessage(anyString());
    }

    @Test
    public void testErrorOnLoadData() {
        when(restService.executeServerCall()).thenReturn(false);

        presenter.loadData();

        verify(view, never()).showText(anyString());
        verify(snackBarManager).showMessage("Error!");
    }
}