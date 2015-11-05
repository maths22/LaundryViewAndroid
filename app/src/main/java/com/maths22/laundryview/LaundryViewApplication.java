package com.maths22.laundryview;

import android.app.Application;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

/**
 * Created by maths22 on 10/30/15.
 */

@ReportsCrashes(
        httpMethod = HttpSender.Method.PUT,
        reportType = HttpSender.Type.JSON,
        formUri = "http://pi.maths22.com:5984/acra-laundryview/_design/acra-storage/_update/report",
        formUriBasicAuthLogin = "defaultReporter",
        formUriBasicAuthPassword = "we3Evumu",
        additionalSharedPreferences = {"com.maths22.laundryview.school", "com.maths22.laundryview.notify"}
)
public class LaundryViewApplication extends Application {
    @Override
    public final void onCreate() {
        super.onCreate();
        ACRA.init(this);
    }
}
