package cn.wontao.popmovies;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

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
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 */
public class DisplayFragment extends Fragment {
    private MovieAdapter movieAdapter;
    private ArrayList<Movie> movieList;
    Movie[] movies;
    private static final String POPULAR = "popular";
    private static final String TOP_RATED = "top_rated";
    private static final String STAE_SAVE = "moives";
    private static final String WARNING = "No network connection!Please connect wifi or mobile network retry.";

    public DisplayFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (savedInstanceState != null && savedInstanceState.containsKey("movies")) {
            movieList = savedInstanceState.getParcelableArrayList(STAE_SAVE);
        }else {
            movieList = new ArrayList<Movie>();
        }

        //Only once
        boolean flag = true;
        if (flag) {
            if (isOnline()) {
                updateMovies(POPULAR);
                flag = false;
            } else {
                Toast.makeText(getContext(), WARNING, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(STAE_SAVE, movieList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.displayfragment, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_display, container, false);

        movieAdapter = new MovieAdapter(getActivity(), movieList);

        GridView gridView = (GridView) rootView.findViewById(R.id.movies_grid);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Movie movie = movieAdapter.getItem(i);
                Intent intent = new Intent(getContext(), DetailActivity.class);
                intent.putExtra("movie", movie);
                startActivity(intent);
            }
        });
        gridView.setAdapter(movieAdapter);
        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_popular) {
            if (isOnline()) {
                updateMovies(POPULAR);
            } else {
                Toast.makeText(getContext(), WARNING, Toast.LENGTH_LONG).show();
            }
            return true;
        }
        if (id == R.id.action_top_rated) {
            if (isOnline()) {
                updateMovies(TOP_RATED);
            } else {
                Toast.makeText(getContext(), WARNING, Toast.LENGTH_LONG).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateMovies(String orderStr) {
        FetchMovieTask fetchMovieTask = new FetchMovieTask();
        fetchMovieTask.execute(orderStr);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public class FetchMovieTask extends AsyncTask<String, Void, Void> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        private void getMovieDataFromJson(String movieJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_RESULTS = "results";
            final String OWM_OVERVIEW = "overview";
            final String OWM_POSTER_PATH = "poster_path";
            final String OWM_RELEASE_DATE = "release_date";
            final String OWM_ORIGINAL_TITLE = "original_title";
            final String OWM_VOTE_AVERAGE = "vote_average";

            String baseURL = "http://image.tmdb.org/t/p/w185";

            JSONObject moviesJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = moviesJson.getJSONArray(OWM_RESULTS);

            if (movieArray != null) {
                movies = new Movie[movieArray.length()];
            }

            for (int i = 0; i < movieArray.length(); i++) {

                String originalTitle;
                String imageURL;
                String overview;
                double voteAverage;
                String releaseDate;

                // Get the JSON object representing movies
                JSONObject movieObject = movieArray.getJSONObject(i);
                originalTitle = movieObject.getString(OWM_ORIGINAL_TITLE);
                imageURL = baseURL + movieObject.getString(OWM_POSTER_PATH);
                overview = movieObject.getString(OWM_OVERVIEW);
                voteAverage = movieObject.getDouble(OWM_VOTE_AVERAGE);
                releaseDate = movieObject.getString(OWM_RELEASE_DATE);

                movies[i] = new Movie(originalTitle, imageURL, overview, voteAverage, releaseDate);
            }
            movieList = new ArrayList<Movie>(Arrays.asList(movies));
        }

        @Override
        protected Void doInBackground(String... parm) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;


            String appId = "[YOUR_API_KEY]";

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                //http://api.themoviedb.org/3/movie/popular?api_key = [YOUR_API_KEY]

                final String FORECAST_BASE_URL = "https://api.themoviedb.org/3/movie/" + parm[0] + "?";
                final String APPID_PARAM = "api_key";
                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(APPID_PARAM, appId).build();

                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
                getMovieDataFromJson(moviesJsonStr);
                return null;
//                Log.v(LOG_TAG, "Forecast JSOn String: " + moviesJsonStr);

            } catch (Exception e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            movieAdapter.clear();
            for (Movie movie : movieList){
                movieAdapter.add(movie);
            }
        }
    }
}
