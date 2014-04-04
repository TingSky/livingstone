package com.joker.livingstone.util;

import java.lang.reflect.Field;

import com.joker.livingstone.EasterActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;

public class DeviceUtil {
	public static void initParams(Context context){
		Const.IMEI = getImei(context);
		Const.USERID = get(context , "IMEI");
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
		String data = null;
		try {
			Class<?> c = Class.forName("com.joker.livingstone.util.Const");
			Object o = c.newInstance();
			Field f = c.getField(key);
			data = (String) f.get(o);
			if(data == null || data == ""){
				SharedPreferences sp = context.getSharedPreferences("user" , Context.MODE_PRIVATE);
				f.set(o, sp.getString(key, ""));
			}
			
			return data;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	public static void set(Context context , String key , String value){
		SharedPreferences.Editor edit = context.getSharedPreferences("user" ,Context.MODE_PRIVATE).edit();
		edit.putString(key, value).commit();
		try {
			Class<?> c = Class.forName("com.joker.livingstone.util.Const");
			Object o = c.newInstance();
			Field f = c.getField(key);
			f.set(o, value);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
