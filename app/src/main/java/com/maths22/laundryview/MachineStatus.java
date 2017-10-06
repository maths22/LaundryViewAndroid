package com.maths22.laundryview;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.maths22.laundryview.data.DataHandler;
import com.maths22.laundryview.data.LaundryRoom;
import com.maths22.laundryview.data.MachineType;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MachineStatus extends AppCompatActivity implements MachineStatusFragment.OnFragmentInteractionListener {

    LaundryRoom room;

    @Bind(R.id.tabs)
    TabLayout tabs;

    private DataHandler dataHandler;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machine_status);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dataHandler = new DataHandler();


        room = dataHandler.getLaundryRoom();
        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.school_preference_file_key), Context.MODE_PRIVATE);
        room.setId(sharedPref.getString("laundry_room_id", ""));
        room.setName(sharedPref.getString("laundry_room_name", ""));

        setTitle(room.getName());

        ButterKnife.bind(this);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabs.setupWithViewPager(viewPager);

        // Obtain the shared Tracker instance.
        LaundryViewApplication application = (LaundryViewApplication) getApplication();
        mTracker = application.getDefaultTracker();
    }

    private static final String name = "MachineStatus";

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("laundryview", "Setting screen name: " + name);
        mTracker.setScreenName("Image~" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getFragmentManager());
        adapter.addFragment(MachineStatusFragment.newInstance(room, MachineType.WASHER), "Washers");
        adapter.addFragment(MachineStatusFragment.newInstance(room, MachineType.DRYER), "Dryers");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onBackPressed() {
        clearPrefs();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                clearPrefs();
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearPrefs() {
        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.school_preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("laundry_room_id");
        editor.remove("laundry_room_name");
        editor.apply();
    }

}
