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
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Provider;

import dagger.Provides;

public class ModuleOverrider {

    private final Map<ObjectId, Provider> fields;

    public ModuleOverrider(Object target) {
        this(target, null);
    }

    public ModuleOverrider(Object target, Map<ObjectId, Provider> extraObjects) {
        fields = new HashMap<>();
        if (extraObjects != null) {
            fields.putAll(extraObjects);
        }
        ReflectUtils.extractFields(target, fields);
    }

    public <T> T override(final T module) {
        checkMethodsVisibility(module);
        Answer defaultAnswer = new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Method method = invocation.getMethod();
                Provider provider = getProvider(method);
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

    private Provider getProvider(Method method) {
        Provider provider = fields.get(new ObjectId(method));
        if (provider == null) {
            provider = fields.get(new ObjectId(method.getReturnType()));
        }
        return provider;
    }

    private <T> void checkMethodsVisibility(T module) {
        Method[] methods = module.getClass().getDeclaredMethods();
        List<Method> visibilityErrors = new ArrayList<>();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Provides.class)) {
                if (!Modifier.isPublic(method.getModifiers()) && !Modifier.isProtected(method.getModifiers())) {
                    visibilityErrors.add(method);
                }
            }
        }
        if (!visibilityErrors.isEmpty()) {
            String message = "The following methods has to be public or protected:";
            for (Method visibilityError : visibilityErrors) {
                message += "\n" + visibilityError;
            }
            throw new RuntimeException(message);
        }
    }

}
