package com.maths22.laundryview.data;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;

/**
 * Created by maths22 on 10/27/15.
 */
public class LaundryRoom implements Comparable<LaundryRoom>, Serializable {
    private String id;
    private String name;
    private SortedSet<Machine> washers;
    private SortedSet<Machine> dryers;
    private MachineLoader loader;

    @Inject
    public LaundryRoom(MachineLoader loader) {
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
        washers = null;
        dryers = null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void refresh() throws APIException {
        loadWashersAndDryers();
    }

    private void loadWashersAndDryers() throws APIException {
        Map<MachineType, Collection<Machine>> machines = loader.findMachines(this);
        washers = new TreeSet<>(machines.get(MachineType.WASHER));
        dryers = new TreeSet<>(machines.get(MachineType.DRYER));
    }

    public SortedSet<Machine> getWashers() throws APIException {
        if (washers == null) {
            loadWashersAndDryers();
        }
        return Collections.unmodifiableSortedSet(washers);
    }

    public SortedSet<Machine> getDryers() throws APIException {
        if (dryers == null) {
            loadWashersAndDryers();
        }
        return Collections.unmodifiableSortedSet(dryers);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LaundryRoom)) {
            return false;
        }
        LaundryRoom lr = (LaundryRoom) o;
        return this.getId().equals(lr.getId());
    }

    @Override
    public int compareTo(@NonNull LaundryRoom another) {
        return this.getName().compareTo(another.getName());
    }
}
