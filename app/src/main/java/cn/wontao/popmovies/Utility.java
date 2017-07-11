package cn.wontao.popmovies;

import android.content.Context;
import android.net.Uri;
import android.text.Html;
import android.text.Spanned;

/**
 * Created by ME on 2016/8/17.
 */
public class Utility {
    public final static String LOG_TAG = Utility.class.getSimpleName();

    public static String[] getDate(String date) {
        return date.split("-");
    }

    public static String getTableNameFromUri(Uri uri) {
        return uri.getPathSegments().get(0);
    }

    public static Spanned strFormatHTML(String url,String str) {
        return Html.fromHtml("<a href=\""+url+"\">"+str+"</a>");
    }

    public static String getMovieTypeFromUri(Uri uri) {
        return uri.getPathSegments().get(1);
    }

    public static String getMovieIdFromUri(Uri uri) {
        return uri.getPathSegments().get(1);
    }

    /*
    单位转换 dp->px
     */
    public static int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
