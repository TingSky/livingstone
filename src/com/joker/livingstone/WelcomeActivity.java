package com.joker.livingstone;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.joker.livingstone.service.DownloadService;
import com.joker.livingstone.util.Const;
import com.joker.livingstone.util.DeviceUtil;


public class WelcomeActivity extends BaseActivity {
	private static final int GOTO_MAIN_ACTIVITY = 0;  
	private ImageView mImageView;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private String now = sdf.format(new Date());
	private String upgradeShowDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	DeviceUtil.initParams(this);
        //全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
        	getSupportActionBar().hide();
        }
        
        String isDefalut = DeviceUtil.get(this, Const.WELCOME_UPGRADE_CONFIG , "default");
        //如果更新配置中使用默认素材或不存在更新配置，则查找本地配置
        if(isDefalut.equals("1") || isDefalut.equals("")){
//Log.d("down","1");
        	displayImage();
        }else{
//Log.d("down","2");
        	upgradeShowDate = DeviceUtil.get(this, Const.WELCOME_UPGRADE_CONFIG , "revealDate");
        	String localShowDate = DeviceUtil.get(this, Const.WELCOME_LOCAL_CONFIG , "revealDate");
        	now = sdf.format(new Date());
        	if(upgradeShowDate.equals(now) && upgradeShowDate.compareTo(localShowDate) > 0){
//Log.d("down","3");
        		pushToLocal();
        	}
        	displayImage();
        }
        
        
        
        
        MyTimer timer = new MyTimer();  
        timer.start();//启动线程  
    }  
    
    /*
     * 将更新配置存入本地配置
     */
    private void pushToLocal() {
		// TODO Auto-generated method stub
    	DeviceUtil.set(this, Const.WELCOME_LOCAL_CONFIG, "mode", DeviceUtil.get(this, Const.WELCOME_UPGRADE_CONFIG, "mode"));
    	DeviceUtil.set(this, Const.WELCOME_LOCAL_CONFIG, "title", DeviceUtil.get(this, Const.WELCOME_UPGRADE_CONFIG, "title"));
    	DeviceUtil.set(this, Const.WELCOME_LOCAL_CONFIG, "source", DeviceUtil.get(this, Const.WELCOME_UPGRADE_CONFIG, "source"));
    	DeviceUtil.set(this, Const.WELCOME_LOCAL_CONFIG, "image", DeviceUtil.get(this, Const.WELCOME_UPGRADE_CONFIG, "image"));
    	DeviceUtil.set(this, Const.WELCOME_LOCAL_CONFIG, "revealDate", DeviceUtil.get(this, Const.WELCOME_UPGRADE_CONFIG, "revealDate"));
    	DeviceUtil.set(this, Const.WELCOME_LOCAL_CONFIG, "exceed", DeviceUtil.get(this, Const.WELCOME_UPGRADE_CONFIG, "exceed"));
    	DeviceUtil.set(this, Const.WELCOME_LOCAL_CONFIG, "default", DeviceUtil.get(this, Const.WELCOME_UPGRADE_CONFIG, "default"));
    	DeviceUtil.set(this, Const.WELCOME_LOCAL_CONFIG, "bgcolor", DeviceUtil.get(this, Const.WELCOME_UPGRADE_CONFIG, "bgcolor"));
		
	}
    /*
     * 从本地配置中查找排期并展示内容
     */
	private void displayImage() {
//Log.d("down","4");
		String showDate = DeviceUtil.get(this, Const.WELCOME_LOCAL_CONFIG, "revealDate");
		String overplay = DeviceUtil.get(this, Const.WELCOME_LOCAL_CONFIG, "exceed");
		setContentView(R.layout.activity_welcome);
		mImageView = (ImageView) findViewById(R.id.welcomePic);
		if(showDate.equals(now) || overplay.equals("1")){
//Log.d("down" , "5");
			String mode = DeviceUtil.get(this, Const.WELCOME_LOCAL_CONFIG, "mode");
			if(mode.equals("1")){
//Log.d("down" , "6");
				try{
					//设置加载图片
					String fileName = DeviceUtil.get(this, Const.WELCOME_LOCAL_CONFIG, "image");
					fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1);
					Bitmap bit =  BitmapFactory.decodeFile(Const.SDCARD_DATA_LOCATION + fileName);
					mImageView.setImageBitmap(bit);
					//设置背景颜色
					String bgcolor = DeviceUtil.get(this, Const.WELCOME_LOCAL_CONFIG, "bgcolor");
					findViewById(R.id.bg).setBackgroundColor(Long.valueOf(bgcolor , 16).intValue());
				}catch(Exception e){
					mImageView.setImageDrawable(getResources().getDrawable(R.drawable.welcome));
				}
			}else if(mode.equals("2")){
				
			}else{
				
			}
		}
		// TODO Auto-generated method stub
		
	}

	Handler mHandler = new Handler() {  
        public void handleMessage(Message msg) {  
  
            switch (msg.what) {  
            case GOTO_MAIN_ACTIVITY:
//DeviceUtil.set(WelcomeActivity.this,"UPDATE_DATE" , "");
                String update_date = DeviceUtil.get(WelcomeActivity.this, "UPDATE_DATE");
                if(!update_date.equals(now) || update_date.equals("")){
//Log.d("down" , "7");
                	DeviceUtil.set(WelcomeActivity.this, "UPDATE_DATE" , now);
                	Intent i = new Intent(WelcomeActivity.this, DownloadService.class);
                	i.putExtra("date", upgradeShowDate);
                	startService(i);
                }
            	
            	Intent intent = new Intent(WelcomeActivity.this, IndexActivity.class);  
                startActivity(intent);  
                
                finish();  
                break;  
            default:  
                break;  
            }  
        };  
    };  
  
    public class MyTimer extends Thread {  
        public MyTimer() {  
            // TODO Auto-generated constructor stub  
        }  
        @Override  
        public void run() {  
            // TODO Auto-generated method stub  
            try {  
                Thread.sleep(1500);// 线程暂停时间，单位毫秒  
                mHandler.sendEmptyMessage(GOTO_MAIN_ACTIVITY);  
            } catch (InterruptedException e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            }  
        }  
    }
    
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return false;
	}

}
