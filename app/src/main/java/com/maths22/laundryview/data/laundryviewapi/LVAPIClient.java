package com.maths22.laundryview.data.laundryviewapi;

import com.squareup.okhttp.OkHttpClient;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit.Retrofit;

/**
 * Created by Jacob on 1/19/2016.
 */
@Singleton
public class LVAPIClient implements Serializable {
    LVAPIService service;

    @Inject
    public LVAPIClient() {
        OkHttpClient client = new OkHttpClient();
        client.networkInterceptors().add(new MashapeInterceptor());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maths22-laundryviewapi-v1.p.mashape.com")
                .client(client)
                .build();
        service = retrofit.create(LVAPIService.class);
    }

    public LVAPIService getService() {
        return service;
    }
}
