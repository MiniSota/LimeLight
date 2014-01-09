package com.centerstage.limelight;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.tabcarousel.BackScrollManager;
import com.android.tabcarousel.CarouselContainer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Smitesh Kharat on 11/21/13.
 */
public class HomeFragment1 extends ListFragment implements AdapterView.OnItemClickListener {

    DBTools dbTools = DBTools.getInstance(getActivity());
    public Boolean initialized = false;

    private final static String RT_IN_THEATRE = "http://api.rottentomatoes.com/api/public/v1.0/lists/movies/in_theaters.json?apikey=2teqvbnez6acxrddr5vp5xyx";
    private final static String TRAKT_DATA = "http://api.trakt.tv/movie/summary.json/5e0bccd550a8d4fcbdff672711dd8bc5/tt";
    private List<String> releaseDates;
    private List<String> posterThumbnails;
    private ArrayList<Movie> movies;
    private CarouselContainer mCarousel;
    private CarouselListAdapter adapter;
    private getRTDataAsyncTask asyncTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!((LimelightApp) getActivity()).initialized1) {
            // Get IMDb Ids from RT for in-theatre movies and then use trakt data for those movies
            asyncTask = new getRTDataAsyncTask();
            asyncTask.execute(RT_IN_THEATRE);
           // ((LimelightApp) getActivity()).initialized1 = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Attaches the header to the list for backscroll to work properly
        mCarousel = (CarouselContainer) getParentFragment().getView().findViewById(R.id.carousel_header);

        adapter = new CarouselListAdapter(getActivity());
        movies = dbTools.getInTheatresMovies();
        for (Movie movie : movies) {
            adapter.add(movie);
        }

        // Bind this data after the first run of the home screen
        if(asyncTask == null) {
            setListAdapter(adapter);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView listView = getListView();
        // Attach the BackScrollManager
        listView.setOnScrollListener(new BackScrollManager(mCarousel, null, CarouselContainer.TAB_INDEX_FIRST));
        // Register the onItemClickListener
        listView.setOnItemClickListener(this);
        // We disable the scroll bar because it would otherwise be incorrect
        // because of the hidden
        // header
        listView.setVerticalScrollBarEnabled(false);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // This is the header
        if (position == 0) {
            return;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (asyncTask != null)
            asyncTask.cancel(true);
    }

    // TODO: Trying to save state of child fragment
/*    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("fragment", getFragmentManager().saveFragmentInstanceState(this));
    }*/

/* -----------------------------------------------------------------------------------------------*/
    /**
     * This is a special adapter used in conjunction with
     * CarouselHeader and {@link BackScrollManager}. In order to
     * smoothly animate the CarouselHeader, a faux header in placed at
     * position == 0 in the adapter. This isn't necessary to use the widget, but
     * it is if you want the animation to appear correct.
     */
    private class CarouselListAdapter extends ArrayAdapter<Movie> {

        /**
         * The header view
         */
        private static final int ITEM_VIEW_TYPE_HEADER = 0;

        /**
         * * The data in the list.
         */
        private static final int ITEM_VIEW_TYPE_DATA = 1;

        /**
         * Number of views (TextView, CarouselHeader)
         */
        private static final int VIEW_TYPE_COUNT = 2;

        /**
         * Fake header
         */
        private final View mHeader;
        private final ImageDownloader imageDownloader = new ImageDownloader();

        /**
         * Constructor of <code>CarouselListAdapter</code>
         *
         * @param context The {@link Context} to use
         */
        public CarouselListAdapter(Context context) {
            super(context, 0);
            // Inflate the fake header
            mHeader = LayoutInflater.from(context).inflate(R.layout.faux_carousel, null);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Return a faux header at position 0
            if (position == 0) {
                return mHeader;
            }

            // Recycle HomeViewHolder's items
            final ViewHolder holder;
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

            // Retrieve the data, but make sure to call one less than the current
            // position to avoid counting the faux header.
            final Movie movie = movies.get(position - 1);

            if (movie != null) {
                imageDownloader.download(movie.getPoster(), holder.poster);

                holder.title.setText(movie.getTitle());
                holder.releaseDate.setVisibility(View.INVISIBLE);

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

                holder.seen.setSelected(movie.isInMovieHistory());
                holder.wantsToSee.setSelected(movie.isInWatchlist());

                holder.seen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (movie.isInMovieHistory()) {
                            holder.seen.setSelected(false);
                            Toast.makeText(getContext(), "Removed from your Movie History", Toast.LENGTH_SHORT).show();
                            movie.setInMovieHistory(false);
                            dbTools.updateHistoryMovie(movie.getImdbId(), 0);
                        } else if (dbTools.isInMovieHistory(movie.getImdbId())) {
                            holder.seen.setSelected(true);
                            Toast.makeText(getContext(), "Added to your Movie History", Toast.LENGTH_SHORT).show();
                            movie.setInMovieHistory(true);
                            dbTools.updateHistoryMovie(movie.getImdbId(), 1);
                        } else {
                            holder.seen.setSelected(true);
                            Toast.makeText(getContext(), "Added to your Movie History", Toast.LENGTH_SHORT).show();
                            dbTools.insertHistoryMovie(movie);
                        }
                    }
                });

                holder.wantsToSee.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(movie.isInWatchlist()) {
                            holder.wantsToSee.setSelected(false);
                            Toast.makeText(getContext(), "Removed from your Watchlist", Toast.LENGTH_SHORT).show();
                            movie.setInWatchlist(false);
                            dbTools.updateWatchlistMovie(movie.getImdbId(), 0);
                        } else if (dbTools.isInWatchlist(movie.getImdbId())) {
                            holder.wantsToSee.setSelected(true);
                            Toast.makeText(getContext(), "Added to your Watchlist", Toast.LENGTH_SHORT).show();
                            movie.setInWatchlist(true);
                            dbTools.updateWatchlistMovie(movie.getImdbId(), 1);
                        } else {
                            holder.wantsToSee.setSelected(true);
                            Toast.makeText(getContext(), "Added to your Watchlist", Toast.LENGTH_SHORT).show();
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

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasStableIds() {
            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getCount() {
            return movies.size() + 1;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public long getItemId(int position) {
            if (position == 0) {
                return -1;
            }
            return position - 1;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getViewTypeCount() {
            return VIEW_TYPE_COUNT;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return ITEM_VIEW_TYPE_HEADER;
            }
            return ITEM_VIEW_TYPE_DATA;
        }
    }

/* -----------------------------------------------------------------------------------------------*/
    // Parse data from Rotten Tomatoes API to get in-theatres and upcoming movies
    private class getRTDataAsyncTask extends AsyncTask<String, String, List<String>> {

        @Override
        protected List<String> doInBackground(String... params) {
            DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
            HttpGet httpGet = new HttpGet(params[0]);
            httpGet.setHeader("Content-type", "application/json");

            List<String> imdbIdList = new ArrayList<String>();
            InputStream inputStream = null;
            String result = null;

            try {
                // Get a response from the web service
                HttpResponse response = httpClient.execute(httpGet);

                // The content from requested URL along with headers
                HttpEntity entity = response.getEntity();

                // Get the main content from the URL
                inputStream = entity.getContent();

                // BufferedReader reads data from InputStream until buffer is full
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);

                StringBuilder theStringBuilder = new StringBuilder();
                String line;

                // Read data from the buffer till its empty
                while ((line = reader.readLine()) != null) {
                    theStringBuilder.append(line).append("\n");
                }

                // Store all the data in result
                result = theStringBuilder.toString();

            } catch (Exception e) {
                e.getStackTrace();
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                    e.getStackTrace();
                }
            }

            // Holds key-value pairs from a JSON source
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(result);
                JSONArray allMoviesJSONArray = jsonObject.getJSONArray("movies");

                releaseDates = new ArrayList<String>();
                posterThumbnails = new ArrayList<String>();
                for (int i=0; i<allMoviesJSONArray.length(); i++) {
                    JSONObject movieJSONObject = allMoviesJSONArray.getJSONObject(i);

                    // Get IMDb ID
                    try {
                        JSONObject idsJSONObject = movieJSONObject.getJSONObject("alternate_ids");

                        // Get release date and detailed poster for the movie
                        releaseDates.add(movieJSONObject.getJSONObject("release_dates").getString("theater"));
                        posterThumbnails.add(movieJSONObject.getJSONObject("posters").getString("detailed"));

                        imdbIdList.add(idsJSONObject.getString("imdb"));
                    } catch (Exception e) {
                        e.getStackTrace();
                    }
                }

            } catch (JSONException e) {
                Log.e("RT_DATA_H1", "Error");
                e.getStackTrace();
            }

            return imdbIdList;
        }

        @Override
        protected void onPostExecute(List<String> imdbIdList) {
            // Use IMDb Ids to get movie data from Trakt.tv
            new getTraktDataAsyncTask().execute(imdbIdList);
        }
    }

    // Parse data from Trakt.tv API to get movie data and add it to movies ArrayList
    private class getTraktDataAsyncTask extends AsyncTask<List<String>, String, ArrayList<Movie>> {

        @Override
        protected ArrayList<Movie> doInBackground(List<String>... params) {
            ArrayList<Movie> newMovies = new ArrayList<Movie>();
            String result = null;

            int num = 0;
            for (String id: params[0]) {
                DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
                HttpPost httpPost = new HttpPost(TRAKT_DATA + id);
                httpPost.setHeader("Content-type", "application/json");

                InputStream inputStream = null;

                try {
                    // Get a response from the web service
                    HttpResponse response = httpClient.execute(httpPost);

                    // The content from requested URL along with headers
                    HttpEntity entity = response.getEntity();

                    // Get the main content from the URL
                    inputStream = entity.getContent();

                    // BufferedReader reads data from InputStream until buffer is full
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);

                    StringBuilder theStringBuilder = new StringBuilder();
                    String line;

                    // Read data from the buffer till its empty
                    while ((line = reader.readLine()) != null) {
                        theStringBuilder.append(line).append("\n");
                    }

                    // Store all the data in result
                    result = theStringBuilder.toString();

                } catch (Exception e) {
                    e.getStackTrace();
                } finally {
                    try {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                    } catch (IOException e) {
                        e.getStackTrace();
                    }
                }

                // Holds key-value pairs from a JSON source
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(result);

                    // Create a new movie object
                    Movie movie = new Movie();
                    movie.setImdbId(id);
                    movie.setTitle(jsonObject.getString("title"));
                    movie.setYear(jsonObject.getInt("year"));
                    movie.setCertification(jsonObject.getString("certification"));
                    movie.setRuntime(jsonObject.getInt("runtime"));
                    movie.setRating_trakt(jsonObject.getJSONObject("ratings").getInt("percentage"));
                    movie.setReleaseDate(dbTools.getFormattedDate(releaseDates.get(num)));
                    movie.setSynopsis(jsonObject.getString("overview"));
                    movie.setTagline(jsonObject.getString("tagline"));
                    movie.setPoster(posterThumbnails.get(num));
                    movie.setPosterLarge(jsonObject.getJSONObject("images").getString("fanart"));
                    movie.setTrailer(jsonObject.getString("trailer"));
                    movie.setInMovieHistory(dbTools.getInMovieHistory(movie.getImdbId()));
                    movie.setInWatchlist(dbTools.getInWatchlist(movie.getImdbId()));
                    movie.setInTheatres(true);
                    movie.setInComingSoon(dbTools.getInComingSoon(movie.getImdbId()));

                    List<String> genres = new ArrayList<String>();
                    JSONArray genreJSONArray = jsonObject.getJSONArray("genres");
                    for(int i = 0; i<genreJSONArray.length(); i++) {
                        String genre = genreJSONArray.getString(i);
                        if (genre != null) {
                            genres.add(genre);
                        }
                    }
                    movie.setGenres(genres);

                    ArrayList<List<String>> actors = new ArrayList<List<String>>();
                    ArrayList<List<String>> directors = new ArrayList<List<String>>();
                    ArrayList<List<String>> producers = new ArrayList<List<String>>();
                    ArrayList<List<String>> writers = new ArrayList<List<String>>();

                    JSONObject peopleJSONObject = jsonObject.getJSONObject("people");
                    JSONArray actorsJSONArray = peopleJSONObject.getJSONArray("actors");
                    JSONArray directorsJSONArray = peopleJSONObject.getJSONArray("directors");
                    JSONArray producersJSONArray = peopleJSONObject.getJSONArray("producers");
                    JSONArray writersJSONArray = peopleJSONObject.getJSONArray("writers");

                    for(int i = 0; i<actorsJSONArray.length(); i++) {
                        JSONObject personJSONObject = actorsJSONArray.getJSONObject(i);
                        List<String> singlePerson = new ArrayList<String>();
                        singlePerson.add(personJSONObject.getString("name"));
                        singlePerson.add(personJSONObject.getJSONObject("images").getString("headshot"));
                        singlePerson.add(personJSONObject.getString("character"));
                        actors.add(singlePerson);
                    }

                    for(int i = 0; i<directorsJSONArray.length(); i++) {
                        JSONObject personJSONObject = directorsJSONArray.getJSONObject(i);
                        List<String> singlePerson = new ArrayList<String>();
                        singlePerson.add(personJSONObject.getString("name"));
                        singlePerson.add(personJSONObject.getJSONObject("images").getString("headshot"));
                        directors.add(singlePerson);
                    }

                    for(int i = 0; i<producersJSONArray.length(); i++) {
                        JSONObject personJSONObject = producersJSONArray.getJSONObject(i);
                        List<String> singlePerson = new ArrayList<String>();
                        singlePerson.add(personJSONObject.getString("name"));
                        singlePerson.add(personJSONObject.getJSONObject("images").getString("headshot"));
                        if (personJSONObject.getBoolean("executive")) {
                            singlePerson.add("1");
                        } else {
                            singlePerson.add("0");
                        }
                        producers.add(singlePerson);
                    }

                    for(int i = 0; i<writersJSONArray.length(); i++) {
                        JSONObject personJSONObject = writersJSONArray.getJSONObject(i);
                        List<String> singlePerson = new ArrayList<String>();
                        singlePerson.add(personJSONObject.getString("name"));
                        singlePerson.add(personJSONObject.getJSONObject("images").getString("headshot"));
                        singlePerson.add(personJSONObject.getString("job"));
                        writers.add(singlePerson);
                    }

                    movie.setActors(actors);
                    movie.setDirectors(directors);
                    movie.setProducers(producers);
                    movie.setWriters(writers);

                    newMovies.add(movie);

                } catch (JSONException e) {
                    Log.e("TRAKT_DATA_H1", "Error");
                    e.getStackTrace();
                }

                num++;
            }

            // If new list of movies doesn't contain any of the previous ones
            //  stored in the DB, then remove them
            // TODO: Add this back after app is finished
     /*       for (Movie movie: movies) {
                if (!newMovies.contains(movie)) {
                    adapter.remove(movie);
                    dbTools.deleteMovie(movie.getImdbId());
                }
            }

            for (Movie newMovie : newMovies) {
                if (!movies.contains(newMovie)) {
                    adapter.add(newMovie);
                }
            }*/

            dbTools.insertAllMovies(newMovies);

            return newMovies;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> newMovies) {
            // Bind new data
            setListAdapter(adapter);
        }
    }
}
