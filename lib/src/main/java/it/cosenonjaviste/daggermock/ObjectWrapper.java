package it.cosenonjaviste.daggermock;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by fabiocollini on 20/04/16.
 */
public class ObjectWrapper<T> {
    private T obj;

    public ObjectWrapper(T obj) {
        this.obj = obj;
    }

    public Method getMethodReturning(Class<?> type) {
        return ReflectUtils.getMethodReturning(obj.getClass(), type);
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

    public Object getFieldValue(Class<?> fieldClass) {
        return ReflectUtils.getFieldValue(obj, fieldClass);
    }

    public ObjectWrapper<T> invokeBuilderSetter(Class<?> parameterClass, Object parameter) {
        try {
            Method setMethod = ReflectUtils.getSetterMethod(obj, parameterClass);
            return new ObjectWrapper<T>((T) setMethod.invoke(obj, parameter));
        } catch (Exception e) {
            throw new RuntimeException("Error invoking setter with parameter " + parameterClass + " on object " + obj, e);
        }
    }

    public static <T> ObjectWrapper<T> newInstance(Class<T> classToInject) {
        try {
            return new ObjectWrapper<>(classToInject.newInstance());
        } catch (Exception e) {
            throw new RuntimeException("Error instantiating class " + classToInject.getName(), e);
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
