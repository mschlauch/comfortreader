package com.mschlauch.comfortreader;


import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class FullscreenActivityTest {

    @Rule
    public ActivityTestRule<FullscreenActivity> mActivityTestRule = new ActivityTestRule<>(FullscreenActivity.class);

    @Test
    public void fullscreenActivityTest() {
    }

}
