package it.cosenonjaviste.daggermock.demo.v3;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import it.cosenonjaviste.daggermock.MockOverrider;
import it.cosenonjaviste.daggermock.demo.DaggerMyComponent;
import it.cosenonjaviste.daggermock.demo.MainService;
import it.cosenonjaviste.daggermock.demo.MyComponent;
import it.cosenonjaviste.daggermock.demo.MyModule;
import it.cosenonjaviste.daggermock.demo.MyPrinter;
import it.cosenonjaviste.daggermock.demo.RestService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MainServiceTest {

    @Rule public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock RestService restService;

    @Mock MyPrinter myPrinter;

    private MainService mainService;

    @Before
    public void setUp() throws Exception {
        MyComponent component = DaggerMyComponent.builder().myModule(new MockOverrider(this).override(new MyModule())).build();
        mainService = component.mainService();
    }

    @Test
    public void testDoSomething() {
        when(restService.doSomething()).thenReturn("abc");

        mainService.doSomething();

        verify(myPrinter).print("ABC");
    }
}