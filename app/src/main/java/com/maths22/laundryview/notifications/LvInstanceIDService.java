package com.maths22.laundryview.notifications;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.maths22.laundryview.R;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Jacob on 10/5/2017.
 */

public class LvInstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(this.getClass().getName(), "Refreshed token: " + refreshedToken);

        SharedPreferences sharedPref = this.getSharedPreferences(
                this.getString(R.string.school_preference_file_key), Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("firebase_token", refreshedToken);
        editor.apply();
    }
}
