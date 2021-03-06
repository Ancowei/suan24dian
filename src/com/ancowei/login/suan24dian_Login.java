package com.ancowei.login;

import java.io.File;

import com.ancowei.main.Suan24dianMain;
import com.ancowei.setting.Tools;
import com.example.suan24dian.R;
import ExitApp.ExitApp;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
	private RelativeLayout switchAvatar;
	private ImageView faceImage;

	private EditText edit_name;
	private EditText edit_password;

	private SharedPreferences sp;

	private static String user_Name;
	private static String user_Password;
	private static Intent user_image_intent;

	private btn_OnClickListener btn_onclick;
	public static int LOGIN_RESULT_CODE = 3;

	// private String[] items = new String[] { "选择本地图片", "拍照" };
	private String[] items = new String[] { "选择本地图片" };
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
		set_Default_User();
	}

	public void findView() {
		btn_cancle = (Button) findViewById(R.id.btn_cancle);
		btn_ok = (Button) findViewById(R.id.btn_ok);
		edit_password = (EditText) findViewById(R.id.edit_password);
		edit_name = (EditText) findViewById(R.id.edit_name);
		switchAvatar = (RelativeLayout) findViewById(R.id.switch_face_rl);
		faceImage = (ImageView) findViewById(R.id.faceImage);
		user_image_intent = new Intent();
		btn_onclick = new btn_OnClickListener();
		sp = this.getSharedPreferences("user_msg", Context.MODE_PRIVATE);
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
						/*
						 * case 1: Intent intentFromCapture = new Intent(
						 * MediaStore.ACTION_IMAGE_CAPTURE); //
						 * 判断存储卡是否可以用，可用进行存储 if (Tools.hasSdcard()) {
						 * intentFromCapture.putExtra( MediaStore.EXTRA_OUTPUT,
						 * Uri.fromFile(new File(Environment
						 * .getExternalStorageDirectory(), IMAGE_FILE_NAME))); }
						 * 
						 * startActivityForResult(intentFromCapture,
						 * CAMERA_REQUEST_CODE); break;
						 */
						default:
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
				user_image_intent = data;
				//getImageToView(data);
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
		intent.putExtra("outputX", 100);
		intent.putExtra("outputY", 100);
		intent.putExtra("return-data", true);
		try {
			Uri user_image = Uri.fromFile(new File(Environment
					.getExternalStorageDirectory(), "user_image.jpg"));
			intent.putExtra(MediaStore.EXTRA_OUTPUT, user_image);
		} catch (Exception e) {

		}
		startActivityForResult(intent, RESULT_REQUEST_CODE);
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	/*private void getImageToView(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			Drawable drawable = new BitmapDrawable(photo);
			faceImage.setImageDrawable(drawable);
		}
	}*/

	public void set_Default_User() {
		edit_name.setText(sp.getString("user_name", ""));
		edit_password.setText(sp.getString("user_password", ""));
	}

	public class btn_OnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_cancle:
				suan24dian_Login.this.finish();
				break;
			case R.id.btn_ok:
				user_Name = edit_name.getText().toString();
				user_Password = edit_password.getText().toString();

				if (user_Name.length() == 0 || user_Password.length() == 0) {
					new AlertDialog.Builder(suan24dian_Login.this)
							.setTitle("用户名和密码都不能为空!")
							.setPositiveButton("确定", null).show();
				} else {
					Suan24dianMain.ifLogin = true;
					setResult(LOGIN_RESULT_CODE, user_image_intent);
					// SharedPreferences
					sp.edit().putString("user_name", user_Name)
							.putString("user_password", user_Password).commit();
					suan24dian_Login.this.finish();
				}
				break;
			}
		}
	}

	public Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		// 根据原来图片大小画一个矩形
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		// 圆角弧度参数,数值越大圆角越大,甚至可以画圆形
		final float roundPx = 15;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		// 画出一个圆角的矩形
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		// 取两层绘制交集,显示上层
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		// 显示图片
		canvas.drawBitmap(bitmap, rect, rect, paint);
		// 返回Bitmap对象
		return output;
	}

	@Override
	protected void onResume() {
		super.onResume();
		Bitmap bm = BitmapFactory.decodeFile(Environment
				.getExternalStorageDirectory() + "/user_image.jpg");
		Bitmap b = getRoundedCornerBitmap(bm);
		faceImage.setImageBitmap(b);

	}
}
