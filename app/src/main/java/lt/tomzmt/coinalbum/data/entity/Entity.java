package lt.tomzmt.coinalbum.data.entity;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Data entry
 * Created by t.zemaitis on 2014.12.08.
 */
public interface Entity {

    String ID = "id";

    int NOT_SET = -1;

    long getId();

    ContentValues getValues();
}
