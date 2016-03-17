package it.cosenonjaviste.daggermock.subcomponent;

import dagger.Module;
import dagger.Provides;

@Module
public class MyModule2 {

    private String param;

    public MyModule2(String param) {
        this.param = param;
    }

    @Provides
    public Integer provideMyLong() {
        return 12345;
    }
}
