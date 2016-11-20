package lt.tomzmt.coinalbum.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.List;

import lt.tomzmt.coinalbum.R;
import lt.tomzmt.coinalbum.data.ListDataSource;
import lt.tomzmt.coinalbum.data.entity.Entity;

/**
 * Abstract activity to display searchable list of entities
 * Created by t.zemaitis on 2015.03.04.
 */
public abstract class AbsListActivity<T extends Entity> extends AppCompatActivity implements ListDataSource.ListDataModelObserver<T> {

    private ArrayAdapter<T> mListAdapter;

    private ListDataSource<T> mListDataSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());

        EditText edit = (EditText)findViewById(R.id.edit_search);
        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                mListDataSource.filter(s.toString());
            }
        });

        mListDataSource = createDataSource();
        mListDataSource.setObserver(this);

        mListAdapter = createAdapter(mListDataSource.getItems());

        ListView listView = (ListView)findViewById(R.id.list);
        listView.setOnItemClickListener((AdapterView<?> adapterView, View view, int i, long l) -> {
            T entity = mListAdapter.getItem(i);
            onEntityClick(entity);
        });
        listView.setAdapter(mListAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mListDataSource.load();
    }

    @Override
    public void onDataSetChanged(ListDataSource<T> model) {
        mListAdapter.notifyDataSetChanged();
    }

    public void refresh() {
        mListDataSource.unload();
        mListDataSource.load();
    }

    protected int getLayoutResourceId() {
        return R.layout.activity_list;
    }

    protected abstract ListDataSource<T> createDataSource();

    protected abstract ArrayAdapter<T> createAdapter(List<T> items);

    protected abstract void onEntityClick(T entity);
}
