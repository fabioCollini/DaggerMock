package it.cosenonjaviste.daggermock;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dagger.Provides;

public class MockOverrider {

    private final Map<Class, Object> fields;

    private final Set<Class> mocks = new HashSet<>();

    public MockOverrider(Object target) {
        this(target, null);
    }

    public MockOverrider(Object target, Map<Class, Object> extraObjects) {
        fields = new HashMap<>();
        if (extraObjects != null) {
            fields.putAll(extraObjects);
        }
        extractFields(target, fields, mocks);
    }

    public <T> T override(final T module) {
        checkMethodsVisibility(module, mocks);
        Answer defaultAnswer = new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Object mock = fields.get(invocation.getMethod().getReturnType());
                if (mock != null) {
                    return mock;
                } else {
                    return invocation.callRealMethod();
//                    return invocation.getMethod().invoke(module, invocation.getArguments());
                }
            }
        };
        return (T) Mockito.mock(module.getClass(), defaultAnswer);
    }

    public <T> T override(final Class<T> module) {
        checkMethodsVisibility(module, mocks);
        Answer defaultAnswer = new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Object mock = fields.get(invocation.getMethod().getReturnType());
                if (mock != null) {
                    return mock;
                } else {
                    return invocation.callRealMethod();
                }
            }
        };
        return Mockito.mock(module, defaultAnswer);
    }

    private <T> void checkMethodsVisibility(T module, Set<Class> mocks) {
        Method[] methods = module.getClass().getDeclaredMethods();
        List<Method> visibilityErrors = new ArrayList<>();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Provides.class) && mocks.contains(method.getReturnType())) {
                if (!Modifier.isPublic(method.getModifiers())) {
                    visibilityErrors.add(method);
                }
            }
        }
        if (!visibilityErrors.isEmpty()) {
            String message = "The following methods has to be public:";
            for (Method visibilityError : visibilityErrors) {
                message += "\n" + visibilityError;
            }
            throw new RuntimeException(message);
        }
    }
//    private <T> void checkMethodsVisibility(T module, Set<Class> mocks) {
//        HashSet<Class> mocksCopy = new HashSet<>(mocks);
//        Method[] methods = module.getClass().getMethods();
//        for (Method method : methods) {
//            if (method.isAnnotationPresent(Provides.class)) {
//                mocksCopy.remove(method.getReturnType());
//            }
//        }
//        if (!mocksCopy.isEmpty()) {
//            throw new RuntimeException("Mocks not");
//        }
//    }

    private static void extractFields(Object target, Map<Class, Object> map, Set<Class> mocks) {
        Field[] fields = target.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(target);
                if (value != null) {
                    map.put(field.getType(), value);
                    if (field.isAnnotationPresent(Mock.class) || field.isAnnotationPresent(Spy.class)) {
                        mocks.add(field.getType());
                    }
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Error accessing field " + field, e);
            }
        }
    }
}
