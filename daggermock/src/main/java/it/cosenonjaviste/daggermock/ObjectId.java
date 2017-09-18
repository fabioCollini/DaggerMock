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
import javax.inject.Named;
import javax.inject.Qualifier;

class ObjectId {
    public final Class objectClass;

    public final String name;

    public final Class<?> qualifierAnnotation;

    public ObjectId(Class objectClass) {
        this.objectClass = objectClass;
        name = null;
        qualifierAnnotation = null;
    }

    public ObjectId(Method method) {
        objectClass = method.getReturnType();
        name = extractName(method.getAnnotation(Named.class));
        qualifierAnnotation = extractQualifierAnnotation(method.getAnnotations());
    }

    public ObjectId(Field field) {
        objectClass = field.getType();
        name = extractName(field.getAnnotation(Named.class));
        qualifierAnnotation = extractQualifierAnnotation(field.getAnnotations());
    }

    private Class<?> extractQualifierAnnotation(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            Class<? extends Annotation> annotationType = annotation.annotationType();
            if (isQualifier(annotationType)) {
                return annotationType;
            }
        }
        return null;
    }

    private boolean isQualifier(Class<? extends Annotation> annotationType) {
        Annotation[] annotations = annotationType.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(Qualifier.class)) {
                return true;
            }
        }
        return false;
    }

    private String extractName(Named annotation) {
        if (annotation != null) {
            return annotation.value();
        } else {
            return null;
        }
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ObjectId objectId = (ObjectId) o;

        if (!objectClass.equals(objectId.objectClass)) return false;
        if (name != null ? !name.equals(objectId.name) : objectId.name != null) return false;
        return qualifierAnnotation != null ? qualifierAnnotation.equals(objectId.qualifierAnnotation) : objectId.qualifierAnnotation == null;
    }

    @Override public int hashCode() {
        int result = objectClass.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (qualifierAnnotation != null ? qualifierAnnotation.hashCode() : 0);
        return result;
    }
}
