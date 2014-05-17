package com.joker.livingstone.util;

import java.io.File;
import java.lang.reflect.Field;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

public class DeviceUtil {
	public static void initParams(Context context){
		createFile();
		Const.IMEI = getImei(context);
		Const.USERID = get(context , "USERID");
		Const.NICKNAME = get(context , "NICKNAME");
		Const.PHONE = get(context , "PHONE");
		Const.VOTE = get(context , "VOTE");
//		Const.EGGID = get(context, "EGGID");
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
		set(context, "user", key, value);
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
			Log.d("util" , nameFiled + "->" +key + ":" +data);
			return data;
		} catch (Exception e) {
			SharedPreferences sp = context.getSharedPreferences(nameFiled , Context.MODE_PRIVATE);
			data = sp.getString(key, "");
			Log.d("util" , nameFiled + ":" +key + "->" +data);
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
			
			Log.d("util" , nameFiled + ":" +key + "<-" +value);
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
	/*
	 * 判断是否有网络连接 
	 */
	public static boolean isNetworkConnected(Context context) { 
		if (context != null) { 
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context .getSystemService(Context.CONNECTIVITY_SERVICE); 
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo(); 
			if (mNetworkInfo != null) { 
				return mNetworkInfo.isAvailable(); 
			} 
		} 
		return false; 
	}

}
