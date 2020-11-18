package com.maths22.laundryview.data.dummy;

import com.maths22.laundryview.data.LaundryRoom;
import com.maths22.laundryview.data.Machine;
import com.maths22.laundryview.data.MachineLoader;
import com.maths22.laundryview.data.MachineType;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by maths22 on 10/27/15.
 */
public class DummyMachineLoader implements MachineLoader {
    private final Machine machine1;
    private final Machine machine2;

    @Inject
    public DummyMachineLoader(Machine machine1, Machine machine2) {
        this.machine1 = machine1;
        this.machine2 = machine2;
    }

    @Override
    public Map<MachineType, Collection<Machine>> findMachines(LaundryRoom laundryRoom) {
        return new EnumMap<>(MachineType.class);
    }
}
