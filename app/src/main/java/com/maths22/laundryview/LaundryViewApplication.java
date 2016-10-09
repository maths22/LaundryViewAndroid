package com.maths22.laundryview;

import android.app.Application;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;


/**
 * Created by maths22 on 10/30/15.
 */

@ReportsCrashes(
        httpMethod = HttpSender.Method.PUT,
        reportType = HttpSender.Type.JSON,
        formUri = "http://brlcad.org:5984/acra-laundryview/_design/acra-storage/_update/report",
        formUriBasicAuthLogin = "defaultReporter",
        formUriBasicAuthPassword = "we3Evumu",
        additionalSharedPreferences = {"com.maths22.laundryview.school", "com.maths22.laundryview.notify"}
)
public class LaundryViewApplication extends Application {
    private Tracker mTracker;

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }


    @Override
    public final void onCreate() {
        super.onCreate();
        ACRA.init(this);
    }
}
