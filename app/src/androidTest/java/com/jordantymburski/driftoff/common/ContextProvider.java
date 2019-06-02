package com.jordantymburski.driftoff.common;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

public class ContextProvider {
    public static Context get() {
        return InstrumentationRegistry.getInstrumentation().getTargetContext();
    }
}
