package com.joker.livingstone.service;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;

import com.joker.livingstone.DiscussActivity;
import com.joker.livingstone.R;
import com.joker.livingstone.util.Const;
import com.joker.livingstone.util.HttpHelper;

public class SendCommentService extends Service {
	
    private int bookId;
    private int chapterNo;
    private String bookName;
    private String content;
    
    private Map<String,String> map;
	
//	private MyBinder mBinder = new MyBinder();  

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		getDataFromIntent(intent);
		startNotification();
		
		final Intent i = intent;
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				// 开始执行后台任务
				String data = HttpHelper.getString(SendCommentService.this, Const.PATH + "msaveComment", map);
				JSONObject json;
				try {
					json = new JSONObject(data);
					if (json.getInt("rtn") == 0) {
						endNotification();
						i.setClass(SendCommentService.this, DiscussActivity.class);
						i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(i);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}).start();
		
		SendCommentService.this.stopSelf();
		return super.onStartCommand(intent, flags, startId);
	}
	


	private void getDataFromIntent(Intent i){
		bookId = i.getIntExtra("bookId", 1);
		chapterNo = i.getIntExtra("chapterNo" , 1);
		bookName = i.getStringExtra("bookName");
		content = i.getStringExtra("content");
		
		map = new HashMap<String, String>();
		map.put("bookId", bookId + "");
		map.put("chapterNo", chapterNo + "");
		map.put("content", content);
	}
	
	protected void startNotification() {
		NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this);
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher); 
		
		notifyBuilder.setContentTitle(getResources().getString(R.string.app_name))
				.setContentText("正在发送评论...")
				.setSmallIcon(R.drawable.ic_launcher)
				.setLargeIcon(bitmap)
				.setWhen(System.currentTimeMillis())
				.setTicker("活石正在发送评论...")
				.setOngoing(true);
		
		
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);  
        mNotificationManager.notify(Const.SEND_COMMENT_ID, notifyBuilder.build());

	}
	
	protected void endNotification() {
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(Const.SEND_COMMENT_ID);
		
		NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this);
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher); 
		
		Intent notifyIntent = new Intent(this, DiscussActivity.class);
		notifyIntent.putExtra("bookId", bookId);
		notifyIntent.putExtra("chapterNo", chapterNo);
		notifyIntent.putExtra("bookName", bookName);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK  
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);  
        // Creates the PendingIntent  
        // 当设置下面PendingIntent.FLAG_UPDATE_CURRENT这个参数的时候，常常使得点击通知栏没效果，你需要给notification设置一个独一无二的requestCode  
        int requestCode = (int) SystemClock.uptimeMillis();  
        PendingIntent pendIntent = PendingIntent.getActivity(this, requestCode, notifyIntent, PendingIntent.FLAG_CANCEL_CURRENT);  
        notifyBuilder.setContentIntent(pendIntent);
		
		notifyBuilder.setContentTitle(getResources().getString(R.string.app_name))
				.setContentText("评论发送成功，点击可以查看")
				.setSmallIcon(R.drawable.ic_launcher)
				.setLargeIcon(bitmap)
				.setWhen(System.currentTimeMillis())
				.setTicker("您的评论已发送成功")
				.setOngoing(false)
				.setDefaults(Notification.DEFAULT_ALL)
				.setContentIntent(pendIntent);
		
		
        mNotificationManager.notify(Const.SEND_COMMENT_SUCCESS_ID, notifyBuilder.build());
	
	}

//	class MyBinder extends Binder {
//
//		public void startDownload() {
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
//					// 执行具体的下载任务
//				}
//			}).start();
//		}
//
//	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
