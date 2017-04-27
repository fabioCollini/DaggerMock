package it.cosenonjaviste.daggermock.subcomponentbuilder;

import dagger.Subcomponent;

@Subcomponent(modules = MySubModule.class)
public interface MySubComponent {
    MainService mainService();

    @Subcomponent.Builder
    interface Builder {
        Builder mySubModule(MySubModule module);

        MySubComponent build();
    }
}
