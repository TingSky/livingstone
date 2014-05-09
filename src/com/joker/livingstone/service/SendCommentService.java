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
				// ��ʼִ�к�̨����
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
				.setContentText("���ڷ�������...")
				.setSmallIcon(R.drawable.ic_launcher)
				.setLargeIcon(bitmap)
				.setWhen(System.currentTimeMillis())
				.setTicker("��ʯ���ڷ�������...")
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
        // ����������PendingIntent.FLAG_UPDATE_CURRENT���������ʱ�򣬳���ʹ�õ��֪ͨ��ûЧ��������Ҫ��notification����һ����һ�޶���requestCode  
        int requestCode = (int) SystemClock.uptimeMillis();  
        PendingIntent pendIntent = PendingIntent.getActivity(this, requestCode, notifyIntent, PendingIntent.FLAG_CANCEL_CURRENT);  
        notifyBuilder.setContentIntent(pendIntent);
		
		notifyBuilder.setContentTitle(getResources().getString(R.string.app_name))
				.setContentText("���۷��ͳɹ���������Բ鿴")
				.setSmallIcon(R.drawable.ic_launcher)
				.setLargeIcon(bitmap)
				.setWhen(System.currentTimeMillis())
				.setTicker("���������ѷ��ͳɹ�")
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
//					// ִ�о������������
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
