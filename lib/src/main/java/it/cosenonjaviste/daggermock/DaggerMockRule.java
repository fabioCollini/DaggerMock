package it.cosenonjaviste.daggermock;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DaggerMockRule<C> implements MethodRule {
    protected Class<C> componentClass;
    private ComponentSetter<C> componentSetter;
    private List<Object> modules = new ArrayList<>();
    private final Map<Class, Object> overridenObjects = new HashMap<>();

    public DaggerMockRule(Class<C> componentClass, Object... modules) {
        this.componentClass = componentClass;
        for (int i = 0; i < modules.length; i++) {
            Object module = modules[i];
            this.modules.add(module);
        }
    }

    public DaggerMockRule<C> set(ComponentSetter<C> componentSetter) {
        this.componentSetter = componentSetter;
        return this;
    }

    public <S> DaggerMockRule<C> override(Class<S> originalClass, S newObject) {
        overridenObjects.put(originalClass, newObject);
        return this;
    }

    @Override public Statement apply(final Statement base, FrameworkMethod method, final Object target) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                MockitoAnnotations.initMocks(target);

                initComponent(target);

                base.evaluate();

                Mockito.validateMockitoUsage();
            }
        };
    }

    private void initComponent(Object target) {
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
                Method setMethod = getSetterMethod(builder, module);
                builder = setMethod.invoke(builder, mockOverrider.override(module));
            }
            C component = (C) builder.getClass().getMethod("build").invoke(builder);

            if (componentSetter != null) {
                componentSetter.setComponent(component);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Method getSetterMethod(Object builder, Object module) throws NoSuchMethodException {
        Class<?> moduleClass = module.getClass();
        while (true) {
            try {
                String moduleName = moduleClass.getSimpleName();
                String setterName = moduleName.substring(0, 1).toLowerCase() + moduleName.substring(1);
                return builder.getClass().getMethod(setterName, moduleClass);
            } catch (NoSuchMethodException e) {
                moduleClass = moduleClass.getSuperclass();
                if (moduleClass.equals(Object.class)) {
                    throw e;
                }
            }
        }
    }

    public interface ComponentSetter<C> {
        void setComponent(C component);
    }
}
