package it.cosenonjaviste.daggermock.subcomponenterror;

import org.junit.Rule;
import org.junit.Test;

import it.cosenonjaviste.daggermock.DaggerMockRule;

import static org.assertj.core.api.Assertions.assertThat;

public class NestedSubComponentTest {

    @Rule
    public final DaggerMockRule<MyComponent> rule = new DaggerMockRule<>(MyComponent.class, new MyModule())
            .set(new DaggerMockRule.ComponentSetter<MyComponent>() {
                @Override
                public void setComponent(MyComponent component) {
                    mainService = component.mySubComponent().mySubComponent2(new MySubModule2()).mySubComponent3().mainService();
                }
            });

    String s = "BBBB";

    Long l = 2L;

    MainService mainService;

    @Test
    public void testSubComponentError() throws Throwable {
        assertThat(mainService.getString()).isEqualTo("BBBB_12345_2_2345");
    }
}
