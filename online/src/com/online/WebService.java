/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.online;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This helper class download images from the Internet and binds those with the provided ImageView.
 *
 * <p>It requires the INTERNET permission, which should be added to your application's manifest
 * file.</p>
 *
 * A local cache of downloaded images is maintained internally to improve performance.
 */
public class WebService {
    private static final String LOG_TAG = "WebService";



    /**
     * @param imageView Any imageView
     * @return Retrieve the currently active download task (if any) associated with this imageView.
     * null if there is no such task.
     */
    private static BitmapDownloaderTask getBitmapDownloaderTask(ImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof DownloadedDrawable) {
                DownloadedDrawable downloadedDrawable = (DownloadedDrawable)drawable;
                return downloadedDrawable.getBitmapDownloaderTask();
            }
        }
        return null;
    }

    /**
     * The actual AsyncTask 
     */
    class WebServiceTask extends AsyncTask<String, Void, Bitmap> {
        private static final int IO_BUFFER_SIZE = 4 * 1024;
        private String url;
        private final WeakReference<ImageView> imageViewReference;

        public BitmapDownloaderTask(ImageView imageView) {
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        /**
         * Actual download method.
         */
        @Override
        protected Bitmap doInBackground(String... params) {
            final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
            url = params[0];
            final HttpGet getRequest = new HttpGet(url);
            String cookie = params[1];
            if (cookie != null) {
                getRequest.setHeader("cookie", cookie);
            }

            try {
                HttpResponse response = client.execute(getRequest);
                final int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != HttpStatus.SC_OK) {
                    Log.w("ImageDownloader", "Error " + statusCode +
                            " while retrieving bitmap from " + url);
                    return null;
                }

                final HttpEntity entity = response.getEntity();
                if (entity != null) {
                    InputStream inputStream = null;
                    OutputStream outputStream = null;
                    try {
                        inputStream = entity.getContent();
                        final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
                        outputStream = new BufferedOutputStream(dataStream, IO_BUFFER_SIZE);
                        copy(inputStream, outputStream);
                        outputStream.flush();

                        final byte[] data = dataStream.toByteArray();
                        final Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                        // FIXME : Should use BitmapFactory.decodeStream(inputStream) instead.
                        //final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                        return bitmap;

                    } finally {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        if (outputStream != null) {
                            outputStream.close();
                        }
                        entity.consumeContent();
                    }
                }
            } catch (IOException e) {
                getRequest.abort();
                Log.w(LOG_TAG, "I/O error while retrieving bitmap from " + url, e);
            } catch (IllegalStateException e) {
                getRequest.abort();
                Log.w(LOG_TAG, "Incorrect URL: " + url);
            } catch (Exception e) {
                getRequest.abort();
                Log.w(LOG_TAG, "Error while retrieving bitmap from " + url, e);
            } finally {
                if (client != null) {
                    client.close();
                }
            }
            return null;
        }

        /**
         * Once the image is downloaded, associates it to the imageView
         */
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            // Add bitmap to cache
            if (bitmap != null) {
                synchronized (sHardBitmapCache) {
                    sHardBitmapCache.put(url, bitmap);
                }
            }

            if (imageViewReference != null) {
                ImageView imageView = imageViewReference.get();
                BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);
                // Change bitmap only if this process is still associated with it
                if (this == bitmapDownloaderTask) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }

        public void copy(InputStream in, OutputStream out) throws IOException {
            byte[] b = new byte[IO_BUFFER_SIZE];
            int read;
            while ((read = in.read(b)) != -1) {
                out.write(b, 0, read);
            }
        }
    }

}
