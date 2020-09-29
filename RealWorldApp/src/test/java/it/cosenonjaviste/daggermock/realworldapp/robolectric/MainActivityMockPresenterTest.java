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

package it.cosenonjaviste.daggermock.realworldapp.robolectric;

import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import it.cosenonjaviste.daggermock.DaggerMockRule;
import it.cosenonjaviste.daggermock.realworldapp.AppComponent;
import it.cosenonjaviste.daggermock.realworldapp.main.MainActivity;
import it.cosenonjaviste.daggermock.realworldapp.main.MainPresenter;
import it.cosenonjaviste.daggeroverride.R;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21)
public class MainActivityMockPresenterTest {

    @Rule public final DaggerMockRule<AppComponent> rule = new RobolectricMockTestRule();

    @Mock MainPresenter presenter;

    @Test
    public void testOnCreate() {

        final ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);

        scenario.onActivity(activity -> {
                    doAnswer(invocation -> {
                        activity.showText("Hello mocked world");
                        return null;
                    }).when(presenter).loadData();

                    activity.findViewById(R.id.reload).performClick();

                    TextView textView = (TextView) activity.findViewById(R.id.text);
                    assertThat(textView.getText()).isEqualTo("Hello mocked world");
                }
        );
    }
}
