package com.ancowei.initiate_game;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.suan24dian.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class Initiate_game extends Activity {
	Button btn_start_game;
	Button btn_exit;

	ServerSocket serverSocket;
	Socket socket;
	TCP_Link_Thread tcp_link;

	DataInputStream din;
	DataOutputStream dout;

	ListView list_player;
	List<String> list_player_show;
	static String data[] = new String[10];

	SimpleAdapter listItemAdapter;

	myOnClickListener myOnclick;

	static int playerNum = 0;
	static int i = 0;

	Handler TcpHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// super.handleMessage(msg);
			Bundle bundle = msg.getData();
			switch (msg.what) {
			case 1:
				for (int j = 0; j < i; j++) {
					System.out.println(data[j]);
				}
				playerNum = bundle.getInt("playerNum");
				listItemAdapter.notifyDataSetChanged();
				list_player.setAdapter(listItemAdapter);
				Toast.makeText(Initiate_game.this, "" + playerNum,
						Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.suan24dian_initiate_game);
		btn_start_game = (Button) findViewById(R.id.btn_start_game);
		btn_exit = (Button) findViewById(R.id.btn_exit);
		myOnclick = new myOnClickListener();
		btn_start_game.setOnClickListener(myOnclick);
		btn_exit.setOnClickListener(myOnclick);

		list_player = (ListView) findViewById(R.id.list_player);

		// 生成适配器的Item和动态数组对应的元素
		listItemAdapter = new SimpleAdapter(this, getData(),// 数据源
				R.layout.list_item,// ListItem的XML实现
				// 动态数组与ImageItem对应的子项
				new String[] { "ItemImage", "ItemTitle" },
				// ImageItem的XML文件里面的一个ImageView,两个TextView ID
				new int[] { R.id.ItemImage, R.id.ItemTitle });
		list_player.setAdapter(listItemAdapter);

		tcp_link = new TCP_Link_Thread();
		tcp_link.start();
	}

	public ArrayList<HashMap<String, Object>> getData() {
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < 3; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", R.drawable.ic_launcher);// 图像资源的ID
			map.put("ItemTitle", "InetAddress " + i);

			listItem.add(map);
		}
		for (int j = 0; j < i; j++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", R.drawable.ic_launcher);
			map.put("ItemTitle", data[j]);
			listItem.add(map);
		}
		return listItem;
	}

	// tcp socket listenning
	public class TCP_Link_Thread extends Thread {
		int playerNum = 0;

		public void run() {
			try {
				serverSocket = new ServerSocket(3000);
				while (true) {
					socket = serverSocket.accept();
					din = new DataInputStream(socket.getInputStream());
					dout = new DataOutputStream(socket.getOutputStream());
					String s = din.readUTF();
					dout.writeUTF("TCP link succeed");
					++playerNum;
					// 只接收3个人进入游戏组
					if (playerNum > 8)
						break;
					data[i++] = socket.getInetAddress().toString();
					Message msg = TcpHandler.obtainMessage();
					Bundle bundle = new Bundle();
					bundle.putInt("playerNum", playerNum);
					bundle.putString("InetAddress", socket.getInetAddress()
							.toString());
					msg.what = 1;
					msg.setData(bundle);
					TcpHandler.sendMessage(msg);
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
