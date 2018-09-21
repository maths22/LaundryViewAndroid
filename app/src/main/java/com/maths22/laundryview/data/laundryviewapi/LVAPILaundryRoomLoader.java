package com.maths22.laundryview.data.laundryviewapi;

import com.appspot.laundryview_1197.laundryView.LaundryView;
import com.appspot.laundryview_1197.laundryView.model.LaundryRoomCollection;
import com.crashlytics.android.Crashlytics;
import com.maths22.laundryview.data.APIException;
import com.maths22.laundryview.data.LaundryRoom;
import com.maths22.laundryview.data.LaundryRoomLoader;
import com.maths22.laundryview.data.School;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Created by maths22 on 10/27/15.
 */
//TODO: Real error handling
public class LVAPILaundryRoomLoader implements LaundryRoomLoader, Serializable {
    private Provider<LaundryRoom> laundryRoomProvider;
    private LVAPIClient client;

    @Inject
    public LVAPILaundryRoomLoader(Provider<LaundryRoom> laundryRoomProvider, LVAPIClient client) {
        this.laundryRoomProvider = laundryRoomProvider;
        this.client = client;
    }

    @Override
    public Collection<LaundryRoom> findLaundryRooms(School school) throws APIException {

        LaundryView.LaundryViewEndpoint service = client.getService();

        SortedSet<LaundryRoom> set = new TreeSet<>();
        LaundryRoomCollection rooms;
        try {
            rooms = service.findLaundryRooms(school.getId()).execute();
            if (rooms == null) {
                throw new APIException("Server error");
            }
        } catch (IOException e) {
            Crashlytics.logException(e);
            throw new APIException(e);
        }

        for (com.appspot.laundryview_1197.laundryView.model.LaundryRoom room : rooms.getItems()) {
            LaundryRoom lr = laundryRoomProvider.get();
            lr.setId(room.getId());
            lr.setName(room.getName());
            set.add(lr);
        }
        return set;
    }
}
