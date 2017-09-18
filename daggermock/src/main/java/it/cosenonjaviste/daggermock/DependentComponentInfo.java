/*
 *   Copyright 2016 Fabio Collini.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package it.cosenonjaviste.daggermock;

import java.util.List;

class DependentComponentInfo {
    public ComponentClassWrapper<?> parentComponent;

    public ComponentClassWrapper<?> childComponent;

    public List<Object> modules;

    public DependentComponentInfo(Class<?> parentComponentClass, Class<?> childComponentClass, List<Object> modules) {
        this.parentComponent = new ComponentClassWrapper<>(parentComponentClass);
        this.childComponent = new ComponentClassWrapper<>(childComponentClass);
        this.modules = modules;
    }
}
