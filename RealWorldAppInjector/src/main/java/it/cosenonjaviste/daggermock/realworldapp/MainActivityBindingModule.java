package it.cosenonjaviste.daggermock.realworldapp;

import dagger.Binds;
import dagger.Module;
import dagger.android.AndroidInjector;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;
import it.cosenonjaviste.daggermock.realworldapp.main.MainActivity;
import it.cosenonjaviste.daggermock.realworldapp.main.MainActivityComponent;

@Module(subcomponents = {
        MainActivityComponent.class
})
abstract public class MainActivityBindingModule {

    @Binds
    @IntoMap
    @ClassKey(MainActivity.class)
    abstract AndroidInjector.Factory<?> mainActivityInjectorFactory(
            MainActivityComponent.Builder builder);
}
