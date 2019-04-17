package it.cosenonjaviste.daggermock.subcomponenterror;

import dagger.Module;
import dagger.Provides;

@Module
public class MySubModule2 {

    @Provides
    public Long provideMyLong() {
        return 12345678L;
    }
}
