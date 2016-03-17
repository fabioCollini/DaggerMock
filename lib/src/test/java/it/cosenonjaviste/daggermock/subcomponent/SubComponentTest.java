package it.cosenonjaviste.daggermock.subcomponent;

import org.junit.Rule;
import org.junit.Test;

import it.cosenonjaviste.daggermock.DaggerMockRule;

public class SubComponentTest {

    @Rule
    public final DaggerMockRule<MyComponent> mockitoRule = new DaggerMockRule<>(MyComponent.class, new MyModule())
            .addComponentDependency(MySubComponent.class, new MyModule2())
            .set(new DaggerMockRule.ComponentSetter<MyComponent>() {
                @Override
                public void setComponent(MyComponent component) {
                    mainService = component.mySubComponent().mainService();
                }
            });

//    @Mock
    String s;
//    @Mock
    Integer i;

    private MainService mainService;

    @Test
    public void testSubComponent() throws Exception {
        System.out.println(mainService);

    }
}
