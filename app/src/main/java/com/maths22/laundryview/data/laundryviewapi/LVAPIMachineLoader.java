package com.maths22.laundryview.data.laundryviewapi;

import com.appspot.laundryview_1197.laundryView.LaundryView;
import com.appspot.laundryview_1197.laundryView.model.RoomMachineStatus;
import com.maths22.laundryview.data.APIException;
import com.maths22.laundryview.data.LaundryRoom;
import com.maths22.laundryview.data.Machine;
import com.maths22.laundryview.data.MachineLoader;
import com.maths22.laundryview.data.MachineType;
import com.maths22.laundryview.data.Status;

import org.acra.ACRA;

import java.io.IOException;
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
    private Provider<Machine> machineProvider;
    private LVAPIClient client;

    @Inject
    public LVAPIMachineLoader(Provider<Machine> machineProvider, LVAPIClient client) {
        this.machineProvider = machineProvider;
        this.client = client;
    }

    @Override
    public Map<MachineType, Collection<Machine>> findMachines(LaundryRoom laundryRoom) throws APIException {
        Map<MachineType, Collection<Machine>> ret = new EnumMap<>(MachineType.class);


        LaundryView.LaundryViewEndpoint service = client.getService();

        RoomMachineStatus machines;

        try {
            machines = service.machineStatus(laundryRoom.getId()).execute();
            if (machines == null) {
                throw new APIException("Server error");
            }
        } catch (IOException e) {
            ACRA.getErrorReporter().handleException(e);
            throw new APIException(e);
        }

        ret.put(MachineType.WASHER, readJsonSet(machines.getWashers()));
        ret.put(MachineType.DRYER, readJsonSet(machines.getDryers()));

        return ret;
    }

    private Collection<Machine> readJsonSet(List<com.appspot.laundryview_1197.laundryView.model.Machine> machines)  {
        SortedSet<Machine> set = new TreeSet<>();
        for (com.appspot.laundryview_1197.laundryView.model.Machine m : machines) {
            Machine machine = machineProvider.get();
            machine.setId(m.getId());
            machine.setNumber(m.getNumber());
            machine.setStatus(Status.valueOf(m.getStatus()));
            if (m.getTimeRemaining() >= 0) {
                machine.setTimeRemaining(m.getTimeRemaining());
            } else {
                machine.setTimeRemaining(Machine.NO_TIME);
            }
            set.add(machine);

        }
        return set;
    }
}
