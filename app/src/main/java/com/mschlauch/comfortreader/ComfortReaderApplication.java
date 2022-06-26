package com.mschlauch.comfortreader;

import android.app.Application;

import com.github.stkent.amplify.feedback.DefaultEmailFeedbackCollector;
import com.github.stkent.amplify.feedback.GooglePlayStoreFeedbackCollector;
import com.github.stkent.amplify.logging.AndroidLogger;
import com.github.stkent.amplify.tracking.Amplify;

/**
 * Created by michael on 07.08.18.
 */

public class ComfortReaderApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Amplify.setLogger(new AndroidLogger());
        Amplify.initSharedInstance(this)
                .setPositiveFeedbackCollectors(new GooglePlayStoreFeedbackCollector())
                .setCriticalFeedbackCollectors(new DefaultEmailFeedbackCollector("support@rhizomaticdesign.net"))
                .applyAllDefaultRules();
        // .setAlwaysShow(BuildConfig.DEBUG);
    }
}
