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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Smitesh Kharat on 11/30/13.
 */
public class MovieNightListFragment extends ListFragment {

    public static final String ARG_SECTION_NUMBER = "section_number";
    DBTools dbTools = DBTools.getInstance(getActivity());
    static String genre_choice = "";

    public static MovieNightListFragment newInstance(int sectionNumber, String genre) {
        MovieNightListFragment movieNightListFragment = new MovieNightListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        movieNightListFragment.setArguments(args);
        genre_choice = genre;
        return movieNightListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_night_list, container, false);

        // Get movies from the database
        ArrayList<Movie> movies = dbTools.getAllMovies();
        ArrayList<Movie> movies_to_display = new ArrayList<Movie>();
        if(genre_choice.equals("Top Movies")){
            movies_to_display = movies;
            final ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView);
            imageView.setImageResource(R.drawable.ic_top_movies);
        }
        else{
           int i =0;
           for (Movie movie : movies) {

                if(movie.getGenres().contains(genre_choice)){
                    movies_to_display.add(i,movie);
                    i++;
                }
          }
        }
        if(genre_choice.equals("Comedy")){
            final ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView);
            imageView.setImageResource(R.drawable.ic_genre_comedy);
        }
        else if(genre_choice.equals("Romance")){
            final ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView);
            imageView.setImageResource(R.drawable.ic_genre_romance);
        }
        else if(genre_choice.equals("Music")){
            final ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView);
            imageView.setImageResource(R.drawable.ic_genre_musical);
        }
        else if(genre_choice.equals("Documentary")){
            final ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView);
            imageView.setImageResource(R.drawable.ic_genre_documentary);
        }
        else if(genre_choice.equals("Mystery")){
            final ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView);
            imageView.setImageResource(R.drawable.ic_genre_mystery);
        }
        else if(genre_choice.equals("Sports")){
            final ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView);
            imageView.setImageResource(R.drawable.ic_genre_sports);
        }
        else if(genre_choice.equals("Fantasy")){
            final ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView);
            imageView.setImageResource(R.drawable.ic_genre_fantasy);
        }
        else if(genre_choice.equals("Drama")){
            final ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView);
            imageView.setImageResource(R.drawable.ic_genre_drama);
        }

        // Create the list view
        MovieNightAdapter movieNightAdapter = new MovieNightAdapter(getActivity(), R.layout.fragment_movie_item2, movies_to_display);
        setListAdapter(movieNightAdapter);

        return rootView;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ListView listView = getListView();
        Object object = listView.getItemAtPosition(position);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((LimelightApp) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    // Custom adapter for Movie Night
    private class MovieNightAdapter extends ArrayAdapter<Movie> {
        private Context context;
        private ArrayList<Movie> movies;
        private final ImageDownloader imageDownloader = new ImageDownloader();
        DBTools dbTools = DBTools.getInstance(context);

        public MovieNightAdapter(Context context, int textViewResourceId, ArrayList<Movie> movies) {
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
            final ViewHolder holder;

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
                        holder.genres.setText(movie.getGenres().get(0) + ", " + movie.getGenres().get(1));
                    } else {
                        holder.genres.setText(movie.getGenres().get(0));
                    }
                } else {
                    holder.genres.setText(null);
                }

                Double rating_trakt = movie.getRating_trakt()/10.0;
                holder.ratingTextView.setText(rating_trakt.toString());

                holder.seen.setSelected(movie.isInMovieHistory());
                holder.wantsToSee.setSelected(movie.isInWatchlist());

                holder.seen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (movie.isInMovieHistory()) {
                            holder.seen.setSelected(false);
                            movie.setInMovieHistory(false);
                            dbTools.updateHistoryMovie(movie.getImdbId(), 0);
                        } else if (dbTools.isInMovieHistory(movie.getImdbId())) {
                            holder.seen.setSelected(true);
                            movie.setInMovieHistory(true);
                            dbTools.updateHistoryMovie(movie.getImdbId(), 1);
                        } else {
                            holder.seen.setSelected(true);
                            dbTools.insertHistoryMovie(movie);
                        }
                    }
                });

                holder.wantsToSee.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(movie.isInWatchlist()) {
                            holder.wantsToSee.setSelected(false);
                            movie.setInWatchlist(false);
                            dbTools.updateWatchlistMovie(movie.getImdbId(), 0);
                        } else if (dbTools.isInWatchlist(movie.getImdbId())) {
                            holder.wantsToSee.setSelected(true);
                            movie.setInWatchlist(true);
                            dbTools.updateWatchlistMovie(movie.getImdbId(), 1);
                        } else {
                            holder.wantsToSee.setSelected(true);
                            dbTools.insertWatchlistMovie(movie);
                        }
                    }
                });

                holder.movieButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getActivity(), MovieInfoActivity.class);
                        i.putExtra("imdb_id", movie.getImdbId());
                        startActivity(i);
                    }
                });
            }

            return convertView;
        }

    }
}
