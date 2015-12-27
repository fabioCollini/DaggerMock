package it.cosenonjaviste.daggermock.simple;

import dagger.Module;
import dagger.Provides;

@Module
public class MyModule {
    @Provides public MyService provideMyService() {
        return new MyService();
    }
}
