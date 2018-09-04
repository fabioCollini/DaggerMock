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
    private ComponentClassWrapper<C> componentClass;
    private ComponentSetter<C> componentSetter;
    private BuilderCustomizer customizer;
    private List<Object> modules = new ArrayList<>();
    private final List<DependentComponentInfo> dependencies = new ArrayList<>();
    private final Map<Class<?>, ObjectWrapper<?>> dependenciesWrappers = new HashMap<>();
    private final Map<Class<?>, ComponentSetter<?>> dependentComponentsSetters = new HashMap<>();
    private final OverriddenObjectsMap overriddenObjectsMap = new OverriddenObjectsMap();
    private final Map<Class<?>, ObjectDecorator<?>> decorators = new HashMap<>();

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

    public <B> DaggerMockRule<C> customizeBuilder(BuilderCustomizer<B> customizer) {
        this.customizer = customizer;
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

    public DaggerMockRule<C> addComponentDependency(Class<?> childcomponentClass, Object... modules) {
        return addComponentDependency(componentClass.getWrappedClass(), childcomponentClass, modules);
    }

    public DaggerMockRule<C> addComponentDependency(Class<?> parentComponentClasses, Class<?> childComponentClasses, Object... modules) {
        dependencies.add(new DependentComponentInfo(parentComponentClasses, childComponentClasses, Arrays.asList(modules)));
        return this;
    }

    public DaggerMockRule<C> providesMock(final Class<?>... originalClasses) {
        overriddenObjectsMap.putMocks(originalClasses);
        return this;
    }

    public <M> DaggerMockRule<C> providesMock(final Class<M> originalClass, MockInitializer<M> initializer) {
        overriddenObjectsMap.putMock(originalClass, initializer);
        return this;
    }

    public <S> DaggerMockRule<C> decorate(Class<S> originalClass, ObjectDecorator<S> decorator) {
        decorators.put(originalClass, decorator);
        return this;
    }

    public void initMocks(Object target) {
        MockitoAnnotations.initMocks(target);

        setupComponent(target);

        Mockito.validateMockitoUsage();
    }

    @Override
    public Statement apply(final Statement base, FrameworkMethod method, final Object target) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                MockitoAnnotations.initMocks(target);

                setupComponent(target);

                base.evaluate();

                Mockito.validateMockitoUsage();
            }
        };
    }

    private void setupComponent(Object target) {
        ObjectWrapper<Object> targetWrapper = new ObjectWrapper<>(target);
        overriddenObjectsMap.redefineMocksWithInitializer(targetWrapper);

        overriddenObjectsMap.init(target);
        overriddenObjectsMap.checkOverriddenInjectAnnotatedClass(modules);

        ModuleOverrider moduleOverrider = new ModuleOverrider(overriddenObjectsMap);

        overriddenObjectsMap.checkOverridesInSubComponentsWithNoParameters(componentClass);

        ObjectWrapper<Object> componentBuilder = initComponent(componentClass, modules, moduleOverrider, decorators);

        componentBuilder = initComponentDependencies(componentClass.getWrappedClass(), componentBuilder, moduleOverrider);

        if (customizer != null) {
            componentBuilder = new ObjectWrapper<>(customizer.customize(componentBuilder.getValue()));
        }

        C component = componentBuilder.invokeMethod("build");

        component = new ComponentOverrider(moduleOverrider).override(componentClass.getWrappedClass(), component, decorators);

        invokeSetters(component);

        initInjectFromComponentFields(targetWrapper, new ObjectWrapper<>(component, componentClass.getWrappedClass()), moduleOverrider);
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

    private void initInjectFromComponentFields(ObjectWrapper<Object> target, ObjectWrapper<C> component, ModuleOverrider moduleOverrider) {
        List<Field> fields = target.extractAnnotatedFields(InjectFromComponent.class);
        for (Field field : fields) {
            InjectFromComponent annotation = field.getAnnotation(InjectFromComponent.class);
            Class<?>[] annotationValues = annotation.value();
            if (annotationValues.length == 0) {
                Object obj = getObjectFromComponentOrDependencies(component, new ObjectId(field));
                if (obj != null) {
                    target.setFieldValue(field, obj);
                }
            } else {
                Class<Object> classToInject = (Class<Object>) annotationValues[0];
                ObjectWrapper<Object> obj = ObjectWrapper.newInstance(classToInject,
                        "Error instantiating class " + classToInject.getName() + " defined as parameter in InjectFromComponent annotation");
                injectObject(component, obj, moduleOverrider);
                for (int i = 1; i < annotationValues.length; i++) {
                    Class<?> c = annotationValues[i];
                    Object fieldValue = obj.getFieldOrProviderOrLazyValue(c);
                    obj = new ObjectWrapper<>(fieldValue);
                }
                Object fieldValue = obj.getFieldOrProviderOrLazyValue(field.getType());
                target.setFieldValue(field, fieldValue);
            }
        }
    }

    private Object getObjectFromComponentOrDependencies(ObjectWrapper<C> component, ObjectId objectId) {
        Method m = component.getMethodReturning(objectId);
        if (m != null) {
            return component.invokeMethod(m);
        }
        for (ObjectWrapper<?> dependencyWrapper : dependenciesWrappers.values()) {
            m = dependencyWrapper.getMethodReturning(objectId);
            if (m != null) {
                return dependencyWrapper.invokeMethod(m);
            }
        }
        return null;
    }

    private void injectObject(ObjectWrapper<C> component, ObjectWrapper<Object> obj, ModuleOverrider moduleOverrider) {
        Method injectMethod = component.getMethodWithParameter(obj.getValue().getClass());
        if (injectMethod != null) {
            component.invokeMethod(injectMethod, obj.getValue());
        } else {
            boolean injected = injectObjectUsingSubComponents(component, obj, moduleOverrider);
            if (!injected) {
                throw new RuntimeException("Inject method for class " + obj.getValueClass().getName() +
                        " not found in component " + component.getValueClass().getName() + " or in subComponents");
            }
        }
    }

    private boolean injectObjectUsingSubComponents(ObjectWrapper<C> component, ObjectWrapper<Object> obj, ModuleOverrider moduleOverrider) {
        ComponentClassWrapper<?> componentClassWrapper = new ComponentClassWrapper<>(component.getValue().getClass());
        List<SubComponentMethod<?>> subComponentMethods = componentClassWrapper.getSubComponentMethods();
        for (SubComponentMethod<?> subComponentMethod : subComponentMethods) {
            Method injectMethod = subComponentMethod.subComponentClassWrapper.getMethodWithParameter(obj.getValue().getClass());
            if (injectMethod != null) {
                ObjectWrapper<?> subComponent = subComponentMethod.createSubComponent(component, moduleOverrider);
                subComponent.invokeMethod(injectMethod, obj.getValue());
                return true;
            }
        }
        List<SubComponentBuilderMethod<?>> subComponentBuilderMethods = componentClassWrapper.getSubComponentBuilderMethods();
        for (SubComponentBuilderMethod<?> subComponentMethod : subComponentBuilderMethods) {
            Method injectMethod = subComponentMethod.subComponentClassWrapper.getMethodWithParameter(obj.getValue().getClass());
            if (injectMethod != null) {
                ObjectWrapper<?> subComponent = subComponentMethod.createSubComponent(component, moduleOverrider);
                subComponent.invokeMethod(injectMethod, obj.getValue());
                return true;
            }
        }
        return false;
    }

    private ObjectWrapper<Object> initComponent(ComponentClassWrapper<?> componentClass, List<Object> modules, ModuleOverrider moduleOverrider,
                                                Map<Class<?>, DaggerMockRule.ObjectDecorator<?>> decorators) {
        Class<Object> daggerComponent = componentClass.getDaggerComponentClass();
        ObjectWrapper<Object> builderWrapper = ObjectWrapper.invokeStaticMethod(daggerComponent, "builder");
        for (Object module : modules) {
            builderWrapper = builderWrapper.invokeBuilderSetter(module.getClass(), moduleOverrider.override(module, decorators));
        }
        return builderWrapper;
    }

    private ObjectWrapper<Object> initComponentDependencies(Class<?> componentClass, ObjectWrapper<Object> componentBuilder, ModuleOverrider moduleOverrider) {
        for (DependentComponentInfo entry : dependencies) {
            if (entry.parentComponent.getWrappedClass().equals(componentClass)) {
                ObjectWrapper<Object> componentDependencyBuilder = initComponent(entry.childComponent, entry.modules, moduleOverrider, decorators);
                componentDependencyBuilder = initComponentDependencies(entry.childComponent.getWrappedClass(), componentDependencyBuilder, moduleOverrider);
                Object componentDependency = componentDependencyBuilder.invokeMethod("build");
                Class<?> componentClazz = entry.childComponent.getWrappedClass();
                componentBuilder = componentBuilder.invokeBuilderSetter(componentClazz, componentDependency);
                dependenciesWrappers.put(componentClazz, new ObjectWrapper<>(componentDependency));
            }
        }
        return componentBuilder;
    }

    public interface ComponentSetter<C> {
        void setComponent(C component);
    }

    public interface BuilderCustomizer<B> {
        B customize(B builder);
    }

    public interface MockInitializer<M> {
        void init(M mock);
    }

    public interface ObjectDecorator<T> {
        T decorate(T obj);
    }
}
