package it.cosenonjaviste.daggermock.demo;

public class Main {
    public static void main(String[] args) {
        DaggerMyComponent.create().mainService().doSomething();
    }
}
