package lt.tomzmt.coinalbum.ui;

import android.widget.ArrayAdapter;

import java.util.List;

import lt.tomzmt.coinalbum.data.ListDataSource;
import lt.tomzmt.coinalbum.data.entity.Set;

/**
 * Created by t.zemaitis on 2015.08.19.
 */
public class SetListActivity extends AbsListActivity<Set> {

    @Override
    protected ListDataSource<Set> createDataSource() {
        return null;
    }

    @Override
    protected ArrayAdapter<Set> createAdapter(List<Set> items) {
        return null;
    }

    @Override
    protected void onEntityClick(Set entity) {

    }
}
