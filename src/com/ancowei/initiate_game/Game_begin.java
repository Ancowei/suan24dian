package com.ancowei.initiate_game;

import java.io.ByteArrayInputStream;


import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

import com.ancowei.calculate.Calculate;
import com.ancowei.main.Suan24dianMain;
import com.example.suan24dian.R;

import ExitApp.ExitApp;
import android.R.color;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Game_begin extends Activity {

	Button btn_1;
	Button btn_2;
	Button btn_3;
	Button btn_4;

	Button btn_plus; // 加
	Button btn_minus; // 减
	Button btn_cal; // 乘
	Button btn_devide; // 除
	Button btn_left; // 左括号
	Button btn_right;// 右括号

	Button btn_back; // 后退
	Button btn_clear; // 清屏
	Button btn_commit; // 提交

	Button btn_next;
	Button btn_exit;

	TextView text_countdown;
	TextView text_countdown_show;
	TextView text_result;
	TextView text_time;
	EditText edit_calculate;

	public static String num1;
	public static String num2;
	public static String num3;
	public static String num4;
	public int btn_1_background;
	public int btn_2_background;
	public int btn_3_background;
	public int btn_4_background;

	public String calculate = "";
	// 两个数字不可以连在一起
	public boolean preIfnum = false;
	// 只有当前符号是数字、（、），才可以继续输入+、-、*、/等符号
	public boolean preIfnumorleftorright = false;
	// 当前数字/符号
	public String preNum = "";
	// 数字使用个数，本次计算必须使用完四个数字
	public int count = 0;
	// 数字点击顺序
	public int[] numOrder = new int[4];
	int i = 0;
	// message 的what字段
	public static final int RANDOM = 0;
	public static final int TIME = 1;
	public static final int NEXT = 2;
	public static final int COLLECT = 3;
	public static int questionNum = 10;
	public static int time = 10;

	// 玩家计算正确的题数
	public int correctNum[] = new int[4];
	public static int highestNum = 0;

	private static Handler myH;

	private btnOnClickListener btnOnclick;

	public static TimeThread timeThread;
	public static NumThread numThread;
	public static boolean STOP = false;
	//private String user_Name;
	private static String ADDR[] = new String[10];
	private static String NAME[] = new String[10];
	private static int[] NAME_COLLECT=new int[10];
	private static int playerNum;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 退出程序
		ExitApp.getInstance().addActivity(Game_begin.this);
		// 无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_suan24dian_initiate_play);
		// 全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		findView();
		registerListeners();
		getFirstNum();
		myH = new myHandler();
		// new NumThread().start();
		timeThread = new TimeThread();
		numThread = new NumThread();
		new collect_UDP_listenning().start();
		setQuestionNum();

	}

	public void findView() {
		btn_1 = (Button) findViewById(R.id.btn_1);
		btn_2 = (Button) findViewById(R.id.btn_2);
		btn_3 = (Button) findViewById(R.id.btn_3);
		btn_4 = (Button) findViewById(R.id.btn_4);

		btn_plus = (Button) findViewById(R.id.btn_plus);
		btn_minus = (Button) findViewById(R.id.btn_minus);
		btn_cal = (Button) findViewById(R.id.btn_cal);
		btn_devide = (Button) findViewById(R.id.btn_devide);
		btn_left = (Button) findViewById(R.id.btn_left);
		btn_right = (Button) findViewById(R.id.btn_right);

		btn_back = (Button) findViewById(R.id.btn_back);
		btn_clear = (Button) findViewById(R.id.btn_clear);
		btn_commit = (Button) findViewById(R.id.btn_commit);

		btn_next = (Button) findViewById(R.id.btn_next);
		btn_exit = (Button) findViewById(R.id.btn_exit);
		edit_calculate = (EditText) findViewById(R.id.edit_calculate);

		text_countdown_show = (TextView) findViewById(R.id.text_countdown_show);
		text_result = (TextView) findViewById(R.id.text_result);
		text_time = (TextView) findViewById(R.id.text_time);
		
		for(int j=0;j<10;++j){
			NAME_COLLECT[j]=0;
		}
	}

	public void registerListeners() {
		text_countdown_show.setTextSize(20);
		text_countdown_show.setTextColor(Color.BLUE);
		text_time.setTextSize(20);
		text_time.setTextColor(Color.RED);

		btnOnclick = new btnOnClickListener();
		btn_next.setOnClickListener(btnOnclick);
		btn_exit.setOnClickListener(btnOnclick);

		btn_1.setOnClickListener(btnOnclick);
		btn_2.setOnClickListener(btnOnclick);
		btn_3.setOnClickListener(btnOnclick);
		btn_4.setOnClickListener(btnOnclick);

		btn_plus.setOnClickListener(btnOnclick);
		btn_minus.setOnClickListener(btnOnclick);
		btn_cal.setOnClickListener(btnOnclick);
		btn_devide.setOnClickListener(btnOnclick);
		btn_left.setOnClickListener(btnOnclick);
		btn_right.setOnClickListener(btnOnclick);

		btn_back.setOnClickListener(btnOnclick);
		btn_clear.setOnClickListener(btnOnclick);
		btn_commit.setOnClickListener(btnOnclick);
	}

	public void getFirstNum() {
		Intent fNum = this.getIntent();
		playerNum = fNum.getIntExtra("playerNum", 0);
		int j;
		for (j = 0; j < playerNum; ++j) {
			ADDR[j] = fNum.getStringExtra("addr" + j);
			NAME[j] = fNum.getStringExtra("name" + j);
		}
		ADDR[j]="127.0.0.1";
		NAME[j]=this.getSharedPreferences("user_msg", MODE_PRIVATE)
				.getString("user_name", "");
		playerNum++;
		num1 = fNum.getStringExtra("num1");
		num2 = fNum.getStringExtra("num2");
		num3 = fNum.getStringExtra("num3");
		num4 = fNum.getStringExtra("num4");
		setNum();
	}

	public void setNum() {
		if (num1.equals("" + 1)) {
			btn_1_background = R.drawable.card_mouse_1;
		} else if (num1.equals("" + 2)) {
			btn_1_background = R.drawable.card_cattle_2;
		} else if (num1.equals("" + 3)) {
			btn_1_background = R.drawable.card_tiger_3;
		} else if (num1.equals("" + 4)) {
			btn_1_background = R.drawable.card_rabbit_4;
		} else if (num1.equals("" + 5)) {
			btn_1_background = R.drawable.card_dragon_5;
		} else if (num1.equals("" + 6)) {
			btn_1_background = R.drawable.card_snake_6;
		} else if (num1.equals("" + 7)) {
			btn_1_background = R.drawable.card_horse_7;
		} else if (num1.equals("" + 8)) {
			btn_1_background = R.drawable.card_sheep_8;
		} else if (num1.equals("" + 9)) {
			btn_1_background = R.drawable.card_monkey_9;
		} else if (num1.equals("" + 10)) {
			btn_1_background = R.drawable.card_chiken_10;
		} else if (num1.equals("" + 11)) {
			btn_1_background = R.drawable.card_dog_11;
		} else {
			btn_1_background = R.drawable.card_pig_12;
		}
		if (num2.equals("" + 1)) {
			btn_2_background = R.drawable.card_mouse_1;
		} else if (num2.equals("" + 2)) {
			btn_2_background = R.drawable.card_cattle_2;
		} else if (num2.equals("" + 3)) {
			btn_2_background = R.drawable.card_tiger_3;
		} else if (num2.equals("" + 4)) {
			btn_2_background = R.drawable.card_rabbit_4;
		} else if (num2.equals("" + 5)) {
			btn_2_background = R.drawable.card_dragon_5;
		} else if (num2.equals("" + 6)) {
			btn_2_background = R.drawable.card_snake_6;
		} else if (num2.equals("" + 7)) {
			btn_2_background = R.drawable.card_horse_7;
		} else if (num2.equals("" + 8)) {
			btn_2_background = R.drawable.card_sheep_8;
		} else if (num2.equals("" + 9)) {
			btn_2_background = R.drawable.card_monkey_9;
		} else if (num2.equals("" + 10)) {
			btn_2_background = R.drawable.card_chiken_10;
		} else if (num2.equals("" + 11)) {
			btn_2_background = R.drawable.card_dog_11;
		} else {
			btn_2_background = R.drawable.card_pig_12;
		}
		if (num3.equals("" + 1)) {
			btn_3_background = R.drawable.card_mouse_1;
		} else if (num3.equals("" + 2)) {
			btn_3_background = R.drawable.card_cattle_2;
		} else if (num3.equals("" + 3)) {
			btn_3_background = R.drawable.card_tiger_3;
		} else if (num3.equals("" + 4)) {
			btn_3_background = R.drawable.card_rabbit_4;
		} else if (num3.equals("" + 5)) {
			btn_3_background = R.drawable.card_dragon_5;
		} else if (num3.equals("" + 6)) {
			btn_3_background = R.drawable.card_snake_6;
		} else if (num3.equals("" + 7)) {
			btn_3_background = R.drawable.card_horse_7;
		} else if (num3.equals("" + 8)) {
			btn_3_background = R.drawable.card_sheep_8;
		} else if (num3.equals("" + 9)) {
			btn_3_background = R.drawable.card_monkey_9;
		} else if (num3.equals("" + 10)) {
			btn_3_background = R.drawable.card_chiken_10;
		} else if (num3.equals("" + 11)) {
			btn_3_background = R.drawable.card_dog_11;
		} else {
			btn_3_background = R.drawable.card_pig_12;
		}
		if (num4.equals("" + 1)) {
			btn_4_background = R.drawable.card_mouse_1;
		} else if (num4.equals("" + 2)) {
			btn_4_background = R.drawable.card_cattle_2;
		} else if (num4.equals("" + 3)) {
			btn_4_background = R.drawable.card_tiger_3;
		} else if (num4.equals("" + 4)) {
			btn_4_background = R.drawable.card_rabbit_4;
		} else if (num4.equals("" + 5)) {
			btn_4_background = R.drawable.card_dragon_5;
		} else if (num4.equals("" + 6)) {
			btn_4_background = R.drawable.card_snake_6;
		} else if (num4.equals("" + 7)) {
			btn_4_background = R.drawable.card_horse_7;
		} else if (num4.equals("" + 8)) {
			btn_4_background = R.drawable.card_sheep_8;
		} else if (num4.equals("" + 9)) {
			btn_4_background = R.drawable.card_monkey_9;
		} else if (num4.equals("" + 10)) {
			btn_4_background = R.drawable.card_chiken_10;
		} else if (num4.equals("" + 11)) {
			btn_4_background = R.drawable.card_dog_11;
		} else {
			btn_4_background = R.drawable.card_pig_12;
		}
		btn_1.setText(num1);
		btn_2.setText(num2);
		btn_3.setText(num3);
		btn_4.setText(num4);
		btn_1.setTextColor(color.white);
		btn_2.setTextColor(color.white);
		btn_3.setTextColor(color.white);
		btn_4.setTextColor(color.white);
		btn_1.setBackgroundResource(btn_1_background);
		btn_2.setBackgroundResource(btn_2_background);
		btn_3.setBackgroundResource(btn_3_background);
		btn_4.setBackgroundResource(btn_4_background);
		text_result.setText("");

	}

	public class btnOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			// 点击数字时候，需要判断前面一个是否为数字
			case R.id.btn_1:
				if (!preIfnum) {
					numOrder[i++] = R.id.btn_1;
					preNum = "" + btn_1.getText();
					calculate = calculate + btn_1.getText();
					btn_1.setText("");
					btn_1.setBackgroundResource(R.drawable.bailv);
					edit_calculate.setText(calculate);
					preIfnum = true;
					preIfnumorleftorright = true;
					count++;
				}
				break;
			case R.id.btn_2:
				if (!preIfnum) {
					numOrder[i++] = R.id.btn_2;
					preNum = "" + btn_2.getText();
					calculate = calculate + btn_2.getText();
					btn_2.setText("");
					btn_2.setBackgroundResource(R.drawable.bailv);
					edit_calculate.setText(calculate);
					preIfnum = true;
					preIfnumorleftorright = true;
					count++;
				}

				break;
			case R.id.btn_3:
				if (!preIfnum) {
					numOrder[i++] = R.id.btn_3;
					preNum = "" + btn_3.getText();
					calculate = calculate + btn_3.getText();
					btn_3.setText("");
					btn_3.setBackgroundResource(R.drawable.bailv);
					edit_calculate.setText(calculate);
					preIfnum = true;
					preIfnumorleftorright = true;
					count++;
				}
				break;
			case R.id.btn_4:
				if (!preIfnum) {
					numOrder[i++] = R.id.btn_4;
					preNum = "" + btn_4.getText();
					calculate = calculate + btn_4.getText();
					btn_4.setText("");
					btn_4.setBackgroundResource(R.drawable.bailv);
					edit_calculate.setText(calculate);
					preIfnum = true;
					preIfnumorleftorright = true;
					count++;
				}
				break;
			// 只有数字还没全部输入完毕才可以输入+号
			case R.id.btn_plus:
				if (count != 4 && count != 0) {
					if (preIfnumorleftorright) {
						preNum = "" + btn_plus.getText();
						calculate = calculate + btn_plus.getText();
						edit_calculate.setText(calculate);
						preIfnum = false;
						preIfnumorleftorright = false;
					}
				}
				break;
			case R.id.btn_minus:
				if (count != 4 && count != 0) {
					if (preIfnumorleftorright) {
						preNum = "" + btn_minus.getText();
						calculate = calculate + btn_minus.getText();
						edit_calculate.setText(calculate);
						preIfnum = false;
						preIfnumorleftorright = false;
					}
				}
				break;
			case R.id.btn_cal:
				if (count != 4 && count != 0) {
					if (preIfnumorleftorright) {
						preNum = "" + btn_cal.getText();
						calculate = calculate + btn_cal.getText();
						edit_calculate.setText(calculate);
						preIfnum = false;
						preIfnumorleftorright = false;
					}
				}
				break;
			case R.id.btn_devide:
				if (count != 4 && count != 0) {
					if (preIfnumorleftorright) {
						preNum = "" + btn_devide.getText();
						calculate = calculate + btn_devide.getText();
						edit_calculate.setText(calculate);
						preIfnum = false;
						preIfnumorleftorright = false;
					}
				}
				break;
			case R.id.btn_left:
				if (count != 4) {
					preNum = "" + btn_left.getText();
					calculate = calculate + btn_left.getText();
					edit_calculate.setText(calculate);
					preIfnum = false;
					preIfnumorleftorright = true;
				}
				break;
			case R.id.btn_right:
				preNum = "" + btn_right.getText();
				calculate = calculate + btn_right.getText();
				edit_calculate.setText(calculate);
				preIfnum = false;
				preIfnumorleftorright = true;
				break;
			case R.id.btn_back:
				int len = calculate.length();
				if (len != 0) {
					if (len == 1) {
						preNum = "";
						if (Character.isDigit(calculate.charAt(len - 1))) {
							// 调用函数恢复数字
							preIfnum = false;
							i = 0;
							recoverNum(numOrder[0]);
							--count;
						}
						calculate = calculate.substring(0, len - 1);
						edit_calculate.setText(calculate);
					} else { // len>1
						if (Character.isDigit(calculate.charAt(len - 1))) {
							if (Character.isDigit(calculate.charAt(len - 2))) {
								if (len == 2) {
									preNum = "";
								} else {
									preNum = "" + calculate.charAt(len - 3);
								}
								recoverNum(numOrder[--i]);
								--count;
								preIfnum = false;
								calculate = calculate.substring(0, len - 2);
								edit_calculate.setText(calculate);
							} else {
								recoverNum(numOrder[--i]);
								--count;
								preIfnum = false;
								preNum = "" + calculate.charAt(len - 2);
								calculate = calculate.substring(0, len - 1);
								edit_calculate.setText(calculate);
							}
						} else {
							if (Character.isDigit(calculate.charAt(len - 2))
									&& len > 2
									&& Character.isDigit(calculate
											.charAt(len - 3))) {
								preNum = "" + calculate.charAt(len - 3)
										+ calculate.charAt(len - 2);

							} else {
								preNum = "" + calculate.charAt(len - 2);
							}
							calculate = calculate.substring(0, len - 1);
							edit_calculate.setText(calculate);
						}
					}
					System.out.println(preNum);
				}
				break;
			case R.id.btn_clear:
				count = 0;
				i = 0;
				preNum = "";
				preIfnum = false;
				edit_calculate.setText("");
				calculate = "";
				btn_1.setText(num1);
				btn_1.setBackgroundResource(btn_1_background);
				btn_2.setText(num2);
				btn_2.setBackgroundResource(btn_2_background);
				btn_3.setText(num3);
				btn_3.setBackgroundResource(btn_3_background);
				btn_4.setText(num4);
				btn_4.setBackgroundResource(btn_4_background);
				break;
			case R.id.btn_commit:
				if (count == 4) {
					// 调用处理表达式是否正确，运算结果是否为24的函数
					try {
						String res = ifResult(calculate);
						//text_result.setText(res);
					} catch (Exception e) {
						System.out.println(e.toString());
						text_result.setText("请输入正确的表达式！");
					}
				} else {
					text_result.setText("必须用完四个数！");
				}
				break;
			case R.id.btn_next:
				questionNum = questionNum - 1;
				if (questionNum < 0) {
					// 游戏结束时候，应该发送广播给所有玩家，告诉大家游戏已经结束，并且公布游戏结果
					new game_over_Thread().start();
					STOP = true;
					game_over();

				} else {
					text_countdown_show.setText("" + questionNum);
					i = 0;
					count = 0;
					preNum = "";
					preIfnum = false;
					calculate = "";
					edit_calculate.setText(calculate);
					new NumThread().start();
					// 点击下一题的时候，通知其他玩家进入下一题，并发牌给其他玩家
					time = 11;
					STOP = true;
					STOP = false;
				}
				break;
			case R.id.btn_exit:
				timeThread.interrupt();
				Intent intent = new Intent(Game_begin.this,
						Suan24dianMain.class);
				Game_begin.this.finish();
				Game_begin.this.startActivity(intent);
				break;
			}
		}
	}

	// 显示初始化
	public void setQuestionNum() {
		STOP = false;
		time = 100;
		questionNum = 10;
		text_countdown_show.setText("" + questionNum);
		// numThread.start();
		// timeThread.start();
	}

	// 时间线程
	public class TimeThread extends Thread {

		public void run() {
			STOP = false;
			while (!STOP) {
				time = 60;
				try {
					while (!STOP && time >= 0) {
						Message msg = myH.obtainMessage();
						Bundle timeBundle = new Bundle();
						timeBundle.putString("time", "" + time);
						msg.what = TIME;
						msg.setData(timeBundle);
						if (time == 0) {
							msg.arg1 = 0;
						} else {
							msg.arg1 = 1;
						}
						myH.sendMessage(msg);
						Thread.sleep(1000);
						time = time - 1;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	// 回退时候，如果是数字，恢复按钮数字
	public void recoverNum(int id) {
		switch (id) {
		case R.id.btn_1:
			btn_1.setText(num1);
			btn_1.setBackgroundResource(btn_1_background);
			break;
		case R.id.btn_2:
			btn_2.setText(num2);
			btn_2.setBackgroundResource(btn_2_background);
			break;
		case R.id.btn_3:
			btn_3.setText(num3);
			btn_3.setBackgroundResource(btn_3_background);
			break;
		case R.id.btn_4:
			btn_4.setText(num4);
			btn_4.setBackgroundResource(btn_4_background);
			break;
		default:
			break;
		}
	}

	// 判断输入表达式是否正确，结果是否为24的函数
	public String ifResult(String str_calculate) throws Exception {
		String res = "结果有待处理。。。";
		ArrayList postfix;
		Calculate cal = new Calculate();
		postfix = cal.transform(str_calculate);
		boolean resB = cal.calculate(postfix);
		if (resB) {
			res = "结果正确";
			int k=0;
			for(int j=0;j<playerNum;++j){
				if(ADDR[j].equals("127.0.0.1")){
					k=j;
					break;
				}
			}
			NAME_COLLECT[k]++;
			//进入下一题
			questionNum = questionNum - 1;
			if (questionNum < 0) {
				// 游戏结束时候，应该发送广播给所有玩家，告诉大家游戏已经结束，并且公布游戏结果
				new game_over_Thread().start();
				STOP = true;
				game_over();

			} else {
				// 通知其他玩家有玩家做出了这一题，请进入下一题，并发牌给其他玩家
				//new send_card_Thread().start();
				text_countdown_show.setText("" + questionNum);
				i = 0;
				count = 0;
				preNum = "";
				preIfnum = false;
				calculate = "";
				edit_calculate.setText(calculate);
				//产生随机数之后，发牌给其他玩家，然后显示
				new NumThread().start();
				time = 11;
				STOP = true;
				STOP = false;
			}
		} else {
			res = "结果错误，请重新计算";
		}
		text_result.setText(res);
		return res;
	}

	// 发牌线程
	public class send_card_Thread extends Thread {
		public void run() {
			try {
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				DataOutputStream dout = new DataOutputStream(bout);
				dout.writeUTF("fapai");
				dout.writeUTF(num1);
				dout.writeUTF(num2);
				dout.writeUTF(num3);
				dout.writeUTF(num4);
				InetAddress addr = InetAddress.getByName("172.18.13.128");
				byte buf[] = bout.toByteArray();
				DatagramSocket socket = new DatagramSocket();
				DatagramPacket packet = new DatagramPacket(buf, buf.length,
						addr, 4548);
				socket.send(packet);
				bout.close();
				dout.close();
				socket.close();
			} catch (Exception e) {
				System.out.println("\n广播发送失败：" + e.toString());
			}
		}
	}

	// 开始游戏之后，UDP侦听其它玩家是否正确算出来了
	public class collect_UDP_listenning extends Thread {
		public void run() {
			while(true){
			try {
				byte[] collect_buf = new byte[256];
				DatagramPacket packet = new DatagramPacket(collect_buf,
						collect_buf.length);
				DatagramSocket socket = new DatagramSocket(4547);
				socket.receive(packet);
				ByteArrayInputStream bin = new ByteArrayInputStream(collect_buf);
				DataInputStream din = new DataInputStream(bin);
				String s = din.readUTF();
				String name = din.readUTF();
				if ("collect".equals(s)) {
					// 有人做出来了,更新数据库，然后进入下一题
					Message msg = myH.obtainMessage();
					Bundle b = new Bundle();
					b.putString("addr", packet.getAddress().toString());
					b.putString("name", name);
					msg.what = COLLECT;
					msg.setData(b);
					myH.sendMessage(msg);
				}
			} catch (Exception e) {
				System.out.println("接受出错"+e.toString());

			}
			}
			
		}
	}

	// 游戏结束线程--游戏结束的时候，向所有玩家发送广播：游戏已经结束的通知，游戏结果公布
	public class game_over_Thread extends Thread {
		public void run() {
			try {
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				DataOutputStream dout = new DataOutputStream(bout);
				DatagramSocket socket = new DatagramSocket();
				dout.writeUTF("suan24dian_game_over");
				dout.writeInt(playerNum);
				for(int j=0;j<playerNum;++j){
					dout.writeUTF(ADDR[j]);
					dout.writeUTF(NAME[j]);
					dout.writeInt(NAME_COLLECT[j]);
				}
				byte buf[] = bout.toByteArray();
				InetAddress addr = InetAddress.getByName("172.18.13.128");
				DatagramPacket packet = new DatagramPacket(buf, buf.length,
						addr, 4548);
				socket.send(packet);
				bout.close();
				dout.close();
				socket.close();
			} catch (Exception e) {
				System.out.println("\n游戏结束广播发送失败：" + e.toString());
			}
		}
	}

	public class myHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case RANDOM:
				// 产生的四个随机数先广播发给其他客户，再在自己处显示（发牌的公平性考虑）
				new send_card_Thread().start();
				setNum();
				break;
			// 有玩家做出来了，更新数据，然后进入下一题
			case COLLECT:
				Bundle b = msg.getData();
				String addr = b.getString("addr");
				int k=0;
				for(int j=0;j<playerNum;++j){
					if(addr.equals(ADDR[j])){
						k=j;
						break;
					}
				}
				NAME_COLLECT[k]++;		
				questionNum = questionNum - 1;
				if (questionNum < 0) {
					// 游戏结束时候，应该发送广播给所有玩家，告诉大家游戏已经结束，并且公布游戏结果
					new game_over_Thread().start();
					STOP = true;
					game_over();

				} else {
					// 通知其他玩家有玩家做出了这一题，请进入下一题，并发牌给其他玩家
					text_countdown_show.setText("" + questionNum);
					i = 0;
					count = 0;
					preNum = "";
					preIfnum = false;
					calculate = "";
					edit_calculate.setText(calculate);
					new NumThread().start();
					time = 11;
					STOP = true;
					STOP = false;

				}
				break;
			case TIME:
				Bundle timeBundle = msg.getData();
				if (msg.arg1 == 0) {
					text_time.setText(timeBundle.getString("time"));
					STOP = true;
					new AlertDialog.Builder(Game_begin.this)
							.setTitle("时间到,请做下一题")
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// 每个题目有时间限制，当时间用完时候，会重新发牌，进入下一题
											// 这里要实现重新发牌功能
											questionNum = questionNum - 1;
											if (questionNum < 0) {
												// 游戏结束时候，应该发送广播给所有玩家，告诉大家游戏已经结束，并且公布游戏结果
												new game_over_Thread().start();
												STOP = true;
												game_over();

											} else {
												text_countdown_show.setText(""
														+ questionNum);
												i = 0;
												count = 0;
												preNum = "";
												preIfnum = false;
												calculate = "";
												edit_calculate
														.setText(calculate);
												new NumThread().start();

												STOP = true;
												// STOP = false;
												timeThread = new TimeThread();
												timeThread.start();

											}
										}
									}).show();
					// Suan24dianMain.this.finish(); //时间到，退出计算界面
				} else {
					text_time.setText(timeBundle.getString("time"));
				}
				break;
			}
		}
	}

	// 开启一个新的线程，产生1-12之间的随机数
	public class NumThread extends Thread {
		public void run() {
			// 产生1-13之间的四个随机数
			num1 = "" + (int) (Math.random() * 12 + 1);
			num2 = "" + (int) (Math.random() * 12 + 1);
			num3 = "" + (int) (Math.random() * 12 + 1);
			num4 = "" + (int) (Math.random() * 12 + 1);

			Bundle numBundle = new Bundle();
			numBundle.putString("num1", num1);
			numBundle.putString("num2", num2);
			numBundle.putString("num3", num3);
			numBundle.putString("num4", num4);
			Message msg = myH.obtainMessage();
			msg.what = RANDOM;
			msg.setData(numBundle);
			myH.sendMessage(msg);
		}
	}

	public void game_over() {
		Intent game_over = new Intent(Game_begin.this, Initiate_game_over.class);
		game_over.putExtra("playerNum", playerNum);
		for(int j=0;j<playerNum;++j){
			game_over.putExtra("addr"+j, ADDR[j]);
			game_over.putExtra("name"+j, NAME[j]);
			game_over.putExtra("collect"+j, NAME_COLLECT[j]);
		}
		Game_begin.this.finish();
		Game_begin.this.startActivity(game_over);
	}
	
	
}
