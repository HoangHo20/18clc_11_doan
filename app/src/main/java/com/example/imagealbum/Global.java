package com.example.imagealbum;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

public class Global {
    public static final int SEND_IMAGE = 1;
    public static final int STORAGE_PERMISSION = 100;
    public static final int REQUEST_IMAGE_CAPTURE = 2;
    public static final int ITEM_SIZE_GRID_LAYOUT_PORTRAIT = 4;
    public static final int ITEM_SIZE_GRID_LAYOUT_LANDSCAPE = 6;
    public static final int IMAGE_TYPE = 1;
    public static final int VIDEO_TYPE = 2;
    public static final boolean SELECTED_MODE_ON = true;
    public static final boolean SELECTED_MODE_OFF = false;
    public static final boolean DELETE_MODE_ON = true;
    public static final boolean DELETE_MODE_OFF = false;
    public static final boolean SLIDE_SHOW_MODE_ON = true;
    public static final boolean SLIDE_SHOW_MODE_OFF = false;

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
}
