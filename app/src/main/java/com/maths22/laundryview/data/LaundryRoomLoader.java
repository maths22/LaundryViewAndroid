package com.maths22.laundryview.data;

import java.util.Collection;

/**
 * Created by maths22 on 10/27/15.
 */
public interface LaundryRoomLoader {
    Collection<LaundryRoom> findLaundryRooms(School school) throws APIException;
}
