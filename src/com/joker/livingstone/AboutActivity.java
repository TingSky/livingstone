package com.joker.livingstone;

import java.util.Map;

import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;


public class AboutActivity extends BaseActivity{
	public static final String TAG = "AboutActivity";
    
    
    private Map<String, String> map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		setTitle("关于");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		findViewById(R.id.qq).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ClipboardManager c = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
				c.setText("371555068");
				Toast.makeText(AboutActivity.this, "QQ群号已复制到剪贴板～", Toast.LENGTH_LONG).show();
			}
		});
	}
	
	
}
