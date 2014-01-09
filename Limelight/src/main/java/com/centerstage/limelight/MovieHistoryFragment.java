package com.centerstage.limelight;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Smitesh Kharat on 11/30/13.
 */
public class MovieHistoryFragment extends ListFragment {

    public static final String ARG_SECTION_NUMBER = "section_number";
    DBTools dbTools = DBTools.getInstance(getActivity());
    WatchlistAdapter watchlistAdapter;

    public static MovieHistoryFragment newInstance(int sectionNumber) {
        MovieHistoryFragment movieHistoryFragment = new MovieHistoryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        movieHistoryFragment.setArguments(args);
        return movieHistoryFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((LimelightApp) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_history, container, false);

        // Get movies from the database
        ArrayList<Movie> movies = dbTools.getMovieHistory();

        // Create the list view
        watchlistAdapter = new WatchlistAdapter(getActivity(), R.layout.fragment_movie_item2, movies);
        setListAdapter(watchlistAdapter);
        return rootView;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ListView listView = getListView();
        Movie movie = (Movie) listView.getItemAtPosition(position);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    // Custom adapter for Watchlist
    private class WatchlistAdapter extends ArrayAdapter<Movie> {
        private Context context;
        private ArrayList<Movie> movies;
        private final ImageDownloader imageDownloader = new ImageDownloader();
        DBTools dbTools = DBTools.getInstance(context);

        public WatchlistAdapter(Context context, int textViewResourceId, ArrayList<Movie> movies) {
            super(context, textViewResourceId, movies);
            this.movies = movies;
            this.context = context;
        }

        @Override
        public Movie getItem(int position) {
            return movies.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.fragment_movie_item2, parent, false);

                holder = new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.genres = (TextView) convertView.findViewById(R.id.genres);
                holder.releaseDate = (TextView) convertView.findViewById(R.id.release_date);
                holder.ratingTextView = (TextView) convertView.findViewById(R.id.ratingTextView);
                holder.poster = (ImageView) convertView.findViewById(R.id.poster);
                holder.movieButton = (Button) convertView.findViewById(R.id.movie_button);
                holder.seen = (ImageButton) convertView.findViewById(R.id.seen_button);
                holder.wantsToSee = (ImageButton) convertView.findViewById(R.id.wants_to_see_button);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final Movie movie = getItem(position);
            if (movie != null) {
                imageDownloader.download(movie.getPoster(), holder.poster);

                holder.title.setText(movie.getTitle());
                holder.releaseDate.setText(movie.getReleaseDate());

                List<String> allGenres = movie.getGenres();
                if (!allGenres.isEmpty()) {
                    if (allGenres.size() >= 2) {
                        holder.genres.setText(allGenres.get(0) + ", " + allGenres.get(1));
                    } else {
                        holder.genres.setText(allGenres.get(0));
                    }
                } else {
                    holder.genres.setText(null);
                }

                Double rating_trakt = movie.getRating_trakt()/10.0;
                holder.ratingTextView.setText(rating_trakt.toString());

                holder.movieButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getActivity(), MovieInfoActivity.class);
                        i.putExtra("imdb_id", movie.getImdbId());
                        startActivity(i);
                    }
                });
            }

            holder.wantsToSee.setVisibility(View.GONE);
            holder.seen.setVisibility(View.GONE);

            //***********
            ListView listView = getListView();
            // Create a ListView-specific touch listener. ListViews are given special treatment because
            // by default they handle touches for their list items... i.e. they're in charge of drawing
            // the pressed state (the list selector), handling list item clicks, etc.
            SwipeDismissListViewTouchListener touchListener =
                    new SwipeDismissListViewTouchListener(
                            listView,
                            new SwipeDismissListViewTouchListener.DismissCallbacks() {
                                @Override
                                public boolean canDismiss(int position) {
                                    return true;
                                }

                                @Override
                                public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                    for (int position : reverseSortedPositions) {
                                        Movie movie_copy = watchlistAdapter.getItem(position);
                                        watchlistAdapter.remove(movie_copy);

                                        movie_copy.setInMovieHistory(false);
                                        dbTools.updateHistoryMovie(movie_copy.getImdbId(), 0);

                                    }
                                    watchlistAdapter.notifyDataSetChanged();
                                }
                            });
            holder.movieButton.setOnTouchListener(touchListener);
            listView.setOnScrollListener(touchListener.makeScrollListener());

            return convertView;
        }
    }

}
