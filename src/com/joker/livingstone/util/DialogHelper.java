package com.joker.livingstone.util;

import android.app.ProgressDialog;
import android.content.Context;

public class DialogHelper {
	private static ProgressDialog progressDialog;
	
	public static void showDialog(Context context){
		progressDialog = ProgressDialog.show(context , "活石" ,"正在加载...");
	}
	
	public static void dismiss(){
		progressDialog.dismiss();
	}
}
