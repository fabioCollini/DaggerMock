package it.cosenonjaviste.daggermock.demo;

import android.app.Application;
import android.support.annotation.VisibleForTesting;

public class App extends Application {
    private MyComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        component = DaggerMyComponent.create();
    }

    public MyComponent getComponent() {
        return component;
    }

    @VisibleForTesting
    public void setComponent(MyComponent component) {
        this.component = component;
    }
}
