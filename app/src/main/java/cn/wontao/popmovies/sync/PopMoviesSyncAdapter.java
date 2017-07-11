package cn.wontao.popmovies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import cn.wontao.popmovies.R;
import cn.wontao.popmovies.data.MoviesContract;

public class PopMoviesSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = PopMoviesSyncAdapter.class.getSimpleName();

    private static final String POPULAR = "popular";
    private static final String TOP_RATED = "top_rated";
    // 同步数据的时间间隔，单位毫秒。
    // 60秒（1分钟）x 180 = 3小时
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    public static final String appId = "";//Here need API_KEY
    public static final String language = "en";
//    public static final String language = "zh-CN";

    private static final int INDEX_WEATHER_ID = 0;
    private static final int INDEX_MAX_TEMP = 1;
    private static final int INDEX_MIN_TEMP = 2;
    private static final int INDEX_SHORT_DESC = 3;

    public PopMoviesSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting Sync.");

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String moviesJsonStr = null;

        try {
            //http://api.themoviedb.org/3/movie/popular?api_key = [YOUR_API_KEY]

            String BASE_URL = "https://api.themoviedb.org/3/movie/popular";
            final String APP_ID_PARAM = "api_key";
            final String LANGUAGE = "language";
            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(APP_ID_PARAM, appId)
                    .appendQueryParameter(LANGUAGE, language).build();

            moviesJsonStr = getJsonStrFromURL(builtUri.toString());
            getMovieDataFromJson(moviesJsonStr, POPULAR);

            BASE_URL = "https://api.themoviedb.org/3/movie/" + TOP_RATED + "?";

            builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(APP_ID_PARAM, appId)
                    .appendQueryParameter(LANGUAGE, language).build();

            moviesJsonStr = getJsonStrFromURL(builtUri.toString());
            getMovieDataFromJson(moviesJsonStr, TOP_RATED);


        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error ", e);
            return;
        }
    }

    public String getJsonStrFromURL(String baseUrl) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(baseUrl);

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                return null;
            }
            return buffer.toString();

        } catch (Exception e) {
            Log.e(LOG_TAG, "Error ", e);
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

    private void getMovieReviewsDataFromJson(int movieId)
            throws JSONException {

        String BASE_URL = "https://api.themoviedb.org/3/movie/" + movieId + "/reviews?";
        final String APP_ID_PARAM = "api_key";
        final String LANGUAGE = "language";

        final String OWM_RESULTS = "results";
        final String OWM_ID = "id";
        final String OWM_AUTHOR = "author";
        final String OWM_CONTENT = "content";
        final String OWM_URL = "url";

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(APP_ID_PARAM, appId)
                .appendQueryParameter(LANGUAGE, language).build();

        String movieReviewJsonStr = getJsonStrFromURL(builtUri.toString());

        JSONObject reviewsJson = new JSONObject(movieReviewJsonStr);
        JSONArray reviewArray = reviewsJson.getJSONArray(OWM_RESULTS);

        if (movieId == reviewsJson.getInt("id")) {
            for (int i = 0; i < reviewArray.length(); i++) {
                String id;
                String author;
                String content;
                String url;

                JSONObject reviewJson = reviewArray.getJSONObject(i);
                id = reviewJson.getString(OWM_ID);
                author = reviewJson.getString(OWM_AUTHOR);
                content = reviewJson.getString(OWM_CONTENT);
                url = reviewJson.getString(OWM_URL);

                ContentValues reviewValues = new ContentValues();
                reviewValues.put(MoviesContract.MovieReviewEntry.COLUMN_MOVIE_ID, movieId);
                reviewValues.put(MoviesContract.MovieReviewEntry.COLUMN_REVIEW_ID, id);
                reviewValues.put(MoviesContract.MovieReviewEntry.COLUMN_AUTHOR, author);
                reviewValues.put(MoviesContract.MovieReviewEntry.COLUMN_CONTENT, content);
                reviewValues.put(MoviesContract.MovieReviewEntry.COLUMN_URL, url);

                getContext().getContentResolver().insert(MoviesContract.MovieReviewEntry.CONTENT_URI, reviewValues);
            }
        }
    }

    private void getMovieTrailerDataFromJson(int movieId)
            throws JSONException {

        String BASE_URL = "https://api.themoviedb.org/3/movie/" + movieId + "/videos?";
        final String APP_ID_PARAM = "api_key";
        final String LANGUAGE = "language";

        final String OWM_RESULTS = "results";
        final String OWM_ID = "id";
        final String OWM_NAME = "name";
        final String OWM_KEY = "key";
        final String OWM_TYPE = "type";

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(APP_ID_PARAM, appId)
                .appendQueryParameter(LANGUAGE, language).build();

        String movieTrailerJsonStr = getJsonStrFromURL(builtUri.toString());

        JSONObject trailersJson = new JSONObject(movieTrailerJsonStr);
        JSONArray trailerArray = trailersJson.getJSONArray(OWM_RESULTS);

        if (movieId == trailersJson.getInt("id")) {
            for (int i = 0; i < trailerArray.length(); i++) {
                String id;
                String name;
                String key;
                String type;

                JSONObject trailerJson = trailerArray.getJSONObject(i);
                id = trailerJson.getString(OWM_ID);
                name = trailerJson.getString(OWM_NAME);
                key = trailerJson.getString(OWM_KEY);
                type = trailerJson.getString(OWM_TYPE);

                if ("Trailer".equals(type)) {
                    ContentValues trailerValues = new ContentValues();
                    trailerValues.put(MoviesContract.MovieTrailerEntry.COLUMN_MOVIE_ID, movieId);
                    trailerValues.put(MoviesContract.MovieTrailerEntry.COLUMN_TRAILER_ID, id);
                    trailerValues.put(MoviesContract.MovieTrailerEntry.COLUMN_NAME, name);
                    trailerValues.put(MoviesContract.MovieTrailerEntry.COLUMN_KEY, key);

                    getContext().getContentResolver().insert(MoviesContract.MovieTrailerEntry.CONTENT_URI, trailerValues);
                }
            }
        }
    }

    private void getMovieDataFromJson(String movieJsonStr, String type)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String OWM_ID = "id";
        final String OWM_RESULTS = "results";
        final String OWM_OVERVIEW = "overview";
        final String OWM_POSTER_PATH = "poster_path";
        final String OWM_RELEASE_DATE = "release_date";
        final String OWM_ORIGINAL_TITLE = "title";
        final String OWM_VOTE_AVERAGE = "vote_average";
        final String OWM_MOVIE_TYPE = "movie_type";

        String baseURL = "http://image.tmdb.org/t/p/w185";

        JSONObject moviesJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = moviesJson.getJSONArray(OWM_RESULTS);

        Vector<ContentValues> cVVector = new Vector<ContentValues>(movieArray.length());

        for (int i = 0; i < movieArray.length(); i++) {
            int id;
            String originalTitle;
            String imageURL;
            String overview;
            double voteAverage;
            String releaseDate;
            String movieType;

            // Get the JSON object representing movies
            JSONObject movieObject = movieArray.getJSONObject(i);

            id = movieObject.getInt(OWM_ID);
            originalTitle = movieObject.getString(OWM_ORIGINAL_TITLE);
            imageURL = baseURL + movieObject.getString(OWM_POSTER_PATH);
            overview = movieObject.getString(OWM_OVERVIEW);
            voteAverage = movieObject.getDouble(OWM_VOTE_AVERAGE);
            releaseDate = movieObject.getString(OWM_RELEASE_DATE);
            movieType = type;

            ContentValues movieValues = new ContentValues();

            movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_ID, id);
            movieValues.put(MoviesContract.MovieEntry.COLUMN_IMAGE_URL, imageURL);
            movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_TYPE, movieType);
            movieValues.put(MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE, originalTitle);
            movieValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
            movieValues.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, voteAverage);
            movieValues.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, overview);

            cVVector.add(movieValues);

            getMovieTrailerDataFromJson(id);
            getMovieReviewsDataFromJson(id);
        }
        int inserted = 0;
        // add to database
        if (cVVector.size() > 0) {
            ContentValues[] contentValues = new ContentValues[cVVector.size()];
            inserted = getContext().getContentResolver().bulkInsert(MoviesContract.MovieEntry.CONTENT_URI, cVVector.toArray(contentValues));
        }
        Log.d(LOG_TAG, inserted + " Inserted");
    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }


    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }


    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        PopMoviesSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

}