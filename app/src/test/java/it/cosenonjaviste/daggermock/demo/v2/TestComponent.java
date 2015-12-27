package it.cosenonjaviste.daggermock.demo.v2;


import javax.inject.Singleton;

import dagger.Component;
import it.cosenonjaviste.daggermock.demo.MyComponent;
import it.cosenonjaviste.daggermock.demo.MyModule;

@Singleton
@Component(modules = MyModule.class)
public interface TestComponent extends MyComponent {
    void inject(MainServiceDaggerTest test);
}
