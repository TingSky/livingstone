package com.joker.livingstone.util;

import com.joker.livingstone.EasterActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;

public class DeviceUtil {
	public static void initParams(Context context){
		Const.IMEI = getImei(context);
		Const.USERID = getUserId(context);
	}
	

	public static String getImei(Context context){
		if(Const.IMEI == null){
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			Const.IMEI = tm.getDeviceId();
		}
		return Const.IMEI;
	}
	
	public static String getUserId(Context context) {
		if(Const.USERID == null){
			SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
			Const.USERID = sp.getString("userid", "");
		}
		return Const.USERID;
	}

	public static void setUserId(Context context , String userid){
		SharedPreferences.Editor edit = context.getSharedPreferences("user" ,Context.MODE_PRIVATE).edit();
		edit.putString("userid", userid).commit();
		Const.USERID = userid ;
	}
}
