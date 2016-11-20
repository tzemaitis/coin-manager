package lt.tomzmt.coinalbum.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import lt.tomzmt.coinalbum.R;
import lt.tomzmt.coinalbum.Tools;
import lt.tomzmt.coinalbum.data.DatabaseRepository;
import lt.tomzmt.coinalbum.data.entity.Coin;
import lt.tomzmt.coinalbum.data.entity.Entity;
import lt.tomzmt.coinalbum.data.entity.Image;
import lt.tomzmt.coinalbum.ui.widgets.FlippingImageView;

/**
 * Activity to display coin details
 * Created by t.zemaitis on 2015.03.22.
 */
public class CoinDetailsActivity extends AppCompatActivity {

    static final String TAG = CoinDetailsActivity.class.getSimpleName();

    public static final String EXTRA_COIN = TAG + ".coin";

    private static int REQUEST_EDIT = 1001;

    private Coin mCoin;

    private DatabaseRepository<Image> mImageManager = new DatabaseRepository<>(Image.class);

    private int mPreferredImageSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_details);
        setResult(RESULT_CANCELED);

        mPreferredImageSize = getResources().getDimensionPixelSize(R.dimen.details_image_height);

        mCoin = getIntent().getParcelableExtra(EXTRA_COIN);
        if (mCoin == null) {
            throw new RuntimeException("Coin argument not provided");
        }

        populate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.coin_details_action, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit_coin: {
                Intent intent = new Intent(this, CoinEditActivity.class);
                intent.putExtra(CoinEditActivity.EXTRA_COIN, mCoin);
                startActivityForResult(intent, REQUEST_EDIT);
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_EDIT && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            mCoin = data.getParcelableExtra(CoinEditActivity.EXTRA_COIN);
            populate();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void populate() {
        ViewGroup container = (ViewGroup)findViewById(R.id.container_name);
        setFieldValue(container, mCoin.getDenomination() + " " + mCoin.getName());

        container = (ViewGroup)findViewById(R.id.container_country);
        setFieldValue(container, mCoin.getCountry());

        container = (ViewGroup)findViewById(R.id.container_years);
        setFieldValue(container, mCoin.getYears());

        container = (ViewGroup)findViewById(R.id.container_composition);
        setFieldValue(container, mCoin.getComposition());

        container = (ViewGroup)findViewById(R.id.container_diameter);
        setFieldValue(container, mCoin.getDiameter());

        container = (ViewGroup)findViewById(R.id.container_edge);
        setFieldValue(container, mCoin.getEdge());

        container = (ViewGroup)findViewById(R.id.container_description);
        setFieldValue(container, mCoin.getDescription());

        container = (ViewGroup)findViewById(R.id.container_mass);
        setFieldValue(container, mCoin.getMass());

        container = (ViewGroup)findViewById(R.id.container_mint);
        setFieldValue(container, mCoin.getMint());

        container = (ViewGroup)findViewById(R.id.container_mintage);
        setFieldValue(container, mCoin.getMintage());

        FlippingImageView imageView = (FlippingImageView)findViewById(R.id.image);
        imageView.setForegroundBitmap(loadImage(mCoin.getAverseId()));
        imageView.setBackgroundBitmap(loadImage(mCoin.getReverseId()));
    }

    private void setFieldValue(ViewGroup field, String value) {
        if (!TextUtils.isEmpty(value)) {
            ((TextView)field.getChildAt(1)).setText(value);
        } else {
            field.setVisibility(View.GONE);
        }
    }

    private Bitmap loadImage(long id) {
        Bitmap bitmap;
        if (id != Entity.NOT_SET) {
            Image image = mImageManager.get(id);
            bitmap = Tools.decodeImage(image.getData(), mPreferredImageSize, mPreferredImageSize);
        } else {
            bitmap = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_gallery);
        }
        return bitmap;
    }
}
