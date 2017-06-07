package it.cosenonjaviste.daggermock.subcomponenterror;

import dagger.Module;
import dagger.Provides;

@Module
public class MySubModule2 {

    public static final long CONST_MY_LONG = 12345678L;

    @Provides
    public Long provideMyLong() {
        return CONST_MY_LONG;
    }
}
