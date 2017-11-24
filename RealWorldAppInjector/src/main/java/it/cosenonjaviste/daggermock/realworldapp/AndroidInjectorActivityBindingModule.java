package it.cosenonjaviste.daggermock.realworldapp;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import it.cosenonjaviste.daggermock.realworldapp.androidinjector.AndroidInjectorActivity;
import it.cosenonjaviste.daggermock.realworldapp.androidinjector.AndroidInjectorActivityModule;


@Module
public abstract class AndroidInjectorActivityBindingModule {

    @ContributesAndroidInjector(modules = AndroidInjectorActivityModule.class)
    abstract AndroidInjectorActivity bindAndroidInjectorActivity();
}
