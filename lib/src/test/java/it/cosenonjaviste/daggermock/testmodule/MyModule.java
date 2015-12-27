package it.cosenonjaviste.daggermock.testmodule;

import dagger.Module;
import dagger.Provides;

@Module
public class MyModule {
    @Provides public MyService provideMyService() {
        return new MyService();
    }
}
