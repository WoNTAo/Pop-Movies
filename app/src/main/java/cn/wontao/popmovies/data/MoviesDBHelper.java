package cn.wontao.popmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import cn.wontao.popmovies.data.MoviesContract.MovieEntry;
import cn.wontao.popmovies.data.MoviesContract.MovieReviewEntry;
import cn.wontao.popmovies.data.MoviesContract.MovieTrailerEntry;
import cn.wontao.popmovies.data.MoviesContract.UserFavoriteEntry;

/**
 * Created by ME on 2016/9/22.
 */

public class MoviesDbHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = MoviesDbHelper.class.getSimpleName();

    //name & version
    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                MovieEntry.TABLE_MOVIES + "(" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_IMAGE_URL + " TEXT NOT NULL,"+
                MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL,"+
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL,"+
                MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, "+
                MovieEntry.COLUMN_MOVIE_TYPE + " TEXT NOT NULL,"+
                " UNIQUE (" + MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_MOVIE_TRAILERS_TABLE = "CREATE TABLE " +
                MovieTrailerEntry.TABLE_MOVIE_TRAILERS + "(" +
                MovieTrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieTrailerEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieTrailerEntry.COLUMN_TRAILER_ID + " INTEGER NOT NULL, " +
                MovieTrailerEntry.COLUMN_NAME + " TEXT NOT NULL," +
                MovieTrailerEntry.COLUMN_KEY+ " TEXT NOT NULL," +
                " UNIQUE (" + MovieTrailerEntry.COLUMN_TRAILER_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_MOVIE_REVIEWS_TABLE = "CREATE TABLE " +
                MovieReviewEntry.TABLE_MOVIE_REVIEWS + "(" +
                MovieReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieReviewEntry.COLUMN_REVIEW_ID + " INTEGER NOT NULL, " +
                MovieReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL," +
                MovieReviewEntry.COLUMN_CONTENT+ " TEXT NOT NULL," +
                MovieReviewEntry.COLUMN_URL+ " TEXT NOT NULL," +
                " UNIQUE (" + MovieReviewEntry.COLUMN_REVIEW_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_USER_FAVORITES_TABLE = "CREATE TABLE " +
                UserFavoriteEntry.TABLE_USER_FAVORITES + "(" +
                UserFavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                UserFavoriteEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                " UNIQUE (" + UserFavoriteEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";


        db.execSQL(SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_MOVIE_TRAILERS_TABLE);
        db.execSQL(SQL_CREATE_MOVIE_REVIEWS_TABLE);
        db.execSQL(SQL_CREATE_USER_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to " +
                newVersion + ". OLD DATA WILL BE DESTROYED");
        // Drop the table
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_MOVIES);
        db.execSQL("DROP TABLE IF EXISTS " + MovieTrailerEntry.TABLE_MOVIE_TRAILERS);
        db.execSQL("DROP TABLE IF EXISTS " + MovieReviewEntry.TABLE_MOVIE_REVIEWS);
        db.execSQL("DROP TABLE IF EXISTS " + UserFavoriteEntry.TABLE_USER_FAVORITES);

        // re-create database
        onCreate(db);
    }
}
