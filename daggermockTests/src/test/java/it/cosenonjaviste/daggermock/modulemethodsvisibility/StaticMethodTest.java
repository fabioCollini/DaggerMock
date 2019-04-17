package it.cosenonjaviste.daggermock.modulemethodsvisibility;

import org.junit.Test;
import org.mockito.Mock;

import javax.inject.Singleton;

import dagger.Component;
import dagger.Module;
import dagger.Provides;
import it.cosenonjaviste.daggermock.DaggerMockRule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

public class StaticMethodTest {

    @Mock MyService myService;

    @Test
    public void testErrorOnStaticMethods() throws Throwable {
        try {
            DaggerMockRule<MyComponent> rule = new DaggerMockRule<>(MyComponent.class, new MyModule());
            rule.apply(null, null, this).evaluate();
            fail();
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).isEqualTo("The following methods must be non static:\n" +
                    "public static it.cosenonjaviste.daggermock.modulemethodsvisibility.MyService it.cosenonjaviste.daggermock.modulemethodsvisibility.StaticMethodTest$MyModule.provideMyService()");
        }
    }

    @Module
    public static class MyModule {
        @Provides public static MyService provideMyService() {
            return new MyService();
        }

        private void privateMethod() {
        }
    }

    @Singleton
    @Component(modules = MyModule.class)
    public interface MyComponent {
        MainService mainService();
    }
}
