package lt.tomzmt.coinalbum.data;

import android.database.Cursor;

/**
 * Helper class to read information from cursor
 * Created by t.zemaitis on 2014.12.08.
 */
public class CursorReader {

    private final Cursor mCursor;

    public CursorReader(final Cursor cursor) {
        mCursor = cursor;
    }

    public long readLong(String name) {
        int index = getIndex(name);
        return mCursor.getLong(index);
    }

    public String readString(String name) {
        int index = getIndex(name);
        return mCursor.getString(index);
    }

    public byte[] readBlob(String name) {
        int index = getIndex(name);
        return mCursor.getBlob(index);
    }

    private int getIndex(String name) {
        int index = mCursor.getColumnIndex(name);
        if (index == -1) {
            throw new IllegalArgumentException("Field " + name + " not exist on this cursor");
        }
        return index;
    }
}
