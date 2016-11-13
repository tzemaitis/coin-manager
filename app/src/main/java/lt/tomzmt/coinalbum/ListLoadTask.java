package lt.tomzmt.coinalbum;

import java.util.List;

import lt.tomzmt.coinalbum.data.Repository;

/**
 * Async task to load list of entities form abstract repository
 * Created by t.zemaitis on 2014.12.08.
 */
public class ListLoadTask<T> extends BaseAsyncTask<List<T>> {

    private final Repository<T> mRepository;

    private final String mSearchKey;

    public ListLoadTask(Repository<T> repository, String searchKey) {
        mRepository = repository;
        mSearchKey = searchKey;
    }

    @Override
    protected List<T> doInBackground(Void... params) {
        return mRepository.search(mSearchKey);
    }
}
