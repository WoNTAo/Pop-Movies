package cn.wontao.popmovies.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import cn.wontao.popmovies.R;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            String extra = "";
            if (getIntent() != null && getIntent().hasExtra(DetailFragment.DETAIL_URI)){
                extra = getIntent().getStringExtra(DetailFragment.DETAIL_URI);
            }
            arguments.putString(DetailFragment.DETAIL_URI, extra);
            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().add(R.id.coordinator_layout, fragment).commit();
        }
    }
}
