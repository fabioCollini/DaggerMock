package it.cosenonjaviste.daggermock.demo.v2;

import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import it.cosenonjaviste.daggermock.demo.MainService;
import it.cosenonjaviste.daggermock.demo.MyPrinter;
import it.cosenonjaviste.daggermock.demo.RestService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MainServiceDaggerTest {

    @Inject RestService restService;

    @Inject MyPrinter myPrinter;

    @Inject MainService mainService;

    @Before
    public void setUp() throws Exception {
        TestComponent component = DaggerTestComponent.builder().myModule(new TestModule()).build();
        component.inject(this);
    }

    @Test
    public void testDoSomething() {
        when(restService.doSomething()).thenReturn("abc");

        mainService.doSomething();

        verify(myPrinter).print("ABC");
    }
}