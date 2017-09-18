package it.cosenonjaviste.daggermock;

import java.lang.reflect.Method;
import java.util.List;

class SubComponentBuilderMethod<T> {
    private final Method method;

    public final ComponentClassWrapper<T> subComponentClassWrapper;

    public SubComponentBuilderMethod(Method method, ComponentClassWrapper<T> subComponentClassWrapper) {
        this.method = method;
        this.subComponentClassWrapper = subComponentClassWrapper;
    }

    public ObjectWrapper<?> createSubComponent(ObjectWrapper<?> component, ModuleOverrider moduleOverrider) {
        ObjectWrapper<Object> builderWrapper;
        try {
            builderWrapper = new ObjectWrapper<>(method.invoke(component.getValue()));
        } catch (Exception e) {
            throw new RuntimeException("Error invoking method " + method + " on component " + component, e);
        }
        List<Method> builderMethods = builderWrapper.getAllMethodsReturning(method.getReturnType());
        Object builder = builderWrapper.getValue();
        for (Method builderMethod : builderMethods) {
            Class<?> moduleClass = builderMethod.getParameterTypes()[0];
            try {
                builder = builderMethod.invoke(builder, moduleOverrider.instantiateModule(moduleClass));
            } catch (Exception e) {
                throw new RuntimeException("Error invoking method " + builderMethod + " on component builder " + builder, e);
            }
        }
        return new ObjectWrapper<>(builderWrapper.invokeMethod("build"));
    }
}
