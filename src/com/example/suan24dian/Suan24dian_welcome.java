package com.example.suan24dian;

import com.ancowei.main.Suan24dianMain;

import ExitApp.ExitApp;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Suan24dian_welcome extends Activity {
	private Button btn_start;
	private Button btn_check_ranking;
	private Button btn_about;
	private Button btn_exit;
	private btnOnClickListener btn_OnClick;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_suan24dian_welcome);
		
		ExitApp.getInstance().addActivity(Suan24dian_welcome.this);
		
		btn_start=(Button)findViewById(R.id.btn_start);
		btn_check_ranking=(Button)findViewById(R.id.btn_check_ranking);
		btn_about=(Button)findViewById(R.id.btn_about);
		btn_exit=(Button)findViewById(R.id.btn_exit);
		btn_OnClick = new btnOnClickListener();
		btn_start.setOnClickListener(btn_OnClick);
		btn_check_ranking.setOnClickListener(btn_OnClick);
		btn_about.setOnClickListener(btn_OnClick);
		btn_exit.setOnClickListener(btn_OnClick);
	}
	public class btnOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.btn_start:
				Intent start_intent=new Intent(Suan24dian_welcome.this,Suan24dianMain.class);
				Suan24dian_welcome.this.startActivity(start_intent);
				
				//Toast.makeText(Suan24dian_welcome.this, "start", Toast.LENGTH_LONG).show();
				break;
			case R.id.btn_check_ranking:
				Toast.makeText(Suan24dian_welcome.this, "check_ranking", Toast.LENGTH_LONG).show();
				break;
			case R.id.btn_about:
				Toast.makeText(Suan24dian_welcome.this, "这是Ancowei独立开发的算24点手机游戏，希望你喜欢", Toast.LENGTH_LONG).show();
				break;
			case R.id.btn_exit:
				ExitApp.getInstance().exit();
				//Toast.makeText(Suan24dian_welcome.this, "exit", Toast.LENGTH_LONG).show();
				break;
			}
		}
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.suan24dian_welcome, menu);
		return true;
	}

}
