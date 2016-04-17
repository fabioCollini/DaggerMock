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

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Provider;

import dagger.Subcomponent;

public class DaggerMockRule<C> implements MethodRule {
    private Class<C> componentClass;
    private ComponentSetter<C> componentSetter;
    private List<Object> modules = new ArrayList<>();
    private final Map<Class, List<Object>> dependencies = new HashMap<>();
    private final Map<ObjectId, Provider> overridenObjects = new HashMap<>();

    public DaggerMockRule(Class<C> componentClass, Object... modules) {
        this.componentClass = componentClass;
        Collections.addAll(this.modules, modules);
    }

    public DaggerMockRule<C> set(ComponentSetter<C> componentSetter) {
        this.componentSetter = componentSetter;
        return this;
    }

    public <S> DaggerMockRule<C> provides(Class<S> originalClass, final S newObject) {
        overridenObjects.put(new ObjectId(originalClass), new Provider() {
            @Override
            public Object get() {
                return newObject;
            }
        });
        return this;
    }

    public <S> DaggerMockRule<C> provides(Class<S> originalClass, Provider<S> provider) {
        overridenObjects.put(new ObjectId(originalClass), provider);
        return this;
    }

    public DaggerMockRule<C> addComponentDependency(Class componentClass, Object... modules) {
        dependencies.put(componentClass, Arrays.asList(modules));
        return this;
    }

    public DaggerMockRule<C> providesMock(final Class<?>... originalClasses) {
        for (final Class<?> originalClass : originalClasses) {
            overridenObjects.put(new ObjectId(originalClass), new Provider() {
                @Override
                public Object get() {
                    return Mockito.mock(originalClass);
                }
            });
        }
        return this;
    }

    @Override
    public Statement apply(final Statement base, FrameworkMethod method, final Object target) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                MockitoAnnotations.initMocks(target);

                OverriddenObjectsMap overriddenObjectsMap = new OverriddenObjectsMap(target, overridenObjects);

                ModuleOverrider moduleOverrider = new ModuleOverrider(overriddenObjectsMap);

                checkOverridesInSubComponentsWithNoParameters(componentClass, overriddenObjectsMap);

                Object componentBuilder = initComponent(componentClass, modules, moduleOverrider);

                componentBuilder = initComponentDependencies(componentBuilder, moduleOverrider);

                C component = (C) ReflectUtils.buildComponent(componentBuilder);

                component = new ComponentOverrider(moduleOverrider).override(componentClass, component);

                if (componentSetter != null) {
                    componentSetter.setComponent(component);
                }

                initInjectFromComponentFields(target, component);

                base.evaluate();

                Mockito.validateMockitoUsage();
            }
        };
    }

    private void checkOverridesInSubComponentsWithNoParameters(Class<?> componentClass, OverriddenObjectsMap overriddenObjectsMap) {
        HashSet<String> errors = new HashSet<>();
        checkOverridesInSubComponentsWithNoParameters(componentClass, overriddenObjectsMap, errors);
        ErrorsFormatter.throwExceptionOnErrors("Error while trying to override subComponents objects", errors);
    }

    private void checkOverridesInSubComponentsWithNoParameters(Class<?> componentClass, OverriddenObjectsMap overriddenObjectsMap, Set<String> errors) {
        Method[] methods = componentClass.getMethods();
        for (Method method : methods) {
            Subcomponent subComponentAnnotation = method.getReturnType().getAnnotation(Subcomponent.class);
            if (subComponentAnnotation != null) {
                Class<?>[] modules = subComponentAnnotation.modules();
                for (Class<?> module : modules) {
                    if (!existsParameter(method, module)) {
                        Method[] moduleMethods = module.getMethods();
                        for (Method moduleMethod : moduleMethods) {
                            if (!moduleMethod.getDeclaringClass().equals(Object.class) && overriddenObjectsMap.containsField(moduleMethod.getReturnType())) {
                                errors.add(moduleMethod.getReturnType().getName());
                            }
                        }
                    }
                }
                checkOverridesInSubComponentsWithNoParameters(method.getReturnType(), overriddenObjectsMap, errors);
            }
        }
    }

    private boolean existsParameter(Method method, Class<?> module) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (Class<?> parameterClass : parameterTypes) {
            if (parameterClass.equals(module)) {
                return true;
            }
        }
        return false;
    }

    private void initInjectFromComponentFields(Object target, C component) {
        List<Field> fields = ReflectUtils.extractAnnotatedFields(target, InjectFromComponent.class);
        for (Field field : fields) {
            Method m = ReflectUtils.getMethodReturning(component.getClass(), field.getType());
            if (m != null) {
                Object obj = ReflectUtils.invokeMethod(component, m);
                ReflectUtils.setFieldValue(target, field, obj);
            }
        }
    }

    private Object initComponent(Class componentClass, List<Object> modules, ModuleOverrider moduleOverrider) {
        try {
            Class<?> daggerComponent = ReflectUtils.getDaggerComponentClass(componentClass);
            Object builder = daggerComponent.getMethod("builder").invoke(null);
            for (Object module : modules) {
                Method setMethod = ReflectUtils.getSetterMethod(builder, module);
                builder = setMethod.invoke(builder, moduleOverrider.override(module));
            }
            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object initComponentDependencies(Object componentBuilder, ModuleOverrider moduleOverrider) {
        try {
            for (Map.Entry<Class, List<Object>> entry : dependencies.entrySet()) {
                Object componentDependencyBuilder = initComponent(entry.getKey(), entry.getValue(), moduleOverrider);
                Object componentDependency = ReflectUtils.buildComponent(componentDependencyBuilder);
                Method setMethod = ReflectUtils.getComponentSetterMethod(componentBuilder, componentDependency);
                componentBuilder = setMethod.invoke(componentBuilder, componentDependency);
            }
            return componentBuilder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public interface ComponentSetter<C> {
        void setComponent(C component);
    }
}
