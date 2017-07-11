package cn.wontao.popmovies.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import cn.wontao.popmovies.Utility;
import cn.wontao.popmovies.data.MoviesContract.MovieEntry;
import cn.wontao.popmovies.data.MoviesContract.MovieReviewEntry;
import cn.wontao.popmovies.data.MoviesContract.MovieTrailerEntry;
import cn.wontao.popmovies.data.MoviesContract.UserFavoriteEntry;

/**
 * Created by ME on 2016/9/22.
 */

public class MoviesProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MoviesDbHelper mOpenHelper;

    private static final int MOVIE = 100;
    private static final int MOVIE_AND_MOVIE_ID = 101;
    private static final int MOVIE_AND_MOVIE_TYPE = 102;
    private static final int MOVIE_TRAILER_AND_MOVIE_ID = 200;
    private static final int MOVIE_REVIEW_AND_MOVIE_ID = 300;
    private static final int USER_FAVORITE = 400;
    private static final int USER_FAVORITE_AND_MOVIE_ID = 401;
    private static final int MOVIE_AND_TRAILER_TABLE = 500;
    private static final int MOVIE_AND_REVIEW_TABLE = 501;
    private static final int MOVIE_AND_FAVORITE_TABLE = 502;
    private static final int MOVIE_AND_TRAILER_REVIEW_TABLE = 600;
    private static final int MOVIE_TRAILER = 201;
    private static final int MOVIE_REVIEW = 301;
    private static final SQLiteQueryBuilder sTrailerMovieByMovieIdQueryBuilder;
    private static final SQLiteQueryBuilder sReviewByMovieIdQueryBuilder;
    private static final SQLiteQueryBuilder sFavoriteByMovieIdQueryBuilder;
    private static final SQLiteQueryBuilder sDetailByMovieIdQueryBuilder;

    static {
        sTrailerMovieByMovieIdQueryBuilder = new SQLiteQueryBuilder();
        sReviewByMovieIdQueryBuilder = new SQLiteQueryBuilder();
        sFavoriteByMovieIdQueryBuilder = new SQLiteQueryBuilder();
        sDetailByMovieIdQueryBuilder = new SQLiteQueryBuilder();

        sTrailerMovieByMovieIdQueryBuilder.setTables(
                MovieEntry.TABLE_MOVIES + " INNER JOIN " +
                        MovieTrailerEntry.TABLE_MOVIE_TRAILERS +
                        " ON " + MovieEntry.TABLE_MOVIES +
                        "." + MovieEntry.COLUMN_MOVIE_ID +
                        " = " + MovieTrailerEntry.TABLE_MOVIE_TRAILERS +
                        "." + MovieTrailerEntry.COLUMN_MOVIE_ID
        );
        sReviewByMovieIdQueryBuilder.setTables(
                MovieEntry.TABLE_MOVIES + " INNER JOIN " +
                        MovieReviewEntry.TABLE_MOVIE_REVIEWS +
                        " ON " + MovieEntry.TABLE_MOVIES +
                        "." + MovieEntry.COLUMN_MOVIE_ID +
                        " = " + MovieReviewEntry.TABLE_MOVIE_REVIEWS +
                        "." + MovieReviewEntry.COLUMN_MOVIE_ID
        );
        sFavoriteByMovieIdQueryBuilder.setTables(
                MovieEntry.TABLE_MOVIES + " INNER JOIN " +
                        UserFavoriteEntry.TABLE_USER_FAVORITES +
                        " ON " + MovieEntry.TABLE_MOVIES +
                        "." + MovieEntry.COLUMN_MOVIE_ID +
                        " = " + UserFavoriteEntry.TABLE_USER_FAVORITES +
                        "." + UserFavoriteEntry.COLUMN_MOVIE_ID
        );
        sDetailByMovieIdQueryBuilder.setTables(
                MovieEntry.TABLE_MOVIES + " INNER JOIN " +
                        MovieTrailerEntry.TABLE_MOVIE_TRAILERS + " INNER JOIN " + MovieReviewEntry.TABLE_MOVIE_REVIEWS +
                        " ON " + MovieEntry.TABLE_MOVIES + "." + MovieEntry.COLUMN_MOVIE_ID +
                        " = " + MovieTrailerEntry.TABLE_MOVIE_TRAILERS + "." + MovieTrailerEntry.COLUMN_MOVIE_ID + " AND " +
                        MovieEntry.TABLE_MOVIES + "." + MovieEntry.COLUMN_MOVIE_ID +
                        " = " + MovieReviewEntry.TABLE_MOVIE_REVIEWS + "." + MovieReviewEntry.COLUMN_MOVIE_ID);
    }

    private static UriMatcher buildUriMatcher() {

        final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        sUriMatcher.addURI(authority, MovieEntry.TABLE_MOVIES, MOVIE);
        sUriMatcher.addURI(authority, MovieTrailerEntry.TABLE_MOVIE_TRAILERS, MOVIE_TRAILER);
        sUriMatcher.addURI(authority, MovieReviewEntry.TABLE_MOVIE_REVIEWS, MOVIE_REVIEW);
        sUriMatcher.addURI(authority, UserFavoriteEntry.TABLE_USER_FAVORITES, USER_FAVORITE);
        sUriMatcher.addURI(authority, MovieEntry.TABLE_MOVIES + "/*", MOVIE_AND_MOVIE_TYPE);
        sUriMatcher.addURI(authority, MovieEntry.COLUMN_MOVIE_ID + "/#", MOVIE_AND_MOVIE_ID);
        sUriMatcher.addURI(authority, MovieTrailerEntry.TABLE_MOVIE_TRAILERS + "/*", MOVIE_TRAILER_AND_MOVIE_ID);
        sUriMatcher.addURI(authority, MovieReviewEntry.TABLE_MOVIE_REVIEWS + "/*", MOVIE_REVIEW_AND_MOVIE_ID);
        sUriMatcher.addURI(authority, UserFavoriteEntry.TABLE_USER_FAVORITES + "/#", USER_FAVORITE_AND_MOVIE_ID);
        sUriMatcher.addURI(authority, MovieTrailerEntry.MOVIE_AND_TRAILER_TABLE + "/*", MOVIE_AND_TRAILER_TABLE);
        sUriMatcher.addURI(authority, MovieReviewEntry.MOVIE_AND_REVIEW_TABLE + "/*", MOVIE_AND_REVIEW_TABLE);
        sUriMatcher.addURI(authority, UserFavoriteEntry.MOVIE_AND_FAVORITE_TABLE, MOVIE_AND_FAVORITE_TABLE);
        sUriMatcher.addURI(authority, MovieEntry.MOVIE_AND_TRAILER_AND_REVIEW_TABLE + "/*", MOVIE_AND_TRAILER_REVIEW_TABLE);

        return sUriMatcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case MOVIE_AND_TRAILER_REVIEW_TABLE: {
                String movieId = Utility.getMovieIdFromUri(uri);
                selection = MovieEntry.TABLE_MOVIES + "." + MovieEntry.COLUMN_MOVIE_ID + " = ? ";
                selectionArgs = new String[]{movieId};
                retCursor = sDetailByMovieIdQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }case MOVIE_AND_TRAILER_TABLE: {
                String movieId = Utility.getMovieIdFromUri(uri);
                selection = MovieEntry.TABLE_MOVIES + "." + MovieEntry.COLUMN_MOVIE_ID + " = ? ";
                selectionArgs = new String[]{movieId};
                retCursor = sTrailerMovieByMovieIdQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case MOVIE_AND_REVIEW_TABLE: {
                String movieId = Utility.getMovieIdFromUri(uri);
                selection = MovieEntry.TABLE_MOVIES + "." + MovieEntry.COLUMN_MOVIE_ID + " = ? ";
                selectionArgs = new String[]{movieId};
                retCursor = sReviewByMovieIdQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case MOVIE_AND_FAVORITE_TABLE: {
                retCursor = sFavoriteByMovieIdQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            // "movies"
            case MOVIE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieEntry.TABLE_MOVIES,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "movies/movie_id/#"
            case MOVIE_AND_MOVIE_ID: {
                String movieId = Utility.getMovieIdFromUri(uri);
                selection = MovieEntry.COLUMN_MOVIE_ID + " = ? ";
                selectionArgs = new String[]{movieId};
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieEntry.TABLE_MOVIES,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "movies/movie_type/*"
            case MOVIE_AND_MOVIE_TYPE: {
                String movieType = Utility.getMovieTypeFromUri(uri);
                selection = MovieEntry.COLUMN_MOVIE_TYPE + " = ? ";
                selectionArgs = new String[]{movieType};
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieEntry.TABLE_MOVIES,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "movie_trailers/*"
            case MOVIE_TRAILER_AND_MOVIE_ID: {
                String movieId = Utility.getMovieIdFromUri(uri);
                selection = MovieTrailerEntry.COLUMN_MOVIE_ID + " = ? ";
                selectionArgs = new String[]{movieId};
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieTrailerEntry.TABLE_MOVIE_TRAILERS,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "movie_reviews/*"
            case MOVIE_REVIEW_AND_MOVIE_ID: {
                String movieId = Utility.getMovieIdFromUri(uri);
                selection = MovieReviewEntry.COLUMN_MOVIE_ID + " = ? ";
                selectionArgs = new String[]{movieId};
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieReviewEntry.TABLE_MOVIE_REVIEWS,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "user_favorites/#"
            case USER_FAVORITE_AND_MOVIE_ID: {
                String movieId = Utility.getMovieIdFromUri(uri);
                selection = UserFavoriteEntry.COLUMN_MOVIE_ID + " = ? ";
                selectionArgs = new String[]{movieId};
                retCursor = mOpenHelper.getReadableDatabase().query(
                        UserFavoriteEntry.TABLE_USER_FAVORITES,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "movie_trailers"
            case MOVIE_TRAILER: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieTrailerEntry.TABLE_MOVIE_TRAILERS,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "movie_reviews"
            case MOVIE_REVIEW: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieReviewEntry.TABLE_MOVIE_REVIEWS,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "user_favorites"
            case USER_FAVORITE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        UserFavoriteEntry.TABLE_USER_FAVORITES,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE: {
                return MovieEntry.CONTENT_DIR_TYPE;
            }
            case MOVIE_TRAILER: {
                return MovieTrailerEntry.CONTENT_DIR_TYPE;
            }
            case MOVIE_REVIEW: {
                return MovieReviewEntry.CONTENT_DIR_TYPE;
            }
            case MOVIE_AND_MOVIE_ID: {
                return MovieEntry.CONTENT_ITEM_TYPE;
            }
            case MOVIE_AND_MOVIE_TYPE: {
                return MovieEntry.CONTENT_DIR_TYPE;
            }
            case MOVIE_TRAILER_AND_MOVIE_ID: {
                return MovieTrailerEntry.CONTENT_DIR_TYPE;
            }
            case MOVIE_REVIEW_AND_MOVIE_ID: {
                return MovieReviewEntry.CONTENT_DIR_TYPE;
            }
            case USER_FAVORITE: {
                return UserFavoriteEntry.CONTENT_DIR_TYPE;
            }
            case USER_FAVORITE_AND_MOVIE_ID: {
                return UserFavoriteEntry.CONTENT_ITEM_TYPE;
            }
            case MOVIE_AND_TRAILER_TABLE: {
                return MovieEntry.CONTENT_DIR_TYPE;
            }
            case MOVIE_AND_REVIEW_TABLE: {
                return MovieEntry.CONTENT_DIR_TYPE;
            }
            case MOVIE_AND_FAVORITE_TABLE: {
                return MovieEntry.CONTENT_DIR_TYPE;
            }
            case MOVIE_AND_TRAILER_REVIEW_TABLE: {
                return MovieEntry.CONTENT_DIR_TYPE;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE: {
                long _id = db.insert(MovieEntry.TABLE_MOVIES, null, values);
                if (_id > 0)
                    returnUri = MovieEntry.buildMoviesUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case MOVIE_TRAILER: {
                long _id = db.insert(MovieTrailerEntry.TABLE_MOVIE_TRAILERS, null, values);
                if (_id > 0)
                    returnUri = MovieTrailerEntry.buildMovieTrailersUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case MOVIE_REVIEW: {
                long _id = db.insert(MovieReviewEntry.TABLE_MOVIE_REVIEWS, null, values);
                if (_id > 0)
                    returnUri = MovieReviewEntry.buildMovieReviewsUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case USER_FAVORITE: {
                long _id = db.insert(UserFavoriteEntry.TABLE_USER_FAVORITES, null, values);
                if (_id > 0)
                    returnUri = UserFavoriteEntry.buildUserFavoritesUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieEntry.TABLE_MOVIES, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case MOVIE: {
                rowsDeleted = db.delete(MovieEntry.TABLE_MOVIES, selection, selectionArgs);
                break;
            }
            case MOVIE_TRAILER: {
                rowsDeleted = db.delete(MovieTrailerEntry.TABLE_MOVIE_TRAILERS, selection, selectionArgs);
                break;
            }
            case MOVIE_REVIEW: {
                rowsDeleted = db.delete(MovieReviewEntry.TABLE_MOVIE_REVIEWS, selection, selectionArgs);
                break;
            }
            case USER_FAVORITE: {
                rowsDeleted = db.delete(UserFavoriteEntry.TABLE_USER_FAVORITES, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0 || selection == null)
            getContext().getContentResolver().notifyChange(uri, null);

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        int rowsUpdated;
        switch (match) {
            case MOVIE: {
                rowsUpdated = db.update(MovieEntry.TABLE_MOVIES, values, selection, selectionArgs);
                break;
            }
            case MOVIE_TRAILER: {
                rowsUpdated = db.update(MovieTrailerEntry.TABLE_MOVIE_TRAILERS, values, selection, selectionArgs);
                break;
            }
            case MOVIE_REVIEW: {
                rowsUpdated = db.update(MovieReviewEntry.TABLE_MOVIE_REVIEWS, values, selection, selectionArgs);
                break;
            }
            case USER_FAVORITE: {
                rowsUpdated = db.update(UserFavoriteEntry.TABLE_USER_FAVORITES, values, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return rowsUpdated;
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
