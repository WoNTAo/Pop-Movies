package cn.wontao.popmovies.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.wontao.popmovies.R;
import cn.wontao.popmovies.ui.DetailFragment;

/**
 * Created by ME on 2016/9/18.
 */
public class MovieTrailerCursorAdapter extends CursorRecyclerViewAdapter<MovieTrailerCursorAdapter.ViewHolder> {
    private Context mContext;
    private static HashMap<Integer, String> map = new HashMap<Integer, String>();
    private final static String YOUTUBE_URI = "http://www.youtube.com/watch?v=";

    public MovieTrailerCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        mContext = context;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor, int position) {
        String trailerTitleStr = cursor.getString(DetailFragment.COL_TRAILER_NAME);
        String trailerKeyStr = cursor.getString(DetailFragment.COL_TRAILER_KEY);

        map.put(position, trailerKeyStr);

        viewHolder.trailerTitle.setText(trailerTitleStr);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_item_trailer, parent, false);
        ViewHolder vh = new ViewHolder(itemView);

        return vh;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.trailer_title_text_view)
        TextView trailerTitle;
        @BindView(R.id.play_trailer)
        LinearLayout playTrailer;

        private Context context;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            context = view.getContext();


        }

        @OnClick(R.id.play_trailer)
        void OnClick() {
            final Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.parse(YOUTUBE_URI + map.get(getPosition()).toString());
            intent.setData(uri);
            context.startActivity(intent);
        }
    }
}
