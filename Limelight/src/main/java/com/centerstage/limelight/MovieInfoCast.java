package com.centerstage.limelight;

import android.app.Fragment;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Smitesh Kharat on 12/7/13.
 */
public class MovieInfoCast extends ListFragment {

    DBTools dbTools = DBTools.getInstance(getActivity());

    public static MovieInfoCast newInstance(String imdbId) {
        MovieInfoCast movieInfoCast = new MovieInfoCast();
        Bundle args = new Bundle();
        args.putString("imdb_id", imdbId);
        movieInfoCast.setArguments(args);
        return movieInfoCast;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_cast, container, false);

        String imdbId = getArguments().getString("imdb_id");
        Movie movie = dbTools.getSingleMovie(imdbId);

        // Create the list view
        ArrayList<List<String>> actors = movie.getActors();

        if (actors != null) {
            CastAdapter castAdapter = new CastAdapter(getActivity(), R.layout.fragment_cast_item, actors);
            setListAdapter(castAdapter);
        }
        return rootView;
    }

    // Custom adapter for Movie Cast
    private class CastAdapter extends ArrayAdapter<List<String>> {
        private Context context;
        private ArrayList<List<String>> actors;
        private final ImageDownloader imageDownloader = new ImageDownloader();

        public CastAdapter(Context context, int textViewResourceId, ArrayList<List<String>> actors) {
            super(context, textViewResourceId, actors);
            this.actors = actors;
            this.context = context;
        }

        @Override
        public List<String> getItem(int position) {
            return actors.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.fragment_cast_item, parent, false);

                holder = new ViewHolder();
                try {
                holder.actorName = (TextView) convertView.findViewById(R.id.actor_name);
                } catch (NullPointerException e) {
                    throw new NullPointerException();
                }
                holder.actorCharacter = (TextView) convertView.findViewById(R.id.actor_character);
                holder.actorProfile = (ImageView) convertView.findViewById(R.id.actor_profile);
                holder.castButton = (Button) convertView.findViewById(R.id.cast_button);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            List<String> actor = getItem(position);
            if (actor != null) {
                imageDownloader.download(actor.get(1), holder.actorProfile);
                holder.actorName.setText(actor.get(0));
                holder.actorCharacter.setText(actor.get(2));
            }

            return convertView;
        }
    }
}
