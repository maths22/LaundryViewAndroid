package com.maths22.laundryview.data.laundryviewapi;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import com.appspot.laundryview_1197.laundryView.LaundryView;

import java.io.IOException;
import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Jacob on 1/19/2016.
 */
@Singleton
public class LVAPIClient implements Serializable {
    transient private LaundryView.LaundryViewEndpoint service;

    @Inject
    public LVAPIClient() {
        this.service = initializeService();
    }

    private LaundryView.LaundryViewEndpoint initializeService() {
        LaundryView laundryView = new LaundryView(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null);
        return laundryView.laundryViewEndpoint();
    }

    public LaundryView.LaundryViewEndpoint getService() {
        return service;
    }

    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.service = initializeService();
    }
}
