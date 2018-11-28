package com.techexchange.mobileapps.movieapp;

import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.techexchange.mobileapps.movieapp.Utilities.MovieDatabase;
import com.techexchange.mobileapps.movieapp.Utilities.NetworkUtils;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "Deez";
    public static final String SHARED_PREFS_FILE_NAME = "Favorite Movie List";
    public static final String FAVORITE_LIST_KEY = "Favorite List Key";
    private boolean displayView = false;
    String popularMoviesURL, ratedMoviesURL;
    ArrayList<Movie> mPopularList = new ArrayList<>();
    ArrayList<Movie> mTopTopRatedList = new ArrayList<>();
    public static ArrayList<Movie> mFavoriteList = new ArrayList<>();
    public static String newID = "";
    int savedList = 0;

    public MovieDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ButterKnife.bind(this);
        new MovieGetter().execute();

        while(!displayView) {
            Log.d(TAG, "Loading");
        }
        database = new MovieDatabase(this);

        if(savedInstanceState != null)
        {
            savedList = savedInstanceState.getInt("CurrentList");
        }

        if(savedList == 0)
        {
            InitRecyclerView(mPopularList, false);
        } if(savedList == 1)
        {
            InitRecyclerView(mTopTopRatedList, false);
        } if(savedList == 2)
        {
            InitRecyclerView(mFavoriteList, false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Creation");

        if(newID != "")
        {
            Log.d(TAG, String.valueOf(newID));
            database.addData(newID);
            newID = "";
        }
    }

    private void InitRecyclerView(ArrayList<Movie> movieList, boolean favorites)
    {
        if(favorites)
        {
            ArrayList<String> idList = GetIDData();
            saveIntoFavoritesList(idList);
        }

        RecyclerView recyclerView = findViewById(R.id.movie_list);
        RecyclerAdapter recyclerViewAdapter = new RecyclerAdapter(this, movieList);

        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        }
        else
        {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();
    }

    private ArrayList<String> GetIDData()
    {
        Cursor data = database.getData();
        ArrayList<String> tempData = new ArrayList<>();
        while(data.moveToNext())
        {
            Log.d(TAG, "DAta");
            tempData.add(data.getString(1));
        }
        return tempData;
    }

    private void saveIntoFavoritesList(ArrayList<String> idList)
    {
        mFavoriteList.clear();
        for(int i = 0; i < idList.size(); i++)
        {
            Log.d(TAG, String.valueOf(idList.size()));
            for(int j = 0; j < mPopularList.size(); j++)
            {
                Log.d(TAG, String.valueOf(mPopularList.get(j).getId()));

                if(idList.get(i).equals(String.valueOf(mPopularList.get(j).getId())))
                {
                    mFavoriteList.add(mPopularList.get(j));
                    idList.remove(i);
                    i--;
                    break;
                }
            }
        }

        for(int i = 0; i < idList.size(); i++)
        {
            for(int j = 0; j < mTopTopRatedList.size(); j++)
            {
                if(idList.get(i).equals(String.valueOf(mTopTopRatedList.get(j).getId())))
                {
                    mFavoriteList.add(mTopTopRatedList.get(j));
                    idList.remove(i);
                    i--;
                    break;
                }
            }
        }

        Log.d(TAG, String.valueOf(mFavoriteList.size()));
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putInt("CurrentList", savedList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.d(TAG, "g");

        if (id == R.id.pop_movies) {
            InitRecyclerView(mPopularList, false);
            savedList = 0;
        }
        if (id == R.id.top_movies) {
            InitRecyclerView(mTopTopRatedList, false);
            savedList = 1;
        }

        if (id == R.id.fav_movies) {
            Log.d(TAG, "g");
            InitRecyclerView(mFavoriteList, true);
            savedList = 2;
        }
        return super.onOptionsItemSelected(item);
    }


    public class MovieGetter extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            popularMoviesURL = "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=27e151a219f0a3a44542390fc3123cbe";

            ratedMoviesURL = "http://api.themoviedb.org/3/discover/movie?sort_by=vote_average.desc&api_key=27e151a219f0a3a44542390fc3123cbe";

            mPopularList = new ArrayList<>();
            mTopTopRatedList = new ArrayList<>();
            try {
                if(NetworkUtils.networkStatus(MainActivity.this)){
                    mPopularList = NetworkUtils.fetchData(popularMoviesURL); //Get popular movies
                    mTopTopRatedList = NetworkUtils.fetchData(ratedMoviesURL); //Get top rated movies
                    Log.d(TAG, String.valueOf(mPopularList.size()));

                }else{
                    Toast.makeText(MainActivity.this,"No Internet Connection", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Fuck");

                }
            } catch (IOException e){
                e.printStackTrace();
                Log.d(TAG, "Fuck Me");

            }
            displayView = true;
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