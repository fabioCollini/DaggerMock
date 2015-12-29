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

package it.cosenonjaviste.daggermock.demo.v3;

import android.support.test.InstrumentationRegistry;

import it.cosenonjaviste.daggermock.DaggerMockRule;
import it.cosenonjaviste.daggermock.demo.App;
import it.cosenonjaviste.daggermock.demo.MyComponent;
import it.cosenonjaviste.daggermock.demo.MyModule;

public class MyRule extends DaggerMockRule<MyComponent> {
    public MyRule() {
        super(MyComponent.class, new MyModule());
        set(new DaggerMockRule.ComponentSetter<MyComponent>() {
            @Override public void setComponent(MyComponent component) {
                App app = (App) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
                app.setComponent(component);
            }
        });
    }
}
