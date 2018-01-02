package com.itstrong.popularscience.utils;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by itstrong on 2016/6/13.
 */
public class AsyncImageLoader {

    private HashMap<String, SoftReference<Drawable>> imageCache;

    public AsyncImageLoader() {
        imageCache = new HashMap<>();
    }

    public Drawable loadDrawable(final String imageUrl, final ImageCallback imageCallback) {
        if (imageCache.containsKey(imageUrl)) {
            SoftReference<Drawable> softReference = imageCache.get(imageUrl);
            Drawable drawable = softReference.get();
            if (drawable != null) {
                return drawable;
            }
        }
        final Handler handler = new Handler() {
            public void handleMessage(Message message) {
                if (message.what == 0) {
                    imageCallback.loadedFailure();
                } else if (message.what == 1) {
                    imageCallback.imageLoaded((Drawable) message.obj, imageUrl);
                }
            }
        };
        new Thread() {
            @Override
            public void run() {
                Drawable drawable = loadImageFromUrl(imageUrl);
                Message message;
                if (drawable == null) {
                    message = handler.obtainMessage(0);
                } else {
                    imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));
                    message = handler.obtainMessage(1, drawable);
                }
                handler.sendMessage(message);
            }
        }.start();
        return null;
    }

    public static Drawable loadImageFromUrl(String url) {
        URL m;
        InputStream i = null;
        try {
            m = new URL(url);
            i = (InputStream) m.getContent();
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        Drawable d = Drawable.createFromStream(i, "src");
        return d;
    }

    public interface ImageCallback {
        void imageLoaded(Drawable imageDrawable, String imageUrl);
        void loadedFailure();
    }
}
