package com.ancowei.login;

import com.ancowei.db.SqlHandler;
import com.ancowei.main.Suan24dianMain;
import com.ancowei.welcome.Suan24dian_welcome;

import com.example.suan24dian.R;

import ExitApp.ExitApp;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class suan24dian_Login extends Activity {
	private Button btn_cancle;
	private Button btn_ok;

	private EditText edit_name;
	private EditText edit_password;

	public static String user_Name;
	public static String user_Password;

	private btn_OnClickListener btn_onclick;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 退出程序
		ExitApp.getInstance().addActivity(suan24dian_Login.this);
		// 无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.suan24dian_login);
		findView();
		registerListeners();

		/*
		 * sqlHelper = new SqlHandler(suan24dian_Login.this,
		 * SqlHandler.DATABASE_NAME, null, SqlHandler.DATABASE_VERSION);
		 */

		set_Default_User();
	}

	public void findView() {
		btn_cancle = (Button) findViewById(R.id.btn_cancle);
		btn_ok = (Button) findViewById(R.id.btn_ok);
		edit_password = (EditText) findViewById(R.id.edit_password);
		edit_name = (EditText) findViewById(R.id.edit_name);
	}

	public void registerListeners() {
		edit_name.setFocusable(true);
		edit_password.setFocusable(true);
		btn_onclick = new btn_OnClickListener();
		btn_cancle.setOnClickListener(btn_onclick);
		btn_ok.setOnClickListener(btn_onclick);
	}

	public void set_Default_User() {
		Cursor c = com.ancowei.main.Suan24dianMain.sqlHelper.select();
		try {
			c.moveToFirst();
			user_Name = c.getString(c.getColumnIndex(SqlHandler.USER_NAME));
			user_Password = c.getString(c
					.getColumnIndex(SqlHandler.USER_PASSWORD));
			edit_name.setText(user_Name);
			edit_password.setText(user_Password);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public class btn_OnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_cancle:
				suan24dian_Login.this.finish();
				break;
			// 点击确定按钮之后，把用户数据送往本地数据库，如果是第一次登录，则同时把登录数据发送往服务器进行注册
			case R.id.btn_ok:

				Suan24dianMain.USER_NAME = edit_name.getText().toString();
				Suan24dianMain.USER_PASSWORD = edit_password.getText()
						.toString();

				user_Name = edit_name.getText().toString();
				user_Password = edit_password.getText().toString();

				if (user_Name.length() == 0 || user_Password.length() == 0) {
					new AlertDialog.Builder(suan24dian_Login.this)
							.setTitle("用户名和密码都不能为空!")
							.setPositiveButton("确定", null).show();
				} else {
					Intent LIntent = new Intent();
					LIntent.putExtra("user_name", user_Name);
					LIntent.putExtra("user_password", user_Password);
					setResult(1, LIntent);

					Suan24dianMain.sqlHelper.insert(user_Name, user_Password);
					
					Toast.makeText(suan24dian_Login.this,
							"登录信息\n" + "昵称：" + user_Name + "\n密码：*** ",
							Toast.LENGTH_LONG).show();
					Suan24dianMain.ifLogin = true;
					suan24dian_Login.this.finish();
				}
				break;

			}

		}

	}

}
