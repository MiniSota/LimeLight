package com.centerstage.limelight;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Smitesh Kharat on 11/27/13.
 */
public class DBTools extends SQLiteOpenHelper {

    private static DBTools sInstance = null;
    private static final String DATABASE_NAME = "limelight.db";
    private static final int DATABASE_VERSION = 2;

    public static DBTools getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DBTools(context);
        }
        return sInstance;
    }

    private DBTools(Context applicationContext) {
        super(applicationContext, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String queryMovie = "CREATE TABLE movies (imdb_id TEXT PRIMARY KEY UNIQUE ON CONFLICT IGNORE, " +
                        "title TEXT NOT NULL, year INTEGER, certification TEXT, runtime INTEGER, " +
                        "genres TEXT, release_date TEXT, rating_trakt INTEGER, synopsis TEXT, " +
                        "tagline TEXT, poster TEXT, poster_large TEXT, trailer TEXT, in_movie_history INTEGER, " +
                        "in_watchlist INTEGER, in_recommended INTEGER, in_theatres INTEGER, in_coming_soon INTEGER )";

        String queryPeople = "CREATE TABLE people ( person_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "name TEXT NOT NULL, profile_image TEXT, role_name TEXT, character TEXT, " +
                            "writer_job TEXT, producer_executive TEXT)";

        String queryMoviePeople = "CREATE TABLE movies_people ( imdb_id INTEGER, person_id INTEGER, " +
                            "FOREIGN KEY(imdb_id) REFERENCES movies(imdb_id), " +
                            "FOREIGN KEY(person_id) REFERENCES people(person_id) )";

        db.execSQL(queryMovie);
        db.execSQL(queryPeople);
        db.execSQL(queryMoviePeople);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query1 = "DROP TABLE IF EXISTS movies";
        String query2 = "DROP TABLE IF EXISTS people";
        String query3 = "DROP TABLE IF EXISTS movies_people";
        db.execSQL(query1);
        db.execSQL(query2);
        db.execSQL(query3);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query1 = "DROP TABLE IF EXISTS movies";
        String query2 = "DROP TABLE IF EXISTS people";
        String query3 = "DROP TABLE IF EXISTS movies_people";
        db.execSQL(query1);
        db.execSQL(query2);
        db.execSQL(query3);
        onCreate(db);
    }

    public void insertHistoryMovie(Movie movie) {
        // Open a database for reading and writing
        SQLiteDatabase database = this.getWritableDatabase();

        movie.setInMovieHistory(true);
        insertMovie(movie, database);
    }

    public void insertWatchlistMovie(Movie movie) {
        // Open a database for reading and writing
        SQLiteDatabase database = this.getWritableDatabase();

        movie.setInWatchlist(true);
        insertMovie(movie, database);
    }

    public int updateHistoryMovie(String imdbId, int inMovieHistory) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("in_movie_history", inMovieHistory);

        return database.update("movies", values, "imdb_id = ?", new String[] {String.valueOf(imdbId)});
    }

    public int updateWatchlistMovie(String imdbId, int inWatchlist) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("in_watchlist", inWatchlist);

        return database.update("movies", values, "imdb_id = ?", new String[] {imdbId});
    }

    public void insertAllMovies(ArrayList<Movie> movies) {
        SQLiteDatabase database = this.getWritableDatabase();

        for(Movie movie: movies) {
            insertMovie(movie, database);
        }
    }

    public void deleteMovie(String imdbId) {
        SQLiteDatabase database = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM movies WHERE imdb_id='" + imdbId + "'";
        database.execSQL(deleteQuery);
    }

    public ArrayList<Movie> getAllMovies() {
        ArrayList<Movie> movies = new ArrayList<Movie>();
        SQLiteDatabase database = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM movies";
        movies = getMovies(selectQuery, movies, database);

        return movies;
    }

    public ArrayList<Movie> getMovieHistory() {
        ArrayList<Movie> movies = new ArrayList<Movie>();
        SQLiteDatabase database = this.getReadableDatabase();

        //TODO - getPeople to get people info for a particular movie
        String selectQuery = "SELECT * FROM movies WHERE in_movie_history=1";
        movies = getMovies(selectQuery, movies, database);

        return movies;
    }

    public ArrayList<Movie> getWatchlist() {
        ArrayList<Movie> movies = new ArrayList<Movie>();
        SQLiteDatabase database = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM movies WHERE in_watchlist=1";
        movies = getMovies(selectQuery, movies, database);

        return movies;
    }

    public ArrayList<Movie> getInTheatresMovies() {
        ArrayList<Movie> movies = new ArrayList<Movie>();
        SQLiteDatabase database = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM movies WHERE in_theatres=1";
        movies = getMovies(selectQuery, movies, database);

        return movies;
    }

    public ArrayList<Movie> getComingSoonMovies() {
        ArrayList<Movie> movies = new ArrayList<Movie>();
        SQLiteDatabase database = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM movies WHERE in_coming_soon=1";
        movies = getMovies(selectQuery, movies, database);

        return movies;
    }

    public Movie getSingleMovie(String imdbId) {
        SQLiteDatabase database = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM movies WHERE imdb_id='" + imdbId + "'";
        Cursor cursor = database.rawQuery(selectQuery, null);

        Movie movie = new Movie();
        // Move to the first row of the table
        if (cursor.moveToFirst()) {
            movie.setImdbId(cursor.getString(0));
            movie.setTitle(cursor.getString(1));
            movie.setYear(cursor.getInt(2));
            movie.setCertification(cursor.getString(3));
            movie.setRuntime(cursor.getInt(4));
            movie.setReleaseDate(cursor.getString(6));
            movie.setRating_trakt(cursor.getInt(7));
            movie.setSynopsis(cursor.getString(8));
            movie.setTagline(cursor.getString(9));
            movie.setPoster(cursor.getString(10));
            movie.setPosterLarge(cursor.getString(11));
            movie.setTrailer(cursor.getString(12));
            movie.setInMovieHistory(cursor.getInt(13) == 1);
            movie.setInWatchlist(cursor.getInt(14) == 1);
            movie.setInRecommended(cursor.getInt(15) == 1);
            movie.setInTheatres(cursor.getInt(16) == 1);
            movie.setInComingSoon(cursor.getInt(17) == 1);
            try {
                movie.setGenres(Arrays.asList(cursor.getString(5).split("\\s*,\\s*")));
            } catch(NullPointerException e) {
                movie.setGenres(null);
                e.getStackTrace();
            }

            movie.setActors(getAllActorInfo(movie.getImdbId()));
            movie.setDirectors(getAllDirectorInfo(movie.getImdbId()));
            movie.setProducers(getAllProducerInfo(movie.getImdbId()));
            movie.setWriters(getAllWriterInfo(movie.getImdbId()));

        } else {
            return null;
        }

        cursor.close();

        return movie;
    }

    private void insertMovie(Movie movie, SQLiteDatabase database) {
        // Store values to be passed to the database
        ContentValues values = new ContentValues();
        ContentValues people = new ContentValues();
        ContentValues reference = new ContentValues();

        values.put("imdb_id", movie.getImdbId());
        values.put("title", movie.getTitle());
        values.put("year", movie.getYear());
        values.put("certification", movie.getCertification());
        values.put("runtime", movie.getRuntime());
        values.put("genres", TextUtils.join(", " , movie.getGenres()));
        values.put("release_date", movie.getReleaseDate());
        values.put("rating_trakt", movie.getRating_trakt());
        values.put("synopsis", movie.getSynopsis());
        values.put("tagline", movie.getTagline());
        values.put("poster", movie.getPoster());
        values.put("poster_large", movie.getPosterLarge());
        values.put("trailer", movie.getTrailer());

        int inMovieHistory = movie.isInMovieHistory()?1:0;
        int inWatchlist = movie.isInWatchlist()?1:0;
        int inRecommended = movie.isInRecommended()?1:0;
        int inTheatres = movie.isInTheatres()?1:0;
        int inComingSoon = movie.isInComingSoon()?1:0;

        values.put("in_movie_history", inMovieHistory);
        values.put("in_watchlist", inWatchlist);
        values.put("in_recommended", inRecommended);
        values.put("in_theatres", inTheatres);
        values.put("in_coming_soon", inComingSoon);

        database.insertWithOnConflict("movies", null, values, SQLiteDatabase.CONFLICT_IGNORE);
        String test = getAllActorInfo(movie.getImdbId()).toString();
        if (movie.getActors() != null && getAllActorInfo(movie.getImdbId()).size() == 0) {
            for (List<String> actor: movie.getActors()) {
                people.put("name", actor.get(0));
                people.put("profile_image", actor.get(1));
                people.put("character", actor.get(2));
                people.put("role_name", "actor");
                long person_id = database.insert("people", null, people);

                // actor[3] has the people primary key
                actor.add(String.valueOf(person_id));
                reference.put("person_id", person_id);
                reference.put("imdb_id", movie.getImdbId());
                database.insert("movies_people", null, reference);
            }
        }

        if (movie.getDirectors() != null && getAllDirectorInfo(movie.getImdbId()).size() == 0) {
            for (List<String> director: movie.getDirectors()) {
                people.put("name", director.get(0));
                people.put("profile_image", director.get(1));
                people.put("role_name", "director");
                long person_id = database.insert("people", null, people);

                // director[2] has the people primary key
                director.add(String.valueOf(person_id));
                reference.put("person_id", person_id);
                reference.put("imdb_id", movie.getImdbId());
                database.insert("movies_people", null, reference);
            }
        }

        if (movie.getProducers() != null && getAllProducerInfo(movie.getImdbId()).size() == 0) {
            for (List<String> producer: movie.getProducers()) {
                people.put("name", producer.get(0));
                people.put("profile_image", producer.get(1));
                people.put("producer_executive", producer.get(2));
                people.put("role_name", "producer");
                long person_id = database.insert("people", null, people);

                // producer[3] has the people primary key
                producer.add(String.valueOf(person_id));
                reference.put("person_id", person_id);
                reference.put("imdb_id", movie.getImdbId());
                database.insert("movies_people", null, reference);
            }
        }

        if (movie.getWriters() != null && getAllDirectorInfo(movie.getImdbId()).size() == 0) {
            for (List<String> writer: movie.getWriters()) {
                people.put("name", writer.get(0));
                people.put("profile_image", writer.get(1));
                people.put("writer_job", writer.get(2));
                people.put("role_name", "writer");
                long person_id = database.insert("people", null, people);

                // writer[3] has the people primary key
                writer.add(String.valueOf(person_id));
                reference.put("person_id", person_id);
                reference.put("imdb_id", movie.getImdbId());
                database.insert("movies_people", null, reference);
            }
        }
    }

    private ArrayList<Movie> getMovies(String selectQuery, ArrayList<Movie> movies, SQLiteDatabase database) {
        // Cursor provides read/write access to data returned from query
        // rawQuery executes the query and returns a the result as a cursor
        Cursor cursor = database.rawQuery(selectQuery, null);

        // Move to the first row of the table
        if (cursor.moveToFirst()) {
            do {
                Movie movie = new Movie();

                movie.setImdbId(cursor.getString(0));
                movie.setTitle(cursor.getString(1));
                movie.setYear(cursor.getInt(2));
                movie.setCertification(cursor.getString(3));
                movie.setRuntime(cursor.getInt(4));
                movie.setReleaseDate(cursor.getString(6));
                movie.setRating_trakt(cursor.getInt(7));
                movie.setSynopsis(cursor.getString(8));
                movie.setTagline(cursor.getString(9));
                movie.setPoster(cursor.getString(10));
                movie.setPosterLarge(cursor.getString(11));
                movie.setTrailer(cursor.getString(12));
                movie.setInMovieHistory(cursor.getInt(13) == 1);
                movie.setInWatchlist(cursor.getInt(14) == 1);
                movie.setInRecommended(cursor.getInt(15) == 1);
                movie.setInTheatres(cursor.getInt(16) == 1);
                movie.setInComingSoon(cursor.getInt(17) == 1);
                try {
                    movie.setGenres(Arrays.asList(cursor.getString(5).split("\\s*,\\s*")));
                } catch(NullPointerException e) {
                    movie.setGenres(null);
                    e.getStackTrace();
                }

                movie.setActors(getAllActorInfo(movie.getImdbId()));
                movie.setDirectors(getAllDirectorInfo(movie.getImdbId()));
                movie.setProducers(getAllProducerInfo(movie.getImdbId()));
                movie.setWriters(getAllWriterInfo(movie.getImdbId()));

                movies.add(movie);
            } while(cursor.moveToNext());
        }
        cursor.close();

        return movies;
    }

    // Checks if exists in DB and if it does checks the value
    public boolean getInMovieHistory(String imdbId) {
        SQLiteDatabase database = this.getReadableDatabase();
        String selectQuery = "SELECT in_movie_history FROM movies WHERE imdb_id='" + imdbId + "'";
        Cursor cursor = database.rawQuery(selectQuery, null);

        return cursor.moveToFirst() && (cursor.getInt(cursor.getColumnIndexOrThrow("in_movie_history")) == 1);
    }

    public boolean getInWatchlist(String imdbId) {
        SQLiteDatabase database = this.getReadableDatabase();
        String selectQuery = "SELECT in_watchlist FROM movies WHERE imdb_id='" + imdbId + "'";
        Cursor cursor = database.rawQuery(selectQuery, null);

        return cursor.moveToFirst() && (cursor.getInt(cursor.getColumnIndexOrThrow("in_watchlist")) == 1);
    }


    public boolean getInTheatres(String imdbId) {
        SQLiteDatabase database = this.getReadableDatabase();
        String selectQuery = "SELECT in_theatres FROM movies WHERE imdb_id='" + imdbId + "'";
        Cursor cursor = database.rawQuery(selectQuery, null);

        return cursor.moveToFirst() && (cursor.getInt(cursor.getColumnIndexOrThrow("in_theatres")) == 1);
    }

    public boolean getInComingSoon(String imdbId) {
        SQLiteDatabase database = this.getReadableDatabase();
        String selectQuery = "SELECT in_coming_soon FROM movies WHERE imdb_id='" + imdbId + "'";
        Cursor cursor = database.rawQuery(selectQuery, null);

        return cursor.moveToFirst() && (cursor.getInt(cursor.getColumnIndexOrThrow("in_coming_soon")) == 1);
    }

    // Checks if exists in DB
    public boolean isInMovieHistory(String imdbId) {
        SQLiteDatabase database = this.getReadableDatabase();
        String selectQuery = "SELECT in_movie_history FROM movies WHERE imdb_id='" + imdbId + "'";
        Cursor cursor = database.rawQuery(selectQuery, null);

        return cursor.moveToFirst();
    }

    public boolean isInWatchlist(String imdbId) {
        SQLiteDatabase database = this.getReadableDatabase();
        String selectQuery = "SELECT in_watchlist FROM movies WHERE imdb_id='" + imdbId + "'";
        Cursor cursor = database.rawQuery(selectQuery, null);

        return cursor.moveToFirst();
    }

    // Get all people information
    public ArrayList<List<String>> getAllActorInfo(String imdb_id) {
        SQLiteDatabase database = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM people JOIN movies_people ON movies_people.person_id = people.person_id " +
                                "WHERE movies_people.imdb_id='" + imdb_id + "'" + "AND people.role_name='actor'";

        Cursor actorCursor = database.rawQuery(selectQuery, null);
        ArrayList<List<String>> actors = new ArrayList<List<String>>();
        if (actorCursor.moveToFirst()) {
            do {
                List<String> actor = new ArrayList<String>();
                actor.add(actorCursor.getString(1)); //Name
                actor.add(actorCursor.getString(2)); //Profile photo
                actor.add(actorCursor.getString(4)); //Character
                actors.add(actor);
            } while(actorCursor.moveToNext());
        }
        actorCursor.close();

        return actors;
    }

    public ArrayList<List<String>> getAllDirectorInfo(String imdb_id) {
        SQLiteDatabase database = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM people JOIN movies_people ON movies_people.person_id = people.person_id " +
                "WHERE movies_people.imdb_id='" + imdb_id + "'" + "AND people.role_name='director'";

        Cursor directorCursor = database.rawQuery(selectQuery, null);
        ArrayList<List<String>> directors = new ArrayList<List<String>>();
        if (directorCursor.moveToFirst()) {
            do {
                List<String> director = new ArrayList<String>();
                director.add(directorCursor.getString(1)); //Name
                director.add(directorCursor.getString(2)); //Profile photo
                directors.add(director);
            } while(directorCursor.moveToNext());
        }
        directorCursor.close();

        return directors;
    }

    public ArrayList<List<String>> getAllProducerInfo(String imdb_id) {
        SQLiteDatabase database = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM people JOIN movies_people ON movies_people.person_id = people.person_id " +
                "WHERE movies_people.imdb_id='" + imdb_id + "'" + "AND people.role_name='producer'";

        Cursor producerCursor = database.rawQuery(selectQuery, null);
        ArrayList<List<String>> producers = new ArrayList<List<String>>();
        if (producerCursor.moveToFirst()) {
            do {
                List<String> producer = new ArrayList<String>();
                producer.add(producerCursor.getString(1)); //Name
                producer.add(producerCursor.getString(2)); //Profile photo
                producer.add(producerCursor.getString(6)); //Executive producer
                producers.add(producer);
            } while(producerCursor.moveToNext());
        }
        producerCursor.close();

        return producers;
    }

    public ArrayList<List<String>> getAllWriterInfo(String imdb_id) {
        SQLiteDatabase database = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM people JOIN movies_people ON movies_people.person_id = people.person_id " +
                "WHERE movies_people.imdb_id='" + imdb_id + "'" + "AND people.role_name='writer'";

        Cursor writerCursor = database.rawQuery(selectQuery, null);
        ArrayList<List<String>> writers = new ArrayList<List<String>>();
        if (writerCursor.moveToFirst()) {
            do {
                List<String> writer = new ArrayList<String>();
                writer.add(writerCursor.getString(1)); //Name
                writer.add(writerCursor.getString(2)); //Profile photo
                writer.add(writerCursor.getString(5)); //Writer job
                writers.add(writer);
            } while(writerCursor.moveToNext());
        }
        writerCursor.close();

        return writers;
    }


    // Return the date in a nice format
    public String getFormattedDate(String releaseDate) {
        String[] parts = releaseDate.split("-");
        String formattedDate;

        // Day
        if(parts[2].equals("01"))
            formattedDate = parts[2].substring(1) + "st ";
        else if(parts[2].equals("02"))
            formattedDate = parts[2].substring(1) + "nd ";
        else if(parts[2].equals("03"))
            formattedDate = parts[2].substring(1) + "rd ";
        else if(parts[2].equals("31"))
            formattedDate = parts[2] + "st ";
        else if(parts[2].equals("04") || parts[2].equals("05") || parts[2].equals("06") || parts[2].equals("07") ||
                parts[2].equals("08") || parts[2].equals("09"))
            formattedDate = parts[2].substring(1) + "th ";
        else
            formattedDate = parts[2] + "th ";

        // Month
        if(parts[1].equals("1"))
            formattedDate += "January ";
        else if(parts[1].equals("2"))
            formattedDate += "February ";
        else if(parts[1].equals("3"))
            formattedDate += "March ";
        else if(parts[1].equals("4"))
            formattedDate += "April ";
        else if(parts[1].equals("5"))
            formattedDate += "May ";
        else if(parts[1].equals("6"))
            formattedDate += "June ";
        else if(parts[1].equals("7"))
            formattedDate += "July ";
        else if(parts[1].equals("8"))
            formattedDate += "August ";
        else if(parts[1].equals("9"))
            formattedDate += "September ";
        else if(parts[1].equals("10"))
            formattedDate += "October ";
        else if(parts[1].equals("11"))
            formattedDate += "November ";
        else if (parts[1].equals("12"))
            formattedDate += "December ";
        else
            return null;

        return formattedDate + parts[0];
    }

}
