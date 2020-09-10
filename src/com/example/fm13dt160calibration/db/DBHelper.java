package com.example.fm13dt160calibration.db;


import com.example.fm13dt160calibration.utils.Constants;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

/**
 *@author Sai
 *Created on 2014�?9�?10�? 下午21:53:39
 *类说明：数据�?
 */
public class DBHelper extends SQLiteOpenHelper{

	public DBHelper(Context context) {
		this(context, Constants.DBNAME, null, Constants.VERSION);
	}
	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL("CREATE TABLE "+Constants.TB_PARA_CAL+"(" +
				Constants.UID_OUTPUT+" varchar(20)," +
				Constants.DEVICETP+" Double," +
				Constants.U_CODE+" varchar(20)" +
				");");
		
		db.execSQL("CREATE TABLE "+Constants.TB_PARA_RAW+"(" +
				Constants.UID_OUTPUT+" varchar(20)," +
				Constants.ACODE+" varchar(20)," +
				Constants.BCODE+" varchar(20)" +
				");");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
