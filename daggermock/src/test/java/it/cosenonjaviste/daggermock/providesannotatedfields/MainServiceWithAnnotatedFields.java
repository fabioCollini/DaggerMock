package it.cosenonjaviste.daggermock.providesannotatedfields;

import javax.inject.Inject;
import javax.inject.Named;

import it.cosenonjaviste.daggermock.Qualifier1;
import it.cosenonjaviste.daggermock.Qualifier2;
import it.cosenonjaviste.daggermock.simple.MyService;

public class MainServiceWithAnnotatedFields {
    @Inject @Named("service1") public MyService myService;

    @Inject @Named("service2") public MyService myService2;

    public MyService myService3;

    public MyService myService4;

    @Inject public MainServiceWithAnnotatedFields(
            @Qualifier1 MyService myService3,
            @Qualifier2 MyService myService4) {
        this.myService3 = myService3;
        this.myService4 = myService4;
    }
}
