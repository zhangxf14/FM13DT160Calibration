package com.example.fm13dt160calibration.utils;

import java.util.HashMap;
import java.util.Map;

import com.example.fm13dt160calibration.R;
import com.example.fm13dt160calibration.R.raw;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class SoundUtil {

	public static SoundPool sp = null;
	public static Map<Integer, Integer> suondMap = null;
	public static Context context;
	public static int streamID;

	// 初始化声音池
	public static void initSoundPool(Context context) {
		SoundUtil.context = context;
		sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 1);
		suondMap = new HashMap<Integer, Integer>();
		suondMap.put(R.raw.beep51, sp.load(context, R.raw.beep51, 1));
		suondMap.put(R.raw.chimes, sp.load(context, R.raw.chimes, 1));
		suondMap.put(R.raw.ding, sp.load(context, R.raw.ding, 1));
		suondMap.put(R.raw.msg, sp.load(context, R.raw.msg, 1));
		suondMap.put(R.raw.pegconn, sp.load(context, R.raw.pegconn, 1));
		suondMap.put(R.raw.stickykeydown, sp.load(context, R.raw.stickykeydown, 1));
	}

	public static void releaseSoundPool() {
		if (null != sp) {
			sp.release();
			sp = null;
		}
		if (null != suondMap) {
			suondMap.clear();
			suondMap = null;
		}
	}

	// 播放声音池声音
	public static void play(int sound, int number) {
		AudioManager am = (AudioManager) SoundUtil.context
				.getSystemService(Context.AUDIO_SERVICE);
		// 返回当前AlarmManager最大音量
		float audioMaxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

		// 返回当前AudioManager对象的音量值
		float audioCurrentVolume = am
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		float volumnRatio = audioCurrentVolume / audioMaxVolume;
		int id = suondMap.get(sound);
		streamID = sp.play(id, // 播放的音乐Id
				audioCurrentVolume, // 左声道音量
				audioCurrentVolume, // 右声道音量
				1, // 优先级，0为最低
				number, // 循环次数，0无不循环，-1无永远循环
				1);// 回放速度，值在0.5-2.0之间，1为正常速度
	}

}
