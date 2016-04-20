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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

class ReflectUtils {
    public static Object buildComponent(Object builder) {
        try {
            return builder.getClass().getMethod("build").invoke(builder);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String toCamelCase(String str) {
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    public static Method getComponentSetterMethod(Object builder, Object component) throws NoSuchMethodException {
        Class<?> daggerComponentClass = component.getClass();
        String daggerComponentName = daggerComponentClass.getSimpleName();
        String componentName = daggerComponentName.replace("Dagger", "");
        String setterName = toCamelCase(componentName);
        return builder.getClass().getMethod(setterName, daggerComponentClass.getInterfaces()[0]);
    }

    public static Method getSetterMethod(Object builder, Object module) throws NoSuchMethodException {
        Class<?> moduleClass = module.getClass();
        while (true) {
            try {
                String moduleName = moduleClass.getSimpleName();
                String setterName = toCamelCase(moduleName);
                return builder.getClass().getMethod(setterName, moduleClass);
            } catch (NoSuchMethodException e) {
                moduleClass = moduleClass.getSuperclass();
                if (moduleClass.equals(Object.class)) {
                    throw e;
                }
            }
        }
    }

    public static List<Field> extractAnnotatedFields(Object target, Class<? extends Annotation> annotationClass) {
        List<Field> ret = new ArrayList<>();
        Field[] fields = target.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(annotationClass)) {
                ret.add(field);
            }
        }
        return ret;
    }

    public static Method getMethodReturning(Class<?> declaringClass, Class<?> returnClass) {
        Method[] methods = declaringClass.getMethods();
        for (Method method : methods) {
            if (method.getReturnType().equals(returnClass)) {
                return method;
            }
        }
        return null;
    }

    public static Method getMethodWithParameter(Class<?> declaringClass, Class<?> parameterClass) {
        Method[] methods = declaringClass.getMethods();
        for (Method method : methods) {
            Class<?>[] parameters = method.getParameterTypes();
            if (parameters.length == 1 && parameters[0].equals(parameterClass)) {
                return method;
            }
        }
        return null;
    }

    public static Object getFieldValue(Object obj, Class<?> fieldClass) {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getType().equals(fieldClass)) {
                field.setAccessible(true);
                try {
                    return field.get(obj);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }

    public static Object invokeMethod(Object component, Method m, Object... args) {
        try {
            return m.invoke(component, args);
        } catch (Exception e) {
            throw new RuntimeException("Error invoking method " + m, e);
        }
    }

    public static void setFieldValue(Object target, Field field, Object obj) {
        try {
            field.set(target, obj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error setting field " + field, e);
        }
    }

    public static Class<?> getDaggerComponentClass(Class componentClass) throws ClassNotFoundException {
        String packageName = componentClass.getPackage().getName();
        if (componentClass.isMemberClass()) {
            componentClass.getDeclaringClass();
            String declaringClass = componentClass.getDeclaringClass().getSimpleName();
            return Class.forName(packageName + ".Dagger" + declaringClass + "_" + componentClass.getSimpleName());
        } else {
            return Class.forName(packageName + ".Dagger" + componentClass.getSimpleName());
        }
    }

    public static <T> T newInstance(Class<T> classToInject) {
        try {
            return classToInject.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Error instantiating class " + classToInject.getName(), e);
        }
    }
}
