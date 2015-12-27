package it.cosenonjaviste.daggermock.testmodule;

public class TestModule extends MyModule {
    public MyService provideMyService() {
        return new MyService();
    }
}
