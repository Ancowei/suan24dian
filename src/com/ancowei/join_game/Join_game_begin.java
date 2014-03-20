package com.ancowei.join_game;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

import com.ancowei.calculate.Calculate;
import com.ancowei.initiate_game.Game_begin;
import com.ancowei.initiate_game.Game_begin.NumThread;
import com.ancowei.main.Suan24dianMain;
import com.ancowei.welcome.Suan24dian_welcome;
import com.example.suan24dian.R;

import ExitApp.ExitApp;
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
import android.widget.Toast;

public class Join_game_begin extends Activity {

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
	static final int GAME_BEGIN = 0;
	static final int NEXT = 1;
	public static int questionNum = 10;

	// 玩家计算正确的题数
	public int correctNum = 0;

	static Handler myH;

	static UDPLink_after_TCPLINK UDP_listener;

	private btnOnClickListener btnOnclick;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 退出程序
				ExitApp.getInstance().addActivity(Join_game_begin.this);
		//无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_suan24dian_initiate_play);
		// 全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
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

		//text_countdown = (TextView) findViewById(R.id.text_countdown);
		text_countdown_show = (TextView) findViewById(R.id.text_countdown_show);
		text_result = (TextView) findViewById(R.id.text_result);
		text_time = (TextView) findViewById(R.id.text_time);
		text_time.setTextSize(20);
		text_time.setTextColor(Color.RED);

		btnOnclick = new btnOnClickListener();

		btn_next.setClickable(false);
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

		//text_countdown.setText("剩余题数：");

		myH = new myHandler();
		UDP_listener = new UDPLink_after_TCPLINK();
		UDP_listener.start();
		initData();

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
				btn_2.setText(num2);
				btn_3.setText(num3);
				btn_4.setText(num4);
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
			case R.id.btn_exit:
				// timeThread.interrupt();
				Intent intent = new Intent(Join_game_begin.this,
						Suan24dian_welcome.class);
				Join_game_begin.this.finish();
				Join_game_begin.this.startActivity(intent);
				break;
			}
		}
	}

	// 显示初始化
	public void initData() {
		questionNum = 10;
		//text_countdown_show.setText("" + questionNum);
	}

	// 回退时候，如果是数字，恢复按钮数字
	public void recoverNum(int id) {
		switch (id) {
		case R.id.btn_1:
			btn_1.setText(num1);
			break;
		case R.id.btn_2:
			btn_2.setText(num2);
			break;
		case R.id.btn_3:
			btn_3.setText(num3);
			break;
		case R.id.btn_4:
			btn_4.setText(num4);
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

		} else {
			res = "结果错误，请重新计算";
		}
		return res;
	}

	public class myHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle numBundle = msg.getData();
			switch (msg.what) {
			case GAME_BEGIN:
				num1 = numBundle.getString("num1");
				num2 = numBundle.getString("num2");
				num3 = numBundle.getString("num3");
				num4 = numBundle.getString("num4");
				btn_1.setText(num1);
				btn_2.setText(num2);
				btn_3.setText(num3);
				btn_4.setText(num4);
				Toast.makeText(Join_game_begin.this, "" + num1,
						Toast.LENGTH_LONG).show();
				break;
			case NEXT:
				num1 = numBundle.getString("num1");
				num2 = numBundle.getString("num2");
				num3 = numBundle.getString("num3");
				num4 = numBundle.getString("num4");
				btn_1.setText(num1);
				btn_2.setText(num2);
				btn_3.setText(num3);
				btn_4.setText(num4);
				questionNum = questionNum - 1;
				text_countdown_show.setText("" + questionNum);
				i = 0;
				count = 0;
				preNum = "";
				preIfnum = false;
				calculate = "";
				edit_calculate.setText(calculate);
			}
		}
	}

	// UDP
	// TCP链接之后,进行UDP侦听,接收发牌信息和命令
	public class UDPLink_after_TCPLINK extends Thread {
		public void run() {
			while (true) {
				try {
					byte buf[] = new byte[256];
					DatagramSocket UDPSocket = new DatagramSocket(3000);
					DatagramPacket UDPPacket = new DatagramPacket(buf,
							buf.length);
					UDPSocket.receive(UDPPacket);
					ByteArrayInputStream bin = new ByteArrayInputStream(
							UDPPacket.getData());
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(bin));
					String nums[] = new String[4];
					if (reader.readLine().equals("game_begin")) {
						for (int j = 0; j < 3; j++) {
							nums[j] = reader.readLine();
						}
						Bundle numBundle = new Bundle();
						numBundle.putString("num1", nums[0]);
						numBundle.putString("num2", nums[1]);
						numBundle.putString("num3", nums[2]);
						numBundle.putString("num4", nums[3]);
						Message msg = myH.obtainMessage();
						msg.what = GAME_BEGIN;
						msg.setData(numBundle);
						myH.sendMessage(msg);
						// 有人作对了或者时间到了,进入下一题
					} else if (reader.readLine().equals("next")) {
						for (int j = 0; j < 3; j++) {
							nums[j] = reader.readLine();
						}
						Bundle numBundle = new Bundle();
						numBundle.putString("num1", nums[0]);
						numBundle.putString("num2", nums[1]);
						numBundle.putString("num3", nums[2]);
						numBundle.putString("num4", nums[3]);
						Message msg = myH.obtainMessage();
						msg.what = NEXT;
						msg.setData(numBundle);
						myH.sendMessage(msg);
					}
				} catch (Exception e) {
				}
			}
		}
	}

}
