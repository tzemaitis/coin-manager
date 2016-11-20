package lt.tomzmt.coinalbum.ui;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;

import lt.tomzmt.coinalbum.R;
import lt.tomzmt.coinalbum.Tools;
import lt.tomzmt.coinalbum.ui.widgets.CropableImageView;

/**
 * Activity to perform coin image crop
 * Created by t.zemaitis on 2015.03.11.
 */
public class CropImageActivity extends AppCompatActivity {

    static final String TAG = CropImageActivity.class.getSimpleName();

    public static final String EXTRA_IMAGE_PATH = TAG + ".imagePath";

    private String mImagePath = null;
    private Bitmap mOriginalBitmap = null;

    private CropableImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_crop_image);
        findViewById(R.id.buttonDone).setOnClickListener((View v) -> done());
        findViewById(R.id.buttonCancel).setOnClickListener((View v) -> cancel());

        mImagePath = getIntent().getStringExtra(EXTRA_IMAGE_PATH);
        if (mImagePath == null) {
            throw new RuntimeException("Image path do not provided. Add CropImageActivity.EXTRA_IMAGE_PATH as string extra to activity intent");
        }
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        try {
            mOriginalBitmap = Tools.readImage(mImagePath, metrics.widthPixels, metrics.heightPixels);

            ExifInterface exif = new ExifInterface(mImagePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            mOriginalBitmap = rotateBitmap(mOriginalBitmap, orientation);

            mImageView = (CropableImageView) findViewById(R.id.imageView);
            mImageView.setImageBitmap(mOriginalBitmap);
        } catch (IOException e) {
            Toast.makeText(CropImageActivity.this, R.string.error_failed_to_open_file, Toast.LENGTH_SHORT).show();
        }
    }

    private void done() {
        RectF rect = mImageView.getTargetRect();
        mOriginalBitmap = Bitmap.createBitmap(mOriginalBitmap, Math.round(rect.left), Math.round(rect.top), Math.round(rect.width()), Math.round(rect.height()));
        try {
            mOriginalBitmap.compress(Bitmap.CompressFormat.JPEG, 70, new FileOutputStream(mImagePath));
            setResult(RESULT_OK);
            finish();
        } catch (IOException e) {
            Toast.makeText(CropImageActivity.this, R.string.error_failed_to_open_file, Toast.LENGTH_SHORT).show();
        }
    }

    private void cancel() {
        finish();
    }

    private static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }
}
