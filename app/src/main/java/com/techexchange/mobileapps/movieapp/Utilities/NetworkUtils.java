package com.techexchange.mobileapps.movieapp.Utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.techexchange.mobileapps.movieapp.Movie;
import com.techexchange.mobileapps.movieapp.Review;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.techexchange.mobileapps.movieapp.MainActivity.TAG;

public class NetworkUtils {

    // Movie Data Parsing
    public static ArrayList<Movie> fetchData(String url) throws IOException {
        ArrayList<Movie> movies = new ArrayList<Movie>();

        try {
            URL new_url = new URL(url); //create a url from a String
            HttpURLConnection connection = (HttpURLConnection) new_url.openConnection(); //Opening a http connection  to the remote object
            connection.connect();

            InputStream inputStream = connection.getInputStream(); //reading from the object
            String results = convertStreamToString(inputStream);  //IOUtils to convert inputstream objects into Strings type
            parseJson(results, movies);
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();

        }

        return movies;
    }

    public static void parseJson(String data, ArrayList<Movie> list){

        try {
            JSONObject mainObject = new JSONObject(data);

            JSONArray resArray = mainObject.getJSONArray("results"); //Getting the results object
            for (int i = 0; i < resArray.length(); i++) {
                JSONObject jsonObject = resArray.getJSONObject(i);
                Movie movie = new Movie(); //New Movie object
                movie.setId(jsonObject.getInt("id"));
                movie.setVoteAverage(jsonObject.getString("vote_average"));
                movie.setVoteCount(jsonObject.getInt("vote_count"));
                movie.setOriginalTitle(jsonObject.getString("original_title"));
                movie.setTitle(jsonObject.getString("title"));
                movie.setPopularity(jsonObject.getDouble("popularity"));
                movie.setBackdropPath(jsonObject.getString("backdrop_path"));
                movie.setOverview(jsonObject.getString("overview"));
                movie.setReleaseDate(jsonObject.getString("release_date"));
                movie.setPosterPath(jsonObject.getString("poster_path"));
                //Adding a new movie object into ArrayList
                list.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Error occurred during JSON Parsing", e);
        }

    }

    public static Boolean networkStatus(Context context){
        ConnectivityManager manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()){
            return true;
        }
        return false;
    }

    public static String convertStreamToString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    // Trailer Parsing
    public static ArrayList<String> fetchTrailer(String url) throws IOException {
        ArrayList<String> trailers = new ArrayList<String>();

        try {
            URL new_url = new URL(url); //create a url from a String
            HttpURLConnection connection = (HttpURLConnection) new_url.openConnection(); //Opening a http connection  to the remote object
            connection.connect();

            InputStream inputStream = connection.getInputStream(); //reading from the object
            String results = convertStreamToString(inputStream);  //IOUtils to convert inputstream objects into Strings type
            parseJsonTrailer(results, trailers);
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();

        }

        return trailers;
    }

    public static void parseJsonTrailer(String data, ArrayList<String> list){

        try {
            JSONObject mainObject = new JSONObject(data);

            JSONArray resArray = mainObject.getJSONArray("results"); //Getting the results object
            for (int i = 0; i < resArray.length(); i++) {
                JSONObject jsonObject = resArray.getJSONObject(i);
                String linkID;
                linkID = jsonObject.getString("key");

                //Adding a new movie object into ArrayList
                list.add(linkID);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Error occurred during JSON Parsing", e);
        }

    }

    // Review Parsing
    public static ArrayList<Review> fetchReview(String url) throws IOException {
        ArrayList<Review> reviews = new ArrayList<Review>();

        try {
            URL new_url = new URL(url); //create a url from a String
            HttpURLConnection connection = (HttpURLConnection) new_url.openConnection(); //Opening a http connection  to the remote object
            connection.connect();

            InputStream inputStream = connection.getInputStream(); //reading from the object
            String results = convertStreamToString(inputStream);  //IOUtils to convert inputstream objects into Strings type
            parseJsonReview(results, reviews);
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();

        }

        return reviews;
    }

    public static void parseJsonReview(String data, ArrayList<Review> list){

        try {
            JSONObject mainObject = new JSONObject(data);

            JSONArray resArray = mainObject.getJSONArray("results"); //Getting the results object
            for (int i = 0; i < resArray.length(); i++) {
                JSONObject jsonObject = resArray.getJSONObject(i);
                Review review = new Review();
                review.setAuthor(jsonObject.getString("author"));
                review.setContent(jsonObject.getString("content"));
                review.setId(jsonObject.getString("id"));
                review.setUrl(jsonObject.getString("url"));
                //Adding a new review object into ArrayList
                list.add(review);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Error occurred during JSON Parsing", e);
        }

    }
}

