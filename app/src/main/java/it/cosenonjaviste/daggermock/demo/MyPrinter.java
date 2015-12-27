package it.cosenonjaviste.daggermock.demo;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MyPrinter {

    @Inject public MyPrinter() {
    }

    public void print(String s) {
        System.out.println(s);
    }
}
