package com.maths22.laundryview.data;

import java.util.Collection;
import java.util.Map;

/**
 * Created by maths22 on 10/27/15.
 */
public interface MachineLoader {
    Map<MachineType, Collection<Machine>> findMachines(LaundryRoom laundryRoom) throws APIException;
}
