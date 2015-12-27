package it.cosenonjaviste.daggermock.simple;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = MyModule.class)
public interface MyComponent {
    MainService mainService();
}
