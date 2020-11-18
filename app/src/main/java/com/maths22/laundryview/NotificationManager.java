package com.maths22.laundryview;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.common.collect.ImmutableMap;
import com.maths22.laundryview.data.LaundryRoom;
import com.maths22.laundryview.data.Machine;
import com.maths22.laundryview.data.laundryviewapi.LVAPIClient;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by maths22 on 11/5/15.
 */
public class NotificationManager {
    private final LVAPIClient client;
    final Context c;

    public NotificationManager(Context context) {
        client = new LVAPIClient();
        c = context;
    }

    public boolean notificationSet(LaundryRoom lr, Machine machine) {
        String record = lr.getName() + "|" + lr.getId() + "|" + machine.getId();
        SharedPreferences sharedPref = c.getSharedPreferences(
                c.getString(R.string.school_preference_file_key), Context.MODE_PRIVATE);
        Set<String> objs = new HashSet<>(sharedPref.getStringSet(c.getString(R.string.notify_preference_file_key),
                new HashSet<>()));
        return objs.contains(record);
    }

    public boolean setNotification(LaundryRoom lr, Machine machine) {
        SharedPreferences sharedPref = c.getSharedPreferences(
                c.getString(R.string.school_preference_file_key), Context.MODE_PRIVATE);

        String requesterId = sharedPref.getString("firebase_token", null);
        String machineId = lr.getName() + "|" + lr.getId() + "|" + machine.getId();
        if(requesterId == null) return false;


        if(client.request("registerMachine",
                ImmutableMap.of("requesterId", requesterId, "machineId", machineId),
                String.class) == null) {
            return false;
        }


        SharedPreferences.Editor editor = sharedPref.edit();
        Set<String> objs = new HashSet<>(sharedPref.getStringSet(c.getString(R.string.notify_preference_file_key),
                new HashSet<>()));

        objs.add(machineId);
        editor.putStringSet(c.getString(R.string.notify_preference_file_key), objs);
        editor.apply();
        return true;
    }

    public boolean removeNotification(LaundryRoom lr, Machine machine) {
        SharedPreferences sharedPref = c.getSharedPreferences(
                c.getString(R.string.school_preference_file_key), Context.MODE_PRIVATE);

        String requesterId = sharedPref.getString("firebase_token", null);
        String machineId = lr.getName() + "|" + lr.getId() + "|" + machine.getId();
        if(requesterId == null) return false;

        if(client.request("registerMachine",
                ImmutableMap.of("requesterId", requesterId, "machineId", machineId),
                String.class) == null) {
            return false;
        }

        SharedPreferences.Editor editor = sharedPref.edit();
        Set<String> objs = new HashSet<>(sharedPref.getStringSet(c.getString(R.string.notify_preference_file_key),
                new HashSet<>()));

        objs.remove(machineId);
        editor.putStringSet(c.getString(R.string.notify_preference_file_key), objs);
        editor.apply();
        return true;
    }
}
