package com.maths22.laundryview.data.dummy;

import com.maths22.laundryview.data.School;
import com.maths22.laundryview.data.SchoolSearch;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;

/**
 * Created by maths22 on 10/27/15.
 */
public class DummySchoolSearch implements SchoolSearch {

    private final School school;

    @Inject
    public DummySchoolSearch(School school) {
        this.school = school;
    }

    @Override
    public SortedSet<School> findSchools(String name) {
        School s = school;
        s.setId("2507");
        s.setName("University of Chicago");
        SortedSet<School> set = new TreeSet<>();
        set.add(s);
        return set;
    }
}
