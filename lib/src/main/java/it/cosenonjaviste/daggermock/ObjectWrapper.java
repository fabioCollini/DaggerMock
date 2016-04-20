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
}
