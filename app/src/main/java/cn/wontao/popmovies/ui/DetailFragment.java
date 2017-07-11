package cn.wontao.popmovies.ui;


import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.wontao.popmovies.FullyLinearLayoutManager;
import cn.wontao.popmovies.R;
import cn.wontao.popmovies.Utility;
import cn.wontao.popmovies.adapter.MovieReviewCursorAdapter;
import cn.wontao.popmovies.adapter.MovieTrailerCursorAdapter;
import cn.wontao.popmovies.data.MoviesContract;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    @BindView(R.id.recycler_view_trailers)
    RecyclerView mRecyclerViewTrailer;
    @BindView(R.id.recycler_view_reviews)
    RecyclerView mRecyclerViewReview;
    @BindView(R.id.original_title_text_view)
    TextView movieTitle;
    @BindView(R.id.release_date_text_view)
    TextView releaseDate;
    @BindView(R.id.release_year_text_view)
    TextView releaseYear;
    @BindView(R.id.vote_average_text_view)
    TextView voteAverage;
    @BindView(R.id.over_view_text_view)
    TextView overView;
    @BindView(R.id.poster_image)
    ImageView poster;
    @BindView(R.id.favorite_button)
    Button favoriteButton;
    @BindView(R.id.no_trailer_text_view)
    TextView noTrailer;
    @BindView(R.id.no_review_text_view)
    TextView noReview;

    private static final int TRAILER_LOADER = 0;
    private static final int MOVIE_LOADER = 10;
    private static final int REVIEW_LOADER = 20;
    private static final int FAVORITE_LOADER = 30;
    public final String LOG_TAG = DetailFragment.class.getSimpleName();
    public static final String DETAIL_URI = "URI";
    private static final String SELECTED_KEY = "position";

//    public static final String SUCCESSFUL_COLLECTION = "Successful collection";
//    public static final String ABOLISH_SUCCESSFUL_COLLECTION = "Abolish successful collection";

    private MovieTrailerCursorAdapter mTrailerCursorAdapter;
    private MovieReviewCursorAdapter mReviewCursorAdapter;
    private int mPosition;
    private static final String[] MOVIE_COLUMNS = {
            MoviesContract.MovieEntry.TABLE_MOVIES + "." + MoviesContract.MovieEntry._ID,
            MoviesContract.MovieEntry.TABLE_MOVIES + "." + MoviesContract.MovieEntry.COLUMN_MOVIE_ID,
            MoviesContract.MovieEntry.TABLE_MOVIES + "." + MoviesContract.MovieEntry.COLUMN_IMAGE_URL,
            MoviesContract.MovieEntry.TABLE_MOVIES + "." + MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            MoviesContract.MovieEntry.TABLE_MOVIES + "." + MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MoviesContract.MovieEntry.TABLE_MOVIES + "." + MoviesContract.MovieEntry.COLUMN_RELEASE_DATE,
            MoviesContract.MovieEntry.TABLE_MOVIES + "." + MoviesContract.MovieEntry.COLUMN_OVERVIEW,
    };
    private static final String[] TRAILER_COLUMNS = {
            MoviesContract.MovieEntry.TABLE_MOVIES + "." + MoviesContract.MovieEntry._ID,

            MoviesContract.MovieTrailerEntry.TABLE_MOVIE_TRAILERS + "." + MoviesContract.MovieTrailerEntry.COLUMN_NAME,
            MoviesContract.MovieTrailerEntry.TABLE_MOVIE_TRAILERS + "." + MoviesContract.MovieTrailerEntry.COLUMN_KEY,


    };
    private static final String[] REVIEW_COLUMNS = {
            MoviesContract.MovieEntry.TABLE_MOVIES + "." + MoviesContract.MovieEntry._ID,

            MoviesContract.MovieReviewEntry.TABLE_MOVIE_REVIEWS + "." + MoviesContract.MovieReviewEntry.COLUMN_AUTHOR,
            MoviesContract.MovieReviewEntry.TABLE_MOVIE_REVIEWS + "." + MoviesContract.MovieReviewEntry.COLUMN_CONTENT,
            MoviesContract.MovieReviewEntry.TABLE_MOVIE_REVIEWS + "." + MoviesContract.MovieReviewEntry.COLUMN_URL
    };

    public static final int COL_ID = 0;
    public static final int COL_MOVIE_ID = 1;
    public static final int COL_IMAGE_URL = 2;
    public static final int COL_ORIGINAL_TITLE = 3;
    public static final int COL_VOTE_AVERAGE = 4;
    public static final int COL_RELEASE_DATE = 5;
    public static final int COL_OVERVIEW = 6;

    public static final int COL_TRAILER_NAME = 1;
    public static final int COL_TRAILER_KEY = 2;

    public static final int COL_REVIEW_AUTHOR = 1;
    public static final int COL_REVIEW_CONTENT = 2;
    public static final int COL_REVIEW_URL = 3;

    private String mMovieId;
    private boolean mFavorite;
    private boolean favorite;

    public DetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mMovieId = arguments.getString(DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, rootView);

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!favorite) {
                    favoriteButton.setBackgroundColor(Color.WHITE);
                    favoriteButton.setTextColor(getResources().getColor(R.color.black));
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MoviesContract.UserFavoriteEntry.COLUMN_MOVIE_ID, mMovieId);
                    getContext().getContentResolver().insert(MoviesContract.UserFavoriteEntry.CONTENT_URI, contentValues);
                    Toast.makeText(getContext(), getResources().getString(R.string.text_collection), Toast.LENGTH_SHORT).show();
                    favorite = true;
                    return;
                }
                if (favorite) {
                    getContext().getContentResolver().delete(MoviesContract.UserFavoriteEntry.CONTENT_URI, MoviesContract.UserFavoriteEntry.COLUMN_MOVIE_ID + "=?", new String[]{mMovieId});
                    Toast.makeText(getContext(), getResources().getString(R.string.text_abolish), Toast.LENGTH_SHORT).show();
                    favoriteButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    favoriteButton.setTextColor(getResources().getColor(R.color.write));
                    favorite = false;
                    return;
                }


            }
        });
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerViewTrailer.setLayoutManager(
                new FullyLinearLayoutManager(getActivity())
        );

        mRecyclerViewReview.setLayoutManager(
                new FullyLinearLayoutManager(getActivity())
        );

        mTrailerCursorAdapter = new MovieTrailerCursorAdapter(getActivity(), null);
        mRecyclerViewTrailer.setAdapter(mTrailerCursorAdapter);

        mReviewCursorAdapter = new MovieReviewCursorAdapter(getActivity(), null);
        mRecyclerViewReview.setAdapter(mReviewCursorAdapter);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null == mMovieId) {
            return null;
        }
        switch (id) {
            case TRAILER_LOADER: {
                Uri uri = Uri.withAppendedPath(MoviesContract.BASE_CONTENT_URI, MoviesContract.MovieTrailerEntry.MOVIE_AND_TRAILER_TABLE);
                Uri contentUri = Uri.withAppendedPath(uri, mMovieId);
                return new CursorLoader(getActivity(), contentUri, TRAILER_COLUMNS, null, null, null);
            }
            case REVIEW_LOADER: {
                Uri uri = Uri.withAppendedPath(MoviesContract.BASE_CONTENT_URI, MoviesContract.MovieReviewEntry.MOVIE_AND_REVIEW_TABLE);
                Uri contentUri = Uri.withAppendedPath(uri, mMovieId);
                return new CursorLoader(getActivity(), contentUri, REVIEW_COLUMNS, null, null, null);
            }
            case FAVORITE_LOADER: {
                Uri uri = Uri.withAppendedPath(MoviesContract.BASE_CONTENT_URI, MoviesContract.UserFavoriteEntry.TABLE_USER_FAVORITES);
                Uri contentUri = Uri.withAppendedPath(uri, mMovieId);
                return new CursorLoader(getActivity(), contentUri, null, null, null, null);
            }
            case MOVIE_LOADER: {
                Uri uri = Uri.withAppendedPath(MoviesContract.BASE_CONTENT_URI, MoviesContract.MovieEntry.COLUMN_MOVIE_ID);
                Uri contentUri = Uri.withAppendedPath(uri, mMovieId);
                return new CursorLoader(getActivity(), contentUri, MOVIE_COLUMNS, null, null, null);
            }
            default:
                return null;
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case TRAILER_LOADER: {
                mTrailerCursorAdapter.swapCursor(data);
                mTrailerCursorAdapter.notifyDataSetChanged();
                if (data.moveToFirst()) {
                    noTrailer.setVisibility(View.GONE);
                }
                break;
            }
            case REVIEW_LOADER: {
                mReviewCursorAdapter.swapCursor(data);
                mReviewCursorAdapter.notifyDataSetChanged();
                if (data.moveToFirst()) {
                    noReview.setVisibility(View.GONE);
                }
                break;
            }
            case FAVORITE_LOADER: {
                if (!data.moveToFirst()) {
                    return;
                }
                String movieId = data.getString(data.getColumnIndex(MoviesContract.UserFavoriteEntry.COLUMN_MOVIE_ID));
                do {
                    if (movieId.equals(mMovieId)) {
                        mFavorite = true;
                        break;
                    } else {
                        mFavorite = false;
                    }
                }
                while (data.moveToNext());
                if (mFavorite) {
                    favoriteButton.setBackgroundColor(Color.WHITE);
                    favoriteButton.setTextColor(getResources().getColor(R.color.black));
                }
                favorite = mFavorite;
                break;
            }
            case MOVIE_LOADER: {
                if (!data.moveToFirst()) {
                    return;
                }

                movieTitle.setText(data.getString(COL_ORIGINAL_TITLE));

                String[] date = Utility.getDate(data.getString(COL_RELEASE_DATE));
                releaseYear.setText(date[0]);
                releaseDate.setText(date[1] + "." + date[2]);

                DecimalFormat df = new DecimalFormat("0.0");
                voteAverage.setText(df.format(data.getDouble(COL_VOTE_AVERAGE)) + "/10");

                overView.setText(data.getString(COL_OVERVIEW));

                Picasso.with(getContext()).load(data.getString(COL_IMAGE_URL)).into(poster);
                break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTrailerCursorAdapter.swapCursor(null);
        mReviewCursorAdapter.swapCursor(null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(MOVIE_LOADER, savedInstanceState, this);
        getLoaderManager().initLoader(FAVORITE_LOADER, savedInstanceState, this);
        getLoaderManager().initLoader(TRAILER_LOADER, savedInstanceState, this);
        getLoaderManager().initLoader(REVIEW_LOADER, savedInstanceState, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != RecyclerView.NO_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }
}
