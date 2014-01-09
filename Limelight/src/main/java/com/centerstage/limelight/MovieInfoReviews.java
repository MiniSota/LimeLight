package com.centerstage.limelight;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Smitesh Kharat on 12/8/13.
 */
public class MovieInfoReviews extends ListFragment {

    DBTools dbTools = DBTools.getInstance(getActivity());

    public static MovieInfoReviews newInstance(String imdbId) {
        MovieInfoReviews movieInfoReviews = new MovieInfoReviews();
        Bundle args = new Bundle();
        args.putString("imdb_id", imdbId);
        movieInfoReviews.setArguments(args);
        return movieInfoReviews;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_reviews, container, false);

        String imdbId = getArguments().getString("imdb_id");
        Movie movie = dbTools.getSingleMovie(imdbId);

        // Create the list view
      //  ArrayList<List<String>> actors = movie.getActors();

     //   if (actors != null) {
          //  ReviewAdapter reviewAdapter = new ReviewAdapter(getActivity(), R.layout.fragment_review_item, actors);
          //  setListAdapter(reviewAdapter);
      //  }
        return rootView;
    }

}
