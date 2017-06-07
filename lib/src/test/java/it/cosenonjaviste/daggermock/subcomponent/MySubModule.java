package it.cosenonjaviste.daggermock.subcomponent;

import dagger.Module;
import dagger.Provides;

@Module
public class MySubModule {
    public static final int CONST_MY_INT = 12345;

//    private String param;
//
//    public MySubModule(String param) {
//        this.param = param;
//    }

    @Provides
    public Integer provideMyInteger() {
        return CONST_MY_INT;
    }
}
