package it.cosenonjaviste.daggermock.providesannotatedfields;

import it.cosenonjaviste.daggermock.simple.MyService;

public class MyServiceImpl extends MyService {
    String name;

    public MyServiceImpl(String name) {
        this.name = name;
    }
}
