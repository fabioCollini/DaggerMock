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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Provider;
import org.junit.Rule;
import org.mockito.Mockito;

import static it.cosenonjaviste.daggermock.ComponentClassWrapper.SubComponentMethod;

class OverriddenObjectsMap {
    private final Map<ObjectId, Provider> fields = new HashMap<>();

    public void init(Object target) {
        Class<?> targetClass = target.getClass();
        while (!targetClass.equals(Object.class)) {
            initClassFields(target, targetClass);
            targetClass = targetClass.getSuperclass();
        }
    }

    private void initClassFields(Object target, Class<?> targetClass) {
        Field[] targetFields = targetClass.getDeclaredFields();
        for (Field field : targetFields) {
            if (field.getAnnotation(Rule.class) == null) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    field.setAccessible(true);
                    try {
                        final Object value = field.get(target);
                        if (value != null) {
                            fields.put(new ObjectId(field), new Provider() {
                                @Override
                                public Object get() {
                                    return value;
                                }
                            });
                        }
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Error accessing field " + field, e);
                    }
                }
            }
        }
    }

    public void checkOverriddenInjectAnnotatedClass() {
        Set<String> errors = new HashSet<>();
        for (Map.Entry<ObjectId, Provider> entry : fields.entrySet()) {
            ObjectId objectId = entry.getKey();
            Constructor[] constructors = objectId.objectClass.getConstructors();
            for (Constructor constructor : constructors) {
                if (constructor.getAnnotation(Inject.class) != null) {
                    errors.add(objectId.objectClass.getName());
                }
            }
        }
        ErrorsFormatter.throwExceptionOnErrors(
                "Error while trying to override objects",
                errors,
                "You must define overridden objects using a @Provides annotated method instead of using @Inject annotation");
    }

    public void checkOverridesInSubComponentsWithNoParameters(ComponentClassWrapper<?> componentClass) {
        HashSet<String> errors = new HashSet<>();
        checkOverridesInSubComponentsWithNoParameters(componentClass, errors);
        ErrorsFormatter.throwExceptionOnErrors("Error while trying to override subComponents objects", errors);
    }

    private void checkOverridesInSubComponentsWithNoParameters(ComponentClassWrapper<?> componentClass, Set<String> errors) {
        List<SubComponentMethod<?>> methods = componentClass.getSubComponentMethods();
        for (SubComponentMethod<?> method : methods) {
            Class<?>[] modules = method.modules;
            for (Class<?> module : modules) {
                if (!method.existsParameter(module)) {
                    checkOverridesInSubComponentModule(module, errors);
                }
            }
            checkOverridesInSubComponentsWithNoParameters(method.subComponentClassWrapper, errors);
        }
    }

    private void checkOverridesInSubComponentModule(Class<?> module, Set<String> errors) {
        Method[] moduleMethods = module.getMethods();
        for (Method moduleMethod : moduleMethods) {
            if (!moduleMethod.getDeclaringClass().equals(Object.class) && containsField(moduleMethod.getReturnType())) {
                errors.add(moduleMethod.getReturnType().getName());
            }
        }
    }

    public Provider getProvider(Method method) {
        return fields.get(new ObjectId(method));
    }

    public boolean containsField(Class<?> type) {
        for (ObjectId objectId : fields.keySet()) {
            if (objectId.objectClass.equals(type)) {
                return true;
            }
        }
        return false;
    }

    public <S> void put(Class<S> originalClass, final S newObject) {
        fields.put(new ObjectId(originalClass), new Provider() {
            @Override
            public Object get() {
                return newObject;
            }
        });
    }

    public <S> void putProvider(Class<S> originalClass, Provider<S> provider) {
        fields.put(new ObjectId(originalClass), provider);
    }

    public void putMocks(Class<?>[] originalClasses) {
        for (final Class<?> originalClass : originalClasses) {
            fields.put(new ObjectId(originalClass), new MockProvider<>(originalClass));
        }
    }

    public <M> void putMock(Class<M> originalClass, DaggerMockRule.MockInitializer<M> initializer) {
        Provider provider = fields.get(new ObjectId(originalClass));
        if (provider instanceof MockProvider) {
            ((MockProvider) provider).addInitializer(initializer);
        } else {
            fields.put(new ObjectId(originalClass), new MockProvider<>(originalClass, initializer));
        }
    }

    private static class MockProvider<M> implements Provider {
        private final Class<M> originalClass;
        private final List<DaggerMockRule.MockInitializer<M>> initializers = new ArrayList<>();

        public MockProvider(Class<M> originalClass) {
            this.originalClass = originalClass;
        }

        public MockProvider(Class<M> originalClass, DaggerMockRule.MockInitializer<M> initializer) {
            this.originalClass = originalClass;
            initializers.add(initializer);
        }

        @Override
        public Object get() {
            M mock = Mockito.mock(originalClass);
            for (DaggerMockRule.MockInitializer<M> initializer : initializers) {
                initializer.init(mock);
            }
            return mock;
        }

        public void addInitializer(DaggerMockRule.MockInitializer<M> initializer) {
            initializers.add(initializer);
        }
    }

    public void redefineMocksWithInitializer(ObjectWrapper<Object> target) {
        for (Map.Entry<ObjectId, Provider> entry : fields.entrySet()) {
            Provider provider = entry.getValue();
            if (provider instanceof MockProvider && !((MockProvider) provider).initializers.isEmpty()) {
                Field field = target.getField(entry.getKey().objectClass);
                if (field != null) {
                    target.setFieldValue(field, provider.get());
                }
            }
        }
    }
}
