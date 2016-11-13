package lt.tomzmt.coinalbum.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import lt.tomzmt.coinalbum.data.entity.Coin;
import lt.tomzmt.coinalbum.data.entity.Entity;

/**
 * Created by t.zemaitis on 2015.08.19.
 */
public abstract class AbsRepository<T extends Entity> implements Repository<T> {

    @Override
    public T get(long id) {
        T result = null;
        SQLiteDatabase db = DatabaseOpener.openDatabase();
        Cursor cursor = db.query(Coin.TABLE_NAME, null, Coin.ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor.moveToFirst()) {
            result = new T(cursor);
        }
        cursor.close();
        db.close();
        return result;
    }

    @Override
    public long add(T entity) {
        return 0;
    }

    @Override
    public void remove(long id) {

    }

    @Override
    public void update(T entity) {

    }
}
