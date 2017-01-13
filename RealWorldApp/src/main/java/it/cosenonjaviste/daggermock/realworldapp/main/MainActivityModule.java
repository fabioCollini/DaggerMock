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

package it.cosenonjaviste.daggermock.realworldapp.main;

import dagger.Module;
import dagger.Provides;
import it.cosenonjaviste.daggermock.realworldapp.services.MainService;
import it.cosenonjaviste.daggermock.realworldapp.services.SnackBarManager;

@Module
public class MainActivityModule {

    private MainActivity mainActivity;

    public MainActivityModule(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Provides
    public SnackBarManager provideSnackBarManager() {
        return new SnackBarManager(mainActivity);
    }

    @Provides
    public MainView provideMainView() {
        return mainActivity;
    }

    @Provides
    public MainPresenter provideMainPresenter(MainService mainService, MainView view, SnackBarManager snackBarManager) {
        return new MainPresenter(mainService, view, snackBarManager);
    }
}
