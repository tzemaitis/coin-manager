package lt.tomzmt.coinalbum.ui.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import lt.tomzmt.coinalbum.tools.ImageLoader;

/**
 * ImageView subclass capable to listen image loader events
 * Created by t.zemaitis on 2015.03.23.
 */
public class LoaderListeningImageView extends ImageView implements ImageLoader.ImageLoaderListener {

    /**
     * Constructor
     */
    public LoaderListeningImageView(Context context) {
        super(context);
    }

    /**
     * Constructor
     */
    public LoaderListeningImageView(Context context, AttributeSet attributes) {
        super(context, attributes);
    }

    /**
     * Constructor
     */
    public LoaderListeningImageView(Context context, AttributeSet attributes, int defStyle) {
        super(context, attributes, defStyle);
    }

    /**
     *
     */
    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        clearAnimation();
    }

    /**
     *
     */
    @Override
    public void onSetTag(String tag) {
        setTag(tag);
        setVisibility(View.INVISIBLE);
    }

    /**
     *
     */
    @Override
    public void onLoadStarted(String tag, boolean cashed) {
        String currentTag = (String)getTag();
        if (currentTag != null && currentTag.equals(tag)) {
            if (!cashed) {
                setImageBitmap(null);
            }
        }
    }

    /**
     *
     */
    @Override
    public void onLoadFinished(Bitmap bitmap, String tag, boolean cashed) {
        String currentTag = (String)getTag();
        if (currentTag != null && currentTag.equals(tag)) {
            setVisibility(View.VISIBLE);
            setImageBitmap(bitmap);
            if (!cashed) {
                AlphaAnimation anim = new AlphaAnimation(0, 1.0f);
                anim.setDuration(300);
                startAnimation(anim);
            }
        }
    }
}
