package it.cosenonjaviste.daggermock.subcomponent;

import dagger.Subcomponent;

@Subcomponent(modules = MyModule2.class)
public interface MySubComponent {
    MainService mainService();
}
