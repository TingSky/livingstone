package com.joker.livingstone;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.joker.livingstone.LoginActivity.BtnListener;
import com.joker.livingstone.LoginActivity.LoginTask;
import com.joker.livingstone.util.Const;
import com.joker.livingstone.util.DeviceUtil;
import com.joker.livingstone.util.DialogHelper;
import com.joker.livingstone.util.HttpHelper;
import com.umeng.analytics.MobclickAgent;


public class RegActivity extends BaseActivity{
	public static final String TAG = "RegActivity";

    
    private int userid;
    
    private EditText phoneView;
    private EditText passwordView;
    private EditText nicknameView;
    
    private String phone;
    private String password;
    private String nickname;
    
    private Button cancelBtn;
    private Button regBtn;
    
    private Map<String, String> map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reg);
		
		findView();
		setTitle("注册");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

	}
	private void findView() {
		phoneView = (EditText)findViewById(R.id.phone);
		passwordView = (EditText)findViewById(R.id.password);
		nicknameView = (EditText)findViewById(R.id.nickname);

		cancelBtn = (Button) findViewById(R.id.btn_back);
		regBtn = (Button) findViewById(R.id.btn_ok);
		
		
		BtnListener b = new BtnListener();
		cancelBtn.setOnClickListener(b);
		regBtn.setOnClickListener(b);
	}
	
	class BtnListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			if(v.getId() == R.id.btn_ok){
				if(checkInput()){
					map = new HashMap<String, String>();
					map.put("mobileNo", phone);
					map.put("password", password);
					map.put("userName", nickname);
					
					String url = Const.PATH + "mobileRegister";
					new RegTask().execute(url);
				}
			}else{
				RegActivity.this.finish();
			}
		}
	}
	
	
	private boolean checkInput() {
		phone = phoneView.getText().toString().trim();
		password = passwordView.getText().toString().trim();
		nickname = nicknameView.getText().toString().trim();
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
		
		if(password.length()<6){
			Toast.makeText(this, "密码长度太短，请重新输入", Toast.LENGTH_LONG).show();
			return false;
		}
		String format = "[a-zA-Z0-9](@?+\\w){5,19}+";
		if(!password.matches(format)){
			Toast.makeText(this, "您的密码格式不对，改个简单的吧", Toast.LENGTH_LONG).show();
			return false;
		}
		
		if(password.length()>20){
			Toast.makeText(this, "您的密码也太长了吧，改个简单的吧", Toast.LENGTH_LONG).show();
			return false;
		}
		
		if(nickname.equals("")){
			Toast.makeText(this, "昵称不能为空哦，随便填个以后可以修改的～", Toast.LENGTH_LONG).show();
			return false;
		}
		
		if(nickname.length()>10){
			Toast.makeText(this, "您的昵称也太长了吧，改个简单的吧", Toast.LENGTH_LONG).show();
			return false;
		}
		
		return true;
	}
	
	class RegTask extends AsyncTask<String, Void, String>{
		@Override
		protected void onPreExecute() {
			DialogHelper.showDialog(RegActivity.this);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... url) {
			String data = HttpHelper.getString(RegActivity.this , url[0] , map);
			JSONObject json;
			try {
				json = new JSONObject(data);
				if (json.getInt("rtn") == 0) {
					DeviceUtil.set(RegActivity.this, "USERID",json.getJSONObject("data").getString("userId"));
					return "OK";
				}else{
					return json.getString("data");
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
				MobclickAgent.onEvent(RegActivity.this, "注册成功");
				DialogHelper.showFinishDialog(RegActivity.this, "注册成功！");
			}else if(result.equals("ERR")){
				DialogHelper.showFinishDialog(RegActivity.this, "出错了，请与我们联系，谢谢");
			}else if(result.equals("10")){
				MobclickAgent.onEvent(RegActivity.this, "imei为空");
				DialogHelper.showFinishDialog(RegActivity.this, "imei为空");	
			}else if(result.equals("11")){
				DialogHelper.showFinishDialog(RegActivity.this, "手机号码为空" , false);	
			}else if(result.equals("12")){
				DialogHelper.showFinishDialog(RegActivity.this, "手机号码不合法哦" , false);	
			}else if(result.equals("13")){
				DialogHelper.showFinishDialog(RegActivity.this, "用户昵称为空" , false);	
			}else if(result.equals("14")){
				DialogHelper.showFinishDialog(RegActivity.this, "出错了，请与我们联系，谢谢");	
			}else if(result.equals("15")){
				DialogHelper.showFinishDialog(RegActivity.this, "该手机号码已经注册了哦" , false);	
			}
			super.onPostExecute(result);
		}
	}
	


	
	
}
