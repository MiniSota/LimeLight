package com.centerstage.limelight;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Smitesh Kharat on 12/7/13.
 */
public class MovieInfoActivity extends Activity {

    DBTools dbTools = DBTools.getInstance(this);
    private Drawable mActionBarBackgroundDrawable;
    private MovieInfoAdapter mAdapter;
    private ViewPager mPager;
    private String imdbId;
    private ImageDownloader mImageDownloader = new ImageDownloader();

    private NotifyingScrollView mScrollView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_info_container);

        mActionBarBackgroundDrawable = getResources().getDrawable(R.drawable.coming_soon);
        mActionBarBackgroundDrawable.setAlpha(0);

        getActionBar().setBackgroundDrawable(mActionBarBackgroundDrawable);

        mScrollView = (NotifyingScrollView) findViewById(R.id.scroll_view);
        ((NotifyingScrollView) findViewById(R.id.scroll_view)).setOnScrollChangedListener(mOnScrollChangedListener);

        Intent intent = getIntent();
        imdbId = intent.getStringExtra("imdb_id");
        final Movie movie = dbTools.getSingleMovie(imdbId);

        // Set title and poster
        setTitle(movie.getTitle());
        mImageDownloader.download(movie.getPosterLarge(), (ImageView) findViewById(R.id.poster_large));

        // Play the trailer in YouTube
        (findViewById(R.id.play_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(movie.getTrailer()));
                startActivity(i);
            }
        });

        // 'Seen' and 'Wants to See' buttons
        final View seenButton = findViewById(R.id.seen_button);
        final View wantsToSeeButton = findViewById(R.id.wants_to_see_button);

        seenButton.setSelected(movie.isInMovieHistory());
        wantsToSeeButton.setSelected(movie.isInWatchlist());

        seenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (movie.isInMovieHistory()) {
                    seenButton.setSelected(false);
                    movie.setInMovieHistory(false);
                    dbTools.updateHistoryMovie(movie.getImdbId(), 0);
                } else if (dbTools.isInMovieHistory(movie.getImdbId())) {
                    seenButton.setSelected(true);
                    movie.setInMovieHistory(true);
                    dbTools.updateHistoryMovie(movie.getImdbId(), 1);
                } else {
                    seenButton.setSelected(true);
                    dbTools.insertHistoryMovie(movie);
                }
            }
        });

        wantsToSeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(movie.isInWatchlist()) {
                    wantsToSeeButton.setSelected(false);
                    movie.setInWatchlist(false);
                    dbTools.updateWatchlistMovie(movie.getImdbId(), 0);
                } else if (dbTools.isInWatchlist(movie.getImdbId())) {
                    wantsToSeeButton.setSelected(true);
                    movie.setInWatchlist(true);
                    dbTools.updateWatchlistMovie(movie.getImdbId(), 1);
                } else {
                    wantsToSeeButton.setSelected(true);
                    dbTools.insertWatchlistMovie(movie);
                }
            }
        });

        // Rest of the header movie data
        Double rating_trakt = movie.getRating_trakt()/10.0;
        ((TextView) (findViewById(R.id.ratingTextView))).setText(rating_trakt.toString());
        ((TextView) (findViewById(R.id.release_date))).setText(movie.getReleaseDate());
        TextView genresTextView = (TextView) findViewById(R.id.genres);

        List<String> allGenres = movie.getGenres();
        if (!allGenres.isEmpty()) {
            genresTextView.setText(movie.getGenres().get(0));
        } else {
            genresTextView.setText(null);
        }

        // ViewPager Adapter
        mAdapter = new MovieInfoAdapter(getFragmentManager());
        mPager = (ViewPager) findViewById(R.id.movie_pager);

        mPager.setAdapter(mAdapter);
        mPager.setCurrentItem(1);

/*        mPager.setOnTouchListener(new View.OnTouchListener() {

            int dragthreshold = 10;
            int downX;
            int downY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = (int) event.getRawX();
                        downY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int distanceX = Math.abs((int) event.getRawX() - downX);
                        int distanceY = Math.abs((int) event.getRawY() - downY);

                        if (distanceX > distanceY && distanceX > dragthreshold) {
                            mPager.getParent().requestDisallowInterceptTouchEvent(false);
                            mScrollView.getParent().requestDisallowInterceptTouchEvent(true);
                        } else if (distanceY > distanceX && distanceY > dragthreshold) {
                            mPager.getParent().requestDisallowInterceptTouchEvent(true);
                            mScrollView.getParent().requestDisallowInterceptTouchEvent(false);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        mScrollView.getParent().requestDisallowInterceptTouchEvent(false);
                        mPager.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });*/

  /*      mPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });*/

/*        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                mPager.getParent().requestDisallowInterceptTouchEvent(true);
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });*/

    }

    private NotifyingScrollView.OnScrollChangedListener mOnScrollChangedListener = new NotifyingScrollView.OnScrollChangedListener() {
        public void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt) {
            final int headerHeight = findViewById(R.id.poster_large).getHeight() - getActionBar().getHeight();
            final float ratio = (float) Math.min(Math.max(t, 0), headerHeight) / headerHeight;
            final int newAlpha = (int) (ratio * 255);
            mActionBarBackgroundDrawable.setAlpha(newAlpha);
        }
    };

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    // TODO: Add navigation drawer
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Only show items in the action bar relevant to this screen
        // if the drawer is not showing. Otherwise, let the drawer
        // decide what to show in the action bar.
        getMenuInflater().inflate(R.menu.limelight_app_options, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();

        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        restoreActionBar();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public class MovieInfoAdapter extends FragmentStatePagerAdapter {
        public MovieInfoAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This shows the cast
                    return MovieInfoCast.newInstance(imdbId);
                case 1: // Fragment # 1 - This shows the synopsis
                    return MovieInfoCenter.newInstance(imdbId);
                case 2: // Fragment # 2 - This shows the reviews
                    return MovieInfoReviews.newInstance(imdbId);
                default:
                    return MovieInfoCenter.newInstance(imdbId);
            }
        }
    }

}
