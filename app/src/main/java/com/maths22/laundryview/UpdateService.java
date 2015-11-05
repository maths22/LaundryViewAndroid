package com.maths22.laundryview;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.maths22.laundryview.data.APIException;
import com.maths22.laundryview.data.DataHandler;
import com.maths22.laundryview.data.LaundryRoom;
import com.maths22.laundryview.data.Machine;
import com.maths22.laundryview.data.MachineType;

import org.acra.ACRA;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class UpdateService extends IntentService {

    public UpdateService() {
        super("UpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPref = this.getSharedPreferences(
                this.getString(R.string.school_preference_file_key), Context.MODE_PRIVATE);
        Set<String> objs = new HashSet<>(sharedPref.getStringSet(this.getString(R.string.notify_preference_file_key),
                new HashSet<String>()));

        DataHandler handler = new DataHandler();
        Set<String> handled = new HashSet<>();
        for (String obj : objs) {
            boolean notify = false;
            MachineType type = MachineType.DRYER;
            Machine machine = null;

            String[] args = obj.split("\\|");
            String lrName = args[0];
            String lrId = args[1];
            String machineId = args[2];

            LaundryRoom lr = handler.getLaundryRoom();
            lr.setId(lrId);
            lr.setName(lrName);

            Collection<Machine> washers;
            try {
                washers = lr.getWashers();
            } catch (APIException e) {
                ACRA.getErrorReporter().handleSilentException(e);
                continue;
            }
            for (Machine m : washers) {
                if (m.getId().equals(machineId)) {
                    type = MachineType.WASHER;
                    machine = m;
                    switch (m.getStatus()) {
                        case AVAILBLE:
                        case DONE:
                            notify = true;
                        case IN_USE:
                        case OUT_OF_SERVICE:
                        case UNKNOWN:
                            break;
                    }
                    break;
                }
            }
            Collection<Machine> dryers;
            try {
                dryers = lr.getDryers();
            } catch (APIException e) {
                ACRA.getErrorReporter().handleSilentException(e);
                continue;
            }
            for (Machine m : dryers) {
                if (m.getId().equals(machineId)) {
                    type = MachineType.DRYER;
                    machine = m;
                    switch (m.getStatus()) {
                        case AVAILBLE:
                        case DONE:
                            notify = true;
                        case IN_USE:
                        case OUT_OF_SERVICE:
                        case UNKNOWN:
                            break;
                    }
                    break;
                }
            }

            if (notify) {
                PendingIntent notifyPIntent =
                        PendingIntent.getActivity(this.getApplicationContext(), 0, new Intent(), 0);

                NotificationCompat.Builder mBuilder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mBuilder =
                            new NotificationCompat.Builder(this)
                                    .setSmallIcon(R.drawable.washing_machine)
                                    .setAutoCancel(true)
                                    .setContentIntent(notifyPIntent)
                                    .setDefaults(Notification.DEFAULT_ALL);
                } else {
                    mBuilder =
                            new NotificationCompat.Builder(this)
                                    .setSmallIcon(R.drawable.washing_machine_fallback)
                                    .setAutoCancel(true)
                                    .setContentIntent(notifyPIntent)
                                    .setDefaults(Notification.DEFAULT_ALL);
                }


                switch (type) {
                    case WASHER:
                        mBuilder.setContentTitle("Washing Machine Done")
                                .setContentText(lr.getName() + ": Machine #" + machine.getNumber());
                        break;
                    case DRYER:
                        mBuilder.setContentTitle("Dryer Done")
                                .setContentText(lr.getName() + ": Machine #" + machine.getNumber());
                        break;
                }


                NotificationManager mNotificationManager =
                        (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(0, mBuilder.build());
                handled.add(obj);
            }

        }

        objs.removeAll(handled);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putStringSet(this.

                        getString(R.string.notify_preference_file_key), objs

        );
        editor.apply();
    }


}
