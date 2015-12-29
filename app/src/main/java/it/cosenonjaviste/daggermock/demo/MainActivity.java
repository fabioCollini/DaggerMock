/*
 *  Copyright 2016 Fabio Collini.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
