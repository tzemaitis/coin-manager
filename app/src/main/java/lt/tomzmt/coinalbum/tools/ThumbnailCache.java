package lt.tomzmt.coinalbum.tools;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Class to store loaded images for later use
 * Created by t.zemaitis on 2015.03.23.
 */
public class ThumbnailCache {

    /**
     *
     */
    private final LruCache<Long, Bitmap> mCache;

    /**
     * Creates new instance of image cache
     * @param context context to use
     */
    public ThumbnailCache(Context context) {
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        int memoryClassBytes = am.getMemoryClass() * 1028 * 1028;
        mCache = new LruCache<Long, Bitmap>(memoryClassBytes / 8) {
            @Override
            protected int sizeOf(Long key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };

    }

    /**
     * Retrieves image from cache if one exists
     * @param key image identifier
     * @return image bitmap or null if image not found in cache
     */
    public Bitmap get(Long key) {
        return mCache.get(key);
    }

    /**
     * Adds image to cache
     * @param key image identifier
     * @param value image to store
     */
    public void put(Long key, Bitmap value) {
        if (value != null) {
            mCache.put(key, value);
        }
    }
}
