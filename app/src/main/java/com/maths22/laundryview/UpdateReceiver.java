package com.maths22.laundryview;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

public class UpdateReceiver extends WakefulBroadcastReceiver {
    public UpdateReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.school_preference_file_key), Context.MODE_PRIVATE);
        Set<String> objs = new HashSet<>(sharedPref.getStringSet(context.getString(R.string.notify_preference_file_key),
                new HashSet<String>()));

        if (objs.size() > 0) {
            // This is the Intent to deliver to our service.
            Intent service = new Intent(context, UpdateService.class);

            // Start the service, keeping the device awake while it is launching.
            Log.i("SimpleWakefulReceiver", "Starting service @ " + SystemClock.elapsedRealtime());
            startWakefulService(context, service);
        }


    }
}
