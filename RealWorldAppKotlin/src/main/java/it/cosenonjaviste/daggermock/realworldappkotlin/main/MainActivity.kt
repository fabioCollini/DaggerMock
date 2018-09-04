/*
 *   Copyright 2016 Fabio Collini.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package it.cosenonjaviste.daggermock.realworldappkotlin.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import it.cosenonjaviste.daggermock.realworldappkotlin.App
import it.cosenonjaviste.daggeroverride.R
import javax.inject.Inject

class MainActivity : AppCompatActivity(), MainView {

    @Inject lateinit var presenter: MainPresenter

    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as App
        app.component.mainActivityComponentBuilder().mainActivityModule(MainActivityModule(this)).build().inject(this)

        setContentView(R.layout.main)
        textView = findViewById(R.id.text) as TextView

        (findViewById(R.id.reload) as View).setOnClickListener { presenter.loadData() }
    }

    override fun showText(text: String) {
        textView.text = text
    }
}
