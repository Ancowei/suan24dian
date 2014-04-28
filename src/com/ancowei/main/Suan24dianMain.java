package com.ancowei.main;

import com.ancowei.db.Player_msg;

import com.ancowei.initiate_game.Initiate_game;
import com.ancowei.join_game.Join_game;
import com.ancowei.join_game.Join_game_begin;
import com.ancowei.local.Suan24dian_local;
import com.ancowei.login.suan24dian_Login;
import com.ancowei.main.Suan24dianMain;
import com.ancowei.services.Background_music;
import com.example.suan24dian.R;
import ExitApp.ExitApp;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Suan24dianMain extends Activity {

	private Button btn_local;
	private Button btn_initiate_game;
	private Button btn_join_game;
	private Button btn_about;
	private Button btn_exit;
	private Button btn_login;
	private TextView tx_username;
	private ImageView image_user;
	private SharedPreferences sp;
	// 全局变量，整个应用程序公用一份
	public static Player_msg[] play_msg = new Player_msg[10];

	private btnOnClickListener btn_OnClick;
	private static int LOGIN_REQUEST_CODE = 0;
	private static int INITIATE_REQUEST_CODE = 1;
	private static int JOIN_GAME_CODE = 2;

	public static String user_Name = "";
	public static Intent image = new Intent();
	public Intent user_data;
	// 是否已经登录的标志
	public static boolean ifLogin = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 退出程序
		ExitApp.getInstance().addActivity(Suan24dianMain.this);
		// 全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// 无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_suan24dian_main);
		findView();
		registerListeners();
		// 添加背景音乐
		play_music();
		
	}

	public void findView() {
		tx_username = (TextView) findViewById(R.id.tx_username);
		btn_local = (Button) findViewById(R.id.btn_local);
		btn_initiate_game = (Button) findViewById(R.id.btn_initiate_game);
		btn_join_game = (Button) findViewById(R.id.btn_join_game);
		btn_about = (Button) findViewById(R.id.btn_setting);
		btn_exit = (Button) findViewById(R.id.btn_exit);
		btn_login = (Button) findViewById(R.id.btn_login);
		image_user = (ImageView) findViewById(R.id.image_user);
		sp = this.getSharedPreferences("user_msg", Context.MODE_PRIVATE);
	}

	public void registerListeners() {
		btn_OnClick = new btnOnClickListener();
		btn_local.setOnClickListener(btn_OnClick);
		btn_about.setOnClickListener(btn_OnClick);
		btn_exit.setOnClickListener(btn_OnClick);
		btn_login.setOnClickListener(btn_OnClick);
		btn_initiate_game.setOnClickListener(btn_OnClick);
		btn_join_game.setOnClickListener(btn_OnClick);

	}

	private void play_music() {
		Intent mIntent = new Intent(this, Background_music.class);
		startService(mIntent);
	}

	public class btnOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_local:
				Intent start_intent = new Intent(Suan24dianMain.this,
						Suan24dian_local.class);
				Suan24dianMain.this.startActivity(start_intent);
				break;
			case R.id.btn_initiate_game:
				if (!ifLogin) {
					// 创建游戏的时候，如果还没有登录，像登录再创建游戏
					Toast.makeText(Suan24dianMain.this, "请先登录",
							Toast.LENGTH_LONG).show();
					Intent LIntent = new Intent(Suan24dianMain.this,
							suan24dian_Login.class);
					Suan24dianMain.this.startActivityForResult(LIntent,
							INITIATE_REQUEST_CODE);

				} else {
					// 点击发起游戏按钮时候，发送UDP广播，告知其他用户我发起了游戏，你们可以加进来
					Intent create_game = new Intent(Suan24dianMain.this,
							Initiate_game.class);
					create_game.putExtra("image", user_data);
					create_game.putExtra("user_name", user_Name);
					Suan24dianMain.this.startActivity(create_game);
				}

				break;
			case R.id.btn_join_game:
				if (!ifLogin) {
					// 创建游戏的时候，如果还没有登录，像登录再创建游戏
					Toast.makeText(Suan24dianMain.this, "请先登录",
							Toast.LENGTH_LONG).show();
					Intent LIntent = new Intent(Suan24dianMain.this,
							suan24dian_Login.class);
					Suan24dianMain.this.startActivityForResult(LIntent,
							JOIN_GAME_CODE);
				} else {
					// 点击加入游戏按钮时候，搜索UDP数据包，把当前所有发起游戏人用列表列举出来，用户可以点击其中的一个游戏发起人，加入游戏组
					Intent join_game = new Intent(Suan24dianMain.this,
							Join_game.class);
					join_game.putExtra("image", user_data);
					join_game.putExtra("user_name", user_Name);
					Suan24dianMain.this.startActivity(join_game);

				}

				break;
			case R.id.btn_setting:
				Intent setIntent = new Intent(Suan24dianMain.this,
						suan24dian_Login.class);
				Suan24dianMain.this.startActivityForResult(setIntent,
						LOGIN_REQUEST_CODE);

				break;
			case R.id.btn_login:
				if (!ifLogin) {
					Intent loginIntent = new Intent(Suan24dianMain.this,
							suan24dian_Login.class);
					Suan24dianMain.this.startActivityForResult(loginIntent,
							LOGIN_REQUEST_CODE);
				} else {
					Toast.makeText(Suan24dianMain.this, "您已经登录了,不需要再进行登录操作了",
							Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.btn_exit: {
				new AlertDialog.Builder(Suan24dianMain.this)
						.setTitle("确定要退出算24点游戏吗?")
						.setNegativeButton("取消", null)
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// stop background_music
										Intent mIntent = new Intent(
												Suan24dianMain.this,
												Background_music.class);
										Suan24dianMain.this
												.stopService(mIntent);
										// exit system
										ExitApp.getInstance().exit();
									}
								}).show();
				break;
			}
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		image = data;
		if (resultCode == suan24dian_Login.LOGIN_RESULT_CODE) {
			Bundle extras = data.getExtras();
			if (extras != null) {
				Bitmap photo = extras.getParcelable("data");
				Drawable drawable = new BitmapDrawable(photo);
				image_user.setImageDrawable(drawable);
				user_Name = data.getStringExtra("user_name");
				user_data = data;
				tx_username.setText(data.getStringExtra("user_name") + "在线");
				ifLogin = true;
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.suan24dian_welcome, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_about:
			new AlertDialog.Builder(Suan24dianMain.this)

			.setTitle("算24点")

			.setMessage("开发者：Ancowei\n版本：1.0")

			.setPositiveButton("确定", null).show();
			return true;
		case R.id.menu_exit:
			new AlertDialog.Builder(Suan24dianMain.this)
					.setTitle("确定要退出算24点游戏吗?")
					.setNegativeButton("取消", null)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// stop background_music
									Intent mIntent = new Intent(
											Suan24dianMain.this,
											Background_music.class);
									Suan24dianMain.this.stopService(mIntent);
									// exit system
									ExitApp.getInstance().exit();
								}
							}).show();
			return true;
		case R.id.menu_setting:
			new AlertDialog.Builder(Suan24dianMain.this)

			.setTitle("算24点")

			.setMessage("设置部分有待开发")

			.setPositiveButton("确定", null).show();
			return true;

		}
		return false;

	}

	// 重写返回手机返回按钮
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// 这里重写返回键
			new AlertDialog.Builder(Suan24dianMain.this)
					.setTitle("确定要退出算24点游戏吗?")
					.setNegativeButton("取消", null)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Intent mIntent = new Intent(
											Suan24dianMain.this,
											Background_music.class);
									Suan24dianMain.this.stopService(mIntent);
									ExitApp.getInstance().exit();
								}
							}).show();
			return true;
		}
		return false;

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// 界面销毁之前保存数据

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (ifLogin) {
			tx_username.setText(sp.getString("user_name", "") + " 在线");
			if (image != null) {
				Bundle extras = image.getExtras();
				if (extras != null) {
					Bitmap photo = extras.getParcelable("data");
					Drawable drawable = new BitmapDrawable(photo);
					image_user.setImageDrawable(drawable);
				}
			}
		} else {
			//tx_username.setText(""+this.getFilesDir().getAbsolutePath());
			tx_username.setText(sp.getString("user_name", "") + " 未登录");
		}

	}
}
