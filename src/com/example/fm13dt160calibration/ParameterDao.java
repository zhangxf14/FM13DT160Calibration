package com.example.fm13dt160calibration;

import java.util.ArrayList;
import java.util.Date;




import com.example.fm13dt160calibration.db.DBManager;
import com.example.fm13dt160calibration.utils.Constants;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
/**
 * 对应标签的温度集合操�?
 * @author Sai
 *
 */
public class ParameterDao {
	private static ParameterDao instance;
	private Context context;
	private static String table = Constants.TB_PARA_CAL;

	public ParameterDao(Context context) {
		this.context = context;
	}

	public static ParameterDao getInstance(Context context) {
		if (instance == null) {
			instance = new ParameterDao(context);
		}
		return instance;
	}
	public ContentValues objectToValue(Parameter data){
		ContentValues values=new ContentValues();
		values.put(Constants.UID_OUTPUT, data.getUid());
		values.put(Constants.DEVICETP, data.getDeviceTp());
		values.put(Constants.U_CODE, data.getUcode());
		return values;
	}
	public Parameter cursorToValue(Cursor mCursor){
		Parameter data = new Parameter();
		data.setUid(mCursor.getString(mCursor.getColumnIndex(Constants.UID_OUTPUT)));
		data.setDeviceTp(mCursor.getDouble(mCursor.getColumnIndex(Constants.DEVICETP)));
		data.setUcode(mCursor.getString(mCursor.getColumnIndex(Constants.U_CODE)));
		
		return data;
	}
	public int insert(Parameter data) {
		String selection=Constants.UID_OUTPUT+"=? and "+ Constants.DEVICETP+"=?";
		String[] selectionArgs={data.getUid(),String.valueOf(data.getDeviceTp())};
	    Cursor mCursor=DBManager.getInstance(context).query(table, null, selection, selectionArgs,null,null,null);
	    
	    while (mCursor.moveToNext()) {
	    	mCursor.close();
	    	return DBManager.getInstance(context).update(table, objectToValue(data), selection, selectionArgs);
		}
		mCursor.close();
		ArrayList<ContentValues> values=new ArrayList<ContentValues>();
		values.add(objectToValue(data));
		return DBManager.getInstance(context).insert(table, values);
	}
	
	public int delete(String uid) {		
	   //先删除以前的数据
		String selection=Constants.UID_OUTPUT+"=?";
		String[] selectionArgs={uid};
		return DBManager.getInstance(context).delete(table, selection, selectionArgs);	
	}
	
	public ArrayList<Parameter> queryAll(String uid){
		ArrayList<Parameter> mDatas=new ArrayList<Parameter>();
		String selection=Constants.UID_OUTPUT+"=?";
		String[] selectionArgs={uid};
		Cursor mCursor = DBManager.getInstance(context).query(table, null, selection,
				selectionArgs, null, null, Constants.DEVICETP+" ASC");
		while (mCursor.moveToNext()) {
			Parameter data = cursorToValue(mCursor);
			mDatas.add(data);
		}
		mCursor.close();
		DBManager.getInstance(context).closeDatabase();
		return mDatas;
	}

}
