package it.cosenonjaviste.daggermock.innerclass;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;

import javax.inject.Singleton;

import dagger.Component;
import dagger.Module;
import dagger.Provides;
import it.cosenonjaviste.daggermock.DaggerMockRule;

import static org.assertj.core.api.Assertions.assertThat;

public class InnerClassesTest {
    @Rule public final DaggerMockRule<MyComponent> mockitoRule = new DaggerMockRule<>(MyComponent.class, new MyModule())
            .set(new DaggerMockRule.ComponentSetter<MyComponent>() {
                @Override public void setComponent(MyComponent component) {
                    mainService = component.mainService();
                }
            });

    @Mock MyService myService;

    private MainService mainService;

    @Test
    public void testInnerClasses() {
//        it.cosenonjaviste.daggermock.innerclass.InnerClassesTest$MyComponent
        assertThat(mainService).isNotNull();
        assertThat(mainService.getMyService()).isSameAs(myService);
    }

    @Singleton
    @Component(modules = MyModule.class)
    public interface MyComponent {
        MainService mainService();
    }

    @Module
    public static class MyModule {
        @Provides public MyService provideMyService() {
            return new MyService();
        }
    }
}
