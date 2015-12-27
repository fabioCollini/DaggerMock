package it.cosenonjaviste.daggermock.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import javax.inject.Inject;

import it.cosenonjaviste.daggeroverride.R;

public class MainActivity extends AppCompatActivity {

    @Inject MainService mainService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App app = (App) getApplication();
        System.out.println(app + " inject");
        MyComponent component = app.getComponent();
        System.out.println(component);
        component.inject(this);

        mainService.doSomething();

        TextView textView = new TextView(this);
        textView.setText(R.string.app_name);
        setContentView(textView);
    }
}
