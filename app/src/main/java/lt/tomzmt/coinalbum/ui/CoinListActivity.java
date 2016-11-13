package lt.tomzmt.coinalbum.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lt.tomzmt.coinalbum.R;
import lt.tomzmt.coinalbum.data.CoinRepository;
import lt.tomzmt.coinalbum.data.ListDataSource;
import lt.tomzmt.coinalbum.data.Repository;
import lt.tomzmt.coinalbum.data.entity.Coin;
import lt.tomzmt.coinalbum.tools.ImageLoader;
import lt.tomzmt.coinalbum.ui.widgets.LoaderListeningImageView;

/**
 * Activity to display searchable list of coin entities
 * Created by t.zemaitis on 2015.03.06.
 */
public class CoinListActivity extends AbsListActivity<Coin> {

    private static final int REQUEST_DETAILS = 1001;

    private ImageLoader mImageLoader;

    @Override
    protected void onStart() {
        super.onStart();
        mImageLoader.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mImageLoader.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.coin_list_action, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, @NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_coin: {
                Intent intent = new Intent(this, CoinEditActivity.class);
                startActivityForResult(intent, REQUEST_DETAILS);
            }
            default: {
                return super.onMenuItemSelected(featureId, item);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_DETAILS && resultCode == RESULT_OK) {
            refresh();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected ArrayAdapter<Coin> createAdapter(List<Coin> items) {
        mImageLoader = new ImageLoader(getApplicationContext());
        return new CoinListAdapter(this, mImageLoader, items);
    }

    @Override
    protected ListDataSource<Coin> createDataSource() {
        Repository<Coin> repository = new CoinRepository();
        return new ListDataSource<>(repository);
    }

    @Override
    public void onDataSetChanged(ListDataSource model) {
        super.onDataSetChanged(model);

        TextView footer = (TextView)findViewById(R.id.text_list_footer);

        List<Coin> coins = model.getItems();

        int coinCount = coins.size();
        Set<String> countries = new HashSet<>();
        for (Coin coin : coins) {
            countries.add(coin.getCountry());
        }

        int countryCount = countries.size();

        Resources res = getResources();
        footer.setText(res.getString(R.string.coin_list_footer,
                coinCount, res.getQuantityString(R.plurals.coin, coinCount),
                countryCount, res.getQuantityString(R.plurals.country, countryCount)));
    }

    @Override
    protected void onEntityClick(Coin entity) {
        Intent intent = new Intent(this, CoinDetailsActivity.class);
        intent.putExtra(CoinDetailsActivity.EXTRA_COIN, entity);
        startActivityForResult(intent, REQUEST_DETAILS);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_coin_list;
    }

    private static class CoinListAdapter extends ArrayAdapter<Coin> {

        private final ImageLoader mImageLoader;

        private CoinListAdapter(Context context, ImageLoader loader, List<Coin> coins) {
            super(context, 0, coins);
            mImageLoader = loader;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_coin, parent, false);
            }

            Coin coin = getItem(position);

            LoaderListeningImageView imageView = (LoaderListeningImageView) convertView.findViewById(R.id.image);
            mImageLoader.loadImage(coin.getId(), imageView);

            TextView name = (TextView)convertView.findViewById(R.id.name);
            name.setText(coin.getDenomination() + " " + coin.getName() + ", " + coin.getYears() +  ", " + coin.getCountry());

            return convertView;
        }
    }
}
