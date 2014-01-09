package com.centerstage.limelight;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Smitesh Kharat on 12/7/13.
 */
public class MovieInfoCenter extends Fragment {

    DBTools dbTools = DBTools.getInstance(getActivity());

    public static MovieInfoCenter newInstance(String imdbId) {
        MovieInfoCenter movieInfoCenter = new MovieInfoCenter();
        Bundle args = new Bundle();
        args.putString("imdb_id", imdbId);
        movieInfoCenter.setArguments(args);
        return movieInfoCenter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_synopsis, container, false);

        String imdbId = getArguments().getString("imdb_id");
        Movie movie = dbTools.getSingleMovie(imdbId);

        TextView synopsis = (TextView) rootView.findViewById(R.id.synopsis);
        String test = movie.getSynopsis() + movie.getSynopsis() + movie.getSynopsis();
        synopsis.setText(test);

        return rootView;
    }

}
