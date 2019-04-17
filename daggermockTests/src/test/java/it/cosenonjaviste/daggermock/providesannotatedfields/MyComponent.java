package it.cosenonjaviste.daggermock.providesannotatedfields;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = MyModule.class)
public interface MyComponent {
    MainServiceWithAnnotatedFields mainService();
}
