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

package it.cosenonjaviste.daggermock.realworldappkotlin

import android.support.test.InstrumentationRegistry
import it.cosenonjaviste.daggermock.DaggerMock

import it.cosenonjaviste.daggermock.DaggerMockRule

class EspressoDaggerMockRule : DaggerMockRule<AppComponent>(AppComponent::class.java, AppModule(getApp())) {
    init {
        set { component -> getApp().component = component }
    }
}

fun espressoDaggerMockRule() = DaggerMock.rule<AppComponent>(AppModule(getApp())) {
    set { component -> getApp().component = component }
}

fun getApp(): App = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as App
