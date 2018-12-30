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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import javax.inject.Provider;

import dagger.Lazy;

public class ObjectWrapper<T> {
    private T obj;
    private Class<T> valueClass;

    public ObjectWrapper(T obj) {
        this.obj = obj;
        valueClass = (Class<T>) obj.getClass();
    }

    public ObjectWrapper(T obj, Class<T> valueClass) {
        this.obj = obj;
        this.valueClass = valueClass;
    }

    public Method getMethodReturning(ObjectId objectId) {
        List<Method> methods = ReflectUtils.getAllMethodsReturning(obj.getClass(), objectId.objectClass);
        for (Method method : methods) {
            if (new ObjectId(method).equals(objectId)) {
                return method;
            }
        }
        return null;
    }

    public List<Method> getAllMethodsReturning(Class<?> type) {
        return ReflectUtils.getAllMethodsReturning(obj.getClass(), type);
    }

    public <C> C invokeMethod(String methodName) {
        Method m;
        try {
            m = obj.getClass().getMethod("build");
            m.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Method " + methodName + " not found in object " + obj, e);
        }
        return (C) invokeMethod(m);
    }

    public Object invokeMethod(Method m, Object... args) {
        return ReflectUtils.invokeMethod(obj, m, args);
    }

    public Method getMethodWithParameter(Class<?> parameterClass) {
        return ReflectUtils.getMethodWithParameter(obj.getClass(), parameterClass);
    }

    public void setFieldValue(Field field, Object fieldValue) {
        ReflectUtils.setFieldValue(obj, field, fieldValue);
    }

    public List<Field> extractAnnotatedFields(Class<? extends Annotation> annotationClass) {
        return ReflectUtils.extractAnnotatedFields(obj, annotationClass);
    }

    public T getValue() {
        return obj;
    }

    public Class<? super T> getValueClass() {
        return valueClass;
    }

    public Object getFieldValue(Class<?> fieldClass) {
        Field field = getField(fieldClass);
        if (field != null) {
            try {
                return field.get(obj);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public Object getProviderFieldValue(Class<?> fieldClass) {
        Field field = getWrapperField(fieldClass, Provider.class);
        if (field != null) {
            try {
                Provider<?> provider = (Provider<?>) field.get(obj);
                if (provider != null) {
                    return provider.get();
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public Object getLazyFieldValue(Class<?> fieldClass) {
        Field field = getWrapperField(fieldClass, Lazy.class);
        if (field != null) {
            try {
                Lazy<?> lazy = (Lazy<?>) field.get(obj);
                if (lazy != null) {
                    return lazy.get();
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public Field getWrapperField(Class<?> fieldClass, Class<?> wrapperClass) {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getType().equals(wrapperClass) && field.getGenericType() instanceof ParameterizedType) {
                Type[] actualTypeArguments = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
                if (actualTypeArguments.length == 1 && actualTypeArguments[0].equals(fieldClass)) {
                    field.setAccessible(true);
                    return field;
                }
            }
        }
        return null;
    }

    public Object getFieldOrProviderOrLazyValue(Class<?> c) {
        Object fieldValue = getFieldValue(c);
        if (fieldValue == null) {
            fieldValue = getProviderFieldValue(c);
            if (fieldValue == null) {
                fieldValue = getLazyFieldValue(c);
                if (fieldValue == null) {
                    throw new RuntimeException(c.getName() + " field not found in class " + getValueClass().getName() +
                            ", it's defined as parameter in InjectFromComponent annotation");
                }
            }
        }
        return fieldValue;
    }

    public Field getField(Class<?> fieldClass) {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getType().equals(fieldClass)) {
                field.setAccessible(true);
                return field;
            }
        }
        return null;
    }

    @android.support.annotation.RequiresApi(api = 24)
    public ObjectWrapper<T> invokeBuilderSetter(Class<?> parameterClass, Object parameter) {
        try {
            Class<?> aClass = obj.getClass();
            Method[] methods = aClass.getMethods();
            String name = parameterClass.getSimpleName();
            Method method = Arrays.stream(methods).filter(m -> m.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
            if(method != null) {
                method.setAccessible(true);
                return new ObjectWrapper<T>((T) method.invoke(obj, parameter));
            } else {
                Field[] declaredFields = aClass.getDeclaredFields();
                Field field = Arrays.stream(declaredFields).filter(m -> m.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
                if(field == null) throw new Exception("Method or field not found");
                field.setAccessible(true);
                field.set(obj, parameter);
                return new ObjectWrapper<>(obj);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error invoking setter with parameter " + parameterClass + " on object " + obj, e);
        }
    }


    public static Method getSetterMethod(Object builder, Class<?> moduleClass) throws NoSuchMethodException {
        while (true) {
            try {
                String moduleName = moduleClass.getSimpleName();
                String setterName = ReflectUtils.toCamelCase(moduleName);
                return builder.getClass().getMethod(setterName, moduleClass);
            } catch (NoSuchMethodException e) {
                moduleClass = moduleClass.getSuperclass();
                if (moduleClass.equals(Object.class)) {
                    throw e;
                }
            }
        }
    }

    public static <T> ObjectWrapper<T> newInstance(Class<T> classToInject) {
        return newInstance(classToInject, "Error instantiating class " + classToInject.getName());
    }

    public static <T> ObjectWrapper<T> newInstance(Class<T> classToInject, String message) {
        try {
            return new ObjectWrapper<>(classToInject.newInstance());
        } catch (Exception e) {
            throw new RuntimeException(message, e);
        }
    }

    public static <T> ObjectWrapper<T> invokeStaticMethod(Class<T> c, String methodName) {
        try {
            return new ObjectWrapper<T>((T) c.getMethod(methodName).invoke(null));
        } catch (Exception e) {
            throw new RuntimeException("Error while invoking static method " + methodName + " on class " + c, e);
        }
    }
}
