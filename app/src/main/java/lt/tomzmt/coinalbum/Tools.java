package lt.tomzmt.coinalbum;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Contains set of utility functions
 * Created by t.zemaitis on 2015.03.11.
 */
public class Tools {

    public static Bitmap readImage(final String imageFilePath, final int maxWidth, final int maxHeight) {
        if (maxWidth != 0 && maxHeight != 0) {
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imageFilePath, bmOptions);

            int scaleFactor = Math.min(bmOptions.outWidth / maxWidth, bmOptions.outHeight / maxHeight);
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            return BitmapFactory.decodeFile(imageFilePath, bmOptions);
        } else {
            return BitmapFactory.decodeFile(imageFilePath);
        }
    }
}
