package com.maths22.laundryview.data.dummy;

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
public class DummyDataModule {
    @Provides
    @Singleton
    SchoolSearch provideSchoolSearch(DummySchoolSearch search) {
        return search;
    }

    @Provides
    @Singleton
    LaundryRoomLoader provideLaundryRoomLoader(DummyLaundryRoomLoader loader) {
        return loader;
    }

    @Provides
    @Singleton
    MachineLoader provideMachineLoader(DummyMachineLoader loader) {
        return loader;
    }
}
