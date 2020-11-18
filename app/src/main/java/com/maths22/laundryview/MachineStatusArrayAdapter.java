package com.maths22.laundryview;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
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

    final LaundryRoom lr;

    public MachineStatusArrayAdapter(Context context, List<Machine> users, LaundryRoom lr) {
        super(context, 0, users);
        this.lr = lr;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        final Machine machine = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_machine_status, parent, false);
        }
        final View myView = convertView;
        // Lookup view for data population
        TextView tvNumber = convertView.findViewById(R.id.number);
        TextView tvName = convertView.findViewById(R.id.firstLine);
        TextView tvHome = convertView.findViewById(R.id.secondLine);
        final SwitchCompat alertSwitch = convertView.findViewById(R.id.alertSwitch);
        final ImageView alertIcon = convertView.findViewById(R.id.alertIcon);

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
                        new HashSet<>()));

                final String record = lr.getName() + "|" + lr.getId() + "|" + machine.getId();
                alertSwitch.setChecked(objs.contains(record));

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
            alertSwitch.setVisibility(View.VISIBLE);
            alertIcon.setVisibility(View.VISIBLE);
        } else {
            alertSwitch.setVisibility(View.INVISIBLE);
            alertIcon.setVisibility(View.INVISIBLE);
        }

        if (machine.getTimeRemaining() != Machine.NO_TIME) {
            tvHome.setText(getContext().getResources().getQuantityString(R.plurals.time_remaining, machine.getTimeRemaining(), machine.getTimeRemaining()));
            tvHome.setVisibility(View.VISIBLE);
        } else {
            tvHome.setVisibility(View.GONE);
        }

        alertSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(buttonView.getTag() != null) {
                buttonView.setTag(null);
                return;
            }
            if (machine.getStatus() == Status.IN_USE) {
                final NotificationManager notifications = new NotificationManager(MachineStatusArrayAdapter.this.getContext());
                if (isChecked) {
                    MachineStatusArrayAdapter.this.setNotification(notifications, machine, alertSwitch);
                    Snackbar.make(myView, "Alert set for machine #" + machine.getNumber(), Snackbar.LENGTH_LONG)
                            .setAction("Undo", v -> {
                                alertSwitch.setChecked(false);
                                MachineStatusArrayAdapter.this.removeNotification(notifications, machine, alertSwitch);
                            }).show();
                } else {
                    MachineStatusArrayAdapter.this.removeNotification(notifications, machine, alertSwitch);
                    Snackbar.make(myView, "Alert removed for machine #" + machine.getNumber(), Snackbar.LENGTH_LONG)
                            .setAction("Undo", v -> {
                                alertSwitch.setChecked(true);
                                MachineStatusArrayAdapter.this.setNotification(notifications, machine, alertSwitch);
                            }).show();
                }
            }
        });
        // Return the completed view to render on screen
        return convertView;
    }

    private void setNotification(final NotificationManager notifications, final Machine machine, final SwitchCompat alertSwitch) {
        alertSwitch.setEnabled(false);
        new Thread(() -> {
            Handler handler = new Handler(Looper.getMainLooper());
            if(!notifications.setNotification(lr, machine)) {
                handler.post(() -> {
                    alertSwitch.setTag("no");
                    alertSwitch.setEnabled(true);
                    alertSwitch.setChecked(false);
                    AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage("Could not set notification.  Please try again later.");
                    alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                    alertDialog.show();
                });
            } else {
                handler.post(() -> alertSwitch.setEnabled(true));
            }
        }).start();

    }

    private void removeNotification(final NotificationManager notifications, final Machine machine, final SwitchCompat alertSwitch) {
        alertSwitch.setEnabled(false);
        new Thread(() -> {
            Handler handler = new Handler(Looper.getMainLooper());
            if(!notifications.removeNotification(lr, machine)) {
                handler.post(() -> {
                    alertSwitch.setTag("no");
                    alertSwitch.setEnabled(true);
                    alertSwitch.setChecked(true);
                    AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage("Could not unset notification.  Please try again later.");
                    alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                    alertDialog.show();
                });
            } else {
                handler.post(() -> alertSwitch.setEnabled(true));

            }
        }).start();

    }
}
