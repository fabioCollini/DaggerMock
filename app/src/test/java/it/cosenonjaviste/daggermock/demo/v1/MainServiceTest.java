package it.cosenonjaviste.daggermock.demo.v1;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import it.cosenonjaviste.daggermock.demo.MainService;
import it.cosenonjaviste.daggermock.demo.MyPrinter;
import it.cosenonjaviste.daggermock.demo.RestService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MainServiceTest {

    @Rule public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock RestService restService;

    @Mock MyPrinter myPrinter;

    @InjectMocks MainService mainService;

    @Test
    public void testDoSomething() {
        when(restService.doSomething()).thenReturn("abc");

        mainService.doSomething();

        verify(myPrinter).print("ABC");
    }
}