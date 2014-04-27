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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.joker.livingstone.util.Const;
import com.joker.livingstone.util.DeviceUtil;
import com.joker.livingstone.util.DialogHelper;
import com.joker.livingstone.util.HttpHelper;
import com.umeng.analytics.MobclickAgent;


public class EasterActivity extends BaseActivity{
	
    
    private int userid;
    
    private View content;
    private TextView mTextView;
    private View regForm;
    private MenuItem  reg;
//    private Button hangout;
    private EditText phoneView;
    private EditText passwordView;
    private EditText nicknameView;
    
    private String phone;
    private String password;
    private String nickname;
    
    //配置从本地获取
    private boolean local = false;
    
    private Map<String, String> map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_easter);
		
		findView();
		setTitle("感谢神！");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getRank();

	}
	private void findView() {
		content = findViewById(R.id.content);
		content.setVisibility(View.INVISIBLE);
		
		mTextView = (TextView)findViewById(R.id.text);
		regForm = findViewById(R.id.reg_area);
		phoneView = (EditText)findViewById(R.id.phone);
		passwordView = (EditText)findViewById(R.id.password);
		nicknameView = (EditText)findViewById(R.id.nickname);

	}
	
	
	
	
	
	private void getRank() {
		//如果本地存放了彩蛋编号就不从服务器拿数据
		String eggid = DeviceUtil.get(this,"EGGID");
		if(eggid == null ||eggid.equals("")){
			MobclickAgent.onEvent(this, "发现彩蛋");
			getRankFromServer();
		}else{
			MobclickAgent.onEvent(this, "重进彩蛋");
			local = true;
			displayRank(eggid);
		}
		
	}
	
	/*
	 * 展示找到彩蛋的排名
	 * 如果已经注册则不显示注册信息
	 */
	private void displayRank(String rank){
		content.setVisibility(View.VISIBLE);
		String s = getResources().getString(R.string.reg_tag);
		mTextView.setText(s.format(s, rank));
		
		String userid = DeviceUtil.get(EasterActivity.this , "USERID");
		if(userid.equals("")){
			local = false;
		}else{
			regForm.setVisibility(View.GONE);
			if(!local) reg.setVisible(false);
		}
		
	}
	
	private void getRankFromServer() {
		String imei = DeviceUtil.getImei(this);
		String url = Const.PATH + "applyEgg?imei=" + imei;
		new AsyncTask<String, Void, Integer>() {
			@Override
			protected void onPreExecute() {
				DialogHelper.showDialog(EasterActivity.this);
				super.onPreExecute();
			}
			@Override
			protected Integer doInBackground(String... url) {
				String data = HttpHelper.getString(url[0]);
				JSONObject json;
				try {
					json = new JSONObject(data);
					if (json.getInt("rtn") == 0) {
						userid = json.getJSONObject("data").getInt("userId");
						return json.getJSONObject("data").getInt("eggId");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return 0;
			}
			
			@Override
			protected void onPostExecute(Integer result) {
				DialogHelper.dismiss();
				DeviceUtil.set(EasterActivity.this, "EGGID" , result +"");
				//用户如果未注册返回userid为0
				if(userid != 0){
					DeviceUtil.set(EasterActivity.this, "USERID" , userid + "");
				}
				displayRank(result + "");
				
				super.onPostExecute(result);
			}
			
		}.execute(url);

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
	
	class regTask extends AsyncTask<String, Void, String>{
		@Override
		protected void onPreExecute() {
			DialogHelper.showDialog(EasterActivity.this);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... url) {
			String data = HttpHelper.getString(EasterActivity.this , url[0] , map);
			JSONObject json;
			try {
				json = new JSONObject(data);
				if (json.getInt("rtn") == 0) {
					DeviceUtil.set(EasterActivity.this, "USERID",json.getJSONObject("data").getString("userId"));
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
				MobclickAgent.onEvent(EasterActivity.this, "注册成功");
				DialogHelper.showFinishDialog(EasterActivity.this, "注册成功！");
			}else if(result.equals("ERR")){
				DialogHelper.showFinishDialog(EasterActivity.this, "出错了，请与我们联系，谢谢");
			}else if(result.equals("10")){
				MobclickAgent.onEvent(EasterActivity.this, "imei为空");
				DialogHelper.showFinishDialog(EasterActivity.this, "imei为空");	
			}else if(result.equals("11")){
				DialogHelper.showFinishDialog(EasterActivity.this, "手机号码为空" , false);	
			}else if(result.equals("12")){
				DialogHelper.showFinishDialog(EasterActivity.this, "手机号码不合法哦" , false);	
			}else if(result.equals("13")){
				DialogHelper.showFinishDialog(EasterActivity.this, "用户昵称为空" , false);	
			}else if(result.equals("14")){
				DialogHelper.showFinishDialog(EasterActivity.this, "出错了，请与我们联系，谢谢");	
			}else if(result.equals("15")){
				DialogHelper.showFinishDialog(EasterActivity.this, "该手机号码已经注册了哦" , false);	
			}
			super.onPostExecute(result);
		}
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//配置从本地获取则不出现注册按钮
		if(local) return false;
//		 Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.easter, menu);
		reg = menu.findItem(R.id.action_reg);
		
		reg.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if(regForm.getVisibility() !=  View.VISIBLE){
					MobclickAgent.onEvent(EasterActivity.this, "点击注册");
					mTextView.setVisibility(View.GONE);
					regForm.setVisibility(View.VISIBLE);
					phoneView.requestFocus();
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
					reg.setTitle("确定");
					
					return false;
				}
				MobclickAgent.onEvent(EasterActivity.this, "提交注册");
				if(!checkInput()) {
					MobclickAgent.onEvent(EasterActivity.this, "注册验证失败");
					return false;
				}
				else{
					map = new HashMap<String, String>();
					map.put("mobileNo", phone);
					map.put("password", password);
					map.put("userName", nickname);
					
					String url = Const.PATH + "mobileRegister";
					new regTask().execute(url);
				}
				return false;
			}
		});
		
		
		return true;
	}
	
	
}
