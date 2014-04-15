package com.joker.livingstone.service;

import com.joker.livingstone.R;
import com.joker.livingstone.util.Const;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class SendCommentService extends Service {
	
//	private MyBinder mBinder = new MyBinder();  

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
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
		
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				// 开始执行后台任务
			}
		}).start();
		return super.onStartCommand(intent, flags, startId);
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
