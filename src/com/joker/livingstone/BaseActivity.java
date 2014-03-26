package com.joker.livingstone;

import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;

import com.umeng.analytics.MobclickAgent;

public abstract class BaseActivity extends ActionBarActivity {
//	protected TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	
}
