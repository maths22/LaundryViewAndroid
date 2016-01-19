package com.maths22.laundryview.data.laundryviewapi;

import com.squareup.okhttp.ResponseBody;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by maths22 on 10/27/15.
 */
public interface LVAPIService {

    @GET("/findLaundryRooms/{schoolid}")
    Call<ResponseBody> findLaundryRooms(@Path("schoolid") String schoolId);

    @GET("/findSchools/{name}")
    Call<ResponseBody> findSchools(@Path("name") String name);

    @GET("/machineStatus/{roomId}")
    Call<ResponseBody> machineStatus(@Path("roomId") String roomId);
}
