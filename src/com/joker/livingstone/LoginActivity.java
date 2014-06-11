package com.joker.livingstone;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.joker.livingstone.util.Const;
import com.joker.livingstone.util.DeviceUtil;
import com.joker.livingstone.util.DialogHelper;
import com.joker.livingstone.util.HttpHelper;
import com.umeng.analytics.MobclickAgent;


public class LoginActivity extends BaseActivity{
	public static final String TAG = "LoginActivity";

    
    private int userid;
    
    private MenuItem  reg;
    
    private EditText phoneView;
    private EditText passwordView;
    
    private String phone;
    private String password;
    
    private Button cancelBtn;
    private Button loginBtn;
    
    
    private Map<String, String> map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		findView();
		setTitle("登录");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

	}
	private void findView() {
		
		phoneView = (EditText)findViewById(R.id.phone);
		passwordView = (EditText)findViewById(R.id.password);
		
		cancelBtn = (Button) findViewById(R.id.btn_back);
		loginBtn = (Button) findViewById(R.id.btn_ok);
		
		
		BtnListener b = new BtnListener();
		cancelBtn.setOnClickListener(b);
		loginBtn.setOnClickListener(b);
	}
	
	class BtnListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			if(v.getId() == R.id.btn_ok){
				if(checkInput()){
					map = new HashMap<String, String>();
					map.put("mobileNo", phone);
					map.put("password", password);
					new LoginTask().execute(Const.PATH + "mobileLogin");
				}
			}else{
				LoginActivity.this.finish();
				
			}
		}
	}
	
	
	
	private boolean checkInput() {
		phone = phoneView.getText().toString().trim();
		password = passwordView.getText().toString().trim();
		if(phone.length() != 11 || !phone.startsWith("1")){
			Toast.makeText(this, "手机号码输入有误，请检查后重新输入", Toast.LENGTH_LONG).show();
			return false;
		}
		try {
			Long.parseLong(phone);
		} catch (Exception e) {
			Toast.makeText(this, "手机号码输入有误，请检查后重新输入", Toast.LENGTH_LONG).show();
			return false;
		}
		
		
		return true;
	}
	
	class LoginTask extends AsyncTask<String, Void, String>{
		@Override
		protected void onPreExecute() {
			DialogHelper.showDialog(LoginActivity.this);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... url) {
			String data = HttpHelper.getString(LoginActivity.this , url[0] , map);
			JSONObject json;
			try {
				json = new JSONObject(data);
				if (json.getInt("rtn") == 0) {
					DeviceUtil.set(LoginActivity.this, "USERID" , json.getJSONObject("data").getJSONObject("user").getString("userId"));
					DeviceUtil.set(LoginActivity.this, "NICKNAME" , json.getJSONObject("data").getJSONObject("user").getString("userName"));
					DeviceUtil.set(LoginActivity.this, "PHONE" , json.getJSONObject("data").getJSONObject("user").getString("mobileNo"));
					DeviceUtil.set(LoginActivity.this, "VOTE" , json.getJSONObject("data").getInt("vote") + "");
					return "OK";
				}else{
					return json.getInt("data") + "";
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return "ERR";
		}

		@Override
		protected void onPostExecute(String result) {
			DialogHelper.dismiss();
			if(result.equals("OK")){
				MobclickAgent.onEvent(LoginActivity.this, "登录成功");
				Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_LONG).show();
				LoginActivity.this.finish();
			}else if(result.equals("ERR")){
				DialogHelper.showFinishDialog(LoginActivity.this, "出错了，请与我们联系，谢谢");
			}else if(result.equals("2")){
				DialogHelper.showFinishDialog(LoginActivity.this, "您输入的手机号码有误！" , false);	
			}else if(result.equals("17")){
				DialogHelper.showFinishDialog(LoginActivity.this, "您输入的密码不正确！" , false);	
			}
			super.onPostExecute(result);
		}
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//		 Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.easter, menu);
		reg = menu.findItem(R.id.action_reg);
		reg.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent i = new Intent(LoginActivity.this , RegActivity.class);
				LoginActivity.this.startActivity(i);
				LoginActivity.this.finish();
				return false;
			}
		});
			
		
		
		return true;
	}
	
	
}
