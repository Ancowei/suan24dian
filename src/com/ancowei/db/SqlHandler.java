package com.ancowei.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SqlHandler extends SQLiteOpenHelper {
	public final static String DATABASE_NAME = "suan24dian_local.db";
	public final static int DATABASE_VERSION = 1;
	private final static String TABLE_NAME = "suan24dian_table";
	public final static String USER_ID = "user_id";
	public final static String USER_NAME = "user_name";
	public final static String USER_PASSWORD = "user_password";
	public final static String USER_HIGHEST="user_highest";

	public SqlHandler(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	// 创建table
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE " + TABLE_NAME + " (" + USER_ID
				+ " INTEGER primary key autoincrement, " + USER_NAME
				+ " varchar(15), " + USER_PASSWORD + " varchar(15), "+USER_HIGHEST+" INTEGER)";
		db.execSQL(sql);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
		db.execSQL(sql);
		onCreate(db);
	}
	//检索数据库
	public Cursor select() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db
				.query(TABLE_NAME, null, null, null, null, null, null);
		return cursor;
	}

	// 增加操作
	public boolean insert(String user_name, String user_password) {
		SQLiteDatabase db = this.getWritableDatabase();
		boolean b=false;
		
		Cursor c=this.select();
		for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
			if(user_name.equals(c.getString(c.getColumnIndex(USER_NAME)))){
				b=true;
				break;
			}
		}
		if(b){
			return false;
		}else{
			/* ContentValues */
			ContentValues cv = new ContentValues();
			cv.put(USER_NAME, user_name);
			cv.put(USER_PASSWORD, user_password);
			cv.put(USER_HIGHEST, 0);
			db.insert(TABLE_NAME, null, cv);
			return true;
			}
	}
	// 删除操作
	public void delete(String user_name) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = USER_NAME + " = ?";
		String[] whereValue = { user_name };
		db.delete(TABLE_NAME, where, whereValue);
	}

	// 更新操作，在这个应用中，用户名和密码都不需要改变，只需要修改最高记录
	public void update( String user_name,int user_highest) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = USER_NAME + " = ?";
		String[] whereValue = {user_name };
		ContentValues cv = new ContentValues();
		cv.put(USER_HIGHEST,user_highest);
		
		db.update(TABLE_NAME, cv, where, whereValue);
	}
}