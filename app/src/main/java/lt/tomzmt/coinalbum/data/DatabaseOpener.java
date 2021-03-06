package lt.tomzmt.coinalbum.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import lt.tomzmt.coinalbum.data.entity.Coin;
import lt.tomzmt.coinalbum.data.entity.Image;
import lt.tomzmt.coinalbum.data.entity.Link;
import lt.tomzmt.coinalbum.data.entity.CoinSet;

/**
 * Helper class for SQLite database access
 * Created by t.zemaitis on 2014.12.06.
 */
public class DatabaseOpener extends SQLiteOpenHelper {

    private static final String DATA_BASE_FILE_NAME = "coins.db";

    private static final int VERSION_1 = 1;

    private static final int CURRENT_VERSION = VERSION_1;

    private  static DatabaseOpener sInstance = null;

    /**
     * Instantiates new instance
     * @param context application context
     */
    public static void init(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseOpener(context);
        }
    }

    /**
     * Opens writable database
     * @return SQLiteDatabase instance
     */
    static SQLiteDatabase openDatabase() {
        if (sInstance != null) {
            return sInstance.getWritableDatabase();
        }
        else {
            throw new IllegalStateException("Database not initialized");
        }
    }

    /**
     * Default constructor
     * @param context context to associate with
     */
    private DatabaseOpener(Context context) {
        super(context, DATA_BASE_FILE_NAME, null, CURRENT_VERSION);
    }

    /**
     *
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Coin.TABLE_NAME + " (" +
                Coin.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Coin.NAME + " TEXT," +
                Coin.DENOMINATION + " TEXT," +
                Coin.YEARS + " TEXT," +
                Coin.COUNTRY + " TEXT," +
                Coin.COMPOSITION + " TEXT," +
                Coin.DIAMETER + " TEXT," +
                Coin.MASS + " TEXT," +
                Coin.EDGE + " TEXT," +
                Coin.MINT + " TEXT," +
                Coin.MINTAGE + " TEXT," +
                Coin.DESCRIPTION + " TEXT," +
                Coin.AVERSE_ID + " INTEGER," +
                Coin.REVERSE_ID + " INTEGER)"
        );

        db.execSQL("CREATE TABLE " + Image.TABLE_NAME + " (" +
                Image.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Image.DATA + " BLOB)"
        );

        db.execSQL("CREATE TABLE " + CoinSet.TABLE_NAME + " (" +
                CoinSet.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                CoinSet.NAME + " TEXT," +
                CoinSet.DESCRIPTION + " TEXT)");

        db.execSQL("CREATE TABLE " + Link.COIN_TO_SET + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                Link.COIN_ID + " INTEGER," +
                Link.SET_ID + " INTEGER)");
    }

    /**
     *
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
