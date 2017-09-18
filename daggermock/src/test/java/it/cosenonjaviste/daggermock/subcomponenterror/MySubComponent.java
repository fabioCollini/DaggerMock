package it.cosenonjaviste.daggermock.subcomponenterror;

import dagger.Subcomponent;

@Subcomponent(modules = MySubModule.class)
public interface MySubComponent {
    MySubComponent2 mySubComponent2(MySubModule2 mySubModule2);
}
