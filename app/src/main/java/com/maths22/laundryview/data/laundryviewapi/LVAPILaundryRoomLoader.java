package com.maths22.laundryview.data.laundryviewapi;

import android.util.Log;

import com.maths22.laundryview.data.APIException;
import com.maths22.laundryview.data.LaundryRoom;
import com.maths22.laundryview.data.LaundryRoomLoader;
import com.maths22.laundryview.data.School;
import com.squareup.okhttp.ResponseBody;

import org.acra.ACRA;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
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
public class LVAPILaundryRoomLoader implements LaundryRoomLoader, Serializable {
    private Provider<LaundryRoom> laundryRoomProvider;

    @Inject
    public LVAPILaundryRoomLoader(Provider<LaundryRoom> laundryRoomProvider) {
        this.laundryRoomProvider = laundryRoomProvider;
    }

    @Override
    public Collection<LaundryRoom> findLaundryRooms(School school) throws APIException {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://brlcad.org:8081")
                .build();

        LVAPIService service = retrofit.create(LVAPIService.class);

        Call<ResponseBody> rooms = service.findLaundryRooms(school.getId());

        SortedSet<LaundryRoom> set = new TreeSet<>();

        ResponseBody rmstr = null;
        try {
            Response<ResponseBody> rsp = rooms.execute();
            if (!rsp.isSuccess()) {
                Log.w("laundryview", rsp.errorBody().string());
                throw new APIException("Server error");
            }
            rmstr = rsp.body();
        } catch (IOException e) {
            ACRA.getErrorReporter().handleException(e);
            throw new APIException(e.getCause());
        }

        JSONArray jsonList = null;
        try {
            jsonList = new JSONArray(rmstr.string());
        } catch (JSONException e) {
            throw new APIException(e.getCause());
        } catch (IOException e) {
            throw new APIException(e.getCause());
        }

        for (int i = 0; i < jsonList.length(); i++) {
            try {
                JSONObject obj = jsonList.getJSONObject(i);
                LaundryRoom lr = laundryRoomProvider.get();
                lr.setId(obj.getString("id"));
                lr.setName(obj.getString("name"));
                set.add(lr);
            } catch (JSONException e) {
                throw new APIException(e.getCause());
            }

        }
        return set;
    }
}
