package it.cosenonjaviste.daggermock.demo.v1;


import javax.inject.Singleton;

import dagger.Component;
import it.cosenonjaviste.daggermock.demo.MyComponent;
import it.cosenonjaviste.daggermock.demo.MyModule;

@Singleton
@Component(modules = MyModule.class)
public interface EspressoTestComponent extends MyComponent {
    void inject(MainActivityTest test);
}
