package com.mschlauch.comfortreader;

import android.view.View;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

/**
 * Created by michael on 04.02.18.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class StringSegmenterTest {
    private FullscreenActivity mActivity;
    @Before
    public void setup() {
        mActivity = Robolectric.buildActivity(FullscreenActivity.class).create().get();
    }

    @Test
    public void getsegmenthtml() throws Exception {
        TextView contentView = (TextView) mActivity.findViewById(R.id.fullscreen_content);
        String first = contentView.getText().toString();
        mActivity.nextButtonClicked(contentView);
        String second = contentView.getText().toString();
        assertNotEquals(first,second);
      //  assert(mActivity.testMethod());
        //mActivity.playButtonClicked(mActivity.findViewById(R.id.fullscreen_content));
        //assertThat());
        assertEquals(2,2);
    }

}