package cn.wontao.popmovies.ui;


import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.wontao.popmovies.R;
import cn.wontao.popmovies.adapter.MovieListCursorAdapter;
import cn.wontao.popmovies.adapter.MovieUserFavoriteCursorAdapter;
import cn.wontao.popmovies.data.MoviesContract;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    @BindView(R.id.grid_recycler)
    RecyclerView moviesGrid;
    @BindView(R.id.no_favorite_text_view)
    TextView noFavorite;

    public final String LOG_TAG = MainFragment.class.getSimpleName();
    private MovieListCursorAdapter movieListAdapter;
    private MovieUserFavoriteCursorAdapter movieUserFavoriteCursorAdapter;
    private static final int MOVIE_LOADER = 0;
    private static final int FAVORITE_LOADER = 10;
    private static final String SELECTED_KEY = "position";
    public static int mPosition;
    //    private static final String WARNING = "No network connection!Please connect wifi or mobile network retry.";
    public String mOrder;
    private static final String[] MOVIE_COLUMNS = {
            MoviesContract.MovieEntry._ID,
            MoviesContract.MovieEntry.COLUMN_MOVIE_ID,
            MoviesContract.MovieEntry.COLUMN_IMAGE_URL,
            MoviesContract.MovieEntry.COLUMN_MOVIE_TYPE
    };
    private static final String[] FAVORITE_COLUMNS = {
            MoviesContract.MovieEntry.TABLE_MOVIES + "." + MoviesContract.MovieEntry._ID,
            MoviesContract.MovieEntry.TABLE_MOVIES + "." + MoviesContract.MovieEntry.COLUMN_MOVIE_ID,
            MoviesContract.MovieEntry.TABLE_MOVIES + "." + MoviesContract.MovieEntry.COLUMN_IMAGE_URL,
            MoviesContract.MovieEntry.TABLE_MOVIES + "." + MoviesContract.MovieEntry.COLUMN_MOVIE_TYPE,

            MoviesContract.UserFavoriteEntry.TABLE_USER_FAVORITES + "." + MoviesContract.UserFavoriteEntry.COLUMN_MOVIE_ID
    };

    public static final int COL_ID = 0;
    public static final int COL_MOVIE_ID = 1;
    public static final int COL_IMAGE_URL = 2;
    public static final int COL_MOVIE_TYPE = 3;
    public static final int COL_FAVORITE_MOVIE_ID = 4;

    public MainFragment() {
        setHasOptionsMenu(true);
    }

    public static MainFragment newInstance(String order) {
        MainFragment df = new MainFragment();
        df.mOrder = order;
        return df;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!isOnline()) {
            Toast.makeText(getContext(), getResources().getString(R.string.text_warning), Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != RecyclerView.NO_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.activity_menu, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_display, container, false);
        ButterKnife.bind(this, rootView);

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
        movieListAdapter = new MovieListCursorAdapter(getActivity(), null);
        movieUserFavoriteCursorAdapter = new MovieUserFavoriteCursorAdapter(getActivity(), null);

        int i;

        if (getActivity().getResources().getConfiguration().ORIENTATION_PORTRAIT == getActivity().getResources().getConfiguration().orientation) {
            i = 2;
        } else {
            i = 3;
        }

        moviesGrid.setLayoutManager(new GridLayoutManager(getContext(), i, GridLayoutManager.VERTICAL, false));

        if ("favorite".equals(mOrder)) {
            moviesGrid.setAdapter(movieUserFavoriteCursorAdapter);

        } else {
            moviesGrid.setAdapter(movieListAdapter);
        }
        return rootView;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case MOVIE_LOADER: {
                Uri movieForMovieTypeUri = MoviesContract.MovieEntry.buildMovieType(mOrder);
                return new CursorLoader(getContext(), movieForMovieTypeUri, MOVIE_COLUMNS, null, null, null);
            }
            case FAVORITE_LOADER: {
                Uri uri = Uri.withAppendedPath(MoviesContract.BASE_CONTENT_URI, MoviesContract.UserFavoriteEntry.MOVIE_AND_FAVORITE_TABLE);
                return new CursorLoader(getActivity(), uri, FAVORITE_COLUMNS, null, null, null);
            }
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case MOVIE_LOADER: {
                movieListAdapter.swapCursor(data);
                movieListAdapter.notifyDataSetChanged();

                break;
            }
            case FAVORITE_LOADER: {
                movieUserFavoriteCursorAdapter.swapCursor(data);
                movieUserFavoriteCursorAdapter.notifyDataSetChanged();
                if ("favorite".equals(mOrder) && !data.moveToFirst()) {
                    noFavorite.setVisibility(View.VISIBLE);
                }
                break;
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(MOVIE_LOADER, savedInstanceState, this);
        getLoaderManager().initLoader(FAVORITE_LOADER, savedInstanceState, this);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieListAdapter.swapCursor(null);
        movieUserFavoriteCursorAdapter.swapCursor(null);
    }

    void userFavoriteChanged(){
        getLoaderManager().restartLoader(FAVORITE_LOADER,null,this);
        movieUserFavoriteCursorAdapter.notifyDataSetChanged();
    }

    public interface Callback {

        void onItemSelected(String movieId,int position);
    }
}
