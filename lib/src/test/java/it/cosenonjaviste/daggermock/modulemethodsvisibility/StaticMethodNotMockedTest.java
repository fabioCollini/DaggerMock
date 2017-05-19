package it.cosenonjaviste.daggermock.modulemethodsvisibility;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;

import javax.inject.Singleton;

import dagger.Component;
import dagger.Module;
import dagger.Provides;
import it.cosenonjaviste.daggermock.DaggerMockRule;

import static org.junit.Assert.assertNotNull;

public class StaticMethodNotMockedTest {

    @Rule public final DaggerMockRule<MyComponent> rule = new DaggerMockRule<>(MyComponent.class, new MyModule());

    @Mock MyOtherService myOtherService;

    @Test
    public void testErrorOnStaticNotMockedMethods() throws Throwable {
        assertNotNull(myOtherService);
    }

    @Module
    public static class MyModule {
        @Provides public MyOtherService provideMyOtherService(MyService myService) {
            return new MyOtherService(myService);
        }

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

    public static class MyOtherService {
        public MyOtherService(MyService myService) {}
    }
}
