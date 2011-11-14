package com.anil;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
	private static final String TAG = "Image Adapter";
	int mGalleryItemBackground;
	private Context mContext;
	private GridView mView;

	/** URL-Strings to some remote images. */
	private String[] mRemoteImagesURL;
	private Bitmap[] loadedImages;

	public ImageAdapter(Context c, String[] remoteImagesURL, GridView v) {
		mContext = c;
		TypedArray attr = mContext.obtainStyledAttributes(R.styleable.HelloGallery);
		mGalleryItemBackground = attr.getResourceId(R.styleable.HelloGallery_android_galleryItemBackground, 0);
		attr.recycle();
		mView = v;
		mRemoteImagesURL = remoteImagesURL;
		loadedImages = new Bitmap[mRemoteImagesURL.length];

	}

	public int getCount() {
		return mRemoteImagesURL.length;
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.gallery_item, null);
		}

		ImageView imageView = (ImageView) convertView.findViewById(R.id.FrontImageView);

		/* when image is already down-loaded then load that image */
		if (loadedImages[position] != null)
			imageView.setImageBitmap(loadedImages[position]);
		else
			imageView.setImageResource(R.drawable.loading);

		imageView.setBackgroundResource(mGalleryItemBackground);

		return convertView;
	}

	public void loadImage(int position) {
		Bitmap bm;

		try {
			/* Open a new URL and get the InputStream to load data from it. */
			URL aURL = new URL(mRemoteImagesURL[position]);
			URLConnection conn = aURL.openConnection();
			conn.connect();
			InputStream is = conn.getInputStream();
			/* Buffered is always good for a performance plus. */
			BufferedInputStream bis = new BufferedInputStream(is);
			/* Decode url-data to a bitmap. */
			bm = BitmapFactory.decodeStream(bis);
			bis.close();
			is.close();
			loadedImages[position] = bm;

		} catch (Exception e) {

			Log.e(TAG, "Remote Image Load Exception" + e);
		}

	}

	public void setLoadedImage(int position) {
		Log.d(TAG, "Position " + position);
		View childView = mView.getChildAt(position);
		if (loadedImages[position] != null && childView != null) {
			ImageView imageView = (ImageView) childView.findViewById(R.id.FrontImageView);
			imageView.setImageBitmap(loadedImages[position]);
		}
	}

}
