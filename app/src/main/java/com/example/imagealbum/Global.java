package com.example.imagealbum;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;

public class Global {
    // ------------------ Database connect ------------------
    public static final String DATABASE_NAME = "imageAlbumDB";
    public static final int DATABASE_VERSION = 1;

    // ALBUM_TABLE Columns names
    public static final String ALBUM_TABLE = "albumTable";
    public static final class ALBUM_COLUMN {
        public static final String _ID = "id";
        public static final String NAME = "name";
        public static final String PASSWORD = "password";
    }

    // MEDIA_TABLE Columns names
    public static final String MEDIA_TABLE = "mediaTable";
    public static final class MEDIA_COLUMN {
        public static final String _ID = "id";
        public static final String URI = "uri";
        public static final String PATH = "path";
        public static final String PASSWORD = "password";
        public static final String TYPE = "type";
        public static final String STATUS = "status";
        public static final String ALBUM_ID = "albumID";
    }

    // -------------- Database/App Media type ---------------
    public static final int IMAGE_TYPE = 1;
    public static final int VIDEO_TYPE = 2;
    // ----------------- Run-time variable ------------------
    public static final int SEND_IMAGE = 1;
    public static final int STORAGE_PERMISSION = 100;
    public static final int REQUEST_IMAGE_CAPTURE = 2;
    public static final int REQUEST_VIDEO_CAPTURE = 3;
    public static final int ITEM_SIZE_GRID_LAYOUT_PORTRAIT = 4;
    public static final int ITEM_SIZE_GRID_LAYOUT_LANDSCAPE = 6;
    public static final boolean SELECTED_MODE_ON = true;
    public static final boolean SELECTED_MODE_OFF = false;
    public static final boolean DELETE_MODE_ON = true;
    public static final boolean DELETE_MODE_OFF = false;
    public static final boolean SLIDE_SHOW_MODE_ON = true;
    public static final boolean SLIDE_SHOW_MODE_OFF = false;
    public static final String VIDEO_CURRENT_POSITION_STRING_NAME = "VideoCurPos";
    // ------------------------------------------------------
    /**
     * <p>Get video duration from file path</p>
     * <p>References: java2s.com</p>
     * @param context
     * @param path: absolute path of video
     * @return
     */
    public static int getDuration(Context context, String path) {
        MediaPlayer mMediaPlayer = null;
        int duration = 0;
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(context, Uri.parse(path));
            mMediaPlayer.prepare();
            duration = mMediaPlayer.getDuration();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mMediaPlayer != null) {
                mMediaPlayer.reset();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        }
        return duration;
    }

    public static int loadLastTheme(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("THEME", Context.MODE_PRIVATE);
        int temp = sharedPreferences.getInt("ID", 0);
        return temp;
    }

    public static int getImageFromDrawable(Context context, String name) {

        return context.getResources().getIdentifier(name, "drawable", context.getPackageName());
    }
}
