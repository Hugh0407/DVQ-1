package com.techscan.dvq.common;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.annotation.Nullable;

import com.techscan.dvq.R;

import java.util.HashMap;

/**
 * Created by cloverss on 2017/7/18
 * …˘“ÙÃ· æ µ•¿˝
 */

public class SoundHelper {

    private static SoundPool mPool;
    private static Context mContext;
    private static final int MAX_STREAMS = 5;
    private static final int STREAM_TYPE = AudioManager.STREAM_SYSTEM;
    private static final int SRC_QUALITY = 1;
    private static HashMap<Integer, Integer> soundPoolMap;
    @Nullable
    private static SoundHelper instance = null;

    @Nullable
    public static SoundHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (SoundHelper.class) {
                if (instance == null) {
                    instance = new SoundHelper(context);
                }
            }
        }
        return instance;
    }

    private SoundHelper(Context context) {
        mContext = context;
        mPool = new SoundPool(MAX_STREAMS, STREAM_TYPE, SRC_QUALITY);
        soundPoolMap = new HashMap<Integer, Integer>();
        soundPoolMap.put(1, mPool.load(mContext, R.raw.yyy, 1));
        soundPoolMap.put(2, mPool.load(mContext, R.raw.xxx, 1));
    }

    public static void playOK() {
        mPool.play(soundPoolMap.get(1), 1, 1, 0, 0, 1);
    }

    public static void playWarning() {
        mPool.play(soundPoolMap.get(2), 1, 1, 0, 0, 1);
    }

    public static void release() {
        mPool.release();
    }
}
