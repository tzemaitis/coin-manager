package lt.tomzmt.coinalbum.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import lt.tomzmt.coinalbum.data.entity.Coin;
import lt.tomzmt.coinalbum.data.entity.Link;

/**
 * Coin entities repository
 * Created by t.zemaitis on 2014.12.08.
 */
public class CoinRepository implements Repository<Coin> {

    private static final String[] SEARCH_IN_COLUMNS = new String[] {Coin.NAME, Coin.DENOMINATION, Coin.COUNTRY, Coin.YEARS, Coin.DESCRIPTION};
    private static final String SEARCH_ORDER = Coin.COUNTRY + " ASC," + Coin.YEARS + " ASC," + Coin.NAME + " ASC," + Coin.DENOMINATION + " ASC";

    public CoinRepository() {
    }

    public Coin get(long id) {
        Coin result = null;
        SQLiteDatabase db = DatabaseOpener.openDatabase();
        Cursor cursor = db.query(Coin.TABLE_NAME, null, Coin.ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor.moveToFirst()) {
            result = new Coin(cursor);
        }
        cursor.close();
        db.close();
        return result;
    }

    public long add(Coin coin) {
        ContentValues values = coin.getValues();
        SQLiteDatabase db = DatabaseOpener.openDatabase();
        long id = db.insert(Coin.TABLE_NAME, null, values);
        if (id == -1) {
            throw new RuntimeException("Failed to add coin");
        }
        db.close();
        return id;
    }

    public void remove(long id) {
        SQLiteDatabase db = DatabaseOpener.openDatabase();
        db.delete(Coin.TABLE_NAME, Coin.ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    @Override
    public void update(Coin entity) {
        SQLiteDatabase db = DatabaseOpener.openDatabase();
        ContentValues values = entity.getValues();
        db.update(Coin.TABLE_NAME, values, Coin.ID + " = ?", new String[]{String.valueOf(entity.getId())});
        db.close();
    }

    public List<Coin> search(String keys) {
        String selection = null;
        if (!TextUtils.isEmpty(keys)) {
            StringBuilder argBuilder = new StringBuilder();
            String[] keysArray = keys.split(" ");
            for (String key : keysArray) {
                if (key.length() > 0) {
                    if (argBuilder.length() != 0) {
                        argBuilder.append(" AND ");
                    }
                    StringBuilder builderInner = new StringBuilder();
                    for (String column : SEARCH_IN_COLUMNS) {
                        if (builderInner.length() != 0) {
                            builderInner.append(" OR ");
                        }
                        builderInner.append(column).append(" LIKE '%").append(key).append("%'");
                    }
                    argBuilder.append('(');
                    argBuilder.append(builderInner.toString());
                    argBuilder.append(')');
                }
            }
            selection = argBuilder.toString();
        }
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(Coin.TABLE_NAME);
        Cursor cursor = builder.query(DatabaseOpener.openDatabase(),
                null, selection, null, null, null, SEARCH_ORDER);
        List<Coin> result = readAsList(cursor);
        return result;
    }

    private List<Coin> readAsList(Cursor cursor) {
        List<Coin> result = new ArrayList<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    result.add(new Coin(cursor));
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        return result;
    }

    public List<Coin> getForSet(long setId) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(Coin.TABLE_NAME + ", " + Link.COIN_TO_SET);

        String selection = Link.COIN_TO_SET + "." + Link.SET_ID + "= ? AND " +
                Link.COIN_TO_SET + "." + Link.COIN_ID + "=" + Coin.TABLE_NAME + "." + Coin.ID;

        Cursor cursor = builder.query(DatabaseOpener.openDatabase(),
                null, selection, new String[]{String.valueOf(setId)}, null, null, SEARCH_ORDER);
        List<Coin> result = readAsList(cursor);
        return result;
    }
}
