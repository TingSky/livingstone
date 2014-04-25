package com.joker.livingstone.util;

import java.io.File;
import java.lang.reflect.Field;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;

public class DeviceUtil {
	public static void initParams(Context context){
		createFile();
		Const.IMEI = getImei(context);
		Const.USERID = get(context , "USERID");
		Const.EGGID = get(context, "EGGID");
	}
	

	public static String getImei(Context context){
		if(Const.IMEI == null){
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			Const.IMEI = tm.getDeviceId();
		}
		return Const.IMEI;
	}
	
	
	public static String get(Context context , String key){
		return get(context, "user", key);
	}
	
	public static void set(Context context , String key , String value){
		set(context, "name", key, value);
	}
	
	
	public static String get(Context context , String nameFiled, String key){
		String data = null;
		try {
			Class<?> c = Class.forName("com.joker.livingstone.util.Const");
//			Object o = c.newInstance();
			Field f = c.getField(key);
			data = (String) f.get(c);
			if(data == null || data.equals("")){
				SharedPreferences sp = context.getSharedPreferences(nameFiled , Context.MODE_PRIVATE);
				data = sp.getString(key, "");
				f.set(c, data);
				
			}
//			Log.d(key , key + ":" +data);
			return data;
		} catch (Exception e) {
			SharedPreferences sp = context.getSharedPreferences(nameFiled , Context.MODE_PRIVATE);
			data = sp.getString(key, "");
		}
		return data;
	}
	
	public static void set(Context context , String nameFiled, String key , String value){
		SharedPreferences.Editor edit = context.getSharedPreferences(nameFiled ,Context.MODE_PRIVATE).edit();
		edit.putString(key, value).commit();
		try {
			Class<?> c = Class.forName("com.joker.livingstone.util.Const");
//			Object o = c.newInstance();
			Field f = c.getField(key);
			f.set(c, value);
			
//			Log.d(key , value);
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
	}
	
	private static void createFile(){
		File f = new File(Const.SDCARD_DATA_LOCATION);
		if(f.isDirectory()) return ;
		f.mkdirs();
	}

}
