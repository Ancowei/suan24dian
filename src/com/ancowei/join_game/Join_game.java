package com.ancowei.join_game;

import java.io.ByteArrayInputStream;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter.ViewBinder;
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
	private static InetAddress ADDR[] = new InetAddress[20];
	private static String NAME[] = new String[20];
	private static int initiateNum = 0;
	private static int p = 0;
	// 选择的创建游戏玩家的ADDR、NAME
	private static InetAddress initiate_player_addr;
	private static String initiate_player_name = "Ancowei";
	private static String user_Name = "";
	public static Intent image = new Intent();

	public static SimpleAdapter join_game_listAdapter;

	UDP_SerchThread UDP_serchThread = new UDP_SerchThread();
	UDP_link_Thread UDP_link = new UDP_link_Thread();

	private static boolean STOP = false;

	private Handler myH;

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
		UDP_serchThread.start();
	}

	public void findView() {
		myH = new myHandler();
		btn_joingame_exit = (Button) findViewById(R.id.btn_joingame_exit);
		image_user = (ImageView) findViewById(R.id.image_user);
		tx_username = (TextView) findViewById(R.id.tx_username);
		join_game_listview = (MyListView) findViewById(R.id.join_game_listview);
		listItem = new ArrayList<HashMap<String, Object>>();
		join_game_listAdapter = new SimpleAdapter(this, getData(),
				R.layout.list_item,
				new String[] { "ItemImage", "ItemAddress" }, new int[] {
						R.id.ItemImage, R.id.ItemAddress });
		join_game_listAdapter.setViewBinder(new ListViewBinder());
	}

	private class ListViewBinder implements ViewBinder {
		@Override
		public boolean setViewValue(View view, Object data,
				String textRepresentation) {
			if ((view instanceof ImageView) && (data instanceof Bitmap)) {
				ImageView imageView = (ImageView) view;
				Bitmap bmp = (Bitmap) data;
				imageView.setImageBitmap(bmp);
				return true;
			}
			return false;
		}
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
				initiate_player_addr = ADDR[p];
				initiate_player_name = NAME[p];
				UDP_link.start();
				// 进入游戏开始等待界面
				Intent game_wait_Intent = new Intent(Join_game.this,
						Game_begin_wait.class);
				game_wait_Intent.putExtra("initiator_addr",
						initiate_player_addr);
				game_wait_Intent.putExtra("initiator_name",
						initiate_player_name);
				game_wait_Intent.putExtra("user_Name", user_Name);
				Join_game.this.finish();
				Join_game.this.startActivity(game_wait_Intent);
			}
		});
	}

	private void handleList() {
		listItem.clear();
		HashMap<String, Object> map = new HashMap<String, Object>();
		for (int j = 0; j < initiateNum; j++) {
			map = new HashMap<String, Object>();
			try {
				FileInputStream fis = openFileInput(NAME[j]);
				Bitmap bm = BitmapFactory.decodeStream(fis);
				map.put("ItemImage", bm);
			} catch (Exception e) {
				map.put("ItemImage", R.drawable.ic_launcher);
			}
			map.put("ItemAddress", NAME[j].toString());
			listItem.add(map);
		}
	}

	public ArrayList<HashMap<String, Object>> getData() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		for (int j = 0; j < initiateNum; j++) {
			map = new HashMap<String, Object>();
			try {
				FileInputStream fis = openFileInput(NAME[j]);
				Bitmap bm = BitmapFactory.decodeStream(fis);
				map.put("ItemImage", bm);
			} catch (Exception e) {
				map.put("ItemImage", R.drawable.ic_launcher);
			}
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
				STOP = true;
				UDP_serchThread.interrupt();
				Intent i = new Intent(Join_game.this, Suan24dianMain.class);
				Join_game.this.finish();
				Join_game.this.startActivity(i);
				break;
			}
		}
	}

	public class UDP_SerchThread extends Thread {
		public void run() {
			while (true) {
				if (STOP)
					break;
				try {
					InetAddress addr;
					byte buf[] = new byte[30000];
					DatagramSocket UDPSocket = new DatagramSocket(4545);
					DatagramPacket UDPPacket = new DatagramPacket(buf,
							buf.length);
					UDPSocket.receive(UDPPacket);
					ByteArrayInputStream bais = new ByteArrayInputStream(buf); // 把刚才的部分视为输入流
					DataInputStream dis = new DataInputStream(bais);
					String s = dis.readUTF();
					String name = dis.readUTF();
					FileOutputStream fos = openFileOutput(name, MODE_PRIVATE);
					long l = dis.readLong();
					addr = UDPPacket.getAddress();
					if ("suan24dian_initiate".equals(s)) {
						// 加入发起玩家队列
						byte data[] = new byte[(int) l];
						for (int i = 0; i < l; i++) {// 读取图片数据
							data[i] = dis.readByte();
						}
						ADDR[initiateNum]=addr;
						fos.write(data, 0, data.length);
						Message msg = myH.obtainMessage();
						msg.what = INITIATOR_ADD;
						Bundle b = new Bundle();
						b.putString("name", name);
						//b.putString("addr", addr.toString());
						msg.setData(b);
						myH.sendMessage(msg);
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
				addr = ADDR[p];
				Log.e("addr", addr.toString());
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				DataOutputStream dout = new DataOutputStream(bout);
				dout.writeUTF("suan24dian_join_game");
				dout.writeUTF(Suan24dianMain.user_Name);
				InputStream is = new FileInputStream(new File(
						Environment.getExternalStorageDirectory()
								+ "/user_image.jpg"));
				dout.writeLong(is.available());
				byte[] data = new byte[is.available()];
				int len = 0;
				while ((len = is.read(data)) > 0) {
					dout.write(data, 0, len);
					// Log.e("fila_size", "" + len);
				}
				byte buf[] = bout.toByteArray();
				Log.e("fila_size", "" + buf.length);
				DatagramSocket socket = new DatagramSocket();
				DatagramPacket packet = new DatagramPacket(buf, buf.length,
						addr, 4546);
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
		if (keyCode == event.KEYCODE_BACK) {
			UDP_serchThread.interrupt();
			Join_game.this.finish();
			return true;
		}
		return false;
	}

	public class myHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case INITIATOR_ADD:
				// 有玩家加进来了，更新相应的数据
				Bundle b = msg.getData();
				//ADDR[initiateNum] = b.getString("addr");
				NAME[initiateNum] = b.getString("name");
				initiateNum++;
				update();
				break;
			}
		}
	}

	public void update() {
		listItem.clear();
		HashMap<String, Object> map = new HashMap<String, Object>();
		for (int j = 0; j < initiateNum; ++j) {
			map = new HashMap<String, Object>();
			try {
				FileInputStream fis = openFileInput(NAME[j]);
				Bitmap bm = BitmapFactory.decodeStream(fis);
				map.put("ItemImage", bm);
			} catch (Exception e) {
				map.put("ItemImage", R.drawable.ic_launcher);
			}
			map.put("ItemAddress", NAME[j]);
			listItem.add(map);
		}
		join_game_listAdapter.notifyDataSetChanged();
		join_game_listview.onRefreshComplete();
	}

	@Override
	protected void onResume() {
		super.onResume();
		STOP = false;
		initiateNum = 0;
		p = 0;
		listItem.clear();
		// new UDP_SerchThread().start();
		Bitmap bm = BitmapFactory.decodeFile(Environment
				.getExternalStorageDirectory() + "/user_image.jpg");
		Bitmap image = Suan24dianMain.getRoundedCornerBitmap(bm);
		image_user.setImageBitmap(image);
		SharedPreferences sp = this.getSharedPreferences("user_msg",
				Context.MODE_PRIVATE);
		tx_username.setText(sp.getString("user_name", "") + " 在线");

	}
}