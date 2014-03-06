package com.joker.livingstone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;


public class WelcomeActivity extends Activity {
	private static final int GOTO_MAIN_ACTIVITY = 0;  


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN); 
        
        setContentView(R.layout.activity_welcome);

        MyTimer timer = new MyTimer();  
        timer.start();//启动线程  
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
                Thread.sleep(1);// 线程暂停时间，单位毫秒  
                mHandler.sendEmptyMessage(GOTO_MAIN_ACTIVITY);  
            } catch (InterruptedException e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            }  
        }  
    }  
}
