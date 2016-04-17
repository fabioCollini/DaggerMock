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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;

class OverriddenObjectsMap {
    private final Map<ObjectId, Provider> fields = new HashMap<>();

    public OverriddenObjectsMap(Object target, Map<ObjectId, Provider> extraObjects) {
        if (extraObjects != null) {
            fields.putAll(extraObjects);
        }
        ReflectUtils.extractFields(target, fields);
        checkOverriddenInjectAnnotatedClass();
    }

    private void checkOverriddenInjectAnnotatedClass() {
        Set<String> errors = new HashSet<>();
        for (Map.Entry<ObjectId, Provider> entry : fields.entrySet()) {
            ObjectId objectId = entry.getKey();
            Constructor[] constructors = objectId.objectClass.getConstructors();
            for (Constructor constructor : constructors) {
                if (constructor.getAnnotation(Inject.class) != null) {
                    errors.add(objectId.objectClass.getName());
                }
            }
        }
        ErrorsFormatter.throwExceptionOnErrors(
                "Error while trying to override objects",
                errors,
                "You must define overridden objects using a @Provides annotated method instead of using @Inject annotation");
    }

    public Provider getProvider(Method method) {
        Provider provider = fields.get(new ObjectId(method));
        if (provider == null) {
            provider = fields.get(new ObjectId(method.getReturnType()));
        }
        return provider;
    }

    public boolean containsField(Class<?> type) {
        for (ObjectId objectId : fields.keySet()) {
            if (objectId.objectClass.equals(type)) {
                return true;
            }
        }
        return false;
    }
}
