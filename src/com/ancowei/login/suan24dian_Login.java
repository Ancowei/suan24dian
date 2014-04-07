package com.ancowei.login;

import java.io.File;

import com.ancowei.db.SqlHandler;
import com.ancowei.main.Suan24dianMain;
import com.ancowei.setting.Tools;

import com.example.suan24dian.R;

import ExitApp.ExitApp;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcel;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class suan24dian_Login extends Activity {
	private Button btn_cancle;
	private Button btn_ok;

	private EditText edit_name;
	private EditText edit_password;

	public static String user_Name;
	public static String user_Password;
	public static Intent user_image;

	private btn_OnClickListener btn_onclick;
	public static int LOGIN_RESULT_CODE = 3;

	/* 组件 */
	private RelativeLayout switchAvatar;
	private ImageView faceImage;
	private String[] items = new String[] { "选择本地图片", "拍照" };
	/* 头像名称 */
	private static final String IMAGE_FILE_NAME = "faceImage.jpg";

	/* 请求码 */
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 退出程序
		ExitApp.getInstance().addActivity(suan24dian_Login.this);
		// 无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.suan24dian_login);
		findView();
		registerListeners();

		/*
		 * sqlHelper = new SqlHandler(suan24dian_Login.this,
		 * SqlHandler.DATABASE_NAME, null, SqlHandler.DATABASE_VERSION);
		 */

		set_Default_User();
	}

	public void findView() {
		btn_cancle = (Button) findViewById(R.id.btn_cancle);
		btn_ok = (Button) findViewById(R.id.btn_ok);
		edit_password = (EditText) findViewById(R.id.edit_password);
		edit_name = (EditText) findViewById(R.id.edit_name);
		switchAvatar = (RelativeLayout) findViewById(R.id.switch_face_rl);
		faceImage = (ImageView) findViewById(R.id.face);
		user_image = new Intent();
		btn_onclick = new btn_OnClickListener();

	}

	public void registerListeners() {
		edit_name.setFocusable(true);
		edit_password.setFocusable(true);
		btn_cancle.setOnClickListener(btn_onclick);
		btn_ok.setOnClickListener(btn_onclick);
		switchAvatar.setOnClickListener(listener);
	}

	private View.OnClickListener listener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			showDialog();
		}
	};

	/**
	 * 显示选择对话框
	 */
	private void showDialog() {

		new AlertDialog.Builder(this)
				.setTitle("设置头像")
				.setItems(items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							Intent intentFromGallery = new Intent();
							intentFromGallery.setType("image/*"); // 设置文件类型
							intentFromGallery
									.setAction(Intent.ACTION_GET_CONTENT);
							startActivityForResult(intentFromGallery,
									IMAGE_REQUEST_CODE);
							break;
						case 1:

							Intent intentFromCapture = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							// 判断存储卡是否可以用，可用进行存储
							if (Tools.hasSdcard()) {

								intentFromCapture.putExtra(
										MediaStore.EXTRA_OUTPUT,
										Uri.fromFile(new File(Environment
												.getExternalStorageDirectory(),
												IMAGE_FILE_NAME)));
							}

							startActivityForResult(intentFromCapture,
									CAMERA_REQUEST_CODE);
							break;
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case IMAGE_REQUEST_CODE:
			startPhotoZoom(data.getData());
			break;
		case CAMERA_REQUEST_CODE:
			if (Tools.hasSdcard()) {
				File tempFile = new File(
						Environment.getExternalStorageDirectory()
								+ IMAGE_FILE_NAME);
				startPhotoZoom(Uri.fromFile(tempFile));
			} else {
				Toast.makeText(suan24dian_Login.this, "未找到存储卡，无法存储照片！",
						Toast.LENGTH_LONG).show();
			}

			break;
		case RESULT_REQUEST_CODE:
			if (data != null) {
				user_image = data;
				getImageToView(data);
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 320);
		intent.putExtra("outputY", 320);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 2);
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void getImageToView(Intent data) {
		Bundle extras = data.getExtras();
		/*Uri u=data.getData();
		if(u!=null){
			Parcel  p=Parcel.obtain();
			u.writeToParcel(p, 0);
			Bundle b= p.readBundle();
			Bitmap photo = b.getParcelable("data");
			Drawable drawable = new BitmapDrawable(photo);
			faceImage.setImageDrawable(drawable);
		}*/
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			Drawable drawable = new BitmapDrawable(photo);
			faceImage.setImageDrawable(drawable);
		}
	}

	public void set_Default_User() {
		Cursor c = com.ancowei.main.Suan24dianMain.sqlHelper.select();
		try {
			c.moveToFirst();
			user_Name = c.getString(c.getColumnIndex(SqlHandler.USER_NAME));
			user_Password = c.getString(c
					.getColumnIndex(SqlHandler.USER_PASSWORD));
			edit_name.setText(user_Name);
			edit_password.setText(user_Password);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public class btn_OnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_cancle:
				suan24dian_Login.this.finish();
				break;
			// 点击确定按钮之后，把用户数据送往本地数据库，如果是第一次登录，则同时把登录数据发送往服务器进行注册
			case R.id.btn_ok:
				user_Name = edit_name.getText().toString();
				user_Password = edit_password.getText().toString();

				if (user_Name.length() == 0 || user_Password.length() == 0) {
					new AlertDialog.Builder(suan24dian_Login.this)
							.setTitle("用户名和密码都不能为空!")
							.setPositiveButton("确定", null).show();
				} else {
					user_image.putExtra("user_name", user_Name);
					user_image.putExtra("user_password", user_Password);
					setResult(LOGIN_RESULT_CODE, user_image);
					
					Suan24dianMain.sqlHelper.insert(user_Name, user_Password);
					Suan24dianMain.sqlHelper.update(user_Name);

					Toast.makeText(suan24dian_Login.this,
							"登录信息\n" + "昵称：" + user_Name + "\n密码：*** ",
							Toast.LENGTH_LONG).show();
					Suan24dianMain.ifLogin = true;
					suan24dian_Login.this.finish();
				}
				break;

			}

		}

	}
}
