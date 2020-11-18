package com.maths22.laundryview.data.laundryviewapi;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Jacob on 1/19/2016.
 */
@Singleton
public class LVAPIClient implements Serializable {

    private final String endpoint;

    @Inject
    public LVAPIClient() {
        this.endpoint = "https://lvapi.maths22.com/lv_api";
    }

    public <T> T request(String method, Map<String, Object> args, Class<T> clazz) {
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();

        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("method", method);
            jsonObj.put("args", new JSONObject(args));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(JSON, jsonObj.toString());
        Request request = new Request.Builder()
                .url(this.endpoint)
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if(!response.isSuccessful()) return null;
            Gson gson = new Gson();


            return gson.fromJson(response.body().string(), clazz);
        } catch (IOException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
            return null;
        }
    }
}
