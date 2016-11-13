package lt.tomzmt.coinalbum.ui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import lt.tomzmt.coinalbum.R;

/**
 * TODO: document your custom view class.
 */
public class CropableImageView extends ImageView {
//    private String mExampleString; // TODO: use a default from R.string...
//    private int mExampleColor = Color.RED; // TODO: use a default from R.color...
//    private float mExampleDimension = 0; // TODO: use a default from R.dimen...
//    private Drawable mExampleDrawable;
//
//    private TextPaint mTextPaint;
//    private float mTextWidth;
//    private float mTextHeight;

    private enum Action {
        MoveAll,
        MoveTopLeft,
        MoveTopRight,
        MoveBottomRight,
        MoveBottomLeft,
        None
    }

    private Paint mPaint;

    private RectF mTargetBounds = new RectF();

    private PointF mActionStartPoint = new PointF();
    private PointF mOriginalTopLeft = new PointF();
    private PointF mOriginalBottomRight = new PointF();

    private Action mAction = Action.None;

    private int mOriginalImageWidth;
    private int mOriginalImageHeight;

    public CropableImageView(Context context) {
        super(context);
        init(null, 0);
    }

    public CropableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CropableImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    public RectF getTargetRect() {
        float scaleFactor = Math.min((float)mOriginalImageWidth / (float)getWidth(), (float)mOriginalImageHeight / (float)getHeight());
        Matrix matrix = new Matrix();
        matrix.setScale(scaleFactor, scaleFactor);
        RectF resultRect = new RectF();
        matrix.mapRect(resultRect, mTargetBounds);
        return resultRect;
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        mOriginalImageWidth = bm.getWidth();
        mOriginalImageHeight = bm.getHeight();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mActionStartPoint.x = event.getX();
                mActionStartPoint.y = event.getY();

                mOriginalTopLeft.set(mTargetBounds.left, mTargetBounds.top);
                mOriginalBottomRight.set(mTargetBounds.right, mTargetBounds.bottom);

                if (mTargetBounds.contains(mActionStartPoint.x, mActionStartPoint.y)) {
                    mAction = Action.MoveAll;
                } else if ((mActionStartPoint.x < mTargetBounds.left && mActionStartPoint.y < mTargetBounds.centerY()) ||
                        mActionStartPoint.y < mTargetBounds.top && mActionStartPoint.x < mTargetBounds.centerX()){
                    mAction = Action.MoveTopLeft;
                } else if ((mActionStartPoint.x > mTargetBounds.left && mActionStartPoint.y < mTargetBounds.centerY()) ||
                        mActionStartPoint.y < mTargetBounds.top && mActionStartPoint.x > mTargetBounds.centerX()){
                    mAction = Action.MoveTopRight;
                } else if ((mActionStartPoint.x > mTargetBounds.left && mActionStartPoint.y > mTargetBounds.centerY()) ||
                        mActionStartPoint.y > mTargetBounds.top && mActionStartPoint.x > mTargetBounds.centerX()) {
                    mAction = Action.MoveBottomRight;
                } else if ((mActionStartPoint.x < mTargetBounds.left && mActionStartPoint.y > mTargetBounds.centerY()) ||
                        mActionStartPoint.y > mTargetBounds.top && mActionStartPoint.x < mTargetBounds.centerX()) {
                    mAction = Action.MoveBottomLeft;
                } else {
                    mAction = Action.None;
                }
            }
            case MotionEvent.ACTION_MOVE: {
                float deltaX = mActionStartPoint.x - event.getX();
                float deltaY = mActionStartPoint.y - event.getY();
                switch (mAction) {
                    case MoveAll: {
                        mTargetBounds.offsetTo(mOriginalTopLeft.x - deltaX, mOriginalTopLeft.y - deltaY);
                        break;
                    }
                    case MoveTopLeft: {
                        float average = (deltaX + deltaY) / 2;
                        mTargetBounds.top = mOriginalTopLeft.y - average;
                        mTargetBounds.left = mOriginalTopLeft.x - average;
                        break;
                    }
                    case MoveTopRight: {
                        float average = (deltaX + deltaY * -1) / 2;
                        mTargetBounds.top = mOriginalTopLeft.y + average;
                        mTargetBounds.right = mOriginalBottomRight.x - average;
                        break;
                    }
                    case MoveBottomRight: {
                        float average = (deltaX + deltaY) / 2;
                        mTargetBounds.bottom = mOriginalBottomRight.y - average;
                        mTargetBounds.right = mOriginalBottomRight.x - average;
                        break;
                    }
                    case MoveBottomLeft: {
                        float average = (deltaX + deltaY * -1) / 2;
                        mTargetBounds.bottom = mOriginalBottomRight.y + average;
                        mTargetBounds.left = mOriginalTopLeft.x - average;
                        break;
                    }
                    default: {
                        break;
                    }
                }
                invalidate();
            }
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mTargetBounds.isEmpty()) {
            int squareSize = Math.round(Math.min(getWidth(), getHeight()) * 0.6f);
            mTargetBounds.left = (getWidth() - squareSize) / 2;
            mTargetBounds.top = (getHeight() - squareSize) / 2;
            mTargetBounds.right = mTargetBounds.left + squareSize;
            mTargetBounds.bottom = mTargetBounds.top + squareSize;
        }

        canvas.drawRect(mTargetBounds, mPaint);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.CropableImageView, defStyle, 0);

//        mExampleString = a.getString(
//                R.styleable.CropableImageView_exampleString);
//        mExampleColor = a.getColor(
//                R.styleable.CropableImageView_exampleColor,
//                mExampleColor);
//        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
//        // values that should fall on pixel boundaries.
//        mExampleDimension = a.getDimension(
//                R.styleable.CropableImageView_exampleDimension,
//                mExampleDimension);
//
//        if (a.hasValue(R.styleable.CropableImageView_exampleDrawable)) {
//            mExampleDrawable = a.getDrawable(
//                    R.styleable.CropableImageView_exampleDrawable);
//            mExampleDrawable.setCallback(this);
//        }

        a.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.GREEN);
        mPaint.setStrokeWidth(2);
        mPaint.setStyle(Paint.Style.STROKE);
    }
}
