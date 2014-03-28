package com.ancowei.join_game;

import java.io.BufferedReader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import com.ancowei.listview.MyListView;
import com.ancowei.listview.MyListView.OnRefreshListener;
import com.example.suan24dian.R;

import ExitApp.ExitApp;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SimpleAdapter;

public class Join_game extends Activity {
	Button btn_joingame_exit;
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
	static InetAddress ADDR[] = new InetAddress[20];
	static InetAddress TCP_ADDR;
	static final int PORT = 3000;
	static int i = 0;
	static SimpleAdapter join_game_listAdapter;

	UDP_SerchThread UDP_serchThread;
	Handler join_Handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case INITIATOR_ADD:
				join_game_listAdapter.notifyDataSetChanged();
				join_game_listview.setAdapter(join_game_listAdapter);
				break;
			// 链接成功之后,跳转到游戏页面,等待游戏开始
			case TCP_LINK_SUCCEED:
				Intent intent = new Intent(Join_game.this,
						Join_game_begin.class);
				Join_game.this.finish();
				Join_game.this.startActivity(intent);

				break;
			case GAME_BEGIN:
				Bundle numBundle = msg.getData();
				break;
			default:
				break;
			}
		}
	};

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
		UDP_serchThread = new UDP_SerchThread();
		UDP_serchThread.start();
	}

	public void findView() {
		btn_joingame_exit = (Button) findViewById(R.id.btn_joingame_exit);
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
	}

	private void handleList() {
		listItem.clear();
		for (int j = 0; j < 1; j++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", R.drawable.ic_launcher);
			map.put("ItemAddress",i);
			listItem.add(map);
		}
	}

	public ArrayList<HashMap<String, Object>> getData() {
		for (int j = 0; j < i; j++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", R.drawable.ic_launcher);// 图像资源的ID
			map.put("ItemAddress", ADDR[j]);
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
					byte buf[] = new byte[19];
					DatagramSocket UDPSocket = new DatagramSocket(4242);
					DatagramPacket UDPPacket = new DatagramPacket(buf,
							buf.length);
					UDPSocket.receive(UDPPacket);
					String s = new String(buf);
					addr = UDPPacket.getAddress();
					
					if ("suan24dian_initiate".equals(s)) {
						// 加入发起玩家队列,并且进行TCP链接
						ADDR[i++] = addr;
						try{
							Socket socket=new Socket(addr,3000);
							socket.close();
						}catch(Exception e){
							System.out.println(e.toString());
							e.printStackTrace();
						}
					
					} else if (s.equals("game_begin")) {
						String nums[] = new String[4];
						for (int j = 0; j < 3; j++) {
							//nums[j] = reader.readLine();
						}
						Bundle numBundle = new Bundle();
						numBundle.putString("num1", nums[0]);
						numBundle.putString("num2", nums[1]);
						numBundle.putString("num3", nums[2]);
						numBundle.putString("num4", nums[3]);
						Message msg = join_Handler.obtainMessage();
						msg.what = GAME_BEGIN;
						msg.setData(numBundle);
						join_Handler.sendMessage(msg);

					}

				} catch (Exception e) {
					error += "\n" + e.toString();
				}
			}
		}
	}

	// 当用户选择一个玩家时候，发送TCP链接请求，和该玩家进行TCP链接
	public class join_gameThread extends Thread {
		public void run() {
			try {
				String s1 = "suan24dian_joingame";
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				PrintStream pout = new PrintStream(out);
				pout.println(s1);
				byte buf1[] = out.toByteArray();
				DatagramSocket Rsocket = new DatagramSocket();
				DatagramPacket Rpacket = new DatagramPacket(buf1, buf1.length,
						TCP_ADDR, 3000);
				Rsocket.send(Rpacket);
				Message msg = join_Handler.obtainMessage();
				msg.what = TCP_LINK_SUCCEED;
				join_Handler.sendMessage(msg);

			} catch (Exception e) {
				error += "\n" + e.toString();
				System.out.println(error);
			} finally {
				//
			}
		}
	}
}