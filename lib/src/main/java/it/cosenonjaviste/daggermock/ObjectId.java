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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.inject.Named;

class ObjectId {
    public final Class objectClass;

    public final String name;

    public ObjectId(Class objectClass) {
        this.objectClass = objectClass;
        name = null;
    }

    public ObjectId(Method method) {
        this.objectClass = method.getReturnType();
        this.name = extractName(method.getAnnotation(Named.class));
    }

    public ObjectId(Field field) {
        this.objectClass = field.getType();
        this.name = extractName(field.getAnnotation(Named.class));
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
        return name != null ? name.equals(objectId.name) : objectId.name == null;

    }

    @Override public int hashCode() {
        int result = objectClass.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
