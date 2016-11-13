package lt.tomzmt.coinalbum.data.entity;

import android.content.ContentValues;

/**
 * Country entity
 * Created by t.zemaitis on 2014.12.08.
 */
public class Country implements Entity {

    private long mId;

    private String mName;

    public Country() {
    }

    public void setId(long id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    @Override
    public long getId() {
        return mId;
    }

    @Override
    public String getTableName() {
        return null;
    }

    @Override
    public ContentValues getValues() {
        return null;
    }
}
