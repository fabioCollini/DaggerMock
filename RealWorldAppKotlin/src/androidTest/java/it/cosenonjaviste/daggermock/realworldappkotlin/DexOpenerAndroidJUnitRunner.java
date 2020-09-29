package it.cosenonjaviste.daggermock.realworldappkotlin;

import android.app.Application;
import android.content.Context;

import androidx.test.runner.AndroidJUnitRunner;

import com.github.tmurakami.dexopener.DexOpener;

public class DexOpenerAndroidJUnitRunner extends AndroidJUnitRunner {
    @Override
    public Application newApplication(ClassLoader cl, String className, Context context)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        DexOpener.install(this);
        return super.newApplication(cl, "it.cosenonjaviste.daggermock.realworldappkotlin.App", context);
    }
}
