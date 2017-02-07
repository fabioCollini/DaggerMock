package it.cosenonjaviste.daggermock.testmodule;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;

import it.cosenonjaviste.daggermock.DaggerMockRule;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Copyright © 2017 Orion Health. All rights reserved.
 */
@RunWith(JUnit4.class)
public class InitOnSetupMethodTest {

    private final DaggerMockRule<MyComponent> daggerMock = new DaggerMockRule<>(MyComponent.class, new TestModule())
            .set(new DaggerMockRule.ComponentSetter<MyComponent>() {
                @Override public void setComponent(MyComponent component) {
                    mainService = component.mainService();
                }
            });

    @Mock
    MyService myService;

    private MainService mainService;

    @Before
    public void setUp() {
        daggerMock.initMocks(this);
    }

    @Test
    public void initMocksOnSetupTest() {
        assertThat(mainService).isNotNull();
        assertThat(mainService.getMyService()).isSameAs(myService);
    }
}
