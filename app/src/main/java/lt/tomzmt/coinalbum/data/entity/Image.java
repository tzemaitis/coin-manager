package lt.tomzmt.coinalbum.data.entity;

import android.content.ContentValues;
import android.database.Cursor;

import lt.tomzmt.coinalbum.data.CursorReader;

/**
 * Created by tzemaitis on 13/11/16.
 */

public class Image implements Entity {

    public static final String TABLE_NAME = Image.class.getSimpleName();

    public static final String DATA = "data";

    private long mId = NOT_SET;

    private byte[] mData = null;

    public Image() {
    }

    public Image(Cursor cursor) {
        CursorReader reader = new CursorReader(cursor);
        mId = reader.readLong(ID);
        mData = reader.readBlob(DATA);
    }

    @Override
    public ContentValues getValues() {
        ContentValues values = new ContentValues();
        if (mId != NOT_SET) {
            values.put(ID, mId);
        }

        values.put(DATA, mData);

        return values;
    }

    @Override
    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public byte[] getData() {
        return mData;
    }

    public void setData(byte[] mData) {
        this.mData = mData;
    }
}
