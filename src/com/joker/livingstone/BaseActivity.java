package com.joker.livingstone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.view.MenuItem;

import com.umeng.analytics.MobclickAgent;

public abstract class BaseActivity extends ActionBarActivity {
	
	public static final String TAG = ""; 

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(TAG);
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageStart(TAG);
		MobclickAgent.onPause(this);
	}
	

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            // This is called when the Home (Up) button is pressed
	            // in the Action Bar.
	            Intent i = new Intent(this, IndexActivity.class);
	            i.addFlags(
	                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
	                    Intent.FLAG_ACTIVITY_NEW_TASK);
	            startActivity(i);
	            finish();
	            return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
}
