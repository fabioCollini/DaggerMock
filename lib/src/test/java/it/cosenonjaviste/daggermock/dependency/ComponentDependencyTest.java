package it.cosenonjaviste.daggermock.dependency;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;

import it.cosenonjaviste.daggermock.DaggerMockRule;

import static org.assertj.core.api.Assertions.assertThat;

public class ComponentDependencyTest {

    @Rule
    public final DaggerMockRule<MyComponent> mockitoRule = new DaggerMockRule<>(MyComponent.class, new MyModule())
            .addComponentDependency(MyComponent2.class, new MyModule2())
            .set(new DaggerMockRule.ComponentSetter<MyComponent>() {
                @Override public void setComponent(MyComponent component) {
                    mainService = component.mainService();
                }
            });

    @Mock
    MyService myService;
    @Mock
    MyService2 myService2;

    private MainService mainService;

    @Test
    public void testComponentDependencyModulesCanBeOverriden() {
        assertThat(mainService).isNotNull();
        assertThat(mainService.getMyService()).isSameAs(myService);
        assertThat(mainService.getMyService2()).isSameAs(myService2);
    }
}
