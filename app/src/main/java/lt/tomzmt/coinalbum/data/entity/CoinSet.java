package lt.tomzmt.coinalbum.data.entity;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import lt.tomzmt.coinalbum.data.CursorReader;

/**
 * CoinSet specifies the group of coins related to etch other, for example all coins circulated
 * at the same time and depend to the same currency.
 * Created by t.zemaitis on 2015.08.05.
 */
public class CoinSet implements Entity, Parcelable {

    public static final String TABLE_NAME = CoinSet.class.getSimpleName();

    public static final String NAME = "name";

    public static final String DESCRIPTION = "description";

    private long mId = NOT_SET;

    private String mName;

    private String mDescription;

    public static Creator<CoinSet> CREATOR = new Creator<CoinSet>() {
        @Override
        public CoinSet createFromParcel(Parcel source) {
            return new CoinSet(source);
        }

        @Override
        public CoinSet[] newArray(int size) {
            return new CoinSet[size];
        }
    };

    public CoinSet() {
    }

    public CoinSet(Parcel in) {
        mId = in.readLong();
        mName = in.readString();
        mDescription = in.readString();
    }

    public CoinSet(Cursor cursor) {
        CursorReader reader = new CursorReader(cursor);
        mId = reader.readLong(ID);
        mName = reader.readString(NAME);
        mDescription = reader.readString(DESCRIPTION);
    }

    @Override
    public long getId() {
        return 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mName);
        dest.writeString(mDescription);
    }

    public ContentValues getValues() {
        ContentValues values = new ContentValues();
        if (mId != NOT_SET) {
            values.put(ID, mId);
        }
        values.put(NAME, mName);
        values.put(DESCRIPTION, mDescription);
        return  values;
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

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }
}
