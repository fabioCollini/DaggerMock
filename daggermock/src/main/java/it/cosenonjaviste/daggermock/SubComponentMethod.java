package it.cosenonjaviste.daggermock;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

class SubComponentMethod<T> {
    private final Method method;

    public final Class<?>[] modules;

    public final ComponentClassWrapper<T> subComponentClassWrapper;

    public SubComponentMethod(Method method, Class<?>[] modules, ComponentClassWrapper<T> subComponentClassWrapper) {
        this.method = method;
        this.modules = modules;
        this.subComponentClassWrapper = subComponentClassWrapper;
    }

    public boolean existsParameter(Class<?> module) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (Class<?> parameterClass : parameterTypes) {
            if (parameterClass.equals(module)) {
                return true;
            }
        }
        return false;
    }

    public ObjectWrapper<?> createSubComponent(ObjectWrapper<?> component, ModuleOverrider moduleOverrider) {
        Parameter[] parameters = method.getParameters();
        if (parameters.length == 0) {
            try {
                return new ObjectWrapper<>(method.invoke(component.getValue()));
            } catch (Exception e) {
                throw new RuntimeException("Error invoking method " + method + " on component " + component, e);
            }
        } else {
            Object[] args = moduleOverrider.instantiateModules(parameters);
            try {
                return new ObjectWrapper<>(method.invoke(component.getValue(), args));
            } catch (Exception e) {
                throw new RuntimeException("Error invoking method " + method + " on component " + component, e);
            }
        }
    }

}
