package lt.tomzmt.coinalbum.data;

import android.database.Cursor;
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
public class CoinRepository extends DatabaseRepository<Coin> {

    private static final String[] SEARCH_IN_COLUMNS = new String[] {Coin.NAME, Coin.DENOMINATION, Coin.COUNTRY, Coin.YEARS, Coin.DESCRIPTION};
    private static final String SEARCH_ORDER = Coin.COUNTRY + " ASC," + Coin.YEARS + " ASC," + Coin.NAME + " ASC," + Coin.DENOMINATION + " ASC";

    public CoinRepository() {
        super(Coin.class);
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
        return readAsList(cursor);
    }

    public List<Coin> getForSet(long setId) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(Coin.TABLE_NAME + ", " + Link.COIN_TO_SET);

        String selection = Link.COIN_TO_SET + "." + Link.SET_ID + "= ? AND " +
                Link.COIN_TO_SET + "." + Link.COIN_ID + "=" + Coin.TABLE_NAME + "." + Coin.ID;

        Cursor cursor = builder.query(DatabaseOpener.openDatabase(),
                null, selection, new String[]{String.valueOf(setId)}, null, null, SEARCH_ORDER);
        return readAsList(cursor);
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
}
