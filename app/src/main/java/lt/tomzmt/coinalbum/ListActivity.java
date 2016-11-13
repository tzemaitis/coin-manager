package lt.tomzmt.coinalbum;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import lt.tomzmt.coinalbum.data.CountryRepository;
import lt.tomzmt.coinalbum.data.ListDataSource;
import lt.tomzmt.coinalbum.data.entity.Country;

public class ListActivity extends Activity implements ListDataSource.ListDataModelObserver<Country>, TextWatcher {

    private CountryListAdapter mCountryListAdapter;

    private ListDataSource<Country> mListDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);


        EditText edit = (EditText)findViewById(R.id.edit_search);
        edit.addTextChangedListener(this);

        mListDataSource = new ListDataSource<>(new CountryRepository(this));
        mListDataSource.setObserver(this);

        // specify an adapter (see also next example)
        mCountryListAdapter = new CountryListAdapter(this, mListDataSource.getItems());
        ListView listView = (ListView)findViewById(R.id.list);
        listView.setAdapter(mCountryListAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mListDataSource.load();
    }

    @Override
    public void onDataSetChanged(ListDataSource model) {
        mCountryListAdapter.notifyDataSetChanged();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        mListDataSource.filter(s.toString());
    }

    private static class CountryListAdapter extends ArrayAdapter<Country> {

        private CountryListAdapter(Context context, List<Country> countries) {
            super(context, 0, countries);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_country, parent, false);
            }

            Country country = getItem(position);

            TextView name = (TextView)convertView.findViewById(R.id.name);
            name.setText(country.getName());

            return convertView;
        }
    }
}
