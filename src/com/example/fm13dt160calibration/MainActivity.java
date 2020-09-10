package com.example.fm13dt160calibration;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

import com.example.fm13dt160calibration.chip.AS39513;
import com.example.fm13dt160calibration.chip.FM13DT160;
import com.example.fm13dt160calibration.chip.FM13DT160_NfcA;
import com.example.fm13dt160calibration.utils.DataUtil;
import com.example.fm13dt160calibration.utils.PreferenceUtils;
import com.example.fm13dt160calibration.utils.SoundUtil;
import com.example.fm13dt160calibration.utils.TimeUtil;
import com.example.fm13dt160calibration.utils.Utility;

import android.support.v4.app.Fragment;
import android.R.integer;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.os.Build;

@SuppressLint("DefaultLocale") public class MainActivity extends BaseAct {
	private NfcV nfcv;
	private NfcA nfca;
	ArrayList<String> list = new ArrayList<String>();
	
	private EditText etUID,etDeviceTp,etUCode,etA,etB,etA_raw,etB_raw,etDelay,etInterval,etCount;
	private Button btnReadLocalPara,btnCalculation,btnReadChipPara,btnWritePara,btnReWork,
				   btnReadTp,btnRawMode,btnNormalMode,btnOneKey,btnStart;
    private ToggleButton toggleBtn;
    private boolean isReadTp=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        init(false, true);
		initEvents();
		SoundUtil.initSoundPool(this);
		loadLocalParas();
    }
    /**
     * 加载本地配置参数
     */
    private void loadLocalParas() {
		// TODO Auto-generated method stub
      String devicesString = PreferenceUtils.getPrefString(getApplicationContext(), "DeviceTp", "35");	
	  String delayString=PreferenceUtils.getPrefString(getApplicationContext(), "Delay", "2");
	  String intervalString=PreferenceUtils.getPrefString(getApplicationContext(), "Interval", "60");
	  String countString=PreferenceUtils.getPrefString(getApplicationContext(), "Count", "10");
	  
	  etDeviceTp.setText(devicesString);
	  etDelay.setText(delayString);
	  etInterval.setText(intervalString);
	  etCount.setText(countString);
	  
	}

	/** 初始化视图 **/
	protected void initViews() {
		/**
		 * 在Android程序设计中，通常来说在Actionbar中在条目过多时会显示三个竖着的小点的菜单，但在实机测试的时候发现并不显示，
		 * 查找资料并测试之后发现问题所在：如果该机器拥有实体的menu键则不在右侧显示溢出菜单，而改为按menu来生成。这样就不利于统一的界面风格。
		 * 我们可以改变系统探测实体menu键的存在与否来改变这个的显示。
		 */
		try {
			ViewConfiguration mconfig = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(mconfig, false);
			}
		} catch (Exception ex) {
		}
		//etDeviceTp,etUCode,etA,etB,etA_raw,etB_raw;
		etUID =(EditText)findViewById(R.id.etUID);
		etDeviceTp =(EditText)findViewById(R.id.etDeviceTp);
		etUCode =(EditText)findViewById(R.id.etUCode);
		etA =(EditText)findViewById(R.id.etA);
		etB =(EditText)findViewById(R.id.etB);
		etA_raw =(EditText)findViewById(R.id.etA_raw);
		etB_raw =(EditText)findViewById(R.id.etB_raw);
		etDelay=(EditText)findViewById(R.id.etDelay);
		etInterval=(EditText)findViewById(R.id.etInterval);
		etCount=(EditText)findViewById(R.id.etCount);
		
		btnReadLocalPara = (Button) findViewById(R.id.btnReadLocalPara);
		btnCalculation = (Button) findViewById(R.id.btnCalculation);
		btnReWork=(Button) findViewById(R.id.btnReWork);
		btnReadChipPara= (Button) findViewById(R.id.btnReadChipPara);
		btnWritePara=(Button) findViewById(R.id.btnWritePara);
		toggleBtn = (ToggleButton) findViewById(R.id.toggleBtn);
		btnReadTp=(Button) findViewById(R.id.btnReadTp);
		btnRawMode=(Button) findViewById(R.id.btnRawMode);
		btnNormalMode=(Button) findViewById(R.id.btnNormalMode);
		btnOneKey=(Button) findViewById(R.id.btnOneKey);
		btnStart=(Button) findViewById(R.id.btnStart);
	}
	
	/** 初始化事件 **/
	protected void initEvents() {
		super.initEvents();
		btnReadLocalPara.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				readLocalPara();	
			}
		});
		btnReWork.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String uidString=etUID.getText().toString();
				
				if (uidString.equals("")) {
					Toast.makeText(MainActivity.this,"未扫描到标签！", Toast.LENGTH_SHORT).show();
					return ;
				}
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setIcon(android.R.drawable.ic_dialog_info);
				builder.setTitle("是否要重做？");
				builder.setMessage("点击是，将删除当前标签保存的数据？");
				builder.setCancelable(false);
				builder.setPositiveButton( "是", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						int del=ParameterDao.getInstance(getApplicationContext()).delete(uidString);
						if (del!=0) {
							Toast.makeText(MainActivity.this,"清除成功！", Toast.LENGTH_SHORT).show();
							etDeviceTp.setText("");
							etUCode.setText("");	
						}else {
							Toast.makeText(MainActivity.this,"未找到该标签的数据！", Toast.LENGTH_SHORT).show();
						}
						
					}
				});
				builder.setNegativeButton( "否", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
			           	
					}
				});
				builder.create().show();
				
			}
		});
		
		btnReadTp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				final String uidString=etUID.getText().toString();
				if (uidString.equals("")) {
					Toast.makeText(MainActivity.this,"未扫描到标签！", Toast.LENGTH_SHORT).show();
					return;
				}
				if (isReadTp) {
					readCode(uidString);
				}else {
					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
					builder.setIcon(android.R.drawable.ic_dialog_info);
					builder.setTitle("设备温度检查？");
					builder.setMessage("请在[保存Code]前，检查当前输入的设备温度与实际校准设备温度是否一致?");
					builder.setCancelable(false);
					builder.setPositiveButton( "是，本次不再提醒！", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							isReadTp=true;
							readCode(uidString);
						}

						
					});
					builder.setNegativeButton( "否", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Toast.makeText(MainActivity.this,"请重新输入设备温度，再次点击[保存Code]！", Toast.LENGTH_SHORT).show();
						}
					});
//					builder.setNeutralButton("是，本次不再提醒！", new DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							Toast.makeText(MainActivity.this,"请重新输入设备温度，再次[读Code]！", Toast.LENGTH_SHORT).show();
//						}
//					});
					builder.create().show();
				}
			}
		});
		
		btnCalculation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				calculation();
			}
		});
		btnReadChipPara.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				readChipPara();
			}
		});
		btnWritePara.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				writePara();
			}
		});
		
		btnRawMode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (list.contains("android.nfc.tech.NfcA")) {
					if (nfca==null) {
						Toast.makeText(MainActivity.this,"NFC未连接！", Toast.LENGTH_SHORT).show();
						return;
					}
					FM13DT160_NfcA fm13dt160=new FM13DT160_NfcA(nfca);
					int firstAddress=0xb040;
					
					try {
						if (nfca.isConnected()) {
							nfca.close();
						}
						nfca.connect();
						String str1="5da229d6";
						
					    int dataNum=3;
						byte[] data=Utility.HexString2Bytes(str1);
						
						fm13dt160.writeMemory(firstAddress, dataNum, data);
					    
						nfca.close();
					    Toast.makeText(MainActivity.this,"设置成功!", Toast.LENGTH_SHORT).show();
					    btnRawMode.setEnabled(false);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Toast.makeText(MainActivity.this,e.toString(), Toast.LENGTH_SHORT).show();
					}
					
				}else if (list.contains("android.nfc.tech.NfcV")){
					if (nfcv==null) {
						Toast.makeText(MainActivity.this,"NFC未连接！", Toast.LENGTH_SHORT).show();
						return;
					}
					FM13DT160 fm13dt160=new FM13DT160(nfcv);
					int firstAddress=0xb040;
					
					try {
						if (nfcv.isConnected()) {
							nfcv.close();
						}
						nfcv.connect();
						String str1="5da229d6";
						
					    int dataNum=3;
						byte[] data=Utility.HexString2Bytes(str1);
						
						fm13dt160.writeMemory(firstAddress, dataNum, data);
					    
					    nfcv.close();
					    Toast.makeText(MainActivity.this,"设置成功!", Toast.LENGTH_SHORT).show();
					    btnRawMode.setEnabled(false);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Toast.makeText(MainActivity.this,e.toString(), Toast.LENGTH_SHORT).show();
					}
				}else {
					Toast.makeText(MainActivity.this,"未检测到标签！", Toast.LENGTH_SHORT).show();
				}
				
			}
		});
		btnNormalMode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (list.contains("android.nfc.tech.NfcA")) {
					if (nfca==null) {
						Toast.makeText(MainActivity.this,"NFC未连接！", Toast.LENGTH_SHORT).show();
						return;
					}
					FM13DT160_NfcA fm13dt160=new FM13DT160_NfcA(nfca);
					int firstAddress=0xb040;
					
					try {
						if (nfca.isConnected()) {
							nfca.close();
						}
						nfca.connect();
						String str1="4cb329d6";
						
					    int dataNum=3;
						byte[] data=Utility.HexString2Bytes(str1);
						
						fm13dt160.writeMemory(firstAddress, dataNum, data);
					    
						nfca.close();
					    Toast.makeText(MainActivity.this,"恢复成功!", Toast.LENGTH_SHORT).show();
					    btnNormalMode.setEnabled(false);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Toast.makeText(MainActivity.this,e.toString(), Toast.LENGTH_SHORT).show();
					}
				}else if (list.contains("android.nfc.tech.NfcV")){
					if (nfcv==null) {
						Toast.makeText(MainActivity.this,"NFC未连接！", Toast.LENGTH_SHORT).show();
						return;
					}
					FM13DT160 fm13dt160=new FM13DT160(nfcv);
					int firstAddress=0xb040;
					
					try {
						if (nfcv.isConnected()) {
							nfcv.close();
						}
						nfcv.connect();
						String str1="4cb329d6";
						
					    int dataNum=3;
						byte[] data=Utility.HexString2Bytes(str1);
						
						fm13dt160.writeMemory(firstAddress, dataNum, data);
					    
					    nfcv.close();
					    Toast.makeText(MainActivity.this,"恢复成功!", Toast.LENGTH_SHORT).show();
					    btnNormalMode.setEnabled(false);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Toast.makeText(MainActivity.this,e.toString(), Toast.LENGTH_SHORT).show();
					}
				}else {
					Toast.makeText(MainActivity.this,"未检测到标签！", Toast.LENGTH_SHORT).show();
				}
				
			}
		});
		btnOneKey.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String uidString=etUID.getText().toString();
				if (uidString.equals("")) {
					Toast.makeText(MainActivity.this,"未扫描到标签！", Toast.LENGTH_SHORT).show();
					return ;
				}
				if (!readLocalPara()) {
					Toast.makeText(MainActivity.this,"读取本地参数时出现错误！", Toast.LENGTH_SHORT).show();
					return;
				}
				if (!calculation()) {
					Toast.makeText(MainActivity.this,"计算参数时出现错误！", Toast.LENGTH_SHORT).show();
					return;
				}
				if (!readChipPara()) {
					Toast.makeText(MainActivity.this,"读取芯片参数时出现错误！", Toast.LENGTH_SHORT).show();
					return;	
				}
				
				if (writePara()) {
					Toast.makeText(MainActivity.this,"一键校准OK！", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(MainActivity.this,"一键校准失败！", Toast.LENGTH_SHORT).show();
				}
				
			}
		});
		
		btnStart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String delaysString=etDelay.getText().toString();
				String intervalString=etInterval.getText().toString();
				String countString=etCount.getText().toString();
				
				if (delaysString.equals("")) {
					Toast.makeText(MainActivity.this,"延时参数不能为空！", Toast.LENGTH_SHORT).show();
					return;
				}
				if (intervalString.equals("")) {
					Toast.makeText(MainActivity.this,"间隔不能为空！", Toast.LENGTH_SHORT).show();
					return;
				}
				if (countString.equals("")) {
					Toast.makeText(MainActivity.this,"测温次数不能为空！", Toast.LENGTH_SHORT).show();
					return;
				}
				int delay= Integer.parseInt(delaysString);
				int interval= Integer.parseInt(intervalString);
				int cnt = Integer.parseInt(countString);
				if (delay<0) {
					Toast.makeText(MainActivity.this,"延时参数不能<0", Toast.LENGTH_SHORT).show();
					return;
				}
				if (interval<0) {
					Toast.makeText(MainActivity.this,"采集间隔不能<0", Toast.LENGTH_SHORT).show();
					return;
				}
				if (cnt<10) {
					Toast.makeText(MainActivity.this,"建议：测温次数≥10。", Toast.LENGTH_SHORT).show();
					return;
				}
				
				PreferenceUtils.setPrefString(getApplicationContext(), "Delay", delaysString);
				PreferenceUtils.setPrefString(getApplicationContext(), "Interval", intervalString);
				PreferenceUtils.setPrefString(getApplicationContext(), "Count", countString);
				
				if (list.contains("android.nfc.tech.NfcA")) {
					if (nfca==null) {
						Toast.makeText(MainActivity.this,"NFC未连接！", Toast.LENGTH_SHORT).show();
						return;
					}
					try {
						if (nfca.isConnected()) {
							nfca.close();
						}
						nfca.connect();
						FM13DT160_NfcA fm13dt160=new FM13DT160_NfcA(nfca);
						
						byte[] result=null;
						result=fm13dt160.getState((byte)0x01);
						try {
							Thread.sleep(400);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						String resString=Utility.Bytes2HexString(result);
						if (resString.contains("0131")||resString.contains("0431")) {					
							Toast.makeText(MainActivity.this, "当前标签处于RTC测温中...", Toast.LENGTH_SHORT).show();
							return;
						}
						result=fm13dt160.setWakeup();
//						fm13dt160.stopRTC();
						result=fm13dt160.getWakeup();			
						resString=Utility.Bytes2HexString(result);
						if (resString.contains("5555")) {
							fm13dt160.setDelay(delay);
//							fm13dt160.setDelayForiOS(delay);
							fm13dt160.setInterval((int)interval);
							byte[] count = new byte[]{0x00,0x00};
							count[1]=(byte) (cnt>>8 & 0xff);
							count[0]= (byte) (cnt&0xff);
							fm13dt160.setTimeMeasuredCount(count);
							byte[] min=Utility.intTo2Bytes2((int)2);
							byte[] max=Utility.intTo2Bytes2((int)8);
							fm13dt160.setMinTemperature(min);
							fm13dt160.setMaxTemperature(max);
							fm13dt160.setMaxMinTemperatureForiOS((byte) 8, (byte) 2);
							fm13dt160.setIntervalForiOS((int)interval);
//							byte[] minLimit0=Utility.intTo2Bytes2((int)2<<2);
//							byte[] maxLimit0=Utility.intTo2Bytes2((int)8<<2);
							
							int[] stime=TimeUtil.getTimeHex();
							byte[] startTime=new byte[]{(byte) stime[0],(byte) stime[1],(byte) stime[2],(byte) stime[3]};
							result=fm13dt160.setStartTime(startTime);
							
							result=fm13dt160.startRTC();
							try {
								Thread.sleep(800);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							result=fm13dt160.getState((byte)0x01);
							resString=Utility.Bytes2HexString(result);
							if (resString.contains("0131")||resString.contains("0431")) {					

								Toast.makeText(MainActivity.this, "启动成功！", Toast.LENGTH_SHORT).show();
								
							}else {					
								Toast.makeText(MainActivity.this, "启动失败", Toast.LENGTH_SHORT).show();
							}
						}else{
							Toast.makeText(MainActivity.this, "启动失败", Toast.LENGTH_SHORT).show();
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
//						Toast.makeText(ParametersSet.this, getString(R.string.start_fail), Toast.LENGTH_SHORT).show();
						Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
					}finally{
						try {
							nfca.close();							
						} catch (IOException e) {						
						}
					}
					
				}else if (list.contains("android.nfc.tech.NfcV")){
					if (nfcv==null) {
						Toast.makeText(MainActivity.this,"NFC未连接！", Toast.LENGTH_SHORT).show();
						return;
					}
					try {
						if (nfcv.isConnected()) {
							nfcv.close();
						}
						nfcv.connect();
						FM13DT160 fm13dt160=new FM13DT160(nfcv);
						
						byte[] result=null;
						result=fm13dt160.getState((byte)0x01);
						try {
							Thread.sleep(400);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						String resString=Utility.Bytes2HexString(result);
						if (resString.contains("0131")||resString.contains("0431")) {					
							Toast.makeText(MainActivity.this, "当前标签处于RTC测温中...", Toast.LENGTH_SHORT).show();
							return;
						}
						result=fm13dt160.setWakeup();
//						fm13dt160.stopRTC();
						result=fm13dt160.getWakeup();			
						resString=Utility.Bytes2HexString(result);
						if (resString.contains("5555")) {
							fm13dt160.setDelay(delay);
							fm13dt160.setDelayForiOS(delay);
							fm13dt160.setInterval((int)interval);
							byte[] count = new byte[]{0x00,0x00};
							count[1]=(byte) (cnt>>8 & 0xff);
							count[0]= (byte) (cnt&0xff);
							fm13dt160.setTimeMeasuredCount(count);
							byte[] min=Utility.intTo2Bytes2((int)2);
							byte[] max=Utility.intTo2Bytes2((int)8);
							fm13dt160.setMinTemperature(min);
							fm13dt160.setMaxTemperature(max);
							fm13dt160.setMaxMinTemperatureForiOS((byte) 8, (byte) 2);
							fm13dt160.setIntervalForiOS((int)interval);
//							byte[] minLimit0=Utility.intTo2Bytes2((int)2<<2);
//							byte[] maxLimit0=Utility.intTo2Bytes2((int)8<<2);
							
							int[] stime=TimeUtil.getTimeHex();
							byte[] startTime=new byte[]{(byte) stime[0],(byte) stime[1],(byte) stime[2],(byte) stime[3]};
							result=fm13dt160.setStartTime(startTime);
							
							result=fm13dt160.startRTC();
							try {
								Thread.sleep(800);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							result=fm13dt160.getState((byte)0x01);
							resString=Utility.Bytes2HexString(result);
							if (resString.contains("0131")||resString.contains("0431")) {					

								Toast.makeText(MainActivity.this, "启动成功！", Toast.LENGTH_SHORT).show();
								
							}else {					
								Toast.makeText(MainActivity.this, "启动失败", Toast.LENGTH_SHORT).show();
							}
						}else{
							Toast.makeText(MainActivity.this, "启动失败", Toast.LENGTH_SHORT).show();
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
//						Toast.makeText(ParametersSet.this, getString(R.string.start_fail), Toast.LENGTH_SHORT).show();
						Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
					}finally{
						try {
							nfcv.close();							
						} catch (IOException e) {						
						}
					}
				}else {
					Toast.makeText(MainActivity.this, "未扫描到标签！", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		toggleBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (toggleBtn.isChecked()) {
					btnReWork.setVisibility(View.VISIBLE);
					btnReadLocalPara.setVisibility(View.VISIBLE);
					btnCalculation.setVisibility(View.VISIBLE);
					btnReadChipPara.setVisibility(View.VISIBLE);
					btnWritePara.setVisibility(View.VISIBLE);
				}else{
					btnReWork.setVisibility(View.GONE);
					btnReadLocalPara.setVisibility(View.GONE);
					btnCalculation.setVisibility(View.GONE);
					btnReadChipPara.setVisibility(View.GONE);
					btnWritePara.setVisibility(View.GONE);
				}
			}
		});
		
	}
	/**
	 * 读取指定code
	 * @param uidString
	 */
	private void readCode(String uidString) {
		// TODO Auto-generated method stub
		String devicesString=etDeviceTp.getText().toString();
		if (devicesString.equals("")) {
			Toast.makeText(MainActivity.this,"设备温度不能为空！", Toast.LENGTH_SHORT).show();
			return;
		}
		if (devicesString.contains(";")||devicesString.contains(",")||devicesString.contains("℃")||devicesString.contains("；")) {
			Toast.makeText(MainActivity.this,"设备温度格式不正确！", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if (list.contains("android.nfc.tech.NfcA")) {
			if (nfca==null) {
				Toast.makeText(MainActivity.this,"NFC未连接！", Toast.LENGTH_SHORT).show();
				return;
			}
			FM13DT160_NfcA fm13dt160=new FM13DT160_NfcA(nfca);
			int firstAddress=0x1000;
			int numOfByte=0;
			try {
				if (nfca.isConnected()) {
					nfca.close();
				}
				nfca.connect();
				byte[]	result=fm13dt160.getTimeMeasuredCount();
	            int timeCount =((((int) result[1]) & 0xFF) | (((int) result[2]) & 0xFF) << 8) & 0xFFFF;
	            firstAddress = firstAddress+(timeCount-4)*2;//读取倒数指定的温度；
			    result=fm13dt160.readMemory(firstAddress, numOfByte);
			    String string = Utility.Bytes2HexString(result);
			    string = string.toUpperCase();
			    String string1 = string.substring(0, 2);
			    String string2 = string.substring(2, 4);
			    string=string2+string1;
			    int tmp=Integer.parseInt(string, 16);
			    tmp=tmp & 0x1FFF;
			    String hex=Integer.toHexString(tmp).toUpperCase();
			    hex = String.format("%4s",hex);
			    hex= hex.replaceAll(" ","0");
			    etUCode.setText(hex);
			    nfca.close();
			    Parameter parameter=new Parameter();
			    parameter.setUid(uidString);
			    
			    double deviceTp=Double.valueOf(devicesString);
			    PreferenceUtils.setPrefString(getApplicationContext(), "DeviceTp", devicesString);
				parameter.setDeviceTp(deviceTp);
			    String ucode=etUCode.getText().toString();
				parameter.setUcode(ucode);
				ParameterDao.getInstance(getApplicationContext()).insert(parameter);
				Toast.makeText(MainActivity.this,"保存成功！", Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(MainActivity.this,e.toString(), Toast.LENGTH_SHORT).show();
			}
			
		}else if (list.contains("android.nfc.tech.NfcV")){
			if (nfcv==null) {
				Toast.makeText(MainActivity.this,"NFC未连接！", Toast.LENGTH_SHORT).show();
				return;
			}
			FM13DT160 fm13dt160=new FM13DT160(nfcv);
			int firstAddress=0x1000;
			int numOfByte=0;
			try {
				if (nfcv.isConnected()) {
					nfcv.close();
				}
				nfcv.connect();
				byte[]	result=fm13dt160.getTimeMeasuredCount();
	            int timeCount =((((int) result[1]) & 0xFF) | (((int) result[2]) & 0xFF) << 8) & 0xFFFF;
	            firstAddress = firstAddress+(timeCount-4)*2;//读取倒数指定的温度；
			    result=fm13dt160.readMemory(firstAddress, numOfByte);
			    String string = Utility.Bytes2HexString(result);
			    string = string.toUpperCase();
			    String string1 = string.substring(2, 4);
			    String string2 = string.substring(4, 6);
			    string=string2+string1;
			    int tmp=Integer.parseInt(string, 16);
			    tmp=tmp & 0x1FFF;
			    String hex=Integer.toHexString(tmp).toUpperCase();
			    hex = String.format("%4s",hex);
			    hex= hex.replaceAll(" ","0");
			    etUCode.setText(hex);
			    nfcv.close();
			    Parameter parameter=new Parameter();
			    parameter.setUid(uidString);
			    
			    double deviceTp=Double.valueOf(devicesString);
			    PreferenceUtils.setPrefString(getApplicationContext(), "DeviceTp", devicesString);
				parameter.setDeviceTp(deviceTp);
			    String ucode=etUCode.getText().toString();
				parameter.setUcode(ucode);
				ParameterDao.getInstance(getApplicationContext()).insert(parameter);
				Toast.makeText(MainActivity.this,"保存成功！", Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(MainActivity.this,e.toString(), Toast.LENGTH_SHORT).show();
			}
		}else {
			Toast.makeText(MainActivity.this,"未检测到标签！", Toast.LENGTH_SHORT).show();
		}
	}
	/**
	 * 读取本地参数
	 */
	private boolean readLocalPara() {
		String uidString=etUID.getText().toString();
		if (uidString.equals("")) {
			Toast.makeText(MainActivity.this,"未扫描到标签！", Toast.LENGTH_SHORT).show();
			return false;
		}
		ArrayList<Parameter> mDatas=new ArrayList<Parameter>();
		mDatas=ParameterDao.getInstance(getApplicationContext()).queryAll(uidString);
		if (mDatas.size()==0) {
			Toast.makeText(MainActivity.this,"未搜索到该标签对应的参数！", Toast.LENGTH_SHORT).show();
			return false;
		}
		StringBuilder sb1=new StringBuilder();
		StringBuilder sb2=new StringBuilder();
		for (Parameter mdata : mDatas) {
			sb1.append(mdata.getDeviceTp());
			sb1.append(";");
			sb2.append(mdata.getUcode());
			sb2.append(";");
		}
		etDeviceTp.setText(sb1.toString().substring(0, sb1.toString().length()-1));
		etUCode.setText(sb2.toString().substring(0, sb2.toString().length()-1));
		return true;
	}
	/**
	 * 计算参数
	 */
	private boolean calculation() {
		String devString=etDeviceTp.getText().toString();
		String uCodeString=etUCode.getText().toString();
		double tp1=0,tp2=0,u1=0,u2=0,a=0,b=0,a10=0,b10=0;
		if (devString.contains(";")) {
			String[] strings=devString.split(";");
			tp1=Double.valueOf(strings[0]);
			tp2=Double.valueOf(strings[1]);
		}else{
			Toast.makeText(MainActivity.this,"读取的参数格式不正确！", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (uCodeString.contains(";")) {
			String[] strings=uCodeString.split(";");
			u1=Integer.parseInt(strings[0],16)/8192.0;
			u2=Integer.parseInt(strings[1],16)/8192.0;
		}else {
			Toast.makeText(MainActivity.this,"读取的参数格式不正确！", Toast.LENGTH_SHORT).show();
			return false;
		}
		a10=(tp2-tp1)/(u2-u1);
		b10=tp2-a10*u2;
		a=a10*16;
		b=Math.abs(b10)*16;
		int a1,b1;
		a1= (int) Math.round(a);
		b1= 65535-(int) Math.round(b)+1;
		
		String A,B,A1,B1;
		A1=Integer.toHexString(a1).toUpperCase();
		B1=Integer.toHexString(b1).toUpperCase();
		A1 = String.format("%4s",A1);
	    A= A1.replaceAll(" ","0");
	    B1 = String.format("%4s",B1);
	    B= B1.replaceAll(" ","0");
	    etA.setText(A);
	    etB.setText(B);
	    return true;
	}
	
	/**
	 * 读取芯片参数
	 */
	private boolean readChipPara() {
		if (list.contains("android.nfc.tech.NfcA")) {
			if (nfca==null) {
				Toast.makeText(MainActivity.this,"NFC未连接！", Toast.LENGTH_SHORT).show();
				return false;
			}
			FM13DT160_NfcA fm13dt160=new FM13DT160_NfcA(nfca);
			int firstAddress=0xb04c;
			int numOfByte=0;
			try {
				if (nfca.isConnected()) {
					nfca.close();
				}
				nfca.connect();
			    byte[] result=fm13dt160.readMemory(firstAddress, numOfByte);
			    String string = Utility.Bytes2HexString(result);
			    string = string.toUpperCase();
			    String str1=string.substring(0, 4);
			    String str2=string.substring(4);
			    String Acode=str1.substring(2)+str1.substring(0, 2);
			    String Bcode=str2.substring(2)+str2.substring(0, 2);
			    etA_raw.setText(Acode);
			    etB_raw.setText(Bcode);
			    String uidsString=etUID.getText().toString();
			    ParameterRaw parameterRaw=new ParameterRaw();
			    parameterRaw.setUid(uidsString);
			    parameterRaw.setAcode(Acode);
			    parameterRaw.setBcode(Bcode);
			    ParameterRawDao.getInstance(getApplicationContext()).insert(parameterRaw);
			    btnWritePara.setEnabled(true);
			    nfca.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(MainActivity.this,e.toString(), Toast.LENGTH_SHORT).show();
				return false;
			}
			
		}else if (list.contains("android.nfc.tech.NfcV")){
			if (nfcv==null) {
				Toast.makeText(MainActivity.this,"NFC未连接！", Toast.LENGTH_SHORT).show();
				return false;
			}
			FM13DT160 fm13dt160=new FM13DT160(nfcv);
			int firstAddress=0xb04c;
			int numOfByte=0;
			try {
				if (nfcv.isConnected()) {
					nfcv.close();
				}
				nfcv.connect();
			    byte[] result=fm13dt160.readMemory(firstAddress, numOfByte);
			    String string = Utility.Bytes2HexString(result);
			    string = string.toUpperCase();
			    String str1=string.substring(2, 6);
			    String str2=string.substring(6);
			    String Acode=str1.substring(2)+str1.substring(0, 2);
			    String Bcode=str2.substring(2)+str2.substring(0, 2);
			    etA_raw.setText(Acode);
			    etB_raw.setText(Bcode);
			    String uidsString=etUID.getText().toString();
			    ParameterRaw parameterRaw=new ParameterRaw();
			    parameterRaw.setUid(uidsString);
			    parameterRaw.setAcode(Acode);
			    parameterRaw.setBcode(Bcode);
			    ParameterRawDao.getInstance(getApplicationContext()).insert(parameterRaw);
			    btnWritePara.setEnabled(true);
			    nfcv.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(MainActivity.this,e.toString(), Toast.LENGTH_SHORT).show();
				return false;
			}
		}else {
			Toast.makeText(MainActivity.this,"未检测到标签！", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	/**
	 * 写入芯片参数
	 */
	private boolean writePara() {
		if (list.contains("android.nfc.tech.NfcA")) {
			if (nfca==null) {
				Toast.makeText(MainActivity.this,"NFC未连接！", Toast.LENGTH_SHORT).show();
				return false;
			}
			FM13DT160_NfcA fm13dt160=new FM13DT160_NfcA(nfca);
			int firstAddress=0xb04c;
			
			try {
				if (nfca.isConnected()) {
					nfca.close();
				}
				nfca.connect();
				String string1=etA.getText().toString();
				if (string1.length()!=4) {
					Toast.makeText(MainActivity.this,"A(HEX)格式不正确！", Toast.LENGTH_SHORT).show();
					return false;
				}
				String str1=string1.substring(2, 4)+string1.substring(0, 2);
				
				String string2=etB.getText().toString();
				if (string2.length()!=4) {
					Toast.makeText(MainActivity.this,"B(HEX)格式不正确！", Toast.LENGTH_SHORT).show();
					return false;
				}
				String str2=string2.substring(2, 4)+string2.substring(0, 2);
				
			    int dataNum=3;
				byte[] data=Utility.HexString2Bytes(str1+str2);
				
				fm13dt160.writeMemory(firstAddress, dataNum, data);
			    btnWritePara.setEnabled(false);
			    nfca.close();
			    Toast.makeText(MainActivity.this,"写入成功！", Toast.LENGTH_SHORT).show();
//			    etUCode.setText("");
//				etA.setText("");
//				etB.setText("");
				return true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(MainActivity.this,e.toString(), Toast.LENGTH_SHORT).show();
				return false;
			}
			
		}else if (list.contains("android.nfc.tech.NfcV")){
			if (nfcv==null) {
				Toast.makeText(MainActivity.this,"NFC未连接！", Toast.LENGTH_SHORT).show();
				return false;
			}
			FM13DT160 fm13dt160=new FM13DT160(nfcv);
			int firstAddress=0xb04c;
			
			try {
				if (nfcv.isConnected()) {
					nfcv.close();
				}
				nfcv.connect();
				String string1=etA.getText().toString();
				if (string1.length()!=4) {
					Toast.makeText(MainActivity.this,"A(HEX)格式不正确！", Toast.LENGTH_SHORT).show();
					return false;
				}
				String str1=string1.substring(2, 4)+string1.substring(0, 2);
				
				String string2=etB.getText().toString();
				if (string2.length()!=4) {
					Toast.makeText(MainActivity.this,"B(HEX)格式不正确！", Toast.LENGTH_SHORT).show();
					return false;
				}
				String str2=string2.substring(2, 4)+string2.substring(0, 2);
				
			    int dataNum=3;
				byte[] data=Utility.HexString2Bytes(str1+str2);
				
				fm13dt160.writeMemory(firstAddress, dataNum, data);
			    btnWritePara.setEnabled(false);
			    nfcv.close();
			    Toast.makeText(MainActivity.this,"写入成功！", Toast.LENGTH_SHORT).show();
//			    etUCode.setText("");
//				etA.setText("");
//				etB.setText("");
				return true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(MainActivity.this,e.toString(), Toast.LENGTH_SHORT).show();
				return false;
			}
		}else {
			Toast.makeText(MainActivity.this,"未检测到标签！", Toast.LENGTH_SHORT).show();
			return false;
		}
		
	}
    
    private static final int READER_FLAGS = NfcAdapter.FLAG_READER_NFC_A
            | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK
            | NfcAdapter.FLAG_READER_NFC_V; // | NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS;

	protected static final String TAG = "tag";
	
	//private NfcAdapter mNfcAdapter;
	//protected boolean isFm13dt160=false;
	protected String tagType;
	private NfcAdapter.ReaderCallback mReaderCallback = new NfcAdapter.ReaderCallback() {
	    @Override
	    public void onTagDiscovered(Tag tag) {
	//        Log.d(TAG, "onTagDiscovered: " + Arrays.toString(tag.getTechList()));
	        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
	        long[] duration = {15, 300, 60, 90};
	        vibrator.vibrate(duration, -1);
	        /*
			 * UID from a byte array from tag.getId(). Corrects the byte order of
			 * the UID to MSB first. Nicklaus Ng
			 */
			String uidString = new String();
			byte[] uid = tag.getId();
		    for (int index = uid.length - 1; index >= 0; --index) {
				uidString += String.format("%02x", uid[index]);
			}
		    uidString=uidString.toUpperCase();
	
		    final String uidfinalString=uidString;
	
		    String uidStringRev = new String();
			for (int index = 0; index < uid.length; index++) {
				uidStringRev += String.format("%02x", uid[index]);
			}
			final String uidfinalString2=uidStringRev.toUpperCase();
		    
	
	  		String[] techList = tag.getTechList();
	
	
			list.clear();
			for (String string : techList) {
				list.add(string);
				System.out.println("tech=" + string);
			}
			if (list.contains("android.nfc.tech.NfcA")) {
				
				nfca = android.nfc.tech.NfcA.get(tag);	
	
	    		if (nfca == null) {
	    			return;
	    		}
	
	    		try {
	    			if (nfca.isConnected()) {
	    				nfca.close();	
					}
	    			nfca.connect();			
	    			if(uid[0]==0x1D){//FM chip
	    				tagType="FM13DT160";
	    				new Thread(new Runnable() {
				                @Override
				                public void run() {
				                	Message msg = new Message();
				                	msg.what=0x1D;
				                	msg.obj=uidfinalString2;
				                	mHandler.sendMessage(msg);
				                }
				         }).start();
	
	    			}else{
	    				tagType="unknow";
	    				new Thread(new Runnable() {
			                @Override
			                public void run() {
			                	Message msg = new Message();
	//		                	msg.what=0x1D;
	//		                	msg.obj=uidfinalString;
				                mHandler.sendEmptyMessage(0x0000);
	//		                	mHandler.sendMessage(msg);
			                }
			         }).start();
	    			}		
	    			nfca.close();
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    			final String errString=e.toString();
	    			new Thread(new Runnable() {
			                @Override
			                public void run() {
			                	Message msg = new Message();
			                	msg.what=-1;
			                	msg.obj=errString;
	
			                	mHandler.sendMessage(msg);
			                }
			         }).start();
	    		}
			}else if (list.contains("android.nfc.tech.NfcV")) {
				
				nfcv = android.nfc.tech.NfcV.get(tag);	
	
	    		if (nfcv == null) {
	    			return;
	    		}
	       
	    		try {
	    			if (nfcv.isConnected()) {
						nfcv.close();
					}
	    			nfcv.connect();			
	    			if (uid[6]==0x36) {//AMS chip
//	    				Sl13a sl13a=new Sl13a(nfcv);				
	    				AS39513 as39513=new AS39513(nfcv,false);
	    				AS39513.SystemInformation systemInfo=new AS39513.SystemInformation();
	    				systemInfo=as39513.getSystemInformation();
	    				if(systemInfo.icReference==0x24){
	     					 tagType="Sl13a";
	     					 new Thread(new Runnable() {
	  			                @Override
	  			                public void run() {
	  			                	Message msg = new Message();
	  			                	msg.what=0x3601;
	  			                	msg.obj=uidfinalString;
//	  				                mHandler.sendEmptyMessage(0x3601);
	  			                	mHandler.sendMessage(msg);
	  			                }
	  			             }).start();
//	     				}else if(systemInfo.icReference==0x03||systemInfo.icReference==0x04){	
	    				}else{
	    					 tagType="AS39513";
		   					 new Thread(new Runnable() {
		   			                @Override
		   			                public void run() {
		   			                	Message msg = new Message();
		   			                	msg.what=0x3602;
		   			                	msg.obj=uidfinalString;
		//   				            mHandler.sendEmptyMessage(0x3602);
		   			                	mHandler.sendMessage(msg);
		   			                }
		   			          }).start();
	    				}
	    				
	    			}else if(uid[6]==0x1D){//FM chip
	    				tagType="FM13DT160";
	    				new Thread(new Runnable() {
				                @Override
				                public void run() {
				                	Message msg = new Message();
				                	msg.what=0x1D;
				                	msg.obj=uidfinalString;
				                	mHandler.sendMessage(msg);
				                }
				         }).start();
	
	    			}else{
	    				tagType="unknow";
	    				new Thread(new Runnable() {
			                @Override
			                public void run() {
			                	Message msg = new Message();
	//		                	msg.what=0x1D;
	//		                	msg.obj=uidfinalString;
				                mHandler.sendEmptyMessage(0x0000);
	//		                	mHandler.sendMessage(msg);
			                }
	    				}).start();
	    			}		
	    			nfcv.close();
	    		} catch (IOException e) {

	    			e.printStackTrace();
	    			final String errString=e.toString();
	    			new Thread(new Runnable() {
			                @Override
			                public void run() {
			                	Message msg = new Message();
			                	msg.what=-1;
			                	msg.obj=errString;
	
			                	mHandler.sendMessage(msg);
			                }
			         }).start();
	    		}
			}   
	    }
	};
	
	@SuppressLint("HandlerLeak") 
	private Handler mHandler = new Handler() {
	    @Override
	    public void handleMessage(Message msg) {
	        super.handleMessage(msg);
	        
	        if (msg.what == 100) {
	        	etUID.setText((String)msg.obj);
	        }else if(msg.what==0x3601){
	        	etUID.setText((String)msg.obj);
	        	
	        	String devicesString = PreferenceUtils.getPrefString(getApplicationContext(), "DeviceTp", "35");
				etDeviceTp.setText(devicesString);
				etUCode.setText("");
				etA.setText("");
				etB.setText("");
				Toast.makeText(MainActivity.this,"不支持TT1.0温度标签的校准！", Toast.LENGTH_SHORT).show();
				
	        }else if(msg.what==0x3602){
	        	etUID.setText((String)msg.obj);
	        	
	        	String devicesString = PreferenceUtils.getPrefString(getApplicationContext(), "DeviceTp", "35");
				etDeviceTp.setText(devicesString);
				etUCode.setText("");
				etA.setText("");
				etB.setText("");
				Toast.makeText(MainActivity.this,"不支持TT1.1温度标签的校准！", Toast.LENGTH_SHORT).show();
	        }else if(msg.what==0x1D){
	
				if (list.contains("android.nfc.tech.NfcA")) {
					FM13DT160_NfcA fm13dt160=new FM13DT160_NfcA(nfca);
					etUID.setText((String)msg.obj);
	            	try {
	            		if (nfca.isConnected()) {
							nfca.close();
						}
						nfca.connect();
						readTag(fm13dt160, (String)msg.obj);
						nfca.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else if (list.contains("android.nfc.tech.NfcV")) {
					FM13DT160 fm13dt160=new FM13DT160(nfcv);
					etUID.setText((String)msg.obj);
	            	try {
	            		if (nfcv.isConnected()) {
							nfcv.close();
						}
						nfcv.connect();
						readTag(fm13dt160, (String)msg.obj);
						nfcv.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
	        	
	        }else if(msg.what==0x0000){
	        	Toast.makeText(MainActivity.this,"未知标签", Toast.LENGTH_SHORT).show();
	        }else if (msg.what==-1) {
	        	Toast.makeText(MainActivity.this, (String)msg.obj, Toast.LENGTH_SHORT).show();
			}
	    }
	};
	/** 初始化 **/
	@Override
	protected void init(boolean hasBackOnActionBar, boolean hasNfc) {
		super.init(hasBackOnActionBar, hasNfc);
	}
	
	protected void readTag(FM13DT160 fm13dt160, String uidString) {
		// TODO Auto-generated method stub
		try{
			String devicesString = PreferenceUtils.getPrefString(getApplicationContext(), "DeviceTp", "35");
			etDeviceTp.setText(devicesString);
			etUCode.setText("");
			etA.setText("");
			etB.setText("");
			
			byte[] result=null;
			result=fm13dt160.setWakeup();

			result=fm13dt160.getWakeup();			
			String resStr=Utility.Bytes2HexString(result);
			if (resStr.contains("5555")) {
				System.out.println("已唤醒");
			}
			int form=fm13dt160.getLogForm();
			if (form==7) {
				btnRawMode.setEnabled(false);
				btnNormalMode.setEnabled(true);
			}else if (form==3) {
				btnNormalMode.setEnabled(false);
				btnRawMode.setEnabled(true);
			}
			
			result=fm13dt160.getState((byte)0x01);
            
			// 标签工作状态(激活/未激活)
			String resString=Utility.Bytes2HexString(result);
			if (resString.contains("0131")||resString.contains("0431")) {
				// 标签激活状态
				btnStart.setEnabled(false);	
			} else {
				// 标签未激活状态
				btnStart.setEnabled(true);	
			}

			//先开启实时测温
//            result=fm13dt160.startTemperature();
//            try {
//				Thread.sleep(400);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//            //在读取温度
//		    result=fm13dt160.getTemperature();
//
//			String tempStr=Utility.Bytes2HexString(result);
//			tempStr=tempStr.substring(2);
//			double temp=DataUtil.strFromat(tempStr);
//			System.out.println("tempStr:"+tempStr+";temp:"+temp);
//			tempStr="0C66";
//			tempStr="0EE4";
//			etUCode.setText(tempStr);
			//检测场强
//			result = fm13dt160.field_strength_chk(0);
//			int ch = result[1]&0x0f;
//			double cal = DataUtil.calibrateTable(ch);
//			Parameter data=new Parameter();
//			data.setUid(uidString);
//			String devString = etDeviceTp.getText().toString();
//			if (devString.contains(";") && toggleBtn.isChecked()) {
//				Toast.makeText(MainActivity.this, "当前设备温度的参数格式不正确，请重新设置！", Toast.LENGTH_SHORT).show();
//				return;
//			}
//			
//			if (toggleBtn.isChecked()) {
//				double deviceTp= Double.valueOf(devString);
//				data.setDeviceTp(deviceTp);
//				
//				String ucode=tempStr;
//				data.setUcode(ucode);
//				
//				ParameterDao.getInstance(getApplicationContext()).insert(data);
//				Toast.makeText(MainActivity.this, "单点测量结束，保存成功！", Toast.LENGTH_SHORT).show();
//				SoundUtil.play(R.raw.pegconn, 0);
//			}
			
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
		
		}
	}

	/**
	 * 读取FM13温度标签
	 * @param fm13dt160
	 * @param uidStringRev
	 */
	private void readTag(FM13DT160_NfcA fm13dt160, String uidStringRev) {
		
		try{
			String devicesString = PreferenceUtils.getPrefString(getApplicationContext(), "DeviceTp", "35");
			etDeviceTp.setText(devicesString);
			etUCode.setText("");
			etA.setText("");
			etB.setText("");
			byte[] result=null;
			result=fm13dt160.setWakeup();

			result=fm13dt160.getWakeup();			
			String resStr=Utility.Bytes2HexString(result);
			if (resStr.contains("5555")) {
				System.out.println("已唤醒");
			}
			
			int form=fm13dt160.getLogForm();
			if (form==7) {
				btnRawMode.setEnabled(false);
				btnNormalMode.setEnabled(true);
			}else if (form==3) {
				btnNormalMode.setEnabled(false);
				btnRawMode.setEnabled(true);
			}
			
			result=fm13dt160.getState((byte)0x01);
            
			// 标签工作状态(激活/未激活)
			String resString=Utility.Bytes2HexString(result);
			if (resString.contains("0131")||resString.contains("0431")) {
				// 标签激活状态
				btnStart.setEnabled(false);	
			} else {
				// 标签未激活状态
				btnStart.setEnabled(true);	
			}
			
			
//			//先开启实时测温
//            result=fm13dt160.startTemperature();
//            try {
//				Thread.sleep(400);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//            //在读取温度
//		    result=fm13dt160.getTemperature();
//			String tempStr=Utility.Bytes2HexString(result);
//			tempStr=tempStr.substring(2);
//			double temp=DataUtil.strFromat(tempStr);
//			//检测场强
//			result = fm13dt160.field_strength_chk(0);
//			int ch = result[1]&0x0f;
//			double cal = DataUtil.calibrateTable(ch); 
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private void enableReaderMode() {
        Log.i(TAG, "Enabling reader mode");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            NfcAdapter nfc = NfcAdapter.getDefaultAdapter(this);
//            int READER_FLAGS = -1;
            Bundle option = new Bundle();
            option.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 3000);// 延迟对卡片的检测
            if (nfc != null) {
                nfc.enableReaderMode(this, mReaderCallback, READER_FLAGS, option);
            }
        }
 
    }

    private void disableReaderMode() {
        Log.i(TAG, "Disabling reader mode");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            NfcAdapter nfc = NfcAdapter.getDefaultAdapter(this);
            if (nfc != null) {
                nfc.disableReaderMode(this);
            }
        }
    }
	// Sai -----------end
	@Override
	protected void onResume() {
		super.onResume();
		
		enableReaderMode();
	}

	@Override
	protected void onPause() {
		super.onPause();
		disableReaderMode();
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

}
