package lt.tomzmt.coinalbum.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.Bundle;
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
public class CropImageActivity extends Activity {

    static final String TAG = CropImageActivity.class.getSimpleName();

    public static final String EXTRA_IMAGE_PATH = TAG + ".imagePath";

    private String mImagePath = null;
    private Bitmap mOriginalBitmap = null;

    private CropableImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_crop_image);
        findViewById(R.id.buttonDone).setOnClickListener(mOnClickListener);
        findViewById(R.id.buttonCancel).setOnClickListener(mOnClickListener);

        mImagePath = getIntent().getStringExtra(EXTRA_IMAGE_PATH);
        if (mImagePath == null) {
            throw new RuntimeException("Image path do not provided. Add CropImageActivity.EXTRA_IMAGE_PATH as string extra to activity intent");
        }
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mOriginalBitmap = Tools.readImage(mImagePath, metrics.widthPixels, metrics.heightPixels);

        mImageView = (CropableImageView)findViewById(R.id.imageView);
        mImageView.setImageBitmap(mOriginalBitmap);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.buttonDone: {

                    RectF rect = mImageView.getTargetRect();
                    mOriginalBitmap = Bitmap.createBitmap(mOriginalBitmap, Math.round(rect.left), Math.round(rect.top), Math.round(rect.width()), Math.round(rect.height()));
                    try {
                        mOriginalBitmap.compress(Bitmap.CompressFormat.JPEG, 80, new FileOutputStream(mImagePath));
                        setResult(RESULT_OK);
                        finish();
                    } catch (IOException e) {
                        Toast.makeText(CropImageActivity.this, R.string.error_failed_to_open_file, Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case R.id.buttonCancel: {
                    finish();
                    break;
                }
                default:
                    throw new RuntimeException("On Click is not handled");
            }
        }
    };
}
