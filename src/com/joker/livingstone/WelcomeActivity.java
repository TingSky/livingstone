package com.joker.livingstone;

import com.joker.livingstone.util.DeviceUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;


public class WelcomeActivity extends BaseActivity {
	private static final int GOTO_MAIN_ACTIVITY = 0;  
	private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	DeviceUtil.initParams(this);
        //全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN); 
        setContentView(R.layout.activity_welcome);
        mImageView = (ImageView) findViewById(R.id.welcomePic);
        
        String isDefalut = DeviceUtil.get(this, "online" , "default");
        //如果更新配置中使用默认素材或不存在更新配置，则查找本地配置
        if(isDefalut.equals("1") || isDefalut.equals("")){
        	setContent();
        }else{
        	
        }
        
        
        
        
        MyTimer timer = new MyTimer();  
        timer.start();//启动线程  
    }  
  
    private void setContent() {
		// TODO Auto-generated method stub
		
	}

	Handler mHandler = new Handler() {  
        public void handleMessage(Message msg) {  
  
            switch (msg.what) {  
            case GOTO_MAIN_ACTIVITY:  
                Intent intent = new Intent();  
                intent.setClass(WelcomeActivity.this, IndexActivity.class);  
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
                Thread.sleep(1000);// 线程暂停时间，单位毫秒  
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
