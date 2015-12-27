package it.cosenonjaviste.daggermock.demo;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = MyModule.class)
public interface MyComponent {
    MainService mainService();

    void inject(MainActivity mainActivity);
}
