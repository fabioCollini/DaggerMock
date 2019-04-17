package it.cosenonjaviste.daggermock.dependency;

import dagger.Module;
import dagger.Provides;

@Module
public class MyModule2 {

    @Provides
    public MyService2 provideMyService2() {
        return new MyService2();
    }
}
