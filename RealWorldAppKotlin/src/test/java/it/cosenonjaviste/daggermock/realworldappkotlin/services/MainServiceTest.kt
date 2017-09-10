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

package it.cosenonjaviste.daggermock.realworldappkotlin.services

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import it.cosenonjaviste.daggermock.InjectFromComponent
import it.cosenonjaviste.daggermock.realworldappkotlin.jUnitDaggerMockRule
import it.cosenonjaviste.daggermock.realworldappkotlin.main.MainActivity
import it.cosenonjaviste.daggermock.realworldappkotlin.main.MainPresenter
import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.Rule
import org.junit.Test

class MainServiceTest {
    @get:Rule val rule = jUnitDaggerMockRule()

    val restService: RestService = mock()

    val mainActivity: MainActivity = mock()

    val snackBarManager: SnackBarManager = mock()

    @InjectFromComponent(MainActivity::class, MainPresenter::class)
    lateinit var mainService: MainService

    @Test
    fun testDoSomething() {
        whenever(restService.executeServerCall()).thenReturn(true)

        val s = mainService.doSomething()

        assertThat(s).isEqualTo("Hello world")
    }

    @Test(expected = RuntimeException::class)
    fun testErrorOnDoSomething() {
        whenever(restService.executeServerCall()).thenReturn(false)

        mainService.doSomething()
    }
}