package lt.tomzmt.coinalbum.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import lt.tomzmt.coinalbum.data.entity.Coin;
import lt.tomzmt.coinalbum.data.entity.Entity;

/**
 * Created by t.zemaitis on 2015.08.19.
 */
public class DatabaseRepository<T extends Entity> implements Repository<T> {

    private Class<T> mTypeArgumentClass;

    public DatabaseRepository(Class<T> typeArgumentClass) {
        mTypeArgumentClass = typeArgumentClass;
    }

    @Override
    public T get(long id) {
        T result = null;
        if (id != Entity.NOT_SET) {
            SQLiteDatabase db = DatabaseOpener.openDatabase();
            Cursor cursor = db.query(getTableName(), null, Entity.ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);
            if (cursor.moveToFirst()) {
                try {
                    result = mTypeArgumentClass.getConstructor(Cursor.class).newInstance(cursor);
                } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            cursor.close();
            db.close();
        } else {
            try {
                result = mTypeArgumentClass.getConstructor().newInstance();
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public long add(T entity) {
        ContentValues values = entity.getValues();
        SQLiteDatabase db = DatabaseOpener.openDatabase();
        long id = db.insert(getTableName(), null, values);
        if (id == -1) {
            throw new RuntimeException("Failed to add entity to table " + getTableName());
        }
        db.close();
        return id;

    }

    @Override
    public void remove(long id) {
        SQLiteDatabase db = DatabaseOpener.openDatabase();
        db.delete(getTableName(), Entity.ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    @Override
    public void update(T entity) {
        SQLiteDatabase db = DatabaseOpener.openDatabase();
        ContentValues values = entity.getValues();
        db.update(getTableName(), values, Entity.ID + " = ?", new String[]{String.valueOf(entity.getId())});
        db.close();
    }

    @Override
    public List<T> search(String key) {
        return null;
    }

    private String getTableName() {
        try {
            Field f = mTypeArgumentClass.getField("TABLE_NAME");
            return (String)f.get(null);
        } catch (NoSuchFieldException| IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
