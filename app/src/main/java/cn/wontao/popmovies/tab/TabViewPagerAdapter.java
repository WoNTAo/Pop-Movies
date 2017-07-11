package cn.wontao.popmovies.tab;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import cn.wontao.popmovies.ui.MainFragment;

/**
 * Created by ME on 2016/8/7.
 */
public class TabViewPagerAdapter extends FragmentPagerAdapter {
    private final int PAGE_COUNT = 3;
    public static final String POPULAR = "popular";
    public static final String TOP_RATED = "top_rated";
    public static final String FAVORITE = "favorite";

    private Context mContext;

    public TabViewPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.mContext = context;
    }
    private String mTabTitle[] = new String[]{ "Popular","Top rated","Favorite"};

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return MainFragment.newInstance(POPULAR);
        } else if (position == 1) {
            return MainFragment.newInstance(TOP_RATED);
        } else if (position == 2) {
            return MainFragment.newInstance(FAVORITE);
        }
        return null;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabTitle[position];
    }
}
