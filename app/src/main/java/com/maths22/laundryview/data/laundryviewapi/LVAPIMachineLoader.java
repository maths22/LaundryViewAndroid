package com.maths22.laundryview.data.laundryviewapi;

import com.google.common.collect.ImmutableMap;
import com.maths22.laundryview.data.APIException;
import com.maths22.laundryview.data.LaundryRoom;
import com.maths22.laundryview.data.Machine;
import com.maths22.laundryview.data.MachineLoader;
import com.maths22.laundryview.data.MachineType;
import com.maths22.laundryview.data.Status;

import java.io.Serializable;
import java.util.Collection;
import java.util.EnumMap;
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
public class LVAPIMachineLoader implements MachineLoader, Serializable {
    private final Provider<Machine> machineProvider;
    private final LVAPIClient client;

    @Inject
    public LVAPIMachineLoader(Provider<Machine> machineProvider, LVAPIClient client) {
        this.machineProvider = machineProvider;
        this.client = client;
    }

    @Override
    public Map<MachineType, Collection<Machine>> findMachines(LaundryRoom laundryRoom) throws APIException {
        Map<MachineType, Collection<Machine>> ret = new EnumMap<>(MachineType.class);


        Map<String, List<Map<String, Object>>> machines;
        machines = client.request("machineStatus", ImmutableMap.of("roomId", laundryRoom.getId()), Map.class);


        if (machines == null) {
            throw new APIException("Server error");
        }

        ret.put(MachineType.WASHER, readJsonSet(machines.get("washers")));
        ret.put(MachineType.DRYER, readJsonSet(machines.get("dryers")));

        return ret;
    }

    private Collection<Machine> readJsonSet(List<Map<String, Object>> machines)  {
        SortedSet<Machine> set = new TreeSet<>();
        for (Map<String, Object> m : machines) {
            Machine machine = machineProvider.get();
            machine.setId((String) m.get("id"));
            machine.setNumber((String) m.get("number"));
            machine.setStatus(Status.valueOf((String) m.get("status")));
            int remaining = ((Double)m.get("timeRemaining")).intValue();
            if (remaining >= 0) {
                machine.setTimeRemaining(remaining);
            } else {
                machine.setTimeRemaining(Machine.NO_TIME);
            }
            set.add(machine);

        }
        return set;
    }
}
