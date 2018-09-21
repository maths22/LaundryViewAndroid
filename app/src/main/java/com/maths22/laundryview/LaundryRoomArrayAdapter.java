package com.maths22.laundryview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.maths22.laundryview.data.LaundryRoom;

import java.util.List;

/**
 * Created by maths22 on 10/27/15.
 */
public class LaundryRoomArrayAdapter extends ArrayAdapter<LaundryRoom> {

    public LaundryRoomArrayAdapter(Context context, List<LaundryRoom> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        LaundryRoom room = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_laundry_room, parent, false);
        }
        // Lookup view for data population
        TextView tvName = convertView.findViewById(R.id.firstLine);
        TextView tvHome = convertView.findViewById(R.id.secondLine);
        // Populate the data into the template view using the data object
        tvName.setText(room.getName());
        //tvHome.setText("TODO: Machine count");
        tvHome.setVisibility(View.GONE);
        // Return the completed view to render on screen
        return convertView;
    }
}
