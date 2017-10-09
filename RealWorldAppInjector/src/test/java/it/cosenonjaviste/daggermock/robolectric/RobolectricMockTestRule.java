package it.cosenonjaviste.daggermock.robolectric;

import org.robolectric.RuntimeEnvironment;

import it.cosenonjaviste.daggermock.DaggerMockRule;
import it.cosenonjaviste.daggermock.realworldapp.App;
import it.cosenonjaviste.daggermock.realworldapp.AppComponent;
import it.cosenonjaviste.daggermock.realworldapp.AppModule;

public class RobolectricMockTestRule extends DaggerMockRule<AppComponent> {

    public RobolectricMockTestRule() {
        super(AppComponent.class, new AppModule());

        customizeBuilder(new BuilderCustomizer<AppComponent.Builder>() {
            @Override
            public AppComponent.Builder customize(AppComponent.Builder builder) {
                return builder.application(getApplication());
            }
        });

        set(new ComponentSetter<AppComponent>() {
            @Override
            public void setComponent(AppComponent component) {
                component.inject(getApplication());
            }
        });
    }

    private static App getApplication() {
        return ((App) RuntimeEnvironment.application);
    }
}