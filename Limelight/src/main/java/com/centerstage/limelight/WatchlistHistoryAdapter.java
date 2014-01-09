package com.centerstage.limelight;

import android.content.Context;
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
 * Created by Smitesh Kharat on 12/3/13.
 */
public class WatchlistHistoryAdapter extends ArrayAdapter<Movie> {
    private Context context;
    private ArrayList<Movie> movies;
    private final ImageDownloader imageDownloader = new ImageDownloader();
    private WatchlistHistoryAdapter watchlistHistoryAdapter;
    DBTools dbTools = DBTools.getInstance(context);

    public WatchlistHistoryAdapter(Context context, int textViewResourceId, ArrayList<Movie> movies) {
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
            convertView = inflater.inflate(R.layout.fragment_movie_item, parent, false);

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

        Movie movie = getItem(position);

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

            holder.wantsToSee.setVisibility(View.INVISIBLE);
            holder.seen.setVisibility(View.INVISIBLE);
        }

        /*************/
 /*       ListView listView = (ListView)
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
                                    Movie movie_copy = watchlistHistoryAdapter.getItem(position);
                                    watchlistHistoryAdapter.remove(movie_copy);

                                    movie_copy.setInWatchlist(false);
//                                        dbTools.deleteMovie(watchlistAdapter.getItem(position).getImdbId());
                                    dbTools.updateWatchlistMovie(movie_copy.getImdbId(), 0);

                                }
                                watchlistHistoryAdapter.notifyDataSetChanged();
                            }
                        });
        holder.movieButton.setOnTouchListener(touchListener);
        listView.setOnScrollListener(touchListener.makeScrollListener());
*/
        return convertView;
    }

}
