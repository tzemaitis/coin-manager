package lt.tomzmt.coinalbum.ui;

import android.widget.ArrayAdapter;

import java.util.List;

import lt.tomzmt.coinalbum.data.ListDataSource;
import lt.tomzmt.coinalbum.data.entity.CoinSet;

/**
 * Created by t.zemaitis on 2015.08.19.
 */
public class SetListActivity extends AbsListActivity<CoinSet> {

    @Override
    protected ListDataSource<CoinSet> createDataSource() {
        return null;
    }

    @Override
    protected ArrayAdapter<CoinSet> createAdapter(List<CoinSet> items) {
        return null;
    }

    @Override
    protected void onEntityClick(CoinSet entity) {

    }
}
