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

package it.cosenonjaviste.daggermock;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Method;
import java.util.Map;

public class ComponentOverrider {

    private ModuleOverrider moduleOverrider;

    public ComponentOverrider(ModuleOverrider moduleOverrider) {
        this.moduleOverrider = moduleOverrider;
    }

    public <T> T override(Class<T> componentClass, final Object component, final Map<Class<?>, DaggerMockRule.ObjectDecorator<?>> decorators) {
        Answer defaultAnswer = new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Method method = invocation.getMethod();
                Object[] arguments = invocation.getArguments();
                if (ReflectUtils.isSubComponent(method.getReturnType()) || ReflectUtils.isSubComponentBuilder(method.getReturnType())) {
                    Object[] mockedArguments = new Object[arguments.length];
                    for (int i = 0; i < arguments.length; i++) {
                        mockedArguments[i] = moduleOverrider.override(arguments[i], decorators);
                    }
                    arguments = mockedArguments;
                    Object originalSubComponent = method.invoke(component, arguments);
                    return override(method.getReturnType(), originalSubComponent, decorators);
                } else {
                    return method.invoke(component, arguments);
                }
            }
        };
        return Mockito.mock(componentClass, defaultAnswer);
    }

}
