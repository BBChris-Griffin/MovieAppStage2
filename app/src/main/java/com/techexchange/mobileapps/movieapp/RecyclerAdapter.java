package com.techexchange.mobileapps.movieapp;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;
import com.techexchange.mobileapps.movieapp.Utilities.MovieDatabase;

import java.io.Serializable;
import java.util.ArrayList;

import static com.techexchange.mobileapps.movieapp.MainActivity.TAG;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>{

    public static ArrayList<Movie> movieList = new ArrayList<>();
    public static final String MOVIE_URL="https://image.tmdb.org/t/p/w185";
    private Context context;
    static final String MOVIE_INDEX = "Movie";
    public MovieDatabase database;


    public RecyclerAdapter(Context context, ArrayList<Movie> movieList) {
        this.movieList = movieList;
        this.context = context;
        Log.d(TAG, String.valueOf(movieList.size()));

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_layout, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Picasso.with(context)
                .load(MOVIE_URL+movieList.get(i).getPosterPath())
                .placeholder(R.drawable.image_placeholder)
                .into(viewHolder.movie);

        viewHolder.parentLayout.setOnClickListener(v -> OnMovieClicked(i));
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    private void OnMovieClicked(int index)
    {
        Intent intent = new Intent(context, MovieInfo.class);
        intent.putExtra(MOVIE_INDEX, index);
        intent.putExtra("MOVIE_DATABASE", database);
        context.startActivity(intent);
    }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        ImageView imageView;
//        Movie movies = getItem(position);
//        RelativeLayout relativeLayout = new RelativeLayout(context);
//        relativeLayout.setLayoutParams(new ViewGroup.LayoutParams(200, 300));
//        if (convertView == null) {
//            // if it's not recycled, initialize some attributes
//            imageView = new ImageView(context);
//            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//            imageView.setAdjustViewBounds(true);
//            relativeLayout.addView(imageView);
//        } else {
//            imageView = (ImageView) convertView;
//        }
//
//        //load data into the ImageView using Picasso
//        Picasso.with(context).load(MOVIE_URL + movies.getPosterPath())
//                .placeholder(R.drawable.image_placeholder)
//                .into(imageView);
//
//        return imageView;
//    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView movie;
        FrameLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            movie = itemView.findViewById((R.id.movie_image));
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
