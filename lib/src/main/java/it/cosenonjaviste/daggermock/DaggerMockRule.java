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
import java.util.List;
import java.util.Map;

import javax.inject.Provider;

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
            @Override public Object get() {
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

    @Override public Statement apply(final Statement base, FrameworkMethod method, final Object target) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                MockitoAnnotations.initMocks(target);

                Object componentBuilder = initComponent(target, componentClass, modules);

                componentBuilder = initComponentDependencies(componentBuilder, target);

                C component = (C) ReflectUtils.buildComponent(componentBuilder);

                if (componentSetter != null) {
                    componentSetter.setComponent(component);
                }

                initInjectFromComponentFields(target, component);

                base.evaluate();

                Mockito.validateMockitoUsage();
            }
        };
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

    private Object initComponent(Object target, Class componentClass, List<Object> modules) {
        try {
            String packageName = componentClass.getPackage().getName();
            Class<?> daggerComponent;
            if (componentClass.isMemberClass()) {
                componentClass.getDeclaringClass();
                String declaringClass = componentClass.getDeclaringClass().getSimpleName();
                daggerComponent = Class.forName(packageName + ".Dagger" + declaringClass + "_" + componentClass.getSimpleName());
            } else {
                daggerComponent = Class.forName(packageName + ".Dagger" + componentClass.getSimpleName());
            }
            Object builder = daggerComponent.getMethod("builder").invoke(null);
            MockOverrider mockOverrider = new MockOverrider(target, overridenObjects);
            for (Object module : modules) {
                Method setMethod = ReflectUtils.getSetterMethod(builder, module);
                builder = setMethod.invoke(builder, mockOverrider.override(module));
            }
            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object initComponentDependencies(Object componentBuilder, Object target) {
        try {
            for (Map.Entry<Class, List<Object>> entry : dependencies.entrySet()) {
                Object componentDependencyBuilder = initComponent(target, entry.getKey(), entry.getValue());
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
