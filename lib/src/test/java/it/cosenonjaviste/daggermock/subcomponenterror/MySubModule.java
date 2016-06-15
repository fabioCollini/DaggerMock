package it.cosenonjaviste.daggermock.subcomponenterror;

import dagger.Module;
import dagger.Provides;

@Module
public class MySubModule {

    public static final int CONST_MY_INT = 12345;

    @Provides
    public Integer provideMyInteger() {
        return CONST_MY_INT;
    }
}
