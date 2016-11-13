package lt.tomzmt.coinalbum.data;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * Manager for coin images
 * Created by t.zemaitis on 2014.12.08.
 */
public class FileManager {

    static final String TAG = FileManager.class.getSimpleName();

    public static String AVERSE = "_averse.jpg";
    public static String REVERSE = "_reverse.jpg";

    private final File mOutputDirectory;

    public FileManager(Context context) {
        mOutputDirectory = new File(context.getExternalFilesDir(null), "images");
        if (!mOutputDirectory.exists()) {
           boolean result = mOutputDirectory.mkdirs();
            if (!result) {
                Log.e(TAG, "Failed to create direcctory for images");
            }
        }
    }

    public void addCoinImage(long id, File from, String type) {
        File to = new File(mOutputDirectory, Long.toString(id) + type);
        try {
            copy(from, to);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getCoinImage(long id, String type) {
        return new File(mOutputDirectory, Long.toString(id) + type);
    }

    private static void copy(File from, File to) throws IOException {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(from);
            final ReadableByteChannel inputChannel = Channels.newChannel(fis);
            fos = new FileOutputStream(to);
            final WritableByteChannel outputChannel = Channels.newChannel(fos);
            fastCopy(inputChannel, outputChannel);
        } finally {
            if (fis != null) {
                fis.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
    }

    private static void fastCopy(final ReadableByteChannel from, final WritableByteChannel to) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
        while(from.read(buffer) != -1) {
            buffer.flip();
            to.write(buffer);
            buffer.compact();
        }
        buffer.flip();
        while(buffer.hasRemaining()) {
            to.write(buffer);
        }
    }
}
