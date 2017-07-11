package cn.wontao.popmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.wontao.popmovies.ui.DetailFragment;
import cn.wontao.popmovies.R;
import cn.wontao.popmovies.Utility;

/**
 * Created by ME on 2017/6/23.
 */

public class MovieReviewCursorAdapter extends CursorRecyclerViewAdapter<MovieReviewCursorAdapter.ViewHolder> {
//    private final static String MESSAGE = "Click to view details";
    private Context mContext;

    public MovieReviewCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        mContext = context;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor, int position) {
        String reviewAuthorStr = cursor.getString(DetailFragment.COL_REVIEW_AUTHOR);
        String reviewContentStr = cursor.getString(DetailFragment.COL_REVIEW_CONTENT);
        String reviewUrlStr = cursor.getString(DetailFragment.COL_REVIEW_URL);
        viewHolder.reviewAuthor.append(reviewAuthorStr);
        viewHolder.reviewContent.setText(reviewContentStr);
        viewHolder.reviewUrl.setMovementMethod(LinkMovementMethod.getInstance());
        viewHolder.reviewUrl.setText(Utility.strFormatHTML(reviewUrlStr,mContext.getResources().getString(R.string.text_message) ));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_item_review, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.review_author_text_view)
        TextView reviewAuthor;
        @BindView(R.id.review_content_text_view)
        TextView reviewContent;
        @BindView(R.id.review_url_text_view)
        TextView reviewUrl;


        private Context context;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            context = view.getContext();
        }
    }
}
