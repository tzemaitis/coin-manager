package lt.tomzmt.coinalbum.tools;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;

import lt.tomzmt.coinalbum.R;
import lt.tomzmt.coinalbum.Tools;
import lt.tomzmt.coinalbum.data.DatabaseRepository;
import lt.tomzmt.coinalbum.data.entity.Image;

/**
 * Class dedicated to load images asynchronously
 * Created by t.zemaitis on 2015.03.23.
 */
public class ImageLoader {

    private final ThumbnailCache mCache;

    private final Queue<LoadTask> mPendingTasks;

    private final DatabaseRepository<Image> mImageManager;

    private Bitmap mDefaultImage;

    private int mImageWidth;

    private int mImageHeight;

    private State mState;

    private enum State {
        IDLE, RUNNING, STOPPED
    }

    public ImageLoader(Context context) {

        mCache = new ThumbnailCache(context);
        mImageManager = new DatabaseRepository<>(Image.class);
        mState = State.STOPPED;

        mPendingTasks = new LinkedList<>();

        Resources resources = context.getResources();
        mDefaultImage = BitmapFactory.decodeResource(resources, R.drawable.ic_launcher);
        mImageWidth = resources.getDimensionPixelSize(R.dimen.list_image_width);
        mImageHeight = resources.getDimensionPixelSize(R.dimen.list_image_height);
    }

    /**
     * Requests to load image
     * @param id image identifier
     * @param listener listener to call then image is loaded
     */
    public void
    loadImage(long id, ImageLoaderListener listener) {
        listener.onSetTag(Long.toString(id));
        Bitmap bmp = mCache.get(id);
        if (bmp != null) {
            String tag = Long.toString(id);
            listener.onLoadStarted(tag, true);
            listener.onLoadFinished(bmp, tag, true);
        }
        else {
            LoadTask op = getById(id);
            if (op != null) {
                op.addListener(listener);
            }
            else {
                op = new LoadTask(id);
                op.addListener(listener);
                mPendingTasks.offer(op);
                if (mState == State.IDLE) {
                    onStart();
                }
            }
        }
    }

    /**
     * Starts image loader
     */
    public void onStart() {
        mState = State.RUNNING;
        loadNextInQueue();
    }

    /**
     * Stops image loader
     */
    public void onStop() {
        mState = State.STOPPED;
    }

    /**
     * Start to execute next load task in pending queue
     */
    private void loadNextInQueue() {
        if (!mPendingTasks.isEmpty()) {
            LoadTask op = mPendingTasks.peek();
            if (op.getStatus() == Status.PENDING) {
                op.execute();
            }
        }
        else {
            // queue is empty, set to IDLE
            mState = State.IDLE;
        }
    }

    /**
     * Returns existing task by image id, if it exists
     */
    private LoadTask getById(long id) {
        for (LoadTask op : mPendingTasks) {
            if (op.getId() == id) {
                return op;
            }
        }
        return null;
    }

    /**
     * @author tomzmt
     */
    private class LoadTask extends AsyncTask<Void, Void, Bitmap> {

        private long mId;

        private List<WeakReference<ImageLoaderListener>> mListeners;

        LoadTask(long id) {
            mId = id;
            mListeners = new ArrayList<>();
        }

        void addListener(ImageLoaderListener listener) {
            WeakReference<ImageLoaderListener> ref = new WeakReference<>(listener);
            mListeners.add(ref);
        }

        public long getId() {
            return mId;
        }

        @Override
        protected void onPreExecute() {
            for (WeakReference<ImageLoaderListener> ref : mListeners) {
                ImageLoaderListener listener = ref.get();
                if (listener != null) {
                    listener.onLoadStarted(String.valueOf(mId), false);
                }
            }
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap result = null;
            Image image = mImageManager.get(mId);
            if (image.getData() != null) {
                result = Tools.decodeImage(image.getData(), mImageWidth, mImageHeight);
            }
            return result;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            mPendingTasks.poll();

            if (bitmap == null) {
                bitmap = mDefaultImage;
            } else {
                mCache.put(mId, bitmap);
            }
            for (WeakReference<ImageLoaderListener> ref : mListeners) {
                ImageLoaderListener listener = ref.get();
                if (listener != null) {
                    listener.onLoadFinished(bitmap, String.valueOf(mId), false);
                }
            }

            loadNextInQueue();
        }
    }

    /**
     * @author tomzmt
     */
    public interface ImageLoaderListener {
        void onSetTag(String tag);

        void onLoadStarted(String tag, boolean cashed);

        void onLoadFinished(Bitmap bitmap, String tag, boolean cashed);
    }
}
