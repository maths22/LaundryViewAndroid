package com.maths22.laundryview;

import android.content.Context;
import android.content.SharedPreferences;

import com.maths22.laundryview.data.LaundryRoom;
import com.maths22.laundryview.data.Machine;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by maths22 on 11/5/15.
 */
public class NotificationManager {
    Context c;

    public NotificationManager(Context context) {
        c = context;
    }

    public boolean notificationSet(LaundryRoom lr, Machine machine) {
        String record = lr.getName() + "|" + lr.getId() + "|" + machine.getId();
        SharedPreferences sharedPref = c.getSharedPreferences(
                c.getString(R.string.school_preference_file_key), Context.MODE_PRIVATE);
        Set<String> objs = new HashSet<>(sharedPref.getStringSet(c.getString(R.string.notify_preference_file_key),
                new HashSet<String>()));
        return objs.contains(record);
    }

    public void setNotification(LaundryRoom lr, Machine machine) {
        String record = lr.getName() + "|" + lr.getId() + "|" + machine.getId();
        SharedPreferences sharedPref = c.getSharedPreferences(
                c.getString(R.string.school_preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Set<String> objs = new HashSet<>(sharedPref.getStringSet(c.getString(R.string.notify_preference_file_key),
                new HashSet<String>()));

        objs.add(record);
        editor.putStringSet(c.getString(R.string.notify_preference_file_key), objs);
        editor.apply();
    }

    public void removeNotification(LaundryRoom lr, Machine machine) {
        String record = lr.getName() + "|" + lr.getId() + "|" + machine.getId();
        SharedPreferences sharedPref = c.getSharedPreferences(
                c.getString(R.string.school_preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Set<String> objs = new HashSet<>(sharedPref.getStringSet(c.getString(R.string.notify_preference_file_key),
                new HashSet<String>()));

        objs.remove(record);
        editor.putStringSet(c.getString(R.string.notify_preference_file_key), objs);
        editor.apply();
    }
}
