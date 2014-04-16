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
				.setContentText("���ڷ�������...")
				.setSmallIcon(R.drawable.ic_launcher)
				.setLargeIcon(bitmap)
				.setWhen(System.currentTimeMillis())
				.setTicker("��ʯ���ڷ�������...")
				.setOngoing(true);
		
		
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);  
        mNotificationManager.notify(Const.SEND_COMMENT_ID, notifyBuilder.build());
		
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				// ��ʼִ�к�̨����
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
