package it.cosenonjaviste.daggermock.robolectric;

import androidx.test.core.app.ApplicationProvider;

import it.cosenonjaviste.daggermock.DaggerMockRule;
import it.cosenonjaviste.daggermock.realworldapp.App;
import it.cosenonjaviste.daggermock.realworldapp.AppComponent;
import it.cosenonjaviste.daggermock.realworldapp.AppModule;

public class RobolectricMockTestRule extends DaggerMockRule<AppComponent> {

    public RobolectricMockTestRule() {
        super(AppComponent.class, new AppModule());

        customizeBuilder((BuilderCustomizer<AppComponent.Builder>) builder ->
                builder.application(getApplication()));

        set(component -> component.inject(getApplication()));
    }

    private static App getApplication() {
        return ApplicationProvider.getApplicationContext();
    }
}