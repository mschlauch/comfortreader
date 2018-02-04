package com.mschlauch.comfortreader;

import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;



/**
 * Created by michael on 04.02.18.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DisplayTextTest {
    @Test
    public void testnextButtonClicked() throws Exception {
        FullscreenActivity readingscreen = new FullscreenActivity();
        onView(withId(R.id.nextbutton)).perform(click());
        onView(withId(R.id.nextbutton)).perform(click());
        onView(withId(R.id.nextbutton)).perform(click());
        onView(withId(R.id.nextbutton)).perform(click());
        onView(withId(R.id.nextbutton)).perform(click());
        onView(withId(R.id.nextbutton)).perform(click());
        onView(withId(R.id.nextbutton)).perform(click());
        onView(withId(R.id.fullscreen_content)).check(matches(withText("This is a test.")));
        onView(withId(R.id.fullscreen_content)).check(matches(isDisplayed()));
        assertEquals(2,1);
    }

}