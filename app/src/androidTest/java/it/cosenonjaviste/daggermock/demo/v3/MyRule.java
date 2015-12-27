package it.cosenonjaviste.daggermock.demo.v3;

import android.support.test.InstrumentationRegistry;

import it.cosenonjaviste.daggermock.DaggerMockRule;
import it.cosenonjaviste.daggermock.demo.App;
import it.cosenonjaviste.daggermock.demo.MyComponent;
import it.cosenonjaviste.daggermock.demo.MyModule;

public class MyRule extends DaggerMockRule<MyComponent> {
    public MyRule() {
        super(MyComponent.class, new MyModule());
        set(new DaggerMockRule.ComponentSetter<MyComponent>() {
            @Override public void setComponent(MyComponent component) {
                App app = (App) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
                app.setComponent(component);
            }
        });
    }
}
