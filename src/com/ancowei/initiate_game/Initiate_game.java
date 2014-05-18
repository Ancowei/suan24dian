package com.ancowei.initiate_game;

import java.io.ByteArrayInputStream;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import com.ancowei.main.Suan24dianMain;
import com.example.suan24dian.R;
import ExitApp.ExitApp;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

public class Initiate_game extends Activity {
	Button btn_start_game;
	Button btn_back;
	ImageView image_user;
	TextView tx_username;
	ServerSocket serverSocket;
	Socket socket;
	UDP_listenning UDP_link;
	UDP_Brocast_initiate_Thread udp_brocast;
	Inet_initiate_control inet_initiate_control;
	DataInputStream din;
	DataOutputStream dout;

	public static String ADDR[] = new String[10];
	public static String NAME[] = new String[10];
	public static int i = 0;
	public static int playerNum = 0;
	public static String Name;
	private SharedPreferences sp;

	MyListView list_player;
	List<String> list_player_show;
	SimpleAdapter listItemAdapter;
	myOnClickListener myOnclick;
	ArrayList<HashMap<String, Object>> listItem;
	private final static int PLAYER_ADD = 0;
	private final static int IMAGE_RECEIVE_OK = 1;
	private final static int IMAGE_RECEIVE_FAIL = 2;
	private final static int ERROR = 4;
	private Handler myH;

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
		// deleteImageFile();
		udp_brocast.start();
		UDP_link.start();

	}

	public void findView() {
		btn_start_game = (Button) findViewById(R.id.btn_start_game);
		btn_back = (Button) findViewById(R.id.btn_exit);
		list_player = (MyListView) findViewById(R.id.list_player);
		image_user = (ImageView) findViewById(R.id.image_user);
		tx_username = (TextView) findViewById(R.id.tx_username);
		myH = new myHandler();
		sp = this.getSharedPreferences("user_msg", Context.MODE_PRIVATE);
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
		UDP_link = new UDP_listenning();
		//udp_brocast = new UDP_Brocast_initiate_Thread("suan24dian_initiate");
		udp_brocast = new UDP_Brocast_initiate_Thread();
	}

	// 删除测试图片文件
	public void deleteImageFile() {
		try {
			File file = new File(this.getFilesDir() + "/one.png");
			if (file.exists()) {
				Toast.makeText(Initiate_game.this, "" + file.getPath(),
						Toast.LENGTH_LONG).show();
				file.delete();
			} else {
				Toast.makeText(Initiate_game.this, "file not found",
						Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {

		}
	}

	private void handleList() {
		listItem.clear();
		HashMap<String, Object> map = new HashMap<String, Object>();
		for (int j = 0; j < i; j++) {
			map = new HashMap<String, Object>();
			try {
				FileInputStream fis = openFileInput(NAME[j]);
				Bitmap bm = BitmapFactory.decodeStream(fis);
				map.put("ItemImage", bm);
			} catch (Exception e) {
				map.put("ItemImage", R.drawable.ic_launcher);
			}
			map.put("ItemTitle", NAME[j]);
			listItem.add(map);
		}
	}

	// 生成适配器的Item和动态数组对应的元素
	public void buildAdapter() {
		listItemAdapter = new SimpleAdapter(this, getData(),
				R.layout.list_item, new String[] { "ItemImage", "ItemTitle" },
				new int[] { R.id.ItemImage, R.id.ItemAddress });
		listItemAdapter.setViewBinder(new ListViewBinder());
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

	// getData();
	public ArrayList<HashMap<String, Object>> getData() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		for (int j = 0; j < i; j++) {
			map = new HashMap<String, Object>();
			try {
				FileInputStream fis = openFileInput(NAME[j]);
				Bitmap bm = BitmapFactory.decodeStream(fis);
				map.put("ItemImage", bm);
			} catch (Exception e) {
				map.put("ItemImage", R.drawable.ic_launcher);
			}
			map.put("ItemTitle", NAME[j]);
			listItem.add(map);
		}
		return listItem;
	}
	
	public void brocast_test() {
		try {
			DatagramSocket socket = new DatagramSocket();
			InetAddress addr = InetAddress.getByName("192.168.173.27");
			InputStream is = this.getClass().getResourceAsStream("one.png");
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			DataOutputStream dout = new DataOutputStream(bout);
			dout.writeUTF("suan24dian_initiate");
			dout.writeUTF("link");
			dout.writeLong(is.available());
			System.out.println(is.available());
			byte[] data =new byte[is.available()];
			int len=0;
			while((len=is.read(data))>0)
			{
				dout.write(data,0,len);
			}
			byte[] buf = bout.toByteArray();
			buf = bout.toByteArray();
			DatagramPacket packet1 = new DatagramPacket(buf, buf.length, addr,
					4545);
			socket.send(packet1);
			socket.close();
			dout.close();
			bout.close();
		} catch (Exception e) {
			System.out.print(e.toString());
		}
	}
	
	// UDP brocast UDP广播：我发起游戏了,局域网的广播地址是 255.255.255.255
	public class UDP_Brocast_initiate_Thread extends Thread {
		public void run(){
			try {
				DatagramSocket socket = new DatagramSocket();
				InetAddress addr = InetAddress.getByName("192.168.173.27");
				addr=InetAddress.getByName("255.255.255.255");
				//InputStream is = this.getClass().getResourceAsStream("one.png");
				InputStream is = new FileInputStream(new File(
						Environment.getExternalStorageDirectory()
								+ "/user_image.jpg"));
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				DataOutputStream dout = new DataOutputStream(bout);
				dout.writeUTF("suan24dian_initiate");
				dout.writeUTF(sp.getString("user_name", ""));
				dout.writeLong(is.available());
				System.out.println(is.available());
				byte[] data =new byte[is.available()];
				int len=0;
				while((len=is.read(data))>0)
				{
					dout.write(data,0,len);
				}
				byte[] buf = bout.toByteArray();
				buf = bout.toByteArray();
				DatagramPacket packet1 = new DatagramPacket(buf, buf.length, addr,
						4545);
				socket.send(packet1);
				socket.close();
				dout.close();
				bout.close();
			} catch (Exception e) {
				System.out.print(e.toString());
			}
		}
		/*private String msg;
		public UDP_Brocast_initiate_Thread(String s) {
			this.msg = s;
		}
		public void run() {
			try {
				InetAddress addr = InetAddress.getByName("255.255.255.255");
				//addr = InetAddress.getByName("172.18.13.128");
				addr=InetAddress.getByName("192.168.173.27");
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				DataOutputStream dout = new DataOutputStream(bout);
				dout.writeUTF(msg);
				if ("suan24dian_initiate".equals(msg)) {
					dout.writeUTF(Suan24dianMain.user_Name);
					InputStream is = new FileInputStream(new File(
							Environment.getExternalStorageDirectory()
									+ "/user_image.jpg"));
					dout.writeLong(is.available());
					byte[] data = new byte[is.available()];
					int len = 0;
					while ((len = is.read(data)) > 0) {
						dout.write(data, 0, len);
						//Log.e("fila_size", "" + len);
					}
				} else if ("suan24dian_game_begin".equals(msg)) {
					String num1 = "1";
					String num2 = "4";
					String num3 = "7";
					String num4 = "9";
					dout.writeUTF(num1);
					dout.writeUTF(num2);
					dout.writeUTF(num3);
					dout.writeUTF(num4);
				}
				byte buf[] = bout.toByteArray();
				Log.e("fila_size", "" + buf.length);
				DatagramSocket socket = new DatagramSocket();
				DatagramPacket packet = new DatagramPacket(buf, buf.length,
						addr, 4545);
				socket.send(packet);
				socket.close();
				bout.close();
				dout.close();
			} catch (Exception e) {
				System.out.println("\n广播发送失败：" + e.toString());
			}
		}*/
	}
//game_begin brocast
	public class UDP_game_begin_Brocast extends Thread{
		public void run(){
			try {
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				DataOutputStream dout = new DataOutputStream(bout);
				dout.writeUTF("suan24dian_game_begin");
				dout.writeUTF("2");
				dout.writeUTF("4");
				dout.writeUTF("1");
				dout.writeUTF("3");
				InetAddress addr = InetAddress.getByName("255.255.255.255");
				byte buf[] = bout.toByteArray();
				DatagramSocket socket = new DatagramSocket();
				DatagramPacket packet = new DatagramPacket(buf, buf.length, addr,
						4549);
				socket.send(packet);
				bout.close();
				dout.close();
				socket.close();

			} catch (Exception e) {
				System.out.println("\n广播发送失败：" + e.toString());
			}
		}
	}
	// UDP link
	public class UDP_listenning extends Thread {
		public void run() {
			while (true) {
				try {
					InetAddress addr;
					byte buf[] = new byte[30000];
					DatagramSocket UDPSocket = new DatagramSocket(4546);
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
					if ("suan24dian_join_game".equals(s)) {
						// 加入发起玩家队列
						byte data[] = new byte[(int) l];
						for (int i = 0; i < l; i++) {// 读取图片数据
							data[i] = dis.readByte();
						}
						fos.write(data, 0, data.length);
						if (playerNum >= 4) {
							dis.close();
							bais.close();
							UDPSocket.close();
							fos.close();
							break;
						}
						Message msg = myH.obtainMessage();
						msg.what = PLAYER_ADD;
						Bundle b = new Bundle();
						b.putString("name", name);
						b.putString("addr", addr.toString());
						msg.setData(b);
						myH.sendMessage(msg);
					}
					dis.close();
					bais.close();
					UDPSocket.close();
					fos.close();
				} catch (IOException e1) {
					Message msg2 = myH.obtainMessage();
					msg2.what = ERROR;
					Bundle b = new Bundle();
					b.putString("error", e1.toString());
					msg2.setData(b);
					myH.sendMessage(msg2);
					e1.printStackTrace();
					break;
				}
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
					//new UDP_game_begin_Brocast().start();
					new AlertDialog.Builder(Initiate_game.this)
							.setMessage("玩家个数为0\n还不可以开始游戏哦")
							.setPositiveButton("确定", null).show();
				} else {
					// 向所有玩家发送游戏开始的广播
					new UDP_game_begin_Brocast().start();
					Intent intent = new Intent(Initiate_game.this,
							Suan24dian_game_begin_wait.class);
					intent.putExtra("i", i);
					for (int j = 0; j < i; ++j) {
						intent.putExtra("addr" + j, ADDR[j]);
						intent.putExtra("name" + j, NAME[j]);
					}
					intent.putExtra("name", Name);
					intent.putExtra("num1", "2");
					intent.putExtra("num2", "4");
					intent.putExtra("num3", "1");
					intent.putExtra("num4", "3");
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == event.KEYCODE_BACK) {
			Initiate_game.this.finish();
			return true;
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public class myHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case PLAYER_ADD:
				// 有玩家加进来了，更新相应的数据
				Bundle b = msg.getData();
				ADDR[i] = b.getString("addr");
				NAME[i] = b.getString("name");
				i++;
				playerNum++;
				update();
				break;

			case IMAGE_RECEIVE_OK:
				Toast.makeText(Initiate_game.this, "image_ok",
						Toast.LENGTH_LONG).show();
				update();
				break;
			case IMAGE_RECEIVE_FAIL:
				Toast.makeText(Initiate_game.this,
						"image_fail:" + msg.getData().getString("message"),
						Toast.LENGTH_LONG).show();
				break;

			}
		}

	}

	public void update() {
		listItem.clear();
		HashMap<String, Object> map = new HashMap<String, Object>();
		for (int j = 0; j < i; j++) {
			map = new HashMap<String, Object>();
			try {
				FileInputStream fis = openFileInput(NAME[j]);
				Bitmap bm = BitmapFactory.decodeStream(fis);
				map.put("ItemImage", bm);
			} catch (Exception e) {
				map.put("ItemImage", R.drawable.ic_launcher);
			}
			// map.put("ItemImage", R.drawable.ic_launcher);
			map.put("ItemTitle", NAME[j]);
			listItem.add(map);
		}
		listItemAdapter.notifyDataSetChanged();
		list_player.onRefreshComplete();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 重新进入该页面时候，应该清空之前玩家的数据
		playerNum = 0;
		i = 0;
		listItem.clear();
		new UDP_listenning().start();

		SharedPreferences sp = this.getSharedPreferences("user_msg",
				Context.MODE_PRIVATE);
		tx_username.setText(sp.getString("user_name", "") + " 在线");
		try {
			FileInputStream fis = new FileInputStream(
					Environment.getExternalStorageDirectory()
							+ "/user_image.jpg");
			Log.e("get_file_size", ""+fis.available());
		} catch (Exception e) {
			Log.e("get_file_size_error", e.toString());
		}

		Bitmap bm = BitmapFactory.decodeFile(Environment
				.getExternalStorageDirectory() + "/user_image.jpg");
		Bitmap image = Suan24dianMain.getRoundedCornerBitmap(bm);
		image_user.setImageBitmap(image);

	}

}
