package com.ancowei.initiate_game;

import java.io.DataInputStream;
import java.io.PrintWriter;
import java.io.DataOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ancowei.internet.Inet_initiate_control;
import com.ancowei.listview.MyListView;
import com.ancowei.listview.MyListView.OnRefreshListener;
import com.example.suan24dian.R;
import ExitApp.ExitApp;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SimpleAdapter;

public class Initiate_game extends Activity {
	Button btn_start_game;
	Button btn_back;

	ServerSocket serverSocket;
	Socket socket;
	TCP_Link_Thread tcp_link;
	UDP_Brocast_initiate_Thread udp_brocast;
	Inet_initiate_control inet_initiate_control;

	DataInputStream din;
	DataOutputStream dout;

	MyListView list_player;
	List<String> list_player_show;
	static String data[] = new String[10];

	SimpleAdapter listItemAdapter;

	myOnClickListener myOnclick;
	ArrayList<HashMap<String, Object>> listItem;
	static int playerNum = 0;
	static int i = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ExitApp.getInstance().addActivity(Initiate_game.this);
		// 无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.suan24dian_initiate_game);
		// 全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		listItem = new ArrayList<HashMap<String, Object>>();
		findView();
		buildAdapter();
		registerListeners();
		udp_brocast.start();
		tcp_link.start();
	}

	public void findView() {
		btn_start_game = (Button) findViewById(R.id.btn_start_game);
		btn_back = (Button) findViewById(R.id.btn_exit);
		list_player = (MyListView) findViewById(R.id.list_player);
	}

	public void registerListeners() {
		myOnclick = new myOnClickListener();
		btn_start_game.setOnClickListener(myOnclick);
		btn_back.setOnClickListener(myOnclick);
		list_player.setAdapter(listItemAdapter);
		list_player.setonRefreshListener(new OnRefreshListener() {
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
						listItemAdapter.notifyDataSetChanged();
						list_player.onRefreshComplete();
					}

				}.execute();
			}
		});
		inet_initiate_control = new Inet_initiate_control();
		tcp_link = new TCP_Link_Thread();
		udp_brocast = new UDP_Brocast_initiate_Thread();
	}

	private void handleList() {
		listItem.clear();
		for (int j = 0; j < i; j++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", R.drawable.ic_launcher);
			map.put("ItemTitle", data[j]);
			listItem.add(map);
		}
	}

	// 生成适配器的Item和动态数组对应的元素
	public void buildAdapter() {
		listItemAdapter = new SimpleAdapter(this, getData(),
				R.layout.list_item, new String[] { "ItemImage", "ItemTitle" },
				new int[] { R.id.ItemImage, R.id.ItemAddress });
	}

	// getData();
	public ArrayList<HashMap<String, Object>> getData() {
		for (int j = 0; j < i; j++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", R.drawable.ic_launcher);
			map.put("ItemTitle", data[j]);
			listItem.add(map);
		}
		return listItem;
	}

	// UDP brocast UDP广播：我发起游戏了,局域网的广播地址是 255.255.255.255
	public class UDP_Brocast_initiate_Thread extends Thread {
		public void run() {
			try {
				// InetAddress addr = InetAddress.getByName("172.18.54.198");
				InetAddress addr = InetAddress.getByName("255.255.255.255");
				byte[] data = "suan24dian_initiate".getBytes();
				DatagramSocket socket = new DatagramSocket();
				DatagramPacket packet = new DatagramPacket(data, data.length,
						addr, 4242);
				socket.send(packet);
				socket.close();
			} catch (Exception e) {
				System.out.print(e.toString());
			}
		}
	}

	// tcp socket listenning
	public class TCP_Link_Thread extends Thread {
		public void run() {
			try {
				ServerSocket serverSocket = new ServerSocket(3000);
				while (true) {
					socket = serverSocket.accept();
					PrintWriter writer = new PrintWriter(
							socket.getOutputStream());
					String advice = "hava a good day";
					writer.print(advice);
					writer.close();
					playerNum++;
					data[i++] = socket.getInetAddress().toString();
				}
			} catch (Exception e) {
				System.out.print("TCP link listenning exception :"
						+ e.toString());
			}
		}
	}

	public class myOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			// 刚开始，开始游戏按钮是不可点击状态，只有有人加入游戏之后才可以开始游戏
			case R.id.btn_start_game:
				if (playerNum == 0) {
					new AlertDialog.Builder(Initiate_game.this)
							.setMessage("玩家个数为0\n还不可以开始游戏哦")
							.setPositiveButton("确定", null).show();
				} else {
					Intent intent = new Intent(Initiate_game.this,
							Game_begin.class);
					Initiate_game.this.finish();
					Initiate_game.this.startActivity(intent);
				}
				break;
			case R.id.btn_exit:
				Initiate_game.this.finish();
				break;
			default:
				break;
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
