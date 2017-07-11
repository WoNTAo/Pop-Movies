package cn.wontao.popmovies.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class PopMoviesSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static PopMoviesSyncAdapter sPopMoviesSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("PopMoviesSyncAdapter", "onCreate - PopMoviesService");
        synchronized (sSyncAdapterLock) {
            if (sPopMoviesSyncAdapter == null) {
                sPopMoviesSyncAdapter = new PopMoviesSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sPopMoviesSyncAdapter.getSyncAdapterBinder();
    }
}