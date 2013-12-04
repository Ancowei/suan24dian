package com.ancowei.login;

import com.example.suan24dian.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class suan24dian_Login extends Activity {
	private Button btn_cancle;
	private Button btn_ok;

	private EditText edit_name;
	private EditText edit_password;

	public static String useName;
	public static String usePassword;

	private btn_OnClickListener btn_onclick;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.suan24dian_login);
		btn_cancle = (Button) findViewById(R.id.btn_cancle);
		btn_ok = (Button) findViewById(R.id.btn_ok);
		
		edit_name = (EditText) findViewById(R.id.edit_name);
		edit_name.setFocusable(true);
		edit_password = (EditText) findViewById(R.id.edit_password);
		edit_password.setFocusable(true);

		btn_onclick = new btn_OnClickListener();

		btn_cancle.setOnClickListener(btn_onclick);
		btn_ok.setOnClickListener(btn_onclick);
	}

	public class btn_OnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_cancle:
				suan24dian_Login.this.finish();
				break;
				
			//点击确定按钮之后，把用户数据送往本地数据库，如果是第一次登录，则同时把登录数据发送往服务器进行注册
			case R.id.btn_ok:
				useName = edit_name.getText().toString();
				usePassword = edit_password.getText().toString();
				Toast.makeText(suan24dian_Login.this,
						"登录信息\n" + "昵称：" + useName + "\n密码：*** ",
						Toast.LENGTH_LONG).show();
				suan24dian_Login.this.finish();
				break;

			}

		}

	}

}
