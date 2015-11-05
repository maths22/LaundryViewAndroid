package com.maths22.laundryview.data;

import com.maths22.laundryview.data.laundryviewapi.LVAPIDataModule;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by maths22 on 10/28/15.
 */
public class DataHandler {

    @Inject
    School school;
    @Inject
    SchoolSearch searcher;
    @Inject
    LaundryRoom laundryRoom;

    @Singleton
    @Component(modules = LVAPIDataModule.class)
    public interface Data {
        void inject(DataHandler f);
    }

    private void configure() {
        Data data = DaggerDataHandler_Data.builder().lVAPIDataModule(new LVAPIDataModule()).build();
        data.inject(this);
    }

    public School getSchool() {
        if (school == null) {
            this.configure();
        }
        return school;
    }

    public SchoolSearch getSearcher() {
        if (searcher == null) {
            this.configure();
        }
        return searcher;
    }

    public LaundryRoom getLaundryRoom() {
        if (laundryRoom == null) {
            this.configure();
        }
        return laundryRoom;
    }

}
