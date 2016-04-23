package it.cosenonjaviste.daggermock.subcomponenterror;

import dagger.Module;
import dagger.Provides;

@Module
public class MySubModule {

    @Provides
    public Integer provideMyInteger() {
        return 12345;
    }
}
