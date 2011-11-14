package com.anil;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ViewFlipper;

public final class DisplayNextView implements Animation.AnimationListener {
	private View mCurrentView;

	public DisplayNextView(View view) {
		mCurrentView = view;

	}

	public void onAnimationStart(Animation animation) {
	}

	public void onAnimationEnd(Animation animation) {
		ViewFlipper viewFlipper = (ViewFlipper) mCurrentView.findViewById(R.id.viewFlipper1);
		viewFlipper.showNext();
		ImageView image1 = (ImageView) mCurrentView.findViewById(R.id.FrontImageView);
		final float centerX = image1.getWidth() / 2.0f;
		final float centerY = image1.getHeight() / 2.0f;
		Flip3dAnimation rotation;
		rotation = new Flip3dAnimation(90, 0, centerX, centerY);

		rotation.setDuration(500);
		rotation.setFillAfter(true);
		rotation.setInterpolator(new DecelerateInterpolator());
		mCurrentView.startAnimation(rotation);
	}

	public void onAnimationRepeat(Animation animation) {
	}
}