package it.cosenonjaviste.daggermock.privatemodulemethods;

import org.junit.Test;
import org.mockito.Mock;

import it.cosenonjaviste.daggermock.DaggerMockRule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

public class PrivateModuleMethodTest {

    @Mock MyService myService;

    @Test
    public void testErrorOnNotPublicMethods() throws Throwable {
        try {
            DaggerMockRule<MyComponent> rule = new DaggerMockRule<>(MyComponent.class, new MyModule());
            rule.apply(null, null, this).evaluate();
            fail();
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).isEqualTo("java.lang.RuntimeException: The following methods has to be public:\n" +
                    "it.cosenonjaviste.daggermock.privatemodulemethods.MyService it.cosenonjaviste.daggermock.privatemodulemethods.MyModule.provideMyService()");
        }
    }
}
