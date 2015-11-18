package com.maths22.laundryview;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.maths22.laundryview.data.LaundryRoom;
import com.maths22.laundryview.data.Machine;
import com.maths22.laundryview.data.Status;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by maths22 on 10/27/15.
 */
public class MachineStatusArrayAdapter extends ArrayAdapter<Machine> {

    LaundryRoom lr;

    public MachineStatusArrayAdapter(Context context, List<Machine> users, LaundryRoom lr) {
        super(context, 0, users);
        this.lr = lr;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Machine machine = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_machine_status, parent, false);
        }
        // Lookup view for data population
        TextView tvNumber = (TextView) convertView.findViewById(R.id.number);
        TextView tvName = (TextView) convertView.findViewById(R.id.firstLine);
        TextView tvHome = (TextView) convertView.findViewById(R.id.secondLine);
        ImageView icon = (ImageView) convertView.findViewById(R.id.alertIcon);

        // Populate the data into the template view using the data object
        tvNumber.setText(machine.getNumber());

        switch (machine.getStatus()) {
            case AVAILBLE:
                ((GradientDrawable) tvNumber.getBackground()).setColor(ContextCompat.getColor(getContext(), R.color.greenAccent));
                break;
            case DONE:
                ((GradientDrawable) tvNumber.getBackground()).setColor(ContextCompat.getColor(getContext(), R.color.yellowAccent));
                break;
            case IN_USE:
                SharedPreferences sharedPref = getContext().getSharedPreferences(
                        getContext().getString(R.string.school_preference_file_key), Context.MODE_PRIVATE);
                Set<String> objs = new HashSet<>(sharedPref.getStringSet(getContext().getString(R.string.notify_preference_file_key),
                        new HashSet<String>()));

                final String record = lr.getName() + "|" + lr.getId() + "|" + machine.getId();
                if (objs.contains(record)) {
                    icon.setImageResource(R.drawable.ic_alarm_on_black_24dp);
                    icon.setAlpha(138);
                } else {
                    icon.setImageResource(R.drawable.ic_alarm_off_black_24dp);
                    icon.setAlpha(66);
                }


                ((GradientDrawable) tvNumber.getBackground()).setColor(ContextCompat.getColor(getContext(), R.color.redAccent));
                break;
            case OUT_OF_SERVICE:
                ((GradientDrawable) tvNumber.getBackground()).setColor(ContextCompat.getColor(getContext(), R.color.greyAccent));
                break;
            case UNKNOWN:
                break;
        }
        String status = getContext().getResources().getStringArray(R.array.machine_status_enum)[machine.getStatus().ordinal()];
        tvName.setText(status);
        if(machine.getStatus() == Status.IN_USE) {
            icon.setVisibility(View.VISIBLE);
        } else {
            icon.setVisibility(View.INVISIBLE);
        }

        if (machine.getTimeRemaining() != Machine.NO_TIME) {
            tvHome.setText(getContext().getResources().getQuantityString(R.plurals.time_remaining, machine.getTimeRemaining(), machine.getTimeRemaining()));
            tvHome.setVisibility(View.VISIBLE);
        } else {
            tvHome.setVisibility(View.GONE);
        }
        // Return the completed view to render on screen
        return convertView;
    }
}
