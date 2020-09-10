package com.example.fm13dt160calibration;

import java.lang.reflect.Method;
import java.util.Arrays;

import com.example.fm13dt160calibration.utils.Constants;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcV;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

/**
 * @author Sai 类说明：基本Act�?
 */
public abstract class BaseAct extends FragmentActivity {

	private NfcAdapter nfcAdapter;
	private PendingIntent nfcPendingIntent;
	private IntentFilter[] tagFilters;
	private String[][] techList;
	private Bundle bundle;

	private boolean hasBackOnActionBar;
	private boolean hasNfc;
	
	private static final int READER_FLAGS = NfcAdapter.FLAG_READER_NFC_A
            | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK 
            | NfcAdapter.FLAG_READER_NFC_V; //NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS;

	protected static final String TAG = "tag";

//	private NfcAdapter mNfcAdapter;
	
	private NfcAdapter.ReaderCallback mReaderCallback = new NfcAdapter.ReaderCallback() {
	        @Override
	        public void onTagDiscovered(Tag tag) {
	            Log.d(TAG, "onTagDiscovered: " + Arrays.toString(tag.getTechList()));
	            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
	            long[] duration = {15, 300, 60, 90};
	            vibrator.vibrate(duration, -1);
	            /*
	    		 * UID from a byte array from tag.getId(). Corrects the byte order of
	    		 * the UID to MSB first. Nicklaus Ng
	    		 */
//	    		String uidString = new String();
//	    		byte[] uid = tag.getId();
//	    	    for (int index = uid.length - 1; index >= 0; --index) {
//	    			uidString += String.format("%02x", uid[index]);
//	    		}
	    	    new Thread(new Runnable() {
	                @Override
	                public void run() {
	                	Message msg = new Message();
	                	msg.what=0x0000;
	                	mHandler.sendMessage(msg);
	                }
	    	    }).start();
	        }
	  };
	  
	  @SuppressLint("HandlerLeak") 
	    private Handler mHandler = new Handler() {
	        @Override
	        public void handleMessage(Message msg) {
	            super.handleMessage(msg);
	            
	            if (msg.what == 0x0000) {
	                  Toast.makeText(BaseAct.this, "", Toast.LENGTH_SHORT).show();
	            }
	        }
	  };

	/** 初始化视�? **/
	protected abstract void initViews();

	/** 初始化事�? **/
	protected void initEvents() {

		if (hasBackOnActionBar) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}

		if (hasNfc) {
			nfcAdapter = NfcAdapter.getDefaultAdapter(this);
			nfcPendingIntent = PendingIntent.getActivity(this, 0,
					new Intent(this, getClass())
							.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
			techList = new String[][] { { NfcV.class.getName() } };
			IntentFilter ndefDetected = new IntentFilter(
					NfcAdapter.ACTION_NDEF_DISCOVERED);
			tagFilters = new IntentFilter[] { ndefDetected };
		}
	}

	
	
	/** 初始�? **/
	protected void init(boolean hasBackOnActionBar, boolean hasNfc) {
		setHasBackOnActionBar(hasBackOnActionBar);
		setHasNfc(hasNfc);
	}

	/** 短暂显示Toast提示(来自res) **/
	protected void showShortToast(int resId) {
		Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show();
	}

	/** 短暂显示Toast提示(来自String) **/
	protected void showShortToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	/** 长时间显示Toast提示(来自res) **/
	protected void showLongToast(int resId) {
		Toast.makeText(this, getString(resId), Toast.LENGTH_LONG).show();
	}

	/** 长时间显示Toast提示(来自String) **/
	protected void showLongToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	}
	
	private void enableReaderMode() {
        Log.i(TAG, "Enabling reader mode");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            NfcAdapter nfc = NfcAdapter.getDefaultAdapter(this);
//            int READER_FLAGS = -1;
            Bundle option = new Bundle();
            option.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 3000);// 延迟对卡片的�?�?
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

	@Override
	protected void onResume() {
		super.onResume();
		if (hasNfc) {
			nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent,
					tagFilters, techList);
		}
		enableReaderMode();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (hasNfc) {
			nfcAdapter.disableForegroundDispatch(this);
		}
		disableReaderMode();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		if (hasNfc) {
			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			bundle = new Bundle();
			bundle.putParcelable(Constants.NEWTAG, tag);
			setBundle(bundle);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (hasBackOnActionBar) {
			switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				break;
			}
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		setOverflowIconVisible(featureId, menu);
		return super.onMenuOpened(featureId, menu);
	}
	
	/**
	* 利用反射让隐藏在Overflow中的MenuItem显示Icon图标
	* @param featureId
	* @param menu
	* onMenuOpened方法中调�?
	*/
	public static void setOverflowIconVisible(int featureId, Menu menu) {
		if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
			if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
				try {
					Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
					m.setAccessible(true);
					m.invoke(menu, true);
				} catch (Exception e) {
				}
			}
		}
	}

	public Bundle getBundle() {
		return bundle;
	}

	public void setBundle(Bundle bundle) {
		this.bundle = bundle;
	}

	public boolean isHasBackOnActionBar() {
		return hasBackOnActionBar;
	}

	public void setHasBackOnActionBar(boolean hasBackOnActionBar) {
		this.hasBackOnActionBar = hasBackOnActionBar;
	}

	public boolean isHasNfc() {
		return hasNfc;
	}

	public void setHasNfc(boolean hasNfc) {
		this.hasNfc = hasNfc;
	}
	
	@Override
	public void onBackPressed() {
	   finish();      
	}
}
