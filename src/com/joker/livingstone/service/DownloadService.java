package com.joker.livingstone.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

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
import android.util.Log;

import com.joker.livingstone.DiscussActivity;
import com.joker.livingstone.R;
import com.joker.livingstone.util.Const;
import com.joker.livingstone.util.DeviceUtil;
import com.joker.livingstone.util.HttpHelper;

public class DownloadService extends Service {
	
	/*
	 * 更新配置中的更新时间
	 */
    private String date;
    
//	private MyBinder mBinder = new MyBinder();  

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		getDataFromIntent(intent);
		
		
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				// 开始执行后台任务
				String data = HttpHelper.getString(Const.CONFIG_PATH);
				JSONObject json;
				try {
					json = new JSONObject(data);
//Log.d("down" , "8");
					if (json.getString("revealDate").compareTo(date) > 0) {
//Log.d("down" , "9");
						DeviceUtil.set(DownloadService.this, Const.WELCOME_UPGRADE_CONFIG, "mode", json.getString("mode") );
				    	DeviceUtil.set(DownloadService.this, Const.WELCOME_UPGRADE_CONFIG, "title", json.getString("title"));
				    	DeviceUtil.set(DownloadService.this, Const.WELCOME_UPGRADE_CONFIG, "source", json.getString("source"));
				    	DeviceUtil.set(DownloadService.this, Const.WELCOME_UPGRADE_CONFIG, "image", json.getString("image"));
				    	DeviceUtil.set(DownloadService.this, Const.WELCOME_UPGRADE_CONFIG, "revealDate", json.getString("revealDate"));
				    	DeviceUtil.set(DownloadService.this, Const.WELCOME_UPGRADE_CONFIG, "exceed", json.getString("exceed") );
				    	DeviceUtil.set(DownloadService.this, Const.WELCOME_UPGRADE_CONFIG, "default", json.getString("default"));
				    	DeviceUtil.set(DownloadService.this, Const.WELCOME_UPGRADE_CONFIG, "bgcolor", json.getString("bgcolor"));
				    	
				    	String path = json.getString("image");
				    	String name = path.substring(path.lastIndexOf(File.separator) + 1);
				    	
				    	URL url = new URL(path);
			            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();  
			            InputStream in = urlConn.getInputStream(); 
			            FileOutputStream fos = new FileOutputStream(new File(Const.SDCARD_DATA_LOCATION + name));
			            byte b[] = new byte[256] ;
			            int len = 0;
			            while((len = in.read(b)) != -1){
			            	fos.write(b, 0, len);
			            }
			            fos.flush();
			            fos.close();
			            in.close();
//Log.d("down", "down");
			            DownloadService.this.stopSelf();
					}
				} catch (JSONException e) {
//					e.printStackTrace();
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
				}
			}
		}).start();
		return super.onStartCommand(intent, flags, startId);
	}
	


	private void getDataFromIntent(Intent i){
		date = i.getStringExtra("date");
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
