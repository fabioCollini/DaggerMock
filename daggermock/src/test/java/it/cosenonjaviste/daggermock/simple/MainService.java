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

package it.cosenonjaviste.daggermock.simple;

import javax.inject.Inject;

public class MainService {
    private MyService myService;

    @Inject public MyService2 myService2;

    @Inject public MainService(MyService myService) {
        this.myService = myService;
    }

    public MyService getMyService() {
        return myService;
    }

    public String get() {
        return myService.get();
    }

    public String get2() {
        return myService.get() + myService2.get();
    }

    public String getWithParam(int param) {
        return myService.getWithParam(param);
    }
}
