package com.centerstage.limelight;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
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
import android.app.Fragment;
import android.app.FragmentManager;


public class MovieNightFragment extends Fragment  {

    public static final String ARG_SECTION_NUMBER = "section_number";
    DBTools dbTools = DBTools.getInstance(getActivity());

    public static MovieNightFragment newInstance(int sectionNumber) {
        MovieNightFragment movieNightFragment = new MovieNightFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        movieNightFragment.setArguments(args);
        return movieNightFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_night, container, false);
        ImageButton Action = (ImageButton) rootView.findViewById(R.id.Action);
        ImageButton Comedy = (ImageButton) rootView.findViewById(R.id.Comedy);
        ImageButton Music = (ImageButton) rootView.findViewById(R.id.Music);
        ImageButton Documentary = (ImageButton) rootView.findViewById(R.id.Documentary);
        ImageButton Mystery = (ImageButton) rootView.findViewById(R.id.Mystery);
        ImageButton Sports = (ImageButton) rootView.findViewById(R.id.Sports);
        ImageButton Romance = (ImageButton) rootView.findViewById(R.id.Romance);
        ImageButton Fantasy = (ImageButton) rootView.findViewById(R.id.Fantasy);
        ImageButton Drama = (ImageButton) rootView.findViewById(R.id.Drama);
        Button TopMovies = (Button) rootView.findViewById(R.id.Top_Movies);


        Action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
         FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .addToBackStack("movie_night")
                        .replace(R.id.container, MovieNightListFragment.newInstance(4, "Action"), "Action")
                        .commit();
            }
        });

        Comedy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .addToBackStack("movie_night")
                        .replace(R.id.container, MovieNightListFragment.newInstance(4, "Comedy"), "Comedy")
                        .commit();
            }
        });

        Music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .addToBackStack("movie_night")
                        .replace(R.id.container, MovieNightListFragment.newInstance(4, "Music"), "Music")
                        .commit();
            }
        });

        Documentary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .addToBackStack("movie_night")
                        .replace(R.id.container, MovieNightListFragment.newInstance(4, "Documentary"), "Documentary")
                        .commit();
            }
        });

        Mystery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .addToBackStack("movie_night")
                        .replace(R.id.container, MovieNightListFragment.newInstance(4, "Mystery"), "Mystery")
                        .commit();
            }
        });

        Sports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .addToBackStack("movie_night")
                        .replace(R.id.container, MovieNightListFragment.newInstance(4, "Sports"), "Sports")
                        .commit();
            }
        });

        Romance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .addToBackStack("movie_night")
                        .replace(R.id.container, MovieNightListFragment.newInstance(4, "Romance"), "Romance")
                        .commit();
            }
        });

        Fantasy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .addToBackStack("movie_night")
                        .replace(R.id.container, MovieNightListFragment.newInstance(4, "Fantasy"), "Fantasy")
                        .commit();
            }
        });

        Drama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .addToBackStack("movie_night")
                        .replace(R.id.container, MovieNightListFragment.newInstance(4, "Drama"), "Drama")
                        .commit();
            }
        });

        TopMovies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .addToBackStack("movie_night")
                        .replace(R.id.container, MovieNightListFragment.newInstance(4, "Top Movies"), "Top Movies")
                        .commit();
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((LimelightApp) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }
}