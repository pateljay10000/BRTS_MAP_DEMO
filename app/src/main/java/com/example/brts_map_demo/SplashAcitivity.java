package com.example.brts_map_demo;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashAcitivity extends Activity {

	private final int TIMEOUT = 2000;
	String mu, language;
	ImageView imageView;
	Animation anim_move;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main_home_image);
		imageView = (ImageView) findViewById(R.id.imageView1);
		anim_move = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.together);

		imageView.startAnimation(anim_move);
		new Thread() {
			public void run() {
				try {
					Thread.sleep(TIMEOUT);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				onLogin();
			}
		}.start();
	}

	public void onLogin() {
		// define where you want to redirect your activity

		Intent intent = new Intent(SplashAcitivity.this, TabSample.class);
		startActivity(intent);
		SplashAcitivity.this.finish();

	}

}