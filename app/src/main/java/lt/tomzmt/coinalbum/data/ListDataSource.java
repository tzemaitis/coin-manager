package lt.tomzmt.coinalbum.data;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import lt.tomzmt.coinalbum.BaseAsyncTask;
import lt.tomzmt.coinalbum.ListLoadTask;
import lt.tomzmt.coinalbum.data.entity.Entity;

/**
 * Loadable data model implementation for list of entities
 * Created by t.zemaitis on 2014.12.08.
 */
public class ListDataSource<T extends Entity> implements DataModel, BaseAsyncTask.OnCompleteListener<List<T>> {

    private final Repository<T> mRepository;

    private ListLoadTask<T> mLoadTask;

    private String mSearchKey = null;

    private final List<T> mItem = new ArrayList<>();

    private ListDataModelObserver<T> mObserver;

    public ListDataSource(Repository<T> repository) {
        mRepository = repository;
    }

    public void setObserver(ListDataModelObserver<T> observer) {
        mObserver = observer;
    }

    public List<T> getItems() {
        return mItem;
    }

    public void filter(String key) {
        mSearchKey = key;
        unload();
        load();
    }

    @Override
    public void load() {
        if (mLoadTask == null) {
            mLoadTask = new ListLoadTask<>(mRepository, mSearchKey);
            mLoadTask.setOnCompleteListener(this);
            mLoadTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        }
    }

    @Override
    public void unload() {
        if (mLoadTask != null) {
            mLoadTask.setOnCompleteListener(null);
            mLoadTask.cancel(true);
            mLoadTask = null;
        }
    }

    @Override
    public void onComplete(BaseAsyncTask<List<T>> task) {
        try {
            mItem.clear();
            mItem.addAll(task.get());
            if (mObserver != null) {
                mObserver.onDataSetChanged(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface ListDataModelObserver<V extends Entity> {
        void onDataSetChanged(ListDataSource<V> model);
    }
}
