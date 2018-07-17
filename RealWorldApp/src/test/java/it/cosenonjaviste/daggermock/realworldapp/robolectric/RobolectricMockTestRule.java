package it.cosenonjaviste.daggermock.realworldapp.robolectric;

import org.robolectric.RuntimeEnvironment;

import it.cosenonjaviste.daggermock.DaggerMockRule;
import it.cosenonjaviste.daggermock.realworldapp.App;
import it.cosenonjaviste.daggermock.realworldapp.AppComponent;
import it.cosenonjaviste.daggermock.realworldapp.AppModule;

public class RobolectricMockTestRule extends DaggerMockRule<AppComponent> {

    public RobolectricMockTestRule() {
        super(AppComponent.class, new AppModule(getApplication()));
        set(new DaggerMockRule.ComponentSetter<AppComponent>() {
            @Override public void setComponent(AppComponent component) {
                getApplication().setComponent(component);
            }
        });
    }

    private static App getApplication() {
        return ((App) RuntimeEnvironment.application);
    }
}