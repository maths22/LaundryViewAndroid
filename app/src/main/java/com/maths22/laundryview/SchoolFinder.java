package com.maths22.laundryview;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.maths22.laundryview.data.APIException;
import com.maths22.laundryview.data.DataHandler;
import com.maths22.laundryview.data.School;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SchoolFinder extends AppCompatActivity implements androidx.appcompat.widget.SearchView.OnQueryTextListener {

    @BindView(R.id.schoolListView)
    ListView schoolFinderListView;
    @BindView(R.id.emptyLayout)
    LinearLayout emptyLayout;
    @BindView(R.id.searchSuggestions)
    Button searchSuggestions;

    private DataHandler dataHandler;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_finder);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);
        dataHandler = new DataHandler();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        schoolFinderListView.setOnItemClickListener((parent, view, position, id) -> {

            SchoolArrayAdapter arrayAdapter = (SchoolArrayAdapter) parent.getAdapter();
            School school = arrayAdapter.getItem(position);
            SharedPreferences sharedPref = getSharedPreferences(
                    getString(R.string.school_preference_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("school_id", school.getId());
            editor.putString("school_name", school.getName());
            editor.apply();
            finish();
        });

        schoolFinderListView.setEmptyView(emptyLayout);

        searchSuggestions.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(SchoolFinder.this);
            builder.setView(R.layout.dialog_search_help);
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        // Obtain the shared Tracker instance.
        LaundryViewApplication application = (LaundryViewApplication) getApplication();
        mTracker = application.getDefaultTracker();

        handleIntent(getIntent());
    }

    private static final String name = "SchoolFinder";

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("laundryview", "Setting screen name: " + name);
        mTracker.setScreenName("Image~" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            runSearch(query);
        }
    }

    ProgressDialog dialog;

    private void runSearch(String query) {
        dialog = ProgressDialog.show(this, "",
                "Loading. Please wait...", true);
        new FindSchoolsTask(this).execute(query);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.school_preference_file_key), Context.MODE_PRIVATE);
        if (sharedPref.contains("school_id")) {
            return true;
        }
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_school_finder, menu);

        /*// Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.searchView).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default*/

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        if (searchView != null) {
            searchView.setOnQueryTextListener(this);
            searchView.setIconifiedByDefault(false);
            searchView.requestFocusFromTouch();
            searchView.setQueryHint(getString(R.string.search_hint));
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        runSearch(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }


    private static class FindSchoolsTask extends AsyncTask<String, Integer, List<School>> {
        private final SchoolFinder parent;

        private FindSchoolsTask(SchoolFinder parent) {
            this.parent = parent;
        }

        protected List<School> doInBackground(String... query) {
            try {
                return new ArrayList<>(parent.dataHandler.getSearcher().findSchools(query[0]));
            } catch (APIException e) {
                Log.e(e.getClass().getName(), "exception", e);
                return null;
            }
        }

        protected void onPostExecute(final List<School> result) {
            parent.runOnUiThread(() -> {
                if (result == null) {
                    AlertDialog alertDialog = new AlertDialog.Builder(parent).create();
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage("A network error has occured.  Please check your connection and try again.");
                    alertDialog.setIcon(android.R.drawable.ic_dialog_alert);

                    alertDialog.show();
                    return;
                }
                SchoolArrayAdapter adapter = new SchoolArrayAdapter(parent, result);
                ListView listView = parent.findViewById(R.id.schoolListView);
                listView.setAdapter(adapter);
                parent.dialog.dismiss();
            });
        }
    }
}
