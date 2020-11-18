package com.maths22.laundryview.data;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;

/**
 * Created by maths22 on 10/27/15.
 */
public class School implements Comparable<School>, Serializable {
    private String id;
    private String name;
    private SortedSet<LaundryRoom> laundryRooms;
    private final LaundryRoomLoader loader;

    @Inject
    public School(LaundryRoomLoader loader) {
        this.loader = loader;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
        laundryRooms = null;
    }

    public void refresh() throws APIException {
        laundryRooms = new TreeSet<>(loader.findLaundryRooms(this));
    }

    public void setName(String name) {
        this.name = name;
    }

    public SortedSet<LaundryRoom> loadLaundryRooms() throws APIException {
        if (laundryRooms == null) {
            laundryRooms = new TreeSet<>(loader.findLaundryRooms(this));
        }
        return Collections.unmodifiableSortedSet(laundryRooms);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof School)) {
            return false;
        }
        School s = (School) o;
        return this.getId().equals(s.getId());
    }

    @Override
    public int compareTo(@NonNull School another) {
        return this.getName().compareTo(another.getName());
    }
}
