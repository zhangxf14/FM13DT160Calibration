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
public class ParameterRawDao {
	private static ParameterRawDao instance;
	private Context context;
	private static String table = Constants.TB_PARA_RAW;

	public ParameterRawDao(Context context) {
		this.context = context;
	}

	public static ParameterRawDao getInstance(Context context) {
		if (instance == null) {
			instance = new ParameterRawDao(context);
		}
		return instance;
	}
	public ContentValues objectToValue(ParameterRaw data){
		ContentValues values=new ContentValues();
		values.put(Constants.UID_OUTPUT, data.getUid());
		values.put(Constants.ACODE, data.getAcode());
		values.put(Constants.BCODE, data.getBcode());
		return values;
	}
	public ParameterRaw cursorToValue(Cursor mCursor){
		ParameterRaw data = new ParameterRaw();
		data.setUid(mCursor.getString(mCursor.getColumnIndex(Constants.UID_OUTPUT)));
		data.setAcode(mCursor.getString(mCursor.getColumnIndex(Constants.ACODE)));
		data.setBcode(mCursor.getString(mCursor.getColumnIndex(Constants.BCODE)));
		
		return data;
	}
	public int insert(ParameterRaw data) {
		ArrayList<ContentValues> values=new ArrayList<ContentValues>();
		values.add(objectToValue(data));
		return DBManager.getInstance(context).insert(table, values);
	}
	
	public void delete(String uid) {		
	   //先删除以前的数据
		String selection=Constants.UID_OUTPUT+"=?";
		String[] selectionArgs={uid};
		DBManager.getInstance(context).delete(table, selection, selectionArgs);	
		
	}
	
	public ArrayList<ParameterRaw> queryAll(String uid){
		ArrayList<ParameterRaw> mDatas=new ArrayList<ParameterRaw>();
		String selection=Constants.UID_OUTPUT+"=?";
		String[] selectionArgs={uid};
		Cursor mCursor = DBManager.getInstance(context).query(table, null, selection,
				selectionArgs, null, null, Constants.DEVICETP+" ASC");
		while (mCursor.moveToNext()) {
			ParameterRaw data = cursorToValue(mCursor);
			mDatas.add(data);
		}
		mCursor.close();
		DBManager.getInstance(context).closeDatabase();
		return mDatas;
	}

}
