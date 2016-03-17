package it.cosenonjaviste.daggermock.demo;

import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.test.runner.AndroidJUnitRunner;

public class TestRunner extends AndroidJUnitRunner
{
    @Override
    public void onCreate(Bundle arguments)
    {
        MultiDex.install(getTargetContext());
        super.onCreate(arguments);
    }
}