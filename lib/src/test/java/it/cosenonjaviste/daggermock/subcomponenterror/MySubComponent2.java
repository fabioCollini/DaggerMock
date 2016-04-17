package it.cosenonjaviste.daggermock.subcomponenterror;

import dagger.Subcomponent;

@Subcomponent(modules = MySubModule2.class)
public interface MySubComponent2 {
    MySubComponent3 mySubComponent3();
}
