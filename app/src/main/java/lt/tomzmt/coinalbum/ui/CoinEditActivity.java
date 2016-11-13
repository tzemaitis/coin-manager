package lt.tomzmt.coinalbum.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import lt.tomzmt.coinalbum.R;
import lt.tomzmt.coinalbum.Tools;
import lt.tomzmt.coinalbum.data.CoinRepository;
import lt.tomzmt.coinalbum.data.FileManager;
import lt.tomzmt.coinalbum.data.entity.Coin;
import lt.tomzmt.coinalbum.data.entity.Entity;

/**
 * Activity to perform coin editing
 * Created by t.zemaitis on 2015.03.06.
 */
public class CoinEditActivity extends Activity {

    static final String TAG = CoinEditActivity.class.getSimpleName();

    public static final String EXTRA_COIN = TAG + ".TITLE";

    private static final int REQUEST_CAPTURE_AVERSE = 1;
    private static final int REQUEST_CAPTURE_REVERSE = 2;
    private static final int REQUEST_CROP_AVERSE = 3;
    private static final int REQUEST_CROP_REVERSE = 4;

    private ImageView mViewAverse;
    private ImageView mViewReverse;
    private TextView mViewYears;

    private Coin mCoin = null;

    private File mAverseFile = null;
    private File mReverseFile = null;

    private FileManager mFileManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_coin);

        mViewAverse = (ImageView)findViewById(R.id.imageAverse);
        mViewAverse.setOnClickListener(mOnTakeAverseClickListener);

        mViewReverse = (ImageView)findViewById(R.id.imageReverse);
        mViewReverse.setOnClickListener(mOnTakeReverseClickListener);

        mViewYears = (TextView)findViewById(R.id.years);
        mViewYears.setOnClickListener(mOnYearsClickListener);

        String[] countries = getResources().getStringArray(R.array.countries);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, countries);
        AutoCompleteTextView textView = (AutoCompleteTextView)findViewById(R.id.country);
        textView.setAdapter(adapter);

        mAverseFile = new File(getExternalFilesDir(null), "averse_image.jpg");
        mReverseFile = new File(getExternalFilesDir(null), "reverse_image.jpg");

        mFileManager = new FileManager(getApplicationContext());

        mCoin = getIntent().getParcelableExtra(EXTRA_COIN);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            if (mCoin != null) {
                actionBar.setTitle(R.string.edit_coin);
                loadData();
            } else {
                actionBar.setTitle(R.string.new_coin);
                mCoin = new Coin();
            }
        }
    }

    @Override
    public boolean onMenuItemSelected(final int featureId, @NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                saveData();
                Intent intent = new Intent();
                intent.putExtra(EXTRA_COIN, mCoin);
                setResult(RESULT_OK, intent);
                finish();
            }
            default: {
                return super.onMenuItemSelected(featureId, item);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CAPTURE_AVERSE: {
                if (resultCode == RESULT_OK) {
                    dispatchCropPicture(REQUEST_CROP_AVERSE, mAverseFile);
                } else {
                    mAverseFile.delete();
                }
                break;
            }
            case REQUEST_CAPTURE_REVERSE: {
                if (resultCode == RESULT_OK) {
                    dispatchCropPicture(REQUEST_CROP_REVERSE, mReverseFile);
                } else {
                    mReverseFile.delete();
                }
                break;
            }
            case REQUEST_CROP_AVERSE: {
                if (resultCode == RESULT_OK) {
                    loadImage(mAverseFile, mViewAverse);
                } else {
                    mAverseFile.delete();
                }
                break;
            }
            case REQUEST_CROP_REVERSE: {
                if (resultCode == RESULT_OK) {
                    loadImage(mReverseFile, mViewReverse);
                } else {
                    mReverseFile.delete();
                }
                break;
            }
            default: {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    private void loadData() {

        File file = mFileManager.getCoinImage(mCoin.getId(), FileManager.AVERSE);
        loadImage(file, mViewAverse);

        file = mFileManager.getCoinImage(mCoin.getId(), FileManager.REVERSE);
        loadImage(file, mViewReverse);

        TextView view = (TextView)findViewById(R.id.name);
        view.setText(mCoin.getDenomination() + " " + mCoin.getName());

        view = (TextView)findViewById(R.id.country);
        view.setText(mCoin.getCountry());

        view = (TextView)findViewById(R.id.years);
        view.setText(mCoin.getYears());

        view = (TextView)findViewById(R.id.description);
        view.setText(mCoin.getDescription());
    }

    private static void loadImage(File imageFile, ImageView imageView) {
        if (imageFile.exists()) {
            Bitmap imageBitmap = Tools.readImage(imageFile.getAbsolutePath(),
                    imageView.getWidth(), imageView.getHeight());
            imageView.setImageBitmap(imageBitmap);
        }
    }

    private void saveData() {
        TextView view = (TextView)findViewById(R.id.name);
        String name = view.getText().toString();
        if (name != null) {
            int index = name.indexOf(' ');
            if (index != -1) {
                mCoin.setDenomination(name.substring(0, index));
                mCoin.setName(name.substring(index + 1));
            } else {
                mCoin.setName(name);
            }
        }

        view = (TextView)findViewById(R.id.country);
        mCoin.setCountry(view.getText().toString());

        view = (TextView)findViewById(R.id.years);
        mCoin.setYears(view.getText().toString());

        view = (TextView)findViewById(R.id.description);
        mCoin.setDescription(view.getText().toString());

        CoinRepository repository = new CoinRepository();
        if (mCoin.getId() == Entity.NOT_SET) {
            long id = repository.add(mCoin);
            mCoin.setId(id);
        } else {
            repository.update(mCoin);
        }

        if (mAverseFile.exists()) {
            mFileManager.addCoinImage(mCoin.getId(), mAverseFile, FileManager.AVERSE);
            mAverseFile.delete();
        }

        if (mReverseFile.exists()) {
            mFileManager.addCoinImage(mCoin.getId(), mReverseFile, FileManager.REVERSE);
            mReverseFile.delete();
        }
    }

    private void dispatchTakePictureIntent(int requestId, File outputFile) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                if (outputFile.createNewFile()) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outputFile));
                    startActivityForResult(takePictureIntent, requestId);
                } else {
                    throw new IOException("Failed to create file");
                }
            } catch (IOException e) {
                Toast.makeText(this, R.string.error_failed_to_create_file, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void dispatchCropPicture(int requestId, File sourceFile) {
        Intent cropPictureIntent = new Intent(this, CropImageActivity.class);
        cropPictureIntent.putExtra(CropImageActivity.EXTRA_IMAGE_PATH, sourceFile.getAbsolutePath());
        startActivityForResult(cropPictureIntent, requestId);
    }

    private View.OnClickListener mOnTakeAverseClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dispatchTakePictureIntent(REQUEST_CAPTURE_AVERSE, mAverseFile);
        }
    };

    private View.OnClickListener mOnTakeReverseClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dispatchTakePictureIntent(REQUEST_CAPTURE_REVERSE, mReverseFile);
        }
    };

    private View.OnClickListener mOnYearsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int currentYears = Calendar.getInstance().get(Calendar.YEAR);
            int currentSelection = currentYears;
            String yearsString = mViewYears.getText().toString();
            if (!TextUtils.isEmpty(yearsString)) {
                try {
                    currentSelection = Integer.valueOf(yearsString);
                } catch (NumberFormatException e) {/* IGNORE */}
            }

            final Dialog d = new Dialog(CoinEditActivity.this);
            d.setTitle(R.string.select_years);
            d.setContentView(R.layout.dialog_years_picker);

            final NumberPicker np = (NumberPicker) d.findViewById(R.id.pickerNumber);
            np.setMinValue(1800);
            np.setMaxValue(currentYears);
            np.setValue(currentSelection);
            np.setWrapSelectorWheel(false);

            Button b1 = (Button) d.findViewById(R.id.buttonSet);
            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewYears.setText(String.valueOf(np.getValue()));
                    d.dismiss();
                }
            });

            d.show();
        }
    };
}
