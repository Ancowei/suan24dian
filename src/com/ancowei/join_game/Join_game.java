package com.ancowei.join_game;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import com.example.suan24dian.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Join_game extends Activity {
	Button btn_join_game;
	btnOnClickListener btn_onclick;
	String s = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.suan24dian_join_game);
		btn_join_game = (Button) findViewById(R.id.btn_join_game);
		btn_onclick = new btnOnClickListener();
		btn_join_game.setOnClickListener(btn_onclick);

	}

	public class btnOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_join_game:
				new join_gameThread().start();
				btn_join_game.setText(s);
				break;
			}
		}
	}

	public class join_gameThread extends Thread {
		public void run() {
         //  while(true){
			try {
				//现在可以接收到本应用程序发送的广播，不能接收java测试程序发送的广播
				
				System.out.println("2、响应广播");
				
				DatagramSocket socket = new DatagramSocket(3000);
				byte buf[] = new byte[256];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				
				//System.out.println("receive from send_broadcast " + s);
				
				ByteArrayInputStream bin = new ByteArrayInputStream(
						packet.getData());
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(bin));
				s = reader.readLine();

				
				reader.close();
				bin.close();

				System.out.println("2、receive from send_broadcast " + s);

				if (s.equals("suan24dian")) {

					System.out.println("is suan24dian ,respond it");

					String s1 = "suan24dian_respond";
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					PrintStream pout = new PrintStream(out);
					pout.println(s1);
					byte buf1[] = out.toByteArray();

					DatagramSocket Rsocket = new DatagramSocket();

					DatagramPacket Rpacket = new DatagramPacket(buf1,
							buf1.length, packet.getAddress(),3001);
					Rsocket.send(Rpacket);

					System.out.println("2、address: " + packet.getAddress()
							+ " port" + packet.getPort());

					System.out.println("2 "+s);
				}

			} catch (Exception e) {
				s = s + e.toString();
				e.printStackTrace();
			} finally {
				// System.out.println(s);
			}

		}}
		

	//}

}
