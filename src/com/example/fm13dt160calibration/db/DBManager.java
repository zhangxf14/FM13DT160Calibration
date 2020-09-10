package com.example.fm13dt160calibration.db;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author Sai E-mail:xuvirtual@163.com 
 * Created on 2014�?7�?23�? 下午4:28:54
 * 类说明：数据库管理类
 */
public class DBManager {
	private AtomicInteger mOpenCounter = new AtomicInteger();// 控制并发

	private static DBManager instance;
	private static DBHelper mDatabaseHelper;
	private SQLiteDatabase mDatabase;

	public static synchronized void initializeInstance(DBHelper helper) {
		if (instance == null) {
			instance = new DBManager();
			mDatabaseHelper = helper;
		}
	}

	public static synchronized DBManager getInstance(Context context) {
		if (instance == null) {
			initializeInstance(new DBHelper(context));
		}

		return instance;
	}

	public synchronized SQLiteDatabase openDatabase() {
		if (mOpenCounter.incrementAndGet() == 1) {
			mDatabase = mDatabaseHelper.getWritableDatabase();
		}
		return mDatabase;
	}

	public synchronized void closeDatabase() {
		if (mOpenCounter.decrementAndGet() == 0) {
			mDatabase.close();
		}
	}

	/**
	 * 增加记录
	 * 
	 * @param table
	 * @param values
	 * @return
	 */
	public Long insert(String table, ContentValues values) {
		SQLiteDatabase db = openDatabase();

		db.beginTransaction();
		try {
			db.insert(table, null, values);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			closeDatabase();
		}
		return 1L;
	}
	/**
	 * 增加记录
	 * 
	 * @param table
	 * @param values
	 * @return
	 */
	public int insert(String table, ArrayList<ContentValues> values) {
		SQLiteDatabase db = openDatabase();
		
		db.beginTransaction();
		try {
			for(ContentValues value:values)
			    db.insert(table, null, value);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			closeDatabase();
		}
		return values.size();
	}

	/**
	 * 删除记录
	 * 
	 * @param table
	 * @param selection
	 * @param selectionArgs
	 * @return
	 */
	public int delete(String table, String selection, String[] selectionArgs) {
		SQLiteDatabase db = openDatabase();
		int count;
		db.beginTransaction();
		try {
			count = db.delete(table, selection, selectionArgs);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			closeDatabase();
		}
		return count;
	}

	/**
	 * 更新记录
	 * 
	 * @param table
	 * @param values
	 * @param selection
	 * @param selectionArgs
	 * @return
	 */
	public int update(String table, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = openDatabase();
		int count;
		db.beginTransaction();
		try {
			count = db.update(table, values, selection, selectionArgs);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			closeDatabase();
		}
		return count;
	}
	/**
	 * 查询
	 * @param table
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @return
	 */
	public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy){
		SQLiteDatabase db=openDatabase();
		Cursor cursor=db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
		return cursor;
	}
	/**
	 * 查询
	 * @param table
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @return
	 */
	public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy,String limit){
		SQLiteDatabase db=openDatabase();
		Cursor cursor=db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy,limit);
		return cursor;
	}
	public Cursor rawQuery(String sql, String[] selectionArgs){
		SQLiteDatabase db=openDatabase();
		Cursor cursor=db.rawQuery(sql, selectionArgs);
		return cursor;
	}


}
