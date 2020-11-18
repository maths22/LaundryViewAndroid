package com.maths22.laundryview.data.laundryviewapi;

import com.google.common.collect.ImmutableMap;
import com.maths22.laundryview.data.APIException;
import com.maths22.laundryview.data.School;
import com.maths22.laundryview.data.SchoolSearch;

import java.io.Serializable;
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
public class LVAPISchoolSearch implements SchoolSearch, Serializable {

    private final Provider<School> schoolProvider;
    private final LVAPIClient client;

    @Inject
    public LVAPISchoolSearch(Provider<School> schoolProvider, LVAPIClient client) {
        this.schoolProvider = schoolProvider;
        this.client = client;
    }

    @Override
    public SortedSet<School> findSchools(String name) throws APIException {
        SortedSet<School> set = new TreeSet<>();

        List<Map<String, Object>> schools;
        schools = client.request("findSchools", ImmutableMap.of("name", name), List.class);
        if (schools == null) {
            throw new APIException("Server error");
        }


        for (Map<String, Object> school : schools) {
            School s = schoolProvider.get();
            s.setId((String) school.get("id"));
            s.setName((String) school.get("name"));
            set.add(s);

        }
        return set;
    }
}
