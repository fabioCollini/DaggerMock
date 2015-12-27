package it.cosenonjaviste.daggermock.demo;

public class MainService {
    private RestService restService;
    private MyPrinter printer;

    public MainService(RestService restService, MyPrinter printer) {
        this.restService = restService;
        this.printer = printer;
    }

    public void doSomething() {
        String s = restService.doSomething();
        printer.print(s.toUpperCase());
    }
}
