package com.ancowei.initiate_game;

import java.io.ByteArrayInputStream;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ancowei.initiate_game.Game_begin.send_card_Thread;
import com.ancowei.internet.Inet_initiate_control;
import com.ancowei.listview.MyListView;
import com.ancowei.listview.MyListView.OnRefreshListener;
import com.ancowei.login.suan24dian_Login;
import com.ancowei.main.Suan24dianMain;
import com.example.suan24dian.R;
import ExitApp.ExitApp;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

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

	MyListView list_player;
	List<String> list_player_show;
	SimpleAdapter listItemAdapter;
	myOnClickListener myOnclick;
	ArrayList<HashMap<String, Object>> listItem;
	private final static int PLAYER_ADD=0;
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
		//create_player_db();
		findView();
		buildAdapter();
		registerListeners();
		set_image_and_name();
		udp_brocast.start();
		UDP_link.start();
	}

	public void findView() {
		btn_start_game = (Button) findViewById(R.id.btn_start_game);
		btn_back = (Button) findViewById(R.id.btn_exit);
		list_player = (MyListView) findViewById(R.id.list_player);
		image_user = (ImageView) findViewById(R.id.image_user);
		tx_username = (TextView) findViewById(R.id.tx_username);
		myH=new myHandler();
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
		udp_brocast = new UDP_Brocast_initiate_Thread("suan24dian_initiate");
	}

	public void set_image_and_name() {
		Intent image_and_name = this.getIntent();
		Name = image_and_name.getStringExtra("user_name");
		Intent image = image_and_name.getParcelableExtra("image");
		if (image != null) {
			Bundle extras = image.getExtras();
			if (extras != null) {
				Bitmap photo = extras.getParcelable("data");
				Drawable drawable = new BitmapDrawable(photo);
				image_user.setImageDrawable(drawable);
			}
		}
		tx_username.setText(Name+" 在线");
	}


	private void handleList() {
		listItem.clear();
		HashMap<String, Object> map = new HashMap<String, Object>();
		for(int j=0;j<i;j++){
			map = new HashMap<String, Object>();
			map.put("ItemImage", R.drawable.ic_launcher);
			map.put("ItemTitle", NAME[j]);
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
		HashMap<String, Object> map = new HashMap<String, Object>();
		for(int j=0;j<i;j++){
			map = new HashMap<String, Object>();
			map.put("ItemImage", R.drawable.ic_launcher);
			map.put("ItemTitle", NAME[j]);
			listItem.add(map);
		}
		return listItem;
	}

	// UDP brocast UDP广播：我发起游戏了,局域网的广播地址是 255.255.255.255
	public class UDP_Brocast_initiate_Thread extends Thread {
		private String msg;
		public UDP_Brocast_initiate_Thread(String s) {
			this.msg = s;
		}
		public void run() {
			try {
				InetAddress addr = InetAddress.getByName("255.255.255.255");
				addr = InetAddress.getByName("172.18.13.128");
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				DataOutputStream dout = new DataOutputStream(bout);
				dout.writeUTF(msg);
				if ("suan24dian_initiate".equals(msg)) {
					dout.writeUTF(Suan24dianMain.user_Name);
				} else if ("suan24dian_game_begin".equals(msg)) {
					/*dout.writeInt(i);
					for(int j=0;j<i;j++){
						dout.writeUTF(ADDR[j]);
						dout.writeUTF(NAME[j]);
					}*/
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
		}
	}

	// UDP link
	public class UDP_listenning extends Thread {
		public void run() {
			while (true) {
				try {
					InetAddress addr;
					byte buf[] = new byte[1024];
					DatagramSocket UDPSocket = new DatagramSocket(4244);
					DatagramPacket UDPPacket = new DatagramPacket(buf,
							buf.length);
					UDPSocket.receive(UDPPacket);
					ByteArrayInputStream bais = new ByteArrayInputStream(buf); // 把刚才的部分视为输入流
					DataInputStream dis = new DataInputStream(bais);
					String s = dis.readUTF();
					String name = dis.readUTF();
					addr = UDPPacket.getAddress();
					if ("suan24dian_join_game".equals(s)) {
						// 加入发起玩家队列
						if (playerNum >= 4)
							break;
						Message msg=myH.obtainMessage();
						msg.what=PLAYER_ADD;
						Bundle b=new Bundle();
						b.putString("name", name);
						b.putString("addr", addr.toString());
						msg.setData(b);
						myH.sendMessage(msg);
						
					}
					dis.close();
					bais.close();
					UDPSocket.close();
				} catch (Exception e) {
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
					new AlertDialog.Builder(Initiate_game.this)
							.setMessage("玩家个数为0\n还不可以开始游戏哦")
							.setPositiveButton("确定", null).show();
				} else {
					// 向所有玩家发送游戏开始的广播
					new UDP_Brocast_initiate_Thread("suan24dian_game_begin")
							.start();
					Intent intent = new Intent(Initiate_game.this,
							Suan24dian_game_begin_wait.class);
					intent.putExtra("i", i);
					for(int j=0;j<i;++j){
						intent.putExtra("addr"+j, ADDR[j]);
						intent.putExtra("name"+j, NAME[j]);
					}
					intent.putExtra("name", Name);
					intent.putExtra("num1", "1");
					intent.putExtra("num2", "4");
					intent.putExtra("num3", "7");
					intent.putExtra("num4", "9");
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

	@Override
	protected void onResume() {
		super.onResume();
		//重新进入该页面时候，应该清空之前玩家的数据
		playerNum=0;
		i=0;
		listItem.clear();
	}
	
	public class myHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case PLAYER_ADD:
				//有玩家加进来了，更新相应的数据
				Bundle b=msg.getData();
				ADDR[i]=b.getString("addr");
				NAME[i]=b.getString("name");
				i++;
				playerNum++;
				update();
				break;
			}
		}
		
		}
	public void update(){
		listItem.clear();
		HashMap<String, Object> map = new HashMap<String, Object>();
		for(int j=0;j<i;j++){
			map.put("ItemImage", R.drawable.ic_launcher);
			map.put("ItemTitle", NAME[j]);
			listItem.add(map);
		}
		listItemAdapter.notifyDataSetChanged();
		list_player.onRefreshComplete();
	}
}
