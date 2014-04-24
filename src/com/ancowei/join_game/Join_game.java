package com.ancowei.join_game;

import java.io.ByteArrayInputStream;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

import com.ancowei.listview.MyListView;
import com.ancowei.listview.MyListView.OnRefreshListener;
import com.ancowei.main.Suan24dianMain;
import com.example.suan24dian.R;

import ExitApp.ExitApp;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class Join_game extends Activity {
	Button btn_joingame_exit;
	ImageView image_user;
	TextView tx_username;

	MyListView join_game_listview;
	ArrayList<HashMap<String, Object>> listItem;

	btnOnClickListener btn_onclick;

	public static String s = "";
	public static String error = "error:";
	static final int JOIN = 0;
	static final int INITIATOR_ADD = 1;
	static final int TCP_LINK_SUCCEED = 2;
	static final int GAME_BEGIN = 3;

	// 假设发起游戏的玩家最多就20个
	static String ADDR[] = new String[20];
	static String NAME[] = new String[20];
	static InetAddress TCP_ADDR;
	static final int PORT = 3000;
	static int i = 0;
	static int p = 0;
	//选择的创建游戏玩家的ADDR、NAME
	private static String initiate_player_addr="172.18.13.128";
	private static String initiate_player_name="Ancowei";
	
	static SimpleAdapter join_game_listAdapter;

	UDP_SerchThread UDP_serchThread = new UDP_SerchThread();
	UDP_link_Thread UDP_link = new UDP_link_Thread();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 退出程序
		ExitApp.getInstance().addActivity(Join_game.this);
		// 无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.suan24dian_join_game);
		// 全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		findView();
		registerListeners();
		set_image_and_name();
		UDP_serchThread.start();
	}

	public void findView() {
		btn_joingame_exit = (Button) findViewById(R.id.btn_joingame_exit);
		image_user = (ImageView) findViewById(R.id.image_user);
		tx_username = (TextView) findViewById(R.id.tx_username);
		join_game_listview = (MyListView) findViewById(R.id.join_game_listview);
		listItem = new ArrayList<HashMap<String, Object>>();
		join_game_listAdapter = new SimpleAdapter(this, getData(),
				R.layout.list_item,
				new String[] { "ItemImage", "ItemAddress" }, new int[] {
						R.id.ItemImage, R.id.ItemAddress });

	}

	public void registerListeners() {
		btn_onclick = new btnOnClickListener();
		btn_joingame_exit.setOnClickListener(btn_onclick);
		join_game_listview.setAdapter(join_game_listAdapter);
		join_game_listview.setonRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				new AsyncTask<Void, Void, Void>() {
					protected Void doInBackground(Void... params) {
						try {
							Thread.sleep(1000);
						} catch (Exception e) {
							e.printStackTrace();
						}
						handleList();
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						join_game_listAdapter.notifyDataSetChanged();
						join_game_listview.onRefreshComplete();
					}

				}.execute();
			}
		});
		join_game_listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				p = arg2 - 1;
				initiate_player_addr=ADDR[p];
				initiate_player_name=NAME[p];
				UDP_link.start();
				// 进入游戏开始等待界面
				Intent game_wait_Intent = new Intent(Join_game.this,
						Game_begin_wait.class);
				game_wait_Intent.putExtra("addr", initiate_player_addr);
				game_wait_Intent.putExtra("name", initiate_player_name);
				Join_game.this.finish();
				Join_game.this.startActivity(game_wait_Intent);
			}
		});
	}

	public void set_image_and_name() {
		Intent image_and_name = this.getIntent();
		String name = image_and_name.getStringExtra("user_name");
		Intent image = image_and_name.getParcelableExtra("image");
		if (image != null) {
			Bundle extras = image.getExtras();
			if (extras != null) {
				Bitmap photo = extras.getParcelable("data");
				Drawable drawable = new BitmapDrawable(photo);
				image_user.setImageDrawable(drawable);
			}
		}
		tx_username.setText(name);
	}

	private void handleList() {
		listItem.clear();
		for (int j = 0; j < i; j++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", R.drawable.ic_launcher);
			map.put("ItemAddress", NAME[j].toString());
			listItem.add(map);
		}
	}

	public ArrayList<HashMap<String, Object>> getData() {
		for (int j = 0; j < i; j++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", R.drawable.ic_launcher);// 图像资源的ID
			map.put("ItemAddress", NAME[j]);
			listItem.add(map);
		}
		return listItem;
	}

	public class btnOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_joingame_exit:
				Join_game.this.finish();
				break;
			}
		}
	}

	public class UDP_SerchThread extends Thread {
		public void run() {
			while (true) {
				try {
					InetAddress addr;
					byte buf[] = new byte[1024];
					DatagramSocket UDPSocket = new DatagramSocket(4242);
					DatagramPacket UDPPacket = new DatagramPacket(buf,
							buf.length);
					UDPSocket.receive(UDPPacket);
					ByteArrayInputStream bais = new ByteArrayInputStream(buf); // 把刚才的部分视为输入流
					DataInputStream dis = new DataInputStream(bais);
					String s = dis.readUTF();
					String name = dis.readUTF();
					addr = UDPPacket.getAddress();
					if ("suan24dian_initiate".equals(s)) {
						// 加入发起玩家队列
						ADDR[i] = addr.toString();
						NAME[i] = name;
						i++;
					}
					dis.close();
					bais.close();
					UDPSocket.close();
				} catch (Exception e) {
					error += "\n" + e.toString();
				}
			}
		}
	}

	// 当用户选择一个玩家时候，发送UDP链接请求，和该玩家进行UDP链接
	public class UDP_link_Thread extends Thread {
		public void run() {
			try {
				InetAddress addr = InetAddress.getByName("255.255.255.255");
				addr = InetAddress.getByName(ADDR[p]);
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				DataOutputStream dout = new DataOutputStream(bout);
				dout.writeUTF("suan24dian_join_game");
				dout.writeUTF(Suan24dianMain.user_Name);

				byte buf[] = bout.toByteArray();
				DatagramSocket socket = new DatagramSocket();
				DatagramPacket packet = new DatagramPacket(buf, buf.length,
						addr, 4243);
				socket.send(packet);
				socket.close();
				bout.close();
				dout.close();

			} catch (Exception e) {
				System.out.println("\n广播发送失败：" + e.toString());
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==event.KEYCODE_BACK ){
			UDP_serchThread.interrupt();
			Join_game.this.finish();
			return true;
		}
		return false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		i=0;
		p=0;
		listItem.clear();
	}
	
	
}