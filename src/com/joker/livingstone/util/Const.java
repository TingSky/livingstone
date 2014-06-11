package com.joker.livingstone.util;

import java.io.File;

import android.os.Environment;

public class Const {
//	public static String PATH = "http://192.168.1.217/huoshi-backstage/";
	
//	public static String PATH = "http://218.244.146.3/huoshi/";
//	public static String CONFIG_PATH = "http://218.244.146.3/file/launcher.js";
	
	public static String PATH = "http://huoshi.im/huoshi_dev/";
	public static String CONFIG_PATH = "http://huoshi.im/dev/file/launcher.js";
	
	public static boolean FIRST = true;
	public static String IMEI ;
	public static String USERID;
	public static String PHONE;
	public static String NICKNAME;
	public static String VOTE;
	
	public static String EGGID;
	
	public static String UPDATE_DATE;
	
	public final static String WELCOME_LOCAL_CONFIG = "local"; 
	public final static String WELCOME_UPGRADE_CONFIG = "upgrade"; 
	
	public final static String SDCARD_DATA_LOCATION =
			Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +".huoshi" +  File.separator ;
	
	public final static int SEND_COMMENT_ID = 1000;
	public final static int SEND_COMMENT_SUCCESS_ID = 1001;
}
