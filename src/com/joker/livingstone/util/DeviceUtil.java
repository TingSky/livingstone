package com.joker.livingstone.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;

public class DeviceUtil {
	public static void initParams(Context context){
		Const.IMEI = getImei(context);
		Const.USERID = getUserid(context);
	}
	

	public static String getImei(Context context){
		if(Const.IMEI == null){
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			Const.IMEI = tm.getDeviceId();
		}
		return Const.IMEI;
	}
	
	private static Object getUserid(Context context) {
		if(Const.USERID == null){
			SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
			Const.USERID = sp.getString("userid", "");
		}
		return Const.USERID;
	}
}
