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

package it.cosenonjaviste.daggermock.qualifier;

import javax.inject.Inject;

import it.cosenonjaviste.daggermock.Qualifier1;
import it.cosenonjaviste.daggermock.Qualifier2;
import it.cosenonjaviste.daggermock.Qualifier3;

public class MainService {
    @Inject @Qualifier1 String s1;

    @Inject @Qualifier2 String s2;

    @Inject @Qualifier3 String s3;

    @Inject public MainService() {
    }

    public String get() {
        return s1 + s2 + s3;
    }
}
