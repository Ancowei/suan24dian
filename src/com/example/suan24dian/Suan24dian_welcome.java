package com.example.suan24dian;

import java.util.ArrayList;
import java.util.List;

import com.ancowei.db.SqlHandler;
import com.ancowei.login.suan24dian_Login;
import com.ancowei.main.Suan24dianMain;

import ExitApp.ExitApp;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class Suan24dian_welcome extends Activity {
	private Button btn_start;
	private Button btn_check_ranking;
	private Button btn_about;
	private Button btn_exit;
	private Button btn_login;

	private btnOnClickListener btn_OnClick;
	
	public static String USER_NAME;
	public static String USER_PASSWORD;
	
	public String user_Name = "";
	public int user_highest = 0;
	public boolean ifFirst = true;

	public static SqlHandler sqlHelper;

	ListView rankListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_suan24dian_welcome);
		// 退出程序
		ExitApp.getInstance().addActivity(Suan24dian_welcome.this);

		btn_start = (Button) findViewById(R.id.btn_start);
		btn_check_ranking = (Button) findViewById(R.id.btn_check_ranking);
		btn_about = (Button) findViewById(R.id.btn_about);
		btn_exit = (Button) findViewById(R.id.btn_exit);
		btn_login = (Button) findViewById(R.id.btn_login);

		btn_OnClick = new btnOnClickListener();
		btn_start.setOnClickListener(btn_OnClick);
		btn_check_ranking.setOnClickListener(btn_OnClick);
		btn_about.setOnClickListener(btn_OnClick);
		btn_exit.setOnClickListener(btn_OnClick);
		btn_login.setOnClickListener(btn_OnClick);

		sqlHelper = new SqlHandler(Suan24dian_welcome.this,
				SqlHandler.DATABASE_NAME, null, SqlHandler.DATABASE_VERSION);
		// sqlHelper.delete("哈哈");
		//sqlHelper.upgradeByUser();
	}

	private List<String> getData() {

		List<String> data = new ArrayList<String>();
		Cursor c = sqlHelper.select();
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			user_Name = c.getString(c.getColumnIndex(SqlHandler.USER_NAME));
			user_highest = c.getInt(c.getColumnIndex(SqlHandler.USER_HIGHEST));
			data.add("  " + user_Name + ":" + user_highest);
		}
		return data;
	}

	public class btnOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_start:
				Intent start_intent = new Intent(Suan24dian_welcome.this,
						Suan24dianMain.class);
				Suan24dian_welcome.this.startActivity(start_intent);
				break;
			case R.id.btn_check_ranking:

				rankListView = new ListView(Suan24dian_welcome.this);
				rankListView.setAdapter(new ArrayAdapter(
						Suan24dian_welcome.this,
						android.R.layout.simple_list_item_1, getData()));

				new AlertDialog.Builder(Suan24dian_welcome.this)

				.setTitle("本次游戏排名").setView(rankListView)

				.setPositiveButton("确定", null).show();
				break;
			case R.id.btn_about:

				new AlertDialog.Builder(Suan24dian_welcome.this)

				.setTitle("算24点")

				.setMessage("开发者：Ancowei\n版本：1.0")

				.setPositiveButton("确定", null).show();
				break;
			case R.id.btn_login:
				Intent loginIntent = new Intent(Suan24dian_welcome.this,
						suan24dian_Login.class);
				Suan24dian_welcome.this.startActivity(loginIntent);
				break;
			case R.id.btn_exit:
				new AlertDialog.Builder(Suan24dian_welcome.this)
						.setTitle("确定要退出算24点游戏吗?").setNegativeButton("取消", null)
						.setPositiveButton("确定",new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								ExitApp.getInstance().exit();
							}
						}).show();
				//ExitApp.getInstance().exit();
				break;
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.suan24dian_welcome, menu);
		return true;
	}

}
