package cn.wontao.popmovies.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.facebook.stetho.Stetho;

import cn.wontao.popmovies.R;
import cn.wontao.popmovies.sync.PopMoviesSyncAdapter;
import cn.wontao.popmovies.tab.SlidingTabLayout;
import cn.wontao.popmovies.tab.TabViewPagerAdapter;

import static cn.wontao.popmovies.R.id.sliding_tabs;

public class MainActivity extends AppCompatActivity implements MainFragment.Callback {
    public static final String DETAILFRAGMENT_TAG = "DFTAG";
    boolean mTwoPane;
    TabViewPagerAdapter mTVPA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Stetho.initializeWithDefaults(this);
        if (findViewById(R.id.coordinator_layout) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.coordinator_layout, new EmptyFragment(), null)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        mTVPA = new TabViewPagerAdapter(getSupportFragmentManager(),
                MainActivity.this);
        viewPager.setAdapter(mTVPA);

        getSupportActionBar().setElevation(0f);

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(sliding_tabs);
        slidingTabLayout.setCustomTabView(R.layout.tab_view, R.id.tabText);
        slidingTabLayout.setDividerColors(Color.argb(0, 0, 0, 0));
        slidingTabLayout.setSelectedIndicatorColors(Color.WHITE);//设置滚动条颜色
        slidingTabLayout.setViewPager(viewPager);

        PopMoviesSyncAdapter.syncImmediately(this);

    }

    @Override
    public void onItemSelected(String movieId, int position) {
        if (mTwoPane) {
            Bundle argus = new Bundle();
            argus.putString(DetailFragment.DETAIL_URI, movieId);
            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(argus);
            getSupportFragmentManager().beginTransaction().replace(R.id.coordinator_layout, fragment, DETAILFRAGMENT_TAG).commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(DetailFragment.DETAIL_URI, movieId);
            startActivity(intent);

            MainFragment.mPosition = position;
        }
    }
}
