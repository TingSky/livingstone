package com.joker.livingstone.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;


public class DialogHelper {
	private static ProgressDialog progressDialog;

	public static void showDialog(Context context) {
		progressDialog = ProgressDialog.show(context, "活石", "正在加载...");
	}

	public static void dismiss() {
		if(progressDialog != null){
			progressDialog.dismiss();
		}
	}

	public static void showFinishDialog(final Context context ,String Message ){
		  showFinishDialog(context, Message, true);
	}
	
	public static void showFinishDialog(final Context context ,String Message , final boolean finishActivity){
		  AlertDialog.Builder builder = new Builder(context);
		  builder.setMessage(Message);
		  builder.setTitle("活石");
		  
		  builder.setNegativeButton("确认", new OnClickListener() {
			  @Override
			  public void onClick(DialogInterface dialog, int which) {
				  dialog.dismiss();
				  if(finishActivity) ((Activity)context).finish();
			  }
		  });
		  builder.create().show();
	}

}