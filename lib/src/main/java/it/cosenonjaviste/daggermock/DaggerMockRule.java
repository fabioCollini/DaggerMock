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

import it.cosenonjaviste.daggermock.ComponentClassWrapper.SubComponentMethod;

public class DaggerMockRule<C> implements MethodRule {
    private ComponentClassWrapper<C> componentClass;
    private ComponentSetter<C> componentSetter;
    private List<Object> modules = new ArrayList<>();
    private final Map<ComponentClassWrapper<?>, List<Object>> dependencies = new HashMap<>();
    private final Map<Class<?>, ObjectWrapper<?>> dependenciesWrappers = new HashMap<>();
    private final Map<Class<?>, ComponentSetter<?>> dependentComponentsSetters = new HashMap<>();
    private final OverriddenObjectsMap overriddenObjectsMap = new OverriddenObjectsMap();

    public DaggerMockRule(Class<C> componentClass, Object... modules) {
        this.componentClass = new ComponentClassWrapper<>(componentClass);
        Collections.addAll(this.modules, modules);
    }

    public DaggerMockRule<C> set(ComponentSetter<C> componentSetter) {
        this.componentSetter = componentSetter;
        return this;
    }

    public <DC> DaggerMockRule<C> set(Class<DC> dependentComponentClass, ComponentSetter<DC> componentSetter) {
        dependentComponentsSetters.put(dependentComponentClass, componentSetter);
        return this;
    }

    public <S> DaggerMockRule<C> provides(Class<S> originalClass, final S newObject) {
        overriddenObjectsMap.put(originalClass, newObject);
        return this;
    }

    public <S> DaggerMockRule<C> provides(Class<S> originalClass, Provider<S> provider) {
        overriddenObjectsMap.putProvider(originalClass, provider);
        return this;
    }

    public DaggerMockRule<C> addComponentDependency(Class<?> componentClass, Object... modules) {
        dependencies.put(new ComponentClassWrapper<>(componentClass), Arrays.asList(modules));
        return this;
    }

    public DaggerMockRule<C> providesMock(final Class<?>... originalClasses) {
        overriddenObjectsMap.putMocks(originalClasses);
        return this;
    }

    @Override
    public Statement apply(final Statement base, FrameworkMethod method, final Object target) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                MockitoAnnotations.initMocks(target);

                overriddenObjectsMap.init(target);
                overriddenObjectsMap.checkOverriddenInjectAnnotatedClass();

                ModuleOverrider moduleOverrider = new ModuleOverrider(overriddenObjectsMap);

                overriddenObjectsMap.checkOverridesInSubComponentsWithNoParameters(componentClass);

                ObjectWrapper<Object> componentBuilder = initComponent(componentClass, modules, moduleOverrider);

                componentBuilder = initComponentDependencies(componentBuilder, moduleOverrider);

                C component = componentBuilder.invokeMethod("build");

                component = new ComponentOverrider(moduleOverrider).override(componentClass.getWrappedClass(), component);

                invokeSetters(component);

                initInjectFromComponentFields(new ObjectWrapper<>(target), new ObjectWrapper<>(component));

                base.evaluate();

                Mockito.validateMockitoUsage();
            }
        };
    }

    private void invokeSetters(C component) {
        if (componentSetter != null) {
            componentSetter.setComponent(component);
        }
        for (Map.Entry<Class<?>, ComponentSetter<?>> entry : dependentComponentsSetters.entrySet()) {
            ObjectWrapper objectWrapper = dependenciesWrappers.get(entry.getKey());
            ComponentSetter value = entry.getValue();
            value.setComponent(objectWrapper.getValue());
        }
    }

    private void initInjectFromComponentFields(ObjectWrapper<Object> target, ObjectWrapper<C> component) {
        List<Field> fields = target.extractAnnotatedFields(InjectFromComponent.class);
        for (Field field : fields) {
            InjectFromComponent annotation = field.getAnnotation(InjectFromComponent.class);
            Class<?>[] annotationValues = annotation.value();
            if (annotationValues.length == 0) {
                Object obj = getObjectFromComponentOrDependencies(component, field);
                if (obj != null) {
                    target.setFieldValue(field, obj);
                }
            } else {
                Class<Object> classToInject = (Class<Object>) annotationValues[0];
                ObjectWrapper<Object> obj = ObjectWrapper.newInstance(classToInject);
                injectObject(component, obj);
                for (int i = 1; i < annotationValues.length; i++) {
                    Class<?> c = annotationValues[i];
                    obj = new ObjectWrapper<>(obj.getFieldValue(c));
                }
                Object fieldValue = obj.getFieldValue(field.getType());
                target.setFieldValue(field, fieldValue);
            }
        }
    }

    private Object getObjectFromComponentOrDependencies(ObjectWrapper<C> component, Field field) {
        Method m = component.getMethodReturning(field.getType());
        if (m != null) {
            return component.invokeMethod(m);
        }
        for (ObjectWrapper<?> dependencyWrapper : dependenciesWrappers.values()) {
            m = dependencyWrapper.getMethodReturning(field.getType());
            if (m != null) {
                return dependencyWrapper.invokeMethod(m);
            }
        }
        return null;
    }

    private void injectObject(ObjectWrapper<C> component, ObjectWrapper<Object> obj) {
        Method injectMethod = component.getMethodWithParameter(obj.getValue().getClass());
        if (injectMethod != null) {
            component.invokeMethod(injectMethod, obj.getValue());
        } else {
            boolean injected = injectObjectUsingSubComponents(component, obj);
            if (!injected) {
                throw new RuntimeException("Inject method for class " + obj.getValue().getClass() +
                        " not found in component " + component.getValue().getClass() + " or in subComponents");
            }
        }
    }

    private boolean injectObjectUsingSubComponents(ObjectWrapper<C> component, ObjectWrapper<Object> obj) {
        ComponentClassWrapper<?> componentClassWrapper = new ComponentClassWrapper<>(component.getValue().getClass());
        List<SubComponentMethod<?>> subComponentMethods = componentClassWrapper.getSubComponentMethods();
        for (SubComponentMethod<?> subComponentMethod : subComponentMethods) {
            Method injectMethod = subComponentMethod.subComponentClassWrapper.getMethodWithParameter(obj.getValue().getClass());
            if (injectMethod != null) {
                ObjectWrapper<?> subComponent = subComponentMethod.createSubComponent(component);
                subComponent.invokeMethod(injectMethod, obj.getValue());
                return true;
            }
        }
        return false;
    }

    private ObjectWrapper<Object> initComponent(ComponentClassWrapper<?> componentClass, List<Object> modules, ModuleOverrider moduleOverrider) {
        Class<Object> daggerComponent = componentClass.getDaggerComponentClass();
        ObjectWrapper<Object> builderWrapper = ObjectWrapper.invokeStaticMethod(daggerComponent, "builder");
        for (Object module : modules) {
            builderWrapper = builderWrapper.invokeBuilderSetter(module.getClass(), moduleOverrider.override(module));
        }
        return builderWrapper;
    }

    private ObjectWrapper<Object> initComponentDependencies(ObjectWrapper<Object> componentBuilder, ModuleOverrider moduleOverrider) {
        for (Map.Entry<ComponentClassWrapper<?>, List<Object>> entry : dependencies.entrySet()) {
            ObjectWrapper<Object> componentDependencyBuilder = initComponent(entry.getKey(), entry.getValue(), moduleOverrider);
            Object componentDependency = componentDependencyBuilder.invokeMethod("build");
            Class<?> componentClass = entry.getKey().getWrappedClass();
            componentBuilder = componentBuilder.invokeBuilderSetter(componentClass, componentDependency);
            dependenciesWrappers.put(componentClass, new ObjectWrapper<>(componentDependency));
        }
        return componentBuilder;
    }

    public interface ComponentSetter<C> {
        void setComponent(C component);
    }
}
