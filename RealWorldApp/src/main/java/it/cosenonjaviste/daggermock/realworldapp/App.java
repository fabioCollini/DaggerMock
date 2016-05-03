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

package it.cosenonjaviste.daggermock.realworldapp;

import android.app.Application;
import android.support.annotation.VisibleForTesting;

public class App extends Application {
    private AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        component = createComponent();
    }

    protected AppComponent createComponent() {
        return DaggerAppComponent.builder().appModule(new AppModule(this)).build();
    }

    public AppComponent getComponent() {
        return component;
    }

    @VisibleForTesting
    public void setComponent(AppComponent component) {
        this.component = component;
    }
}
