package it.cosenonjaviste.daggermock.simple;

import org.junit.Rule;
import org.junit.Test;

import it.cosenonjaviste.daggermock.DaggerMockRule;

import static org.assertj.core.api.Assertions.assertThat;

public class OverrideTest {
    @Rule public final DaggerMockRule<MyComponent> mockitoRule = new DaggerMockRule<>(MyComponent.class, new MyModule())
            .override(MyService.class, new MyServiceImpl())
            .set(new DaggerMockRule.ComponentSetter<MyComponent>() {
                @Override public void setComponent(MyComponent component) {
                    mainService = component.mainService();
                }
            });

    private MainService mainService;

    @Test
    public void testOverride() {
        assertThat(mainService).isNotNull();
        assertThat(mainService.getMyService()).isInstanceOf(MyServiceImpl.class);
    }
}
