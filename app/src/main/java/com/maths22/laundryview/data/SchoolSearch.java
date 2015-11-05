package com.maths22.laundryview.data;

import java.util.SortedSet;

/**
 * Created by maths22 on 10/27/15.
 */
public interface SchoolSearch {
    SortedSet<School> findSchools(String name) throws APIException;
}
