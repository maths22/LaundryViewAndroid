package com.maths22.laundryview.data.dummy;

import com.maths22.laundryview.data.LaundryRoom;
import com.maths22.laundryview.data.LaundryRoomLoader;
import com.maths22.laundryview.data.School;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;

/**
 * Created by maths22 on 10/27/15.
 */
public class DummyLaundryRoomLoader implements LaundryRoomLoader {
    private final LaundryRoom room;

    @Inject
    public DummyLaundryRoomLoader(LaundryRoom room) {
        this.room = room;
    }

    @Override
    public Collection<LaundryRoom> findLaundryRooms(School school) {
        LaundryRoom lr = room;
        lr.setId("297391");
        lr.setName("International House");
        SortedSet<LaundryRoom> set = new TreeSet<>();
        set.add(lr);
        return set;
    }
}
