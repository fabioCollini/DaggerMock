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

package it.cosenonjaviste.daggermock.builder

import com.nhaarman.mockitokotlin2.mock
import it.cosenonjaviste.daggermock.DaggerMockRule
import it.cosenonjaviste.daggermock.InjectFromComponent
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test

class InjectFromSubComponentTest {

    @get:Rule
    val rule = DaggerMockRule(MyComponent::class.java, MyModule())
            .addComponentDependency(MyComponent2::class.java, MyModule2())

    private val myService: MyService = mock()

    @InjectFromComponent
    lateinit var myService2: MyService2

    @Test
    fun testInjectFromComponentOnObjectDeclaredInDependentComponent() {
        assertThat(myService2).isNotNull()
    }
}
