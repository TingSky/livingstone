package com.joker.livingstone.util;

import android.app.ProgressDialog;
import android.content.Context;

public class DialogHelper {
	private static ProgressDialog progressDialog;
	
	public static void showDialog(Context context){
		progressDialog = ProgressDialog.show(context , "��ʯ" ,"���ڼ���...");
	}
	
	public static void dismiss(){
		progressDialog.dismiss();
	}
}
