package it.cosenonjaviste.daggermock.subcomponenterror;

import dagger.Module;
import dagger.Provides;

@Module
public class MySubModule3 {

    public static final int CONST_MY_INT = 2345;

    @Provides
    public Short provideMyShort() {
        return CONST_MY_INT;
    }
}
