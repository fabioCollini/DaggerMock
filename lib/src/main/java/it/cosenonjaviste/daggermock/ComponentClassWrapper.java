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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import dagger.Subcomponent;

public class ComponentClassWrapper<T> {
    private Class<T> wrappedClass;

    public ComponentClassWrapper(Class<T> wrappedClass) {
        this.wrappedClass = wrappedClass;
    }

    public Class<T> getWrappedClass() {
        return wrappedClass;
    }

    public List<SubComponentMethod<?>> getSubComponentMethods() {
        Method[] methods = wrappedClass.getMethods();
        List<SubComponentMethod<?>> ret = new ArrayList<>();
        for (int i = 0; i < methods.length; i++) {
            Method m = methods[i];
            Subcomponent subComponentAnnotation = m.getReturnType().getAnnotation(Subcomponent.class);
            if (subComponentAnnotation != null) {
                ret.add(new SubComponentMethod<>(m, subComponentAnnotation.modules(), new ComponentClassWrapper<>(m.getReturnType())));
            }
        }
        return ret;
    }

    public List<SubComponentBuilderMethod<?>> getSubComponentBuilderMethods() {
        Method[] methods = wrappedClass.getMethods();
        List<SubComponentBuilderMethod<?>> ret = new ArrayList<>();
        for (int i = 0; i < methods.length; i++) {
            Method m = methods[i];
            if (ReflectUtils.isSubComponentBuilder(m.getReturnType())) {
                ret.add(new SubComponentBuilderMethod<>(m, new ComponentClassWrapper<>(m.getReturnType().getEnclosingClass())));
            }
        }
        return ret;
    }

    public Method getMethodWithParameter(Class<?> parameterClass) {
        return ReflectUtils.getMethodWithParameter(wrappedClass, parameterClass);
    }

    public <T> Class<T> getDaggerComponentClass() {
        String packageName = wrappedClass.getPackage().getName();
        String className;
        if (wrappedClass.isMemberClass()) {
            String declaringClass = wrappedClass.getDeclaringClass().getSimpleName();
            className = packageName + ".Dagger" + declaringClass + "_" + wrappedClass.getSimpleName();
        } else {
            className = packageName + ".Dagger" + wrappedClass.getSimpleName();
        }
        try {
            return (Class<T>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Error searching class " + className, e);
        }
    }
}
