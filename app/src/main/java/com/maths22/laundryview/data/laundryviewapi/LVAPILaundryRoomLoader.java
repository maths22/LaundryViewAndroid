package com.maths22.laundryview.data.laundryviewapi;

import com.appspot.laundryview_1197.laundryView.LaundryView;
import com.appspot.laundryview_1197.laundryView.model.LaundryRoomCollection;
import com.crashlytics.android.Crashlytics;
import com.google.common.collect.ImmutableMap;
import com.maths22.laundryview.data.APIException;
import com.maths22.laundryview.data.LaundryRoom;
import com.maths22.laundryview.data.LaundryRoomLoader;
import com.maths22.laundryview.data.School;

import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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

        SortedSet<LaundryRoom> set = new TreeSet<>();
        List<Map<String, Object>> rooms;
        rooms = client.request("findLaundryRooms", ImmutableMap.of("schoolId", school.getId()), List.class);
        if (rooms == null) {
            throw new APIException("Server error");
        }

        for (Map<String, Object> room : rooms) {
            LaundryRoom lr = laundryRoomProvider.get();
            lr.setId((String) room.get("id"));
            lr.setName((String) room.get("name"));
            set.add(lr);
        }
        return set;
    }
}
