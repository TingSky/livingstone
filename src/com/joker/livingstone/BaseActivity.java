package com.joker.livingstone;

import android.support.v7.app.ActionBarActivity;

import com.umeng.analytics.MobclickAgent;

public abstract class BaseActivity extends ActionBarActivity {

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
