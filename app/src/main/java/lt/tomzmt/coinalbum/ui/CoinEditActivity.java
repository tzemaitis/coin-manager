package lt.tomzmt.coinalbum.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import lt.tomzmt.coinalbum.data.DatabaseRepository;
import lt.tomzmt.coinalbum.data.entity.Coin;
import lt.tomzmt.coinalbum.data.entity.Entity;
import lt.tomzmt.coinalbum.data.entity.Image;

/**
 * Activity to perform coin editing
 * Created by t.zemaitis on 2015.03.06.
 */
public class CoinEditActivity extends AppCompatActivity {

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

    private DatabaseRepository<Image> mImageManager = null;
    private CoinRepository mCoinRepository = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_coin);

        mViewAverse = (ImageView)findViewById(R.id.imageAverse);
        mViewAverse.setOnClickListener((View v) -> dispatchTakePictureIntent(REQUEST_CAPTURE_AVERSE, mAverseFile));

        mViewReverse = (ImageView)findViewById(R.id.imageReverse);
        mViewReverse.setOnClickListener((View v) -> dispatchTakePictureIntent(REQUEST_CAPTURE_REVERSE, mReverseFile));

        mViewYears = (TextView)findViewById(R.id.years);
        mViewYears.setOnClickListener((View v) -> showYearsChooser());

        String[] countries = getResources().getStringArray(R.array.countries);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, countries);
        AutoCompleteTextView textView = (AutoCompleteTextView)findViewById(R.id.country);
        textView.setAdapter(adapter);

        mAverseFile = new File(getExternalFilesDir(null), "averse_image.jpg");
        mReverseFile = new File(getExternalFilesDir(null), "reverse_image.jpg");

        mImageManager = new DatabaseRepository<>(Image.class);
        mCoinRepository = new CoinRepository();

        mCoin = getIntent().getParcelableExtra(EXTRA_COIN);

        ActionBar actionBar = getSupportActionBar();
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
    protected void onDestroy() {
        if (mAverseFile.exists()) {
            mAverseFile.delete();
        }
        if (mReverseFile.exists()) {
            mReverseFile.delete();
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                saveData();
                Intent intent = new Intent();
                intent.putExtra(EXTRA_COIN, mCoin);
                setResult(RESULT_OK, intent);
                finish();
                return true;
            }
            default: {
                return  super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CAPTURE_AVERSE: {
                if (resultCode == RESULT_OK) {
                    dispatchCropPicture(REQUEST_CROP_AVERSE, mAverseFile);
                }
                break;
            }
            case REQUEST_CAPTURE_REVERSE: {
                if (resultCode == RESULT_OK) {
                    dispatchCropPicture(REQUEST_CROP_REVERSE, mReverseFile);
                }
                break;
            }
            case REQUEST_CROP_AVERSE: {
                if (resultCode == RESULT_OK) {
                    loadImage(mAverseFile, mViewAverse);
                }
                break;
            }
            case REQUEST_CROP_REVERSE: {
                if (resultCode == RESULT_OK) {
                    loadImage(mReverseFile, mViewReverse);
                }
                break;
            }
            default: {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    private void loadData() {
        loadImage(mCoin.getAverseId(), mViewAverse);
        loadImage(mCoin.getReverseId(), mViewReverse);

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

    private void loadImage(long id, ImageView imageView) {
        Image image = mImageManager.get(id);
        byte[] imageData = image.getData();
        if (imageData != null) {
            Bitmap imageBitmap = Tools.decodeImage(imageData,
                    imageView.getWidth(), imageView.getHeight());
            imageView.setImageBitmap(imageBitmap);
        }
    }

    private void saveData() {
        TextView view = (TextView)findViewById(R.id.name);
        String name = view.getText().toString();
        int index = name.indexOf(' ');
        if (index != -1) {
            mCoin.setDenomination(name.substring(0, index));
            mCoin.setName(name.substring(index + 1));
        } else {
            mCoin.setName(name);
        }

        view = (TextView)findViewById(R.id.country);
        mCoin.setCountry(view.getText().toString());

        view = (TextView)findViewById(R.id.years);
        mCoin.setYears(view.getText().toString());

        view = (TextView)findViewById(R.id.description);
        mCoin.setDescription(view.getText().toString());

        mCoin.setAverseId(saveImage(mAverseFile, mCoin.getAverseId()));
        mCoin.setReverseId(saveImage(mReverseFile, mCoin.getReverseId()));

        if (mCoin.getId() == Entity.NOT_SET) {
            long id = mCoinRepository.add(mCoin);
            mCoin.setId(id);
        } else {
            mCoinRepository.update(mCoin);
        }
    }

    private long saveImage(File file, long id) {
        if (file.exists()) {
            byte[] data = Tools.readFile(file.getAbsolutePath());
            Image image = new Image();
            image.setId(id);
            image.setData(data);

            if (image.getId() == Entity.NOT_SET) {
                id = mImageManager.add(image);
            } else {
                mImageManager.update(image);
            }
            final boolean delete = file.delete();
            if (!delete) {
                Toast.makeText(this, R.string.error_failed_to_delete_file, Toast.LENGTH_SHORT).show();
            }
        }
        return id;
    }

    private void dispatchTakePictureIntent(int requestId, File outputFile) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                if (outputFile.exists()) {
                    outputFile.delete();
                }
                outputFile.createNewFile();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outputFile));
                startActivityForResult(takePictureIntent, requestId);
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

    private void showYearsChooser() {
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
        b1.setOnClickListener((View) -> {
            mViewYears.setText(String.valueOf(np.getValue()));
            d.dismiss();
        });

        d.show();
    }
}
