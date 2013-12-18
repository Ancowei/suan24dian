package com.ancowei.join_game;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import com.ancowei.welcome.Suan24dian_welcome;
import com.example.suan24dian.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class Join_game extends Activity {
	Button btn_joingame_exit;
	ListView list_inititor;

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
	static SimpleAdapter list_inititor_adapter;

	UDP_SerchThread UDP_serchThread;
	Handler join_Handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case INITIATOR_ADD:
				list_inititor_adapter.notifyDataSetChanged();
				list_inititor.setAdapter(list_inititor_adapter);
				break;
				//链接成功之后,跳转到游戏页面,等待游戏开始
			case TCP_LINK_SUCCEED:
				Intent intent=new Intent(Join_game.this,Join_game_begin.class);
				Join_game.this.startActivity(intent);
				
				break;
			case GAME_BEGIN:
				Bundle numBundle=msg.getData();
				break;
			default:
				break;
			}

		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.suan24dian_join_game);
		btn_joingame_exit = (Button) findViewById(R.id.btn_joingame_exit);
		list_inititor = (ListView) findViewById(R.id.list_initator);

		list_inititor_adapter = new SimpleAdapter(this, getInitiator(),
				R.layout.list_item,
				new String[] { "ItemImage", "ItemAddress" }, new int[] {
						R.id.ItemImage, R.id.ItemAddress });
		list_inititor.setAdapter(list_inititor_adapter);
		list_inititor.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View list,
					int position, long id) {

				ListView listView = (ListView) parent;
				HashMap<String, Object> map = (HashMap<String, Object>) listView
						.getItemAtPosition(position);

				String ItemAddr = (String) map.get("ItemAddress");
				try {
					TCP_ADDR = InetAddress.getByName(ItemAddr);
					new join_gameThread().start();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				// 点击之后获取发起玩家的IP进行TCP链接,链接成功之后进行UDP广播监听,接收发起玩家的发牌和命令
				// Toast.makeText(Join_game.this, addr.toString(),
				// Toast.LENGTH_LONG).show();

			}

		});

		btn_onclick = new btnOnClickListener();
		btn_joingame_exit.setOnClickListener(btn_onclick);

		UDP_serchThread = new UDP_SerchThread();
		UDP_serchThread.start();
	}

	public ArrayList<HashMap<String, Object>> getInitiator() {
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		for (int j = 0; j < i; j++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", R.drawable.ic_launcher);// 图像资源的ID
			map.put("ItemAddress", ADDR[j]);
			listItem.add(map);
		}
		for (int j = 0; j < 3; j++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", R.drawable.ic_launcher);// 图像资源的ID
			map.put("ItemAddress", "172.18.13.128".toString());
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
					byte buf[] = new byte[256];
					DatagramSocket UDPSocket = new DatagramSocket(3000);
					DatagramPacket UDPPacket = new DatagramPacket(buf,
							buf.length);
					UDPSocket.receive(UDPPacket);
					ByteArrayInputStream bin = new ByteArrayInputStream(
							UDPPacket.getData());
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(bin));
					s = reader.readLine();
					if (s.equals("suan24dian_initiate")) {
						// 加入发起玩家队列
						addr = UDPPacket.getAddress();
						ADDR[i++] = addr;
						Message msg = join_Handler.obtainMessage();
						msg.what = INITIATOR_ADD;
						join_Handler.sendMessage(msg);
					} else if (s.equals("game_begin")) {
						String nums[] = new String[4];
						for (int j = 0; j < 3; j++) {
							nums[j] = reader.readLine();
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

	// TCP链接之后,进行UDP侦听,接收发牌信息和命令
	public class UDPLink_after_TCPLINK extends Thread {
		public void run() {
			while (true) {
				try {
					InetAddress addr;
					byte buf[] = new byte[256];
					DatagramSocket UDPSocket = new DatagramSocket(3000);
					DatagramPacket UDPPacket = new DatagramPacket(buf,
							buf.length);
					UDPSocket.receive(UDPPacket);
					ByteArrayInputStream bin = new ByteArrayInputStream(
							UDPPacket.getData());
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(bin));
					s = reader.readLine();
					if (s.equals("suan24dian_initiate")) {
						// 加入发起玩家队列
						addr = UDPPacket.getAddress();
						ADDR[i++] = addr;
					}
					Message msg = join_Handler.obtainMessage();
					msg.what = INITIATOR_ADD;
					join_Handler.sendMessage(msg);

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