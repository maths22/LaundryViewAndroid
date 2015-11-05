package com.maths22.laundryview.data.laundryviewapi;

import android.util.Log;

import com.maths22.laundryview.data.APIException;
import com.maths22.laundryview.data.School;
import com.maths22.laundryview.data.SchoolSearch;
import com.squareup.okhttp.ResponseBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
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
public class LVAPISchoolSearch implements SchoolSearch, Serializable {

    private Provider<School> schoolProvider;

    @Inject
    public LVAPISchoolSearch(Provider<School> schoolProvider) {
        this.schoolProvider = schoolProvider;
    }

    @Override
    public SortedSet<School> findSchools(String name) throws APIException {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://brlcad.org:8081/laundryview")
                .build();

        LVAPIService service = retrofit.create(LVAPIService.class);

        Call<ResponseBody> rooms = service.findSchools(name);

        SortedSet<School> set = new TreeSet<>();

        ResponseBody rmstr = null;
        try {
            Response<ResponseBody> rsp = rooms.execute();
            if (!rsp.isSuccess()) {
                Log.w("laundryview", rsp.errorBody().string());
                throw new APIException("Server error");
            }
            rmstr = rsp.body();
        } catch (IOException e) {
            throw new APIException(e.getCause());
        }

        JSONArray jsonList = null;
        try {
            jsonList = new JSONArray(rmstr.string());
        } catch (JSONException | IOException e) {
            throw new APIException(e.getCause());
        }

        for (int i = 0; i < jsonList.length(); i++) {
            try {
                JSONObject obj = jsonList.getJSONObject(i);
                School s = schoolProvider.get();
                s.setId(obj.getString("id"));
                s.setName(obj.getString("name"));
                set.add(s);
            } catch (JSONException e) {
                throw new APIException(e.getCause());
            }

        }
        return set;
    }
}
