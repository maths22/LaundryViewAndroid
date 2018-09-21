package com.maths22.laundryview;

import android.content.Context;
import android.content.SharedPreferences;

import com.crashlytics.android.Crashlytics;
import com.maths22.laundryview.data.LaundryRoom;
import com.maths22.laundryview.data.Machine;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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

    public boolean setNotification(LaundryRoom lr, Machine machine) {
        SharedPreferences sharedPref = c.getSharedPreferences(
                c.getString(R.string.school_preference_file_key), Context.MODE_PRIVATE);

        String requesterId = sharedPref.getString("firebase_token", null);
        String machineId = lr.getName() + "|" + lr.getId() + "|" + machine.getId();
        if(requesterId == null) return false;

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();

        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("requesterId", requesterId);
            jsonObj.put("machineId", machineId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(JSON, jsonObj.toString());
        Request request = new Request.Builder()
                .url("http://lvserver.maths22.com/registerMachine")
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if(!response.isSuccessful()) return false;
        } catch (IOException e) {
            Crashlytics.logException(e);
            return false;
        }


        SharedPreferences.Editor editor = sharedPref.edit();
        Set<String> objs = new HashSet<>(sharedPref.getStringSet(c.getString(R.string.notify_preference_file_key),
                new HashSet<String>()));

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

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();

        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("requesterId", requesterId);
            jsonObj.put("machineId", machineId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(JSON, jsonObj.toString());
        Request request = new Request.Builder()
                .url("http://lvserver.maths22.com/unregisterMachine")
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if(!response.isSuccessful()) return false;
        } catch (IOException e) {
            Crashlytics.logException(e);
            return false;
        }

        SharedPreferences.Editor editor = sharedPref.edit();
        Set<String> objs = new HashSet<>(sharedPref.getStringSet(c.getString(R.string.notify_preference_file_key),
                new HashSet<String>()));

        objs.remove(machineId);
        editor.putStringSet(c.getString(R.string.notify_preference_file_key), objs);
        editor.apply();
        return true;
    }
}
