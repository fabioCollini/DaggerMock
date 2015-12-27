package it.cosenonjaviste.daggermock.demo;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class MyModule {
    @Provides @Singleton public RestService provideRestService() {
        return new RestService();
    }

    @Provides @Singleton public MyPrinter provideMyPrinter() {
        return new MyPrinter();
    }

    @Provides MainService provideMainService(RestService restService, MyPrinter printer) {
        return new MainService(restService, printer);
    }
}
