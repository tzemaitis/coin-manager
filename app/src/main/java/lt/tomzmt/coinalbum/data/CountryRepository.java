package lt.tomzmt.coinalbum.data;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import lt.tomzmt.coinalbum.R;
import lt.tomzmt.coinalbum.data.entity.Country;

/**
 * Country entities repository
 * Created by t.zemaitis on 2014.12.08.
 */
public class CountryRepository implements Repository<Country> {

    private static List<Country> sFullDateSet = null;

    public CountryRepository(Context context) {
        if (sFullDateSet == null) {
            Resources res = context.getResources();
            String[] names = res.getStringArray(R.array.countries);
            sFullDateSet = new ArrayList<>(names.length);
            for (int i = 0; i < names.length; ++i) {
                Country country = new Country();
                country.setId(i);
                country.setName(names[i]);
                sFullDateSet.add(country);
            }
        }
    }

    @Override
    public Country get(long id) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<Country> search(String key) {
        if (TextUtils.isEmpty(key)) {
            return sFullDateSet;
        }
        else {
            List<Country> subSet = new ArrayList<>();
            for (Country country: sFullDateSet) {
                if (country.getName().toLowerCase().contains(key.toLowerCase())) {
                    subSet.add(country);
                }
            }
            return subSet;
        }
    }

    @Override
    public long add(Country coin) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void remove(long id) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void update(Country entity) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
