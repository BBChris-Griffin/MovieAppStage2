package com.techexchange.mobileapps.movieapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.techexchange.mobileapps.movieapp.Utilities.MovieDatabase;
import com.techexchange.mobileapps.movieapp.Utilities.NetworkUtils;

import java.io.IOException;
import java.util.ArrayList;

import static com.techexchange.mobileapps.movieapp.MainActivity.FAVORITE_LIST_KEY;
import static com.techexchange.mobileapps.movieapp.MainActivity.SHARED_PREFS_FILE_NAME;
import static com.techexchange.mobileapps.movieapp.MainActivity.mFavoriteList;
import static com.techexchange.mobileapps.movieapp.MainActivity.newID;
import static com.techexchange.mobileapps.movieapp.RecyclerAdapter.MOVIE_INDEX;
import static com.techexchange.mobileapps.movieapp.RecyclerAdapter.MOVIE_URL;
import static com.techexchange.mobileapps.movieapp.RecyclerAdapter.movieList;

public class MovieInfo extends AppCompatActivity {

    ImageView moviePoster;
    ImageView playIcon;
    TextView movieTitle;
    TextView movieRating;
    TextView movieDescription;
    TextView movieDate;
    TextView reviewText;
    Button favorite;
    String trailerURL, reviewURL;
    ArrayList<String> trailerList;
    ArrayList<Review> reviewList;
    MovieDatabase database;
    boolean showTrailers, showReviews;
    private int index;
    private int trailerPlay;
    private int margin = 60;
    private ImageView play;
    private TextView trailerText;
    private ArrayList<ImageView> plays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_info);

        LinearLayout layout = findViewById(R.id.trailerANDreviews);
        LayoutInflater inflater = LayoutInflater.from(this);

        moviePoster = findViewById(R.id.movie_poster);
        movieTitle = findViewById(R.id.movie_title);
        movieRating = findViewById(R.id.movie_rating);
        movieDescription = findViewById(R.id.movie_description);
        movieDate = findViewById(R.id.movie_date);
        favorite = findViewById(R.id.fav_button);
//        playIcon = findViewById(R.id.play);
//        trailerText = findViewById(R.id.trailer_text);
//        reviewText = findViewById(R.id.review_text);

        index = getIntent().getIntExtra(MOVIE_INDEX, 0);
        Movie movie = movieList.get(index);
        showTrailers = false;
        showReviews = false;

        new TrailerGetter().execute();
        new ReviewGetter().execute();

        Picasso.with(this)
                .load(MOVIE_URL+ movie.getPosterPath())
                .placeholder(R.drawable.image_placeholder)
                .into(moviePoster);

        movieTitle.setText(String.valueOf(movie.getOriginalTitle()));
        movieRating.setText(String.valueOf(movie.getVoteAverage()) + "/10");
        movieDescription.setText(String.valueOf(movie.getOverview()));
        movieDate.setText(String.valueOf(movie.getReleaseDate()));

        favorite.setOnClickListener(v -> OnFavoriteClick(movie));

        while(!showTrailers || !showReviews){
            Log.d(MainActivity.TAG, "Loading");
        }

        // Use Trailer and Review Stuff
        ArrayList<ImageView> trailerIcon = new ArrayList<>();

        for(int i = 0; i < trailerList.size(); i++)
        {

            TextView trailerNum = new TextView(this);
            trailerNum.setText("Trailer " + (i+1));

            String link = "https://www.youtube.com/watch?v=" + trailerList.get(i);
            ImageView image = new ImageView(this);
            image.setImageDrawable(getDrawable(R.drawable.play_icon));

            image.setOnClickListener(v -> OnPlayTrailerClick(link));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(500, 500);


            trailerNum.setLayoutParams(params);
            image.setLayoutParams(imageParams);

            layout.addView(trailerNum);
            layout.addView(image);

        }

        for(int i = 0; i < reviewList.size(); i++)
        {
            TextView author = new TextView(this);
            TextView review = new TextView(this);

            author.setText(reviewList.get(i).author);
            review.setText(reviewList.get(i).content);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            author.setLayoutParams(params);
            review.setLayoutParams(params);

            layout.addView(author);
            layout.addView(review);
        }
        //playIcon.setOnClickListener(v -> OnPlayTrailerClick());
    }

    private void OnFavoriteClick(Movie movie)
    {
        if(!FindFavoriteMovie(movie))
        {
            mFavoriteList.add(movieList.get(index));
        }
        else
        {
            mFavoriteList.remove(movie);
        }
        Toast.makeText(this, "Action Complete", Toast.LENGTH_SHORT).show();
        newID = String.valueOf(movie.getId());
    }

    private boolean FindFavoriteMovie(Movie target)
    {
        for(int i = 0; i < mFavoriteList.size(); i++)
        {
            if(mFavoriteList.get(i) == target)
            {
                return true;
            }
        }
        return false;
    }

    private void OnPlayTrailerClick(String link)
    {
        Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        try {
            this.startActivity(youtubeIntent);
        } catch (ActivityNotFoundException ex) {
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("CurrentIndex", index);
    }


    public class TrailerGetter extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            trailerURL = "http://api.themoviedb.org/3/movie/" + movieList.get(index).getId() + "/videos?api_key=27e151a219f0a3a44542390fc3123cbe";

            trailerList = new ArrayList<>();
            try {
                if(NetworkUtils.networkStatus(MovieInfo.this)){
                    trailerList = NetworkUtils.fetchTrailer(trailerURL); //Get popular movies

                }else{
                    Toast.makeText(MovieInfo.this,"No Internet Connection", Toast.LENGTH_LONG).show();

                }
            } catch (IOException e){
                e.printStackTrace();

            }
            showTrailers = true;
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void  s) {
            super.onPostExecute(s);
        }
    }

    public class ReviewGetter extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            reviewURL = "http://api.themoviedb.org/3/movie/" + movieList.get(index).getId() + "/reviews?api_key=27e151a219f0a3a44542390fc3123cbe";

            reviewList = new ArrayList<>();
            try {
                if(NetworkUtils.networkStatus(MovieInfo.this)){
                    reviewList = NetworkUtils.fetchReview(reviewURL); //Get popular movies
                }else{
                    Toast.makeText(MovieInfo.this,"No Internet Connection", Toast.LENGTH_LONG).show();

                }
            } catch (IOException e){
                e.printStackTrace();

            }
            showReviews = true;
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void  s) {
            super.onPostExecute(s);
        }
    }
}