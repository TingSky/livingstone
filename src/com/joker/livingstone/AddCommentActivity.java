package com.joker.livingstone;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.joker.livingstone.service.SendCommentService;
import com.joker.livingstone.util.Const;


public class AddCommentActivity extends BaseActivity{
	
	private ActionBarDrawerToggle drawerToggle;
	private DrawerLayout drawerLayout;
	
	private String[] menuList;
    private ListView drawerList;
    
    private ActionBar bar;
    
    private EditText mEditText;
    private int bookId;
    private String bookName;
    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_comment);
		mEditText = (EditText) findViewById(R.id.mEditText);
		
		
		getDataFromIntent();
		initDrawerAndActionBar();
//		loadChapterData();
        
        

	}
	
	private void getDataFromIntent(){
		Intent i = getIntent();
		bookId = i.getIntExtra("bookId", 1);
		bookName = i.getStringExtra("bookName");
	}
	
	/**
	 * 初始化Drawer和ActionBar
	 */
	private void initDrawerAndActionBar() {
		
		bar = getSupportActionBar();

        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeButtonEnabled(true);

	}
	
	



	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_comment, menu);
		menu.findItem(R.id.send).setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				send();
				return true;
			}
		});
		return super.onCreateOptionsMenu(menu);
	}
	
	private void send() {
		Intent i = getIntent();
		i.putExtra("content", mEditText.getText().toString());
		i.setClass(this, SendCommentService.class);
		startService(i);
		
//		Log.d("123","asdfa");
//		NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this);
//		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher); 
//		
//		notifyBuilder.setContentTitle(getResources().getString(R.string.app_name))
//				.setContentText(mEditText.getText().toString())
//				.setSmallIcon(R.drawable.ic_launcher)
//				.setLargeIcon(bitmap)
//				.setWhen(System.currentTimeMillis())
//				.setTicker("活石正在发送评论...")
//				.setOngoing(true);
//		
//		
//		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);  
//        mNotificationManager.notify(Const.SEND_COMMENT_ID, notifyBuilder.build());
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
	}
	 

}
