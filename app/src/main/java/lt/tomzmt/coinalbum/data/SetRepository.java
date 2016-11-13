package lt.tomzmt.coinalbum.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import lt.tomzmt.coinalbum.data.entity.Coin;
import lt.tomzmt.coinalbum.data.entity.Set;

/**
 * Created by t.zemaitis on 2015.08.19.
*/
public class SetRepository implements Repository<Set> {

    @Override
    public Set get(long id) {
        Set result = null;
        SQLiteDatabase db = DatabaseOpener.openDatabase();
        Cursor cursor = db.query(Set.TABLE_NAME, null, Set.ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor.moveToFirst()) {
            result = new Set(cursor);
        }
        cursor.close();
        db.close();
        return result;
    }

    @Override
    public long add(Set entity) {
        ContentValues values = entity.getValues();
        SQLiteDatabase db = DatabaseOpener.openDatabase();
        long id = db.insert(Set.TABLE_NAME, null, values);
        if (id == -1) {
            throw new RuntimeException("Failed to add set");
        }
        db.close();
        return id;
    }

    @Override
    public void remove(long id) {

    }

    @Override
    public void update(Set entity) {

    }

    @Override
    public List<Set> search(String key) {
        return null;
    }
}
