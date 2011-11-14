package com.anil;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ViewFlipper;

public class GallerySampleActivity extends Activity {
	protected static final String TAG = "Gallery Sample Activity";
	protected ImageAdapter imageAdapter;
	protected GridView gridview;
	private String[] myRemoteImages = { 
			"http://www.anddev.org/images/tiny_tutheaders/weather_forecast.png",
	    "http://www.anddev.org/images/tiny_tutheaders/cellidtogeo.png", 
	    "http://www.anddev.org/images/tiny_tutheaders/droiddraw.png", 
			"http://www.anddev.org/images/tiny_tutheaders/weather_forecast.png",
	    "http://www.anddev.org/images/tiny_tutheaders/cellidtogeo.png", 
	    "http://www.anddev.org/images/tiny_tutheaders/droiddraw.png"
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		gridview = (GridView) findViewById(R.id.gridview);
		imageAdapter = new ImageAdapter(this, myRemoteImages, gridview);

		gridview.setAdapter(imageAdapter);

		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				applyRotation(0, -90, v);
/*
				ViewFlipper viewFlipper = (ViewFlipper) v.findViewById(R.id.viewFlipper1);
				if (viewFlipper.getDisplayedChild() == 1) {
					//Intent i = new Intent(getApplicationContext(), GalleryDetailsView.class);
					//startActivity(i);
					//applyRotation(0, -90, v);
				} else {
					//applyRotation(0, -180, v);
				}

*/
			}
		});

		updateImagesAsThread();

	}

	private void applyRotation(float start, float end, View view) {
		// Find the center of image
		ImageView image1 = (ImageView) view.findViewById(R.id.FrontImageView);
		final float centerX = image1.getWidth() / 2.0f;
		final float centerY = image1.getHeight() / 2.0f;

		// Create a new 3D rotation with the supplied parameter
		// The animation listener is used to trigger the next animation
		final Flip3dAnimation rotation = new Flip3dAnimation(start, end, centerX, centerY);
		rotation.setDuration(500);
		rotation.setFillAfter(true);
		rotation.setInterpolator(new AccelerateInterpolator());
		rotation.setAnimationListener(new DisplayNextView(view));

		view.startAnimation(rotation);
	}

	private void updateImagesAsThread() {
		Thread t = new Thread() {

			public void run() {
				try {

					for (int i = 0; i < imageAdapter.getCount(); i++) {
						imageAdapter.loadImage(i);
						listAdapterHandler.sendEmptyMessage(i);
					}
				} catch (Exception e) {
					// TODO: handle exception
					Log.e(TAG, "UpdateImageAsThread " + e);
				}

			}
		};
		t.start();

	}

	private Handler listAdapterHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case -1:
				Log.d(TAG, "here in the handle...");
				break;
			default:
				Log.d(TAG, "here in the handle default...");
				imageAdapter.setLoadedImage(msg.what);
				// imageAdapter.notifyDataSetChanged();
				break;
			}
		}
	};

}