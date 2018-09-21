package com.maths22.laundryview.data.laundryviewapi;

import com.appspot.laundryview_1197.laundryView.LaundryView;
import com.appspot.laundryview_1197.laundryView.model.SchoolCollection;
import com.crashlytics.android.Crashlytics;
import com.maths22.laundryview.data.APIException;
import com.maths22.laundryview.data.School;
import com.maths22.laundryview.data.SchoolSearch;

import java.io.IOException;
import java.io.Serializable;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Created by maths22 on 10/27/15.
 */
//TODO: Real error handling
public class LVAPISchoolSearch implements SchoolSearch, Serializable {

    private Provider<School> schoolProvider;
    private LVAPIClient client;

    @Inject
    public LVAPISchoolSearch(Provider<School> schoolProvider, LVAPIClient client) {
        this.schoolProvider = schoolProvider;
        this.client = client;
    }

    @Override
    public SortedSet<School> findSchools(String name) throws APIException {
        LaundryView.LaundryViewEndpoint service = client.getService();

        SortedSet<School> set = new TreeSet<>();

        SchoolCollection schools;
        try {
            schools = service.findSchools(name).execute();
            if (schools == null) {
                throw new APIException("Server error");
            }
        } catch (IOException e) {
            Crashlytics.logException(e);
            throw new APIException(e);
        }

        if(schools.getItems() == null) return set;

        for (com.appspot.laundryview_1197.laundryView.model.School school : schools.getItems()) {
            School s = schoolProvider.get();
            s.setId(school.getId());
            s.setName(school.getName());
            set.add(s);

        }
        return set;
    }
}
