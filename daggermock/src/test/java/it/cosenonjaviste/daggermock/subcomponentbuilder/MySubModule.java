package it.cosenonjaviste.daggermock.subcomponentbuilder;

import dagger.Module;
import dagger.Provides;

@Module
public class MySubModule {

//    private String param;
//
//    public MySubModule(String param) {
//        this.param = param;
//    }

    @Provides
    public Integer provideMyInteger() {
        return 12345;
    }
}
