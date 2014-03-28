package com.ancowei.initiate_game;

import java.io.ByteArrayOutputStream;

import java.io.PrintStream;
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

	public String num1;
	public String num2;
	public String num3;
	public String num4;
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
	public static int questionNum = 10;
	public static int time = 10;

	// 玩家计算正确的题数
	public int correctNum = 0;
	public static int highestNum = 0;

	private static Handler myH;

	private btnOnClickListener btnOnclick;

	public static TimeThread timeThread;
	public static NumThread numThread;
	public static boolean STOP = false;

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
		myH = new myHandler();
		// new NumThread().start();
		timeThread = new TimeThread();
		numThread = new NumThread();

		setQuestionNum();

		if (correctNum > highestNum) {
			highestNum = correctNum;
			// 更新数据库
			/*Suan24dianMain.sqlHelper.update(Suan24dianMain.USER_NAME,
					highestNum);*/
		}
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
						text_result.setText(res);
					} catch (Exception e) {
						e.printStackTrace();
						text_result.setText("请输入正确的表达式！");
					}
				} else {
					text_result.setText("必须用完四个数！");
				}
				break;
			case R.id.btn_next:
				questionNum = questionNum - 1;
				if (questionNum < 0) {
					//游戏结束时候，应该发送广播给所有玩家，告诉大家游戏已经结束，并且公布游戏结果
					new game_over_Thread().start();
					STOP = true;
					new AlertDialog.Builder(Game_begin.this)
							.setTitle("游戏结束")
							.setMessage(
									"A:" + num1 + "\nB:" + num2 + "\nC:" + num3
											+ "\nD:" + num4)
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											Game_begin.this.finish();
										}
									}).show();
				} else {
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
		timeThread.start();
		questionNum = 10;
		text_countdown_show.setText("" + questionNum);
		numThread.start();
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
			// 正确题数加1
			correctNum++;
			if (correctNum > highestNum) {
				highestNum = correctNum;
				/*Suan24dianMain.sqlHelper.update(Suan24dianMain.USER_NAME,
						highestNum);*/
			}
		} else {
			res = "结果错误，请重新计算";
		}
		return res;
	}

	// 发牌线程
	public class send_card_Thread extends Thread {
		public void run() {
			try {
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				PrintStream pout = new PrintStream(bout);
				pout.println(num1);
				pout.println(num2);
				pout.println(num3);
				pout.println(num4);
				InetAddress addr = InetAddress.getByName(Suan24dianMain.ADDR);
				byte buf[] = bout.toByteArray();
				DatagramSocket socket = new DatagramSocket();
				DatagramPacket packet = new DatagramPacket(buf, buf.length,
						addr, Suan24dianMain.PORT);
				socket.send(packet);
				bout.close();
				pout.close();

			} catch (Exception e) {
				System.out.println("\n广播发送失败：" + e.toString());
			}
		}
	}
//游戏结束线程--游戏结束的时候，向所有玩家发送广播：游戏已经结束的通知，游戏结果公布
	public class game_over_Thread extends Thread{
		public void run(){
			try {
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				PrintStream pout = new PrintStream(bout);
				pout.println("gameIsover");
				pout.println("The resault of this game is:");
				InetAddress addr = InetAddress.getByName(Suan24dianMain.ADDR);
				byte buf[] = bout.toByteArray();

				DatagramSocket socket = new DatagramSocket();
				DatagramPacket packet = new DatagramPacket(buf, buf.length,
						addr, Suan24dianMain.PORT);
				socket.send(packet);
				bout.close();
				pout.close();

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
				//Bundle numBundle = msg.getData();
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
												//游戏结束时候，应该发送广播给所有玩家，告诉大家游戏已经结束，并且公布游戏结果
												new game_over_Thread().start();
												STOP = true;
												new AlertDialog.Builder(
														Game_begin.this)
														.setTitle("游戏结束")
														.setPositiveButton(
																"确定",
																new DialogInterface.OnClickListener() {
																	@Override
																	public void onClick(
																			DialogInterface dialog,
																			int which) {

																		Game_begin.this
																				.finish();

																	}
																}).show();
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

	// 开启一个新的线程，产生1-13之间的随机数
	public class NumThread extends Thread {
		public void run() {
			// 产生1-13之间的四个随机数
			num1 = "" + (int) (Math.random() * 13 + 1);
			num2 = "" + (int) (Math.random() * 13 + 1);
			num3 = "" + (int) (Math.random() * 13 + 1);
			num4 = "" + (int) (Math.random() * 13 + 1);

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

}
