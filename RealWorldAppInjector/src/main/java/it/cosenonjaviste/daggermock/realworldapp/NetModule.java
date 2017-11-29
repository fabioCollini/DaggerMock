package it.cosenonjaviste.daggermock.realworldapp;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import it.cosenonjaviste.daggermock.realworldapp.services.RestService;

/**
 * Created by thomasschmidt on 23/11/2017.
 */
@Module
public class NetModule {

    @Provides
    @Singleton
    public RestService provideRestService(App app) {
        return new RestService(app);
    }
}
