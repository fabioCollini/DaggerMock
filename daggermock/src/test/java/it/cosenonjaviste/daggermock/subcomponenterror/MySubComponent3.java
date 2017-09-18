package it.cosenonjaviste.daggermock.subcomponenterror;

import dagger.Subcomponent;

@Subcomponent(modules = MySubModule3.class)
public interface MySubComponent3 {
    MainService mainService();
}
