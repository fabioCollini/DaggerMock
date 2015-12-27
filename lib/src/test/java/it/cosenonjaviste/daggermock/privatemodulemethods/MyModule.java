package it.cosenonjaviste.daggermock.privatemodulemethods;

import dagger.Module;
import dagger.Provides;

@Module
public class MyModule {
    @Provides MyService provideMyService() {
        return new MyService();
    }
}
