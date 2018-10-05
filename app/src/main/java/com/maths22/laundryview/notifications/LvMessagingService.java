package com.maths22.laundryview.notifications;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.maths22.laundryview.R;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Jacob on 10/5/2017.
 */

public class LvMessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String refreshedToken) {
        // Get updated InstanceID token.
        Log.d(this.getClass().getName(), "Refreshed token: " + refreshedToken);

        SharedPreferences sharedPref = this.getSharedPreferences(
                this.getString(R.string.notify_preference_file_key), Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("firebase_token", refreshedToken);
        editor.apply();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            SharedPreferences sharedPref = this.getSharedPreferences(
                    this.getString(R.string.school_preference_file_key), Context.MODE_PRIVATE);
            Set<String> objs = new HashSet<>(sharedPref.getStringSet(this.getString(R.string.notify_preference_file_key),
                    new HashSet<>()));

            objs.remove(remoteMessage.getData().get("completed"));
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putStringSet(this.getString(R.string.notify_preference_file_key), objs);
            editor.apply();

            Log.d("MessagingService", "GOT A MESSAGE!");

        }
    }
}
