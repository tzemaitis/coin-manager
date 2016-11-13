package lt.tomzmt.coinalbum;

import android.os.AsyncTask;

/**
 * Base asynchronous task.
 * Created by t.zemaitis on 2014.12.08.
 */
public abstract class BaseAsyncTask<T> extends AsyncTask<Void, Void, T> {

    private OnCompleteListener<T> mOnCompleteListener;

    public void setOnCompleteListener(OnCompleteListener<T> listener) {
        mOnCompleteListener = listener;
    }

    @Override
    protected void onPostExecute(T t) {
        if (mOnCompleteListener != null) {
            mOnCompleteListener.onComplete(this);
        }
    }

    public interface OnCompleteListener<V> {
        void onComplete(BaseAsyncTask<V> task);
    }
}
