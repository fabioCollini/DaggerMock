package it.cosenonjaviste.daggermock.subcomponenterror;

import dagger.Module;
import dagger.Provides;

@Module
public class MySubModule3 {

    @Provides
    public Short provideMyShort() {
        return 2345;
    }
}
