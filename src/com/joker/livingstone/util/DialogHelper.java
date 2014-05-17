package com.joker.livingstone.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.widget.Toast;


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

	public static void showLogoutDialog(final Context context){
		  AlertDialog.Builder builder = new Builder(context);
		  builder.setMessage("您确定要注销吗？");
		  builder.setTitle("活石");
		  
		  builder.setNegativeButton("注销", new OnClickListener() {
			  @Override
			  public void onClick(DialogInterface dialog, int which) {
				  dialog.dismiss();
				  DeviceUtil.set(context, "USERID", "");
				  Toast.makeText(context, "注销成功", Toast.LENGTH_LONG).show();
			  }
		  });
		  builder.setPositiveButton("取消", new OnClickListener() {
			  @Override
			  public void onClick(DialogInterface dialog, int which) {
				  dialog.dismiss();
			  }
		  });
		  builder.create().show();
	}
}