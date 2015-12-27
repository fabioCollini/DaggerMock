package it.cosenonjaviste.daggermock.demo.v4;


import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;

import it.cosenonjaviste.daggermock.DaggerMockRule;
import it.cosenonjaviste.daggermock.demo.MainService;
import it.cosenonjaviste.daggermock.demo.MyComponent;
import it.cosenonjaviste.daggermock.demo.MyModule;
import it.cosenonjaviste.daggermock.demo.MyPrinter;
import it.cosenonjaviste.daggermock.demo.RestService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MainServiceTest {

    @Rule public final DaggerMockRule<MyComponent> mockitoRule = new DaggerMockRule<>(MyComponent.class, new MyModule())
            .set(new DaggerMockRule.ComponentSetter<MyComponent>() {
                @Override public void setComponent(MyComponent component) {
                    mainService = component.mainService();
                }
            });

//    @Rule public final DaggerMockRule<MyComponent> mockitoRule = new DaggerMockRule<>(MyComponent.class, new MyModule())
//            .set(component -> mainService = component.mainService());

    @Mock RestService restService;

    @Mock MyPrinter myPrinter;

    private MainService mainService;

    @Test
    public void testDoSomething() {
        when(restService.doSomething()).thenReturn("abc");

        mainService.doSomething();

        verify(myPrinter).print("ABC");
    }
}