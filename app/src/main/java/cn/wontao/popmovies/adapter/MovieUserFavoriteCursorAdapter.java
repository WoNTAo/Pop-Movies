package cn.wontao.popmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.wontao.popmovies.R;
import cn.wontao.popmovies.ui.MainFragment;
import cn.wontao.popmovies.ui.MainActivity;

/**
 * Created by ME on 2017/6/29.
 */

public class MovieUserFavoriteCursorAdapter extends CursorRecyclerViewAdapter<MovieUserFavoriteCursorAdapter.ViewHolder> {
    private Context mContext;
    HashMap<Integer, String> map = new HashMap<Integer, String>();

    public MovieUserFavoriteCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        mContext = context;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor, int position) {
        map.put(position, cursor.getString(MainFragment.COL_FAVORITE_MOVIE_ID));
        String imageURL = cursor.getString(MainFragment.COL_IMAGE_URL);
        Picasso.with(mContext).load(imageURL).into(viewHolder.iconView);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_item_display, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.movies_image)
        ImageView iconView;

        private Context context;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            context = view.getContext();
        }

        @OnClick(R.id.movies_image)
        void OnClick() {
//            Toast.makeText(mContext, map.get(getPosition())+"",Toast.LENGTH_SHORT).show();
            MainActivity activity = (MainActivity) mContext;
            String movieId = map.get(getPosition());
            activity.onItemSelected(movieId,getPosition());
        }
    }
}
