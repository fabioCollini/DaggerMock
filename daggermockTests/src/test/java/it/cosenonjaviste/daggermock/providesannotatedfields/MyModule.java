package it.cosenonjaviste.daggermock.providesannotatedfields;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import it.cosenonjaviste.daggermock.Qualifier1;
import it.cosenonjaviste.daggermock.Qualifier2;
import it.cosenonjaviste.daggermock.simple.MyService;

@Module
public class MyModule {
    @Provides @Named("service1") public MyService provideMyService1() {
        return new MyService();
    }

    @Provides @Named("service2") public MyService provideMyService2() {
        return new MyService();
    }

    @Provides @Qualifier1 public MyService provideMyService3() {
        return new MyService();
    }

    @Provides @Qualifier2 public MyService provideMyService4() {
        return new MyService();
    }
}
