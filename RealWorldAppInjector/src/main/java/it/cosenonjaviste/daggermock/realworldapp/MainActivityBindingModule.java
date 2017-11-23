package it.cosenonjaviste.daggermock.realworldapp;

import android.app.Activity;

import dagger.Binds;
import dagger.Module;
import dagger.android.ActivityKey;
import dagger.android.AndroidInjector;
import dagger.multibindings.IntoMap;
import it.cosenonjaviste.daggermock.realworldapp.main.MainActivity;
import it.cosenonjaviste.daggermock.realworldapp.main.MainActivityComponent;

@Module(subcomponents = {
        MainActivityComponent.class
})
abstract public class MainActivityBindingModule {

    @Binds
    @IntoMap
    @ActivityKey(MainActivity.class)
    abstract AndroidInjector.Factory<? extends Activity> mainActivityInjectorFactory(
            MainActivityComponent.Builder builder);
}
