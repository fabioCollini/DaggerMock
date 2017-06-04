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

package it.cosenonjaviste.daggermock.realworldappkotlin.main

import it.cosenonjaviste.daggermock.InjectFromComponent
import it.cosenonjaviste.daggermock.realworldappkotlin.JUnitDaggerMockRule
import it.cosenonjaviste.daggermock.realworldappkotlin.services.RestService
import it.cosenonjaviste.daggermock.realworldappkotlin.services.SnackBarManager
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.*

class MainPresenterTest {
    @get:Rule val rule = JUnitDaggerMockRule()

    @Mock lateinit internal var restService: RestService

    @Mock lateinit internal var activity: MainActivity

    @Mock lateinit internal var view: MainView

    @Mock lateinit internal var snackBarManager: SnackBarManager

    @InjectFromComponent(MainActivity::class)
    lateinit internal var presenter: MainPresenter

    @Test
    fun testLoadData() {
        doReturn(true).`when`<RestService>(restService).executeServerCall()

        presenter.loadData()

        verify<MainView>(view).showText("Hello world")
        //        verify(snackBarManager, never()).showMessage(anyString());
    }

    @Test
    fun testErrorOnLoadData() {
        `when`(restService.executeServerCall()).thenReturn(false)

        presenter.loadData()

        verify<MainView>(view, never()).showText(ArgumentMatchers.anyString())
        verify<SnackBarManager>(snackBarManager).showMessage("Error!")
    }
}