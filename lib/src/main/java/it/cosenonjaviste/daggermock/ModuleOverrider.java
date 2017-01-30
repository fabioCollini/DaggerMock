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

import dagger.Provides;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Provider;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static java.lang.reflect.Modifier.isFinal;
import static java.lang.reflect.Modifier.isProtected;
import static java.lang.reflect.Modifier.isPublic;

public class ModuleOverrider {

    private final OverriddenObjectsMap overriddenObjectsMap;

    public ModuleOverrider(Object target) {
        this(new OverriddenObjectsMap());
        overriddenObjectsMap.init(target);
    }

    public ModuleOverrider(OverriddenObjectsMap overriddenObjectsMap) {
        this.overriddenObjectsMap = overriddenObjectsMap;
    }

    public <T> T override(final T module) {
        checkMethodsVisibility(module);
        Answer defaultAnswer = new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Method method = invocation.getMethod();
                Provider provider = overriddenObjectsMap.getProvider(method);
                if (provider != null) {
                    return provider.get();
                } else {
                    method.setAccessible(true);
                    return method.invoke(module, invocation.getArguments());
                }
            }
        };
        return (T) Mockito.mock(module.getClass(), defaultAnswer);
    }

    private <T> void checkMethodsVisibility(T module) {
        Method[] methods = module.getClass().getDeclaredMethods();
        List<String> visibilityErrors = new ArrayList<>();
        List<String> finalErrors = new ArrayList<>();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Provides.class)) {
                if (!isPublic(method.getModifiers()) && !isProtected(method.getModifiers())) {
                    visibilityErrors.add(method.toString());
                }
                if (isFinal(method.getModifiers())) {
                    finalErrors.add(method.toString());
                }
            }
        }
        ErrorsFormatter.throwExceptionOnErrors("The following methods must be declared public or protected", visibilityErrors);
        ErrorsFormatter.throwExceptionOnErrors("The following methods must be non final", finalErrors);
    }
}
