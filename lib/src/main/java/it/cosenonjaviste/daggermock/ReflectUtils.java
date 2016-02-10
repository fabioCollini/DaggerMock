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
import java.util.Map;

import javax.inject.Provider;

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

    public static void extractFields(Object target, Map<ObjectId, Provider> map) {
        Field[] fields = target.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                final Object value = field.get(target);
                if (value != null) {
                    map.put(new ObjectId(field), new Provider() {
                        @Override public Object get() {
                            return value;
                        }
                    });
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Error accessing field " + field, e);
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

    public static Object invokeMethod(Object component, Method m) {
        try {
            return m.invoke(component);
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
}
