package lt.tomzmt.coinalbum.ui.widgets;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import lt.tomzmt.coinalbum.tools.Rotate3dAnimation;

/**
 * Image View which flips in touch
 * Created by t.zemaitis on 2015.03.23.
 */
public class FlippingImageView extends ImageView {

    private static final int ANIMATION_DURATION = 300;

    private static final float FIRST_ROTATE_START = 0;
    private static final float FIRST_ROTATE_END = 90;
    private static final float SECOND_ROTATE_START = 270;
    private static final float SECOND_ROTATE_END = 360;
    private static final float MAX_SCALE = 1.0f;
    private static final float MIN_SCALE = 0.5f;

    private final Interpolator accelerator = new AccelerateInterpolator();
    private final Interpolator decelerator = new DecelerateInterpolator();

    boolean mFlipped = false;

    private Bitmap mForeground;
    private Bitmap mBackground;

    /**
     * Constructor
     */
    public FlippingImageView(Context context) {
        super(context);
    }

    /**
     * Constructor
     */
    public FlippingImageView(Context context, AttributeSet attributes) {
        super(context, attributes);
    }

    /**
     * Constructor
     */
    public FlippingImageView(Context context, AttributeSet attributes, int defStyle) {
        super(context, attributes, defStyle);
    }

    public void setForegroundBitmap(Bitmap bm) {
        mForeground = bm;
        setImageBitmap(bm);
    }

    public void setBackgroundBitmap(Bitmap bm) {
        mBackground = bm;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            mFlipped = !mFlipped;
            doFlip();
        }
        return true;
    }

    /**
     * Performs flip animation of the view
     */
    private void doFlip() {
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentApiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {

            PropertyValuesHolder zoomOutX = PropertyValuesHolder.ofFloat("scaleX", MAX_SCALE, MIN_SCALE);
            PropertyValuesHolder zoomOutY = PropertyValuesHolder.ofFloat("scaleY", MAX_SCALE, MIN_SCALE);
            PropertyValuesHolder rotate1 = PropertyValuesHolder.ofFloat("rotationY", FIRST_ROTATE_START, FIRST_ROTATE_END);
            ObjectAnimator visibleToInvisible = ObjectAnimator.ofPropertyValuesHolder(this, rotate1, zoomOutX, zoomOutY);
            visibleToInvisible.setDuration(ANIMATION_DURATION);
            visibleToInvisible.setInterpolator(accelerator);

            PropertyValuesHolder zoomInX = PropertyValuesHolder.ofFloat("scaleX", MIN_SCALE, MAX_SCALE);
            PropertyValuesHolder zoomInY = PropertyValuesHolder.ofFloat("scaleY", MIN_SCALE, MAX_SCALE);
            PropertyValuesHolder rotate2 = PropertyValuesHolder.ofFloat("rotationY", SECOND_ROTATE_START, SECOND_ROTATE_END);
            final ObjectAnimator invisibleToVisible = ObjectAnimator.ofPropertyValuesHolder(this, rotate2, zoomInX, zoomInY);
            invisibleToVisible.setDuration(ANIMATION_DURATION);
            invisibleToVisible.setInterpolator(decelerator);

            visibleToInvisible.addListener(new AnimatorListenerAdapter() {
                @Override public void onAnimationEnd(Animator anim) {
                    updateImage();
                    invisibleToVisible.start();
                }
            });
            visibleToInvisible.start();

        } else {
            final float centerX = getWidth() / 2.0f;
            final float centerY = getHeight() / 2.0f;

            final AnimationSet visibleToInvisible = new AnimationSet(true);
            visibleToInvisible.setDuration(ANIMATION_DURATION);
            visibleToInvisible.setInterpolator(accelerator);
            visibleToInvisible.setFillAfter(false);

            Rotate3dAnimation rotate1 = new Rotate3dAnimation(FIRST_ROTATE_START, FIRST_ROTATE_END, centerX, centerY, 0.0f, true);
            rotate1.setDuration(ANIMATION_DURATION);
            rotate1.setInterpolator(accelerator);
            rotate1.setFillAfter(false);

            visibleToInvisible.addAnimation(rotate1);

            ScaleAnimation scale1 = new ScaleAnimation(MAX_SCALE, MIN_SCALE, MAX_SCALE, MIN_SCALE, centerX, centerY);
            scale1.setDuration(ANIMATION_DURATION);
            scale1.setInterpolator(accelerator);
            scale1.setFillAfter(false);

            visibleToInvisible.addAnimation(scale1);

            final AnimationSet invisibleToVisible = new AnimationSet(true);
            invisibleToVisible.setDuration(ANIMATION_DURATION);
            invisibleToVisible.setInterpolator(decelerator);
            invisibleToVisible.setFillAfter(false);

            Rotate3dAnimation rotate2 = new Rotate3dAnimation(SECOND_ROTATE_START, SECOND_ROTATE_END, centerX, centerY, 0.0f, false);
            rotate2.setDuration(ANIMATION_DURATION);
            rotate2.setInterpolator(decelerator);
            rotate2.setFillAfter(false);

            invisibleToVisible.addAnimation(rotate2);

            ScaleAnimation scale2 = new ScaleAnimation(MIN_SCALE, MAX_SCALE, MIN_SCALE, MAX_SCALE, centerX, centerY);
            scale2.setDuration(ANIMATION_DURATION);
            scale2.setInterpolator(decelerator);
            scale2.setFillAfter(false);

            invisibleToVisible.addAnimation(scale2);

            rotate1.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    updateImage();
                    startAnimation(invisibleToVisible);
                }

                public void onAnimationRepeat(Animation animation) {
                }
            });
            startAnimation(visibleToInvisible);
        }
    }

    private void updateImage() {
        if (mFlipped) {
            setImageBitmap(mBackground);
        } else {
            setImageBitmap(mForeground);
        }
    }
}
