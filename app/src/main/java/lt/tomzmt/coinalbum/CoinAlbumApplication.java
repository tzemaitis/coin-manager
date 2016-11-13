package lt.tomzmt.coinalbum;

import android.app.Application;

import lt.tomzmt.coinalbum.data.DatabaseOpener;

/**
 * Custom application singleton
 * Created by t.zemaitis on 2014.12.08.
 */
public class CoinAlbumApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DatabaseOpener.init(this);
    }
}
