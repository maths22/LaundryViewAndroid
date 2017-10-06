package com.maths22.laundryview.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.maths22.laundryview.R;
import com.maths22.laundryview.data.APIException;
import com.maths22.laundryview.data.DataHandler;
import com.maths22.laundryview.data.LaundryRoom;
import com.maths22.laundryview.data.Machine;
import com.maths22.laundryview.data.MachineType;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Jacob on 10/5/2017.
 */

public class LvMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            SharedPreferences sharedPref = this.getSharedPreferences(
                    this.getString(R.string.school_preference_file_key), Context.MODE_PRIVATE);
            Set<String> objs = new HashSet<>(sharedPref.getStringSet(this.getString(R.string.notify_preference_file_key),
                    new HashSet<String>()));

            objs.remove(remoteMessage.getData().get("completed"));
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putStringSet(this.getString(R.string.notify_preference_file_key), objs);
            editor.apply();

            Log.d("MessagingService", "GOT A MESSAGE!");

        }
    }
}
