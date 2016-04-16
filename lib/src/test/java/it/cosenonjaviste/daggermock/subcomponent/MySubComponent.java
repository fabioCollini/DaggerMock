package it.cosenonjaviste.daggermock.subcomponent;

import dagger.Subcomponent;

@Subcomponent(modules = MySubModule.class)
public interface MySubComponent {
    MainService mainService();
}
