package com.centerstage.limelight;

import android.app.Activity;
import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.widget.SearchView;


public class LimelightApp extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    DBTools dbTools = DBTools.getInstance(this);
    public static final String PREFS = "prefs";
    public Boolean initialized1 = false;
    public Boolean initialized2 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_limelight_app);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        // Restore preferences after configuration change
        /*SharedPreferences settings = getSharedPreferences(PREFS, 0);
        initialized1 = settings.getBoolean("initialized1", false);
        initialized2 = settings.getBoolean("initialized2", false);*/
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();

        switch (position+1) {
            case 1:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, HomeCarouselFragment.newInstance(position + 1))
                        .commit();
                break;
            case 2:
                fragmentManager.beginTransaction()
                        .addToBackStack("home")
                        .replace(R.id.container, MovieHistoryFragment.newInstance(position + 1))
                        .commit();
                break;
            case 3:
                fragmentManager.beginTransaction()
                        .addToBackStack("home")
                        .replace(R.id.container, WatchlistFragment.newInstance(position + 1))
                        .commit();
                break;
            case 4:
                fragmentManager.beginTransaction()
                        .addToBackStack("home")
                        .replace(R.id.container, MovieNightFragment.newInstance(position + 1))
                        .commit();
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.limelight_app_options, menu);

            // Get the SearchView and set the searchable configuration
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

            // Assumes current activity is the searchable activity
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // TODO: Make back button work properly
    @Override
    public void onBackPressed(){
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount()!=0){

            if (fm.getBackStackEntryAt(fm.getBackStackEntryCount()-1).getName().equals("home")){
                for (int i=0;i<fm.getBackStackEntryCount();i++){
                    fm.popBackStack();
                }
                mNavigationDrawerFragment.selectItem(0);
                onSectionAttached(1);
                restoreActionBar();
                fm.beginTransaction()
                        .replace(R.id.container, HomeCarouselFragment.newInstance(1))
                        .commit();
            }
            else{
                mNavigationDrawerFragment.selectItem(3);
                onSectionAttached(4);
                restoreActionBar();
                fm.beginTransaction()
                        .addToBackStack("home")
                        .replace(R.id.container, MovieNightFragment.newInstance(4))
                        .commit();
            }
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        // We need an editor object to make preference changes
        SharedPreferences settings = getSharedPreferences(PREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("initialized1", initialized1);
        editor.putBoolean("initialized2", initialized2);
        editor.commit();
    }

}
