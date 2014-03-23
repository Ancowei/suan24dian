package com.ancowei.main;

import java.io.ByteArrayOutputStream;

import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

import java.util.List;

import com.ancowei.db.SqlHandler;
import com.ancowei.initiate_game.Initiate_game;
import com.ancowei.join_game.Join_game;
import com.ancowei.local.Suan24dian_local;
import com.ancowei.login.suan24dian_Login;
import com.ancowei.main.Suan24dianMain;
import com.ancowei.model.PropertyBean;
import com.ancowei.services.Background_music;
import com.example.suan24dian.R;
import ExitApp.ExitApp;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
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
	private PropertyBean property;

	private btnOnClickListener btn_OnClick;

	public static String USER_NAME = "";
	public static String USER_PASSWORD = "";

	public String user_Name = "";
	public int user_highest = 0;
	// 是否已经登录的标志
	public static boolean ifLogin = false;

	public static SqlHandler sqlHelper;

	// 广播地址和端口号
	public static final String ADDR = "172.18.13.128";
	public static final int PORT = 3000;

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
		sqlHelper = new SqlHandler(Suan24dianMain.this,
				SqlHandler.DATABASE_NAME, null, SqlHandler.DATABASE_VERSION);

		// sqlHelper.delete("哈哈");
		// sqlHelper.upgradeByUser();
		// 添加背景音乐
		play_music();

	}

	public void findView() {
		tx_username = (TextView) findViewById(R.id.text_username);
		btn_local = (Button) findViewById(R.id.btn_local);
		btn_initiate_game = (Button) findViewById(R.id.btn_initiate_game);
		btn_join_game = (Button) findViewById(R.id.btn_join_game);
		btn_about = (Button) findViewById(R.id.btn_about);
		btn_exit = (Button) findViewById(R.id.btn_exit);
		btn_login = (Button) findViewById(R.id.btn_login);
	}

	public void registerListeners() {
		btn_OnClick = new btnOnClickListener();
		btn_local.setOnClickListener(btn_OnClick);
		btn_about.setOnClickListener(btn_OnClick);
		btn_exit.setOnClickListener(btn_OnClick);
		btn_login.setOnClickListener(btn_OnClick);
		btn_initiate_game.setOnClickListener(btn_OnClick);
		btn_join_game.setOnClickListener(btn_OnClick);
		if (ifLogin) {
			tx_username.setText(USER_NAME + "在线");
		}

	}

	private void play_music() {
		Intent mIntent = new Intent(this, Background_music.class);
		startService(mIntent);
	}

	/*
	 * private List<String> getData() {
	 * 
	 * List<String> data = new ArrayList<String>(); Cursor c =
	 * sqlHelper.select(); for (c.moveToFirst(); !c.isAfterLast();
	 * c.moveToNext()) { user_Name =
	 * c.getString(c.getColumnIndex(SqlHandler.USER_NAME)); user_highest =
	 * c.getInt(c.getColumnIndex(SqlHandler.USER_HIGHEST)); data.add("  " +
	 * user_Name + ":" + user_highest); } return data; }
	 */
	// send broadcast Thread
	public class Send_Broadcast_Thread extends Thread {
		InetAddress broadcast_addr;
		int port;
		DatagramPacket send_Packet;
		DatagramSocket send_Socket;
		String s;
		ByteArrayOutputStream Bout;
		PrintStream Pout;
		byte buf[];

		public void run() {
			s = "suan24dian_initiate_game";
			try {
				// broadcast_addr=InetAddress.getByName("255.255.255.255");
				broadcast_addr = InetAddress.getByName(ADDR);
				Bout = new ByteArrayOutputStream();
				Pout = new PrintStream(Bout);
				Pout.println(s);
				buf = Bout.toByteArray();
				send_Packet = new DatagramPacket(buf, buf.length,
						broadcast_addr, PORT);
				send_Socket = new DatagramSocket();
				send_Socket.send(send_Packet);
				Pout.close();
				Bout.close();
				buf = null;

			} catch (Exception e) {
				System.out.println("广播异常： " + e.toString());
			}
		}

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
					Suan24dianMain.this.startActivityForResult(LIntent, 0);

				} else {
					// 点击发起游戏按钮时候，发送UDP广播，告知其他用户我发起了游戏，你们可以加进来
					new Send_Broadcast_Thread().start();
					Intent create_game = new Intent(Suan24dianMain.this,
							Initiate_game.class);
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
					Suan24dianMain.this.startActivityForResult(LIntent, 0);
				} else {
					// 点击加入游戏按钮时候，搜索UDP数据包，把当前所有发起游戏人用列表列举出来，用户可以点击其中的一个游戏发起人，加入游戏组
					Intent join_game = new Intent(Suan24dianMain.this,
							Join_game.class);
					Suan24dianMain.this.startActivity(join_game);
				}

				break;
			case R.id.btn_about:

				new AlertDialog.Builder(Suan24dianMain.this)

				.setTitle("算24点")

				.setMessage("开发者：Ancowei\n版本：1.0")

				.setPositiveButton("确定", null).show();
				break;
			case R.id.btn_login:
				Intent loginIntent = new Intent(Suan24dianMain.this,
						suan24dian_Login.class);
				Suan24dianMain.this.startActivityForResult(loginIntent, 0);

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
		if (resultCode == 1 && requestCode == 0) {
			USER_NAME = data.getStringExtra("user_name");
			USER_PASSWORD = data.getStringExtra("user_password");
			property.setAndsave_user(USER_NAME, USER_PASSWORD);
			tx_username.setText(USER_NAME + "在线");
			ifLogin = true;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.suan24dian_welcome, menu);
		return true;
	}

	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
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
							Suan24dianMain.this
									.stopService(mIntent);
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
	protected void onResume() {
		super.onResume();
		property = new PropertyBean(this);
		// tx_username.setText(property.get_user_name());

	}
}
