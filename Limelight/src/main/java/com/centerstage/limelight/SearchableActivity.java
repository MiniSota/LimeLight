package com.centerstage.limelight;

import android.app.ActionBar;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
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
import java.util.Iterator;
import java.util.List;

/**
 * Created by Smitesh Kharat on 12/1/13.
 */
public class SearchableActivity extends ListActivity {

    private final static String TRAKT_SEARCH = "http://api.trakt.tv/search/movies.json/5e0bccd550a8d4fcbdff672711dd8bc5?query=";
    private final static String RT_DATA = "http://api.rottentomatoes.com/api/public/v1.0/movie_alias.json?apikey=2teqvbnez6acxrddr5vp5xyx&type=imdb&id=";
    private ArrayList<Movie> movies;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_limelight_app);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            query = query.replaceAll("\\s+", "%20");
            Log.e("QUERY", query);
            new getTraktDataAsyncTask().execute(query);
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Search");
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

        // Expand search action view
        searchMenuItem.expandActionView();

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


    // Parse search data from Trakt.tv API and add it to movies ArrayList
    private class getTraktDataAsyncTask extends AsyncTask<String, String, ArrayList<Movie>> {

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
            movies = new ArrayList<Movie>();
            String result = null;

            DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
            HttpGet httpGet = new HttpGet(TRAKT_SEARCH + params[0]);
            httpGet.setHeader("Content-type", "application/json");

            InputStream inputStream = null;

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
            JSONArray jsonArray;
            try {

                jsonArray = new JSONArray(result);

                for(int i=0; i<jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    // Create a new movie object
                    Movie movie = new Movie();
                    if (jsonObject.getString("imdb_id").equals(""))
                        continue;

                    Log.e("IMD", jsonObject.getString("imdb_id").toString() + "here");
                    movie.setImdbId(jsonObject.getString("imdb_id").substring(2));
                    movie.setTitle(jsonObject.getString("title"));
                    movie.setYear(jsonObject.getInt("year"));
                    movie.setCertification(jsonObject.getString("certification"));
                    movie.setRuntime(jsonObject.getInt("runtime"));
                    movie.setRating_trakt(jsonObject.getJSONObject("ratings").getInt("percentage"));
                    movie.setSynopsis(jsonObject.getString("overview"));
                    movie.setTagline(jsonObject.getString("tagline"));
                    movie.setPosterLarge(jsonObject.getJSONObject("images").getString("fanart"));
                    movie.setTrailer(jsonObject.getString("trailer"));

                    List<String> genres = new ArrayList<String>();
                    JSONArray genreJSONArray = jsonObject.getJSONArray("genres");
                    for(int j = 0; j<genreJSONArray.length(); j++) {
                        String genre = genreJSONArray.getString(j);
                        if (genre != null) {
                            genres.add(genre);
                        }
                    }
                    movie.setGenres(genres);

                    movies.add(movie);
                }

            } catch (JSONException e) {
                Log.e("TRAKT_DATA", "Error");
                e.getStackTrace();
            }

            return movies;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            new getRTDataAsyncTask().execute();
        }
    }

    // Parse some extra data from Rotten Tomatoes API that's not available with trakt
    private class getRTDataAsyncTask extends AsyncTask<String, String, ArrayList<Movie>> {

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
            String result = null;

            for (Iterator<Movie> iter = movies.iterator(); iter.hasNext();) {
                Movie movie = iter.next();

                Log.e("MMMM", movie.getTitle());
                DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
                HttpGet httpGet = new HttpGet(RT_DATA + movie.getImdbId());
                httpGet.setHeader("Content-type", "application/json");

                InputStream inputStream = null;
                Log.e("START", "1st pass=" + movie.getImdbId().toString());
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
                    Log.e("INSIDE", "here");
                    if (!jsonObject.has("error")) {
                        //  Get release date and detailed poster for the movie
                        if(jsonObject.getJSONObject("release_dates").getString("theater") != null)
                            movie.setReleaseDate(jsonObject.getJSONObject("release_dates").getString("theater"));
                        if(jsonObject.getJSONObject("posters").getString("detailed") != null)
                            movie.setPoster(jsonObject.getJSONObject("posters").getString("detailed"));
                    } else {
                        iter.remove();
                    }
                } catch (JSONException e) {
                    Log.e("RT_DATA", "Error");
                    e.getStackTrace();
                }
            }

            return movies;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            // Bind the data
            SearchAdapter searchAdapter = new SearchAdapter(context, R.layout.fragment_movie_item2, movies);
            setListAdapter(searchAdapter);
        }
    }

/*------------------------------------------------------------------------------------------------*/

    private class SearchAdapter extends ArrayAdapter<Movie> {

        private Context context;
        private ArrayList<Movie> movies;
        private final ImageDownloader imageDownloader = new ImageDownloader();
        DBTools dbTools = DBTools.getInstance(context);

        public SearchAdapter(Context context, int textViewResourceId, ArrayList<Movie> movies) {
            super(context, textViewResourceId, movies);
            this.movies = movies;
            this.context = context;
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
                if (movie.getPoster() != null)
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
                        } else {
                            holder.seen.setSelected(true);
                            movie.setInMovieHistory(true);
                            dbTools.updateHistoryMovie(movie.getImdbId(), 1);
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
                        } else {
                            holder.wantsToSee.setSelected(true);
                            movie.setInWatchlist(true);
                            dbTools.updateWatchlistMovie(movie.getImdbId(), 1);
                        }
                    }
                });

                holder.movieButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context, MovieInfoActivity.class);
                        i.putExtra("imdb_id", movie.getImdbId());
                        startActivity(i);
                    }
                });
            }

            return convertView;
        }
    }
}
