package com.maths22.laundryview.data.laundryviewapi;

import com.maths22.laundryview.data.LaundryRoomLoader;
import com.maths22.laundryview.data.MachineLoader;
import com.maths22.laundryview.data.SchoolSearch;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by maths22 on 10/27/15.
 */
@Module
public class LVAPIDataModule {
    @Provides
    @Singleton
    SchoolSearch provideSchoolSearch(LVAPISchoolSearch search) {
        return search;
    }

    @Provides
    @Singleton
    LaundryRoomLoader provideLaundryRoomLoader(LVAPILaundryRoomLoader loader) {
        return loader;
    }

    @Provides
    @Singleton
    MachineLoader provideMachineLoader(LVAPIMachineLoader loader) {
        return loader;
    }
}
