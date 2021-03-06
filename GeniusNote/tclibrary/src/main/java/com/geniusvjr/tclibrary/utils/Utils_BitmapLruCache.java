package com.geniusvjr.tclibrary.utils;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * 图像缓存，默认大小占该虚拟机的1/5
 * Created by dream on 16/1/31.
 */
public class Utils_BitmapLruCache {

    private static LruCache<String, Bitmap> cache;

    public static synchronized LruCache<String, Bitmap> getInstance()
    {
        if(cache == null)
        {
            initCache();
        }
        return cache;
    }

    private static void initCache() {
        int maxSize = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxSize / 5;
        cache = new LruCache<String, Bitmap>(cacheSize)
        {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }
}
