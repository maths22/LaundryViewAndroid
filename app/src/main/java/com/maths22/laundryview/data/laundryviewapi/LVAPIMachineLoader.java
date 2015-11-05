package com.maths22.laundryview.data.laundryviewapi;

import android.util.Log;

import com.maths22.laundryview.data.APIException;
import com.maths22.laundryview.data.LaundryRoom;
import com.maths22.laundryview.data.Machine;
import com.maths22.laundryview.data.MachineLoader;
import com.maths22.laundryview.data.MachineType;
import com.maths22.laundryview.data.Status;
import com.squareup.okhttp.ResponseBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.inject.Provider;

import retrofit.Call;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by maths22 on 10/27/15.
 */
//TODO: Real error handling
public class LVAPIMachineLoader implements MachineLoader, Serializable {
    private Provider<Machine> machineProvider;

    @Inject
    public LVAPIMachineLoader(Provider<Machine> machineProvider) {
        this.machineProvider = machineProvider;
    }

    @Override
    public Map<MachineType, Collection<Machine>> findMachines(LaundryRoom laundryRoom) throws APIException {
        Map<MachineType, Collection<Machine>> ret = new EnumMap<>(MachineType.class);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://brlcad.org:8081")
                .build();

        LVAPIService service = retrofit.create(LVAPIService.class);

        Call<ResponseBody> machines = service.machineStatus(laundryRoom.getId());

        ResponseBody rmstr = null;
        try {
            Response<ResponseBody> rsp = machines.execute();
            if (!rsp.isSuccess()) {
                Log.w("laundryview", rsp.errorBody().string());
                throw new APIException("Server error");
            }
            rmstr = rsp.body();
        } catch (IOException e) {
            throw new APIException(e.getCause());
        }

        JSONObject jsonMap = null;
        try {
            jsonMap = new JSONObject(rmstr.string());
        } catch (JSONException | IOException e) {
            throw new APIException(e.getCause());
        }

        try {
            ret.put(MachineType.WASHER, readJsonSet(jsonMap.getJSONArray("washers")));
        } catch (JSONException e) {
            throw new APIException(e.getCause());
        }
        try {
            ret.put(MachineType.DRYER, readJsonSet(jsonMap.getJSONArray("dryers")));
        } catch (JSONException e) {
            throw new APIException(e.getCause());
        }

        return ret;
    }

    private Collection<Machine> readJsonSet(JSONArray jsonList) throws JSONException {
        SortedSet<Machine> set = new TreeSet<>();
        for (int i = 0; i < jsonList.length(); i++) {

            JSONObject obj = jsonList.getJSONObject(i);
            Machine machine = machineProvider.get();
            machine.setId(obj.getString("id"));
            machine.setNumber(obj.getString("number"));
            machine.setStatus(Status.valueOf(obj.getString("status")));
            if (obj.has("timeRemaining")) {
                machine.setTimeRemaining(obj.getInt("timeRemaining"));
            } else {
                machine.setTimeRemaining(Machine.NO_TIME);
            }
            set.add(machine);

        }
        return set;
    }
}
