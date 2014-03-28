package com.ancowei.welcome;

import com.ancowei.main.Suan24dianMain;

import com.example.suan24dian.R;

import ExitApp.ExitApp;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class Suan24dian_welcome extends Activity {
	private ImageView welcomeImage;
	private TextView welcomeTextView;
	private Animation animation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 退出程序
		ExitApp.getInstance().addActivity(Suan24dian_welcome.this);
		// 全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// 无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_suan24dian_welcome);
		
		// 添加欢迎界面
		welcomeImage = (ImageView) findViewById(R.id.welcome_image);
		welcomeTextView = (TextView) findViewById(R.id.welcome_text);
		animation = AnimationUtils.loadAnimation(this, R.anim.my_anim_design);
		animation.setDuration(2000);
		welcomeImage.startAnimation(animation);
		welcomeTextView.startAnimation(animation);

		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				Intent MIntent = new Intent(Suan24dian_welcome.this,
						Suan24dianMain.class);
				Suan24dian_welcome.this.startActivity(MIntent);
				Suan24dian_welcome.this.finish();
			}
		});

	}

}