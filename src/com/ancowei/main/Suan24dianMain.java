package com.ancowei.main;

import java.util.ArrayList;

import com.ancowei.calculate.Calculate;
import com.example.suan24dian.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Suan24dianMain extends Activity {
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

	Button btn_last;
	Button btn_next;
	Button btn_exit;

	TextView text_result;
	
	EditText edit_calculate;
	
	public String num1;
	public String num2;
	public String num3;
	public String num4;
	
	public String calculate = "";
	
	private Handler myH;
	

	private btnOnClickListener btnOnclick;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_suan24dian_main);
		btn_1 = (Button) findViewById(R.id.btn_1);
		btn_2 = (Button) findViewById(R.id.btn_2);
		btn_3 = (Button) findViewById(R.id.btn_3);
		btn_4 = (Button) findViewById(R.id.btn_4);
		
		btn_plus=(Button)findViewById(R.id.btn_plus);
		btn_minus=(Button)findViewById(R.id.btn_minus);
		btn_cal=(Button)findViewById(R.id.btn_cal);
		btn_devide=(Button)findViewById(R.id.btn_devide);
		btn_left=(Button)findViewById(R.id.btn_left);
		btn_right=(Button)findViewById(R.id.btn_right);
		
		btn_back=(Button)findViewById(R.id.btn_back);
		btn_clear=(Button)findViewById(R.id.btn_clear);
		btn_commit=(Button)findViewById(R.id.btn_commit);
		
		btn_last = (Button) findViewById(R.id.btn_last);
		btn_next = (Button) findViewById(R.id.btn_next);
		btn_exit = (Button) findViewById(R.id.btn_exit);
		
		edit_calculate = (EditText) findViewById(R.id.edit_calculate);
		
		text_result=(TextView)findViewById(R.id.text_result);
		
		btnOnclick = new btnOnClickListener();
		
		btn_last.setOnClickListener(btnOnclick);
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
		
		// getRandom();
		myH = new myHandler();
		new NumThread().start();
	}

	public class btnOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_1:
				calculate = calculate + btn_1.getText();
				btn_1.setText("");
				edit_calculate.setText(calculate);
				break;
			case R.id.btn_2:
				calculate = calculate + btn_2.getText();
				btn_2.setText("");
				edit_calculate.setText(calculate);
				break;
			case R.id.btn_3:
				calculate = calculate + btn_3.getText();
				btn_3.setText("");
				edit_calculate.setText(calculate);
				break;
			case R.id.btn_4:
				calculate = calculate + btn_4.getText();
				btn_4.setText("");
				edit_calculate.setText(calculate);
				break;

			case R.id.btn_plus:
				calculate = calculate+btn_plus.getText();
				edit_calculate.setText(calculate);
				break;
			case R.id.btn_minus:
				calculate = calculate+btn_minus.getText();
				edit_calculate.setText(calculate);
				break;
			case R.id.btn_cal:
				calculate = calculate+btn_cal.getText();
				edit_calculate.setText(calculate);
				break;
			case R.id.btn_devide:
				calculate = calculate+ btn_devide.getText();
				edit_calculate.setText(calculate);
				break;
			case R.id.btn_left:
				calculate = calculate+ btn_left.getText();
				edit_calculate.setText(calculate);
				break;
			case R.id.btn_right:
				calculate = calculate+ btn_right.getText();
				edit_calculate.setText(calculate);
				break;
			case R.id.btn_back:
				
				break;
			case R.id.btn_clear:
				edit_calculate.setText("");
				calculate="";
				btn_1.setText(num1);
				btn_2.setText(num2);
				btn_3.setText(num3);
				btn_4.setText(num4);
				break;
			case R.id.btn_commit:
				//调用处理表达式是否正确，运算结果是否为24的函数
				String res=ifResult(calculate);;
				text_result.setText(res);
				
				break;
			case R.id.btn_last:
				calculate="";
				edit_calculate.setText(calculate);
				new NumThread().start();
				// getRandom();
				break;
			case R.id.btn_next:
				// getRandom();
				calculate="";
				edit_calculate.setText(calculate);
				new NumThread().start();
				break;
			case R.id.btn_exit:
				Suan24dianMain.this.finish();
				break;
			}
		}

	}
	
	//判断输入表达式是否正确，结果是否为24的函数
	public String ifResult(String str_calculate){
		String res="结果有待处理。。。";
		ArrayList postfix;
		Calculate cal = new Calculate();
		postfix=cal.transform(str_calculate);
		boolean resB=cal.calculate(postfix);
		if(resB){
			res="结果正确";
		}else{
			res="结果错误，请重新计算";
		}
		
		return res;
	}

	public class myHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle numBundle = msg.getData();
			btn_1.setText(numBundle.getString("num1"));
			btn_2.setText(numBundle.getString("num2"));
			btn_3.setText(numBundle.getString("num3"));
			btn_4.setText(numBundle.getString("num4"));
		}
	}

	public void getRandom() {
		// 产生1-13之间的四个随机数
		String num1 = "" + (int) (Math.random() * 13 + 1);
		String num2 = "" + (int) (Math.random() * 13 + 1);
		String num3 = "" + (int) (Math.random() * 13 + 1);
		String num4 = "" + (int) (Math.random() * 13 + 1);
		System.out.println(num1);
		System.out.println(num4);
		btn_1.setText(num1);
		btn_2.setText(num2);
		btn_3.setText(num3);
		btn_4.setText(num4);
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
			msg.setData(numBundle);
			myH.sendMessage(msg);
		}
	}

}
