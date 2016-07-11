package cn.wontao.popmovies;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.math.RoundingMode;
import java.text.DecimalFormat;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment {


    public DetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Movie movie = getActivity().getIntent().getParcelableExtra("movie");

        TextView title = (TextView) rootView.findViewById(R.id.original_title_text_view);
        title.setText(movie.getOriginalTitle());

        TextView releaseYear = (TextView) rootView.findViewById(R.id.release_year_text_view);
        TextView releaseDate = (TextView) rootView.findViewById(R.id.release_date_text_view);
        String[] date = movie.getReleaseDate().split("-");
        releaseYear.setText(date[0]);
        releaseDate.setText(date[1]+"."+date[2]);

        TextView voteAverage = (TextView) rootView.findViewById(R.id.vote_average_text_view);
        DecimalFormat df = new DecimalFormat("#.#");
            df.setRoundingMode(RoundingMode.CEILING);
        voteAverage.setText(df.format(movie.getVoteAverage()) + "/10");

        TextView overView = (TextView) rootView.findViewById(R.id.over_view_text_view);
        overView.setText(movie.getOverview());

        ImageView poster = (ImageView) rootView.findViewById(R.id.poster_image);
        Picasso.with(getContext()).load(movie.imageURL).into(poster);

        return rootView;
    }

}
