package com.maths22.laundryview;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.maths22.laundryview.data.APIException;
import com.maths22.laundryview.data.DataHandler;
import com.maths22.laundryview.data.LaundryRoom;
import com.maths22.laundryview.data.School;

import org.acra.ACRA;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LaundryRoomChooser extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    public final static String EXTRA_MESSAGE = "com.maths22.laundryview.MESSAGE";

    private DataHandler dataHandler;
    private Tracker mTracker;

    @Bind(R.id.refreshLaundryRoomLayout)
    SwipeRefreshLayout refreshLaundryRoomLayout;

    @Bind(R.id.laundryRoomListView)
    ListView laundryRoomsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laundry_room_chooser);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                Intent intent = new Intent(LaundryRoomChooser.this, SchoolFinder.class);
                startActivity(intent);
            }
        });*/
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        refreshLaundryRoomLayout.setOnRefreshListener(this);

        laundryRoomsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view,
                                    int position, long id) {
                LaundryRoomArrayAdapter arrayAdapter = (LaundryRoomArrayAdapter) parent.getAdapter();
                LaundryRoom room = arrayAdapter.getItem(position);
                SharedPreferences sharedPref = getSharedPreferences(
                        getString(R.string.school_preference_file_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("laundry_room_id", room.getId());
                editor.putString("laundry_room_name", room.getName());
                editor.apply();


                Intent intent = new Intent(LaundryRoomChooser.this, MachineStatus.class);
                startActivity(intent);

            }
        });

        dataHandler = new DataHandler();

        Intent intentUpdate = new Intent(this, UpdateReceiver.class);

        // create the object
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //set the alarm for particular time
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                5000, 60000,
                PendingIntent.getBroadcast(this, 1, intentUpdate, PendingIntent.FLAG_UPDATE_CURRENT));

        // Obtain the shared Tracker instance.
        LaundryViewApplication application = (LaundryViewApplication) getApplication();
        mTracker = application.getDefaultTracker();
    }

    private static final String name = "LaundryRoomChooser";

    @Override
    public void onResume() {
        super.onResume();
        View currentFocus = this.getCurrentFocus();
        if (currentFocus != null) {
            currentFocus.clearFocus();
        }
        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.school_preference_file_key), Context.MODE_PRIVATE);

        Log.i("laundryview", "Setting screen name: " + name);
        mTracker.setScreenName("Image~" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        if (sharedPref.contains("school_id")) {
            dataHandler.getSchool().setId(sharedPref.getString("school_id", "0"));
            dataHandler.getSchool().setName(sharedPref.getString("school_name", ""));
            setTitle(dataHandler.getSchool().getName());
            if (sharedPref.contains("laundry_room_id")) {
                Intent intent = new Intent(LaundryRoomChooser.this, MachineStatus.class);
                startActivity(intent);
            } else {
                this.refresh();
            }
        } else {
            Intent intent = new Intent(LaundryRoomChooser.this, SchoolFinder.class);
            startActivity(intent);
        }
    }

    SearchView searchView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_laundry_room_chooser, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        if (searchView != null) {
            searchView.setSearchableInfo(
                    searchManager.getSearchableInfo(getComponentName()));
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setQuery("", false);
            searchView.setIconified(true);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public void onRefresh() {
        this.refresh();
    }

    ProgressDialog dialog;

    public void refresh() {
        if (laundryRoomsListView.getAdapter() == null) {
            dialog = ProgressDialog.show(this, "",
                    "Loading. Please wait...", true);
        }
        refreshLaundryRoomLayout.setRefreshing(true);
        new LoadLaundryRoomsTask().execute(dataHandler.getSchool());
    }

    private class LoadLaundryRoomsTask extends AsyncTask<School, Integer, List<LaundryRoom>> {
        protected List<LaundryRoom> doInBackground(School... schools) {
            try {
                schools[0].refresh();
                return new ArrayList<>(schools[0].loadLaundryRooms());
            } catch (APIException e) {
                ACRA.getErrorReporter().handleSilentException(e);
                return null;
            }
        }

        protected void onPostExecute(final List<LaundryRoom> result) {
            runOnUiThread(new Runnable() {
                public void run() {
                    if (result == null) {
                        AlertDialog alertDialog = new AlertDialog.Builder(LaundryRoomChooser.this).create();
                        alertDialog.setTitle("Error");
                        alertDialog.setMessage("A network error has occured.  Please check your connection and try again.");
                        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();

                            }
                        });

                        alertDialog.show();
                        return;
                    }
                    LaundryRoomArrayAdapter adapter = new LaundryRoomArrayAdapter(LaundryRoomChooser.this, result);
                    laundryRoomsListView.setAdapter(adapter);
                    refreshLaundryRoomLayout.setRefreshing(false);
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });
        }
    }

}
