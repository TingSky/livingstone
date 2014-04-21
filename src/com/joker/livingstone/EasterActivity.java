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


public class EasterActivity extends BaseActivity{
	
	private ActionBarDrawerToggle drawerToggle;
	private DrawerLayout drawerLayout;
	
	private String[] menuList;
    private ListView drawerList;
    
    private ActionBar bar;
    
    private int userid;
    
    private View content;
    private TextView mTextView;
    private View regForm;
//    private Button reg;
//    private Button hangout;
    private EditText phoneView;
    private EditText passwordView;
    private EditText nicknameView;
    
    private String phone;
    private String password;
    private String nickname;
    
    private MenuItem reg;
    
    private Map<String, String> map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_easter);
		
		findView();

		initDrawerAndActionBar("感谢神！");
        getRank();
//        setListener();

	}
	private void findView() {
		content = findViewById(R.id.content);
		content.setVisibility(View.INVISIBLE);
		
		mTextView = (TextView)findViewById(R.id.text);
		regForm = findViewById(R.id.reg_area);
//		reg = (Button)findViewById(R.id.reg);
//		hangout = (Button)findViewById(R.id.hangout);
		phoneView = (EditText)findViewById(R.id.phone);
		passwordView = (EditText)findViewById(R.id.password);
		nicknameView = (EditText)findViewById(R.id.nickname);

	}
	
	
	
	
//	private void getDataFromIntent(){
//		Intent i = getIntent();
//		bookId = i.getIntExtra("bookId", 1);
//		bookName = i.getStringExtra("bookName");
//	}
	
	/**
	 * 初始化Drawer和ActionBar
	 */
	private void initDrawerAndActionBar(final String title) {
		menuList = getResources().getStringArray(R.array.menu);
		setTitle(title);
		
		drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
		drawerList = (ListView) findViewById(R.id.left_drawer);
		drawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, menuList));
		
		bar = getSupportActionBar();
		drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
            	bar.setTitle(title);
            	supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
            	bar.setTitle(R.string.drawer_open);
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);

        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeButtonEnabled(true);

	}
	
	
	private void getRank() {
		//如果本地存放了彩蛋编号就不从服务器拿数据
		String eggid = DeviceUtil.get(this,"EGGID");
		if(eggid == null ||eggid.equals("")){
			getRankFromServer();
		}else{
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
		
		//已经注册过
		String userid = DeviceUtil.get(EasterActivity.this , "USERID");
		if(!userid.equals("")){
			regForm.setVisibility(View.GONE);
			reg.setVisible(false);
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
	
//	private void setListener(){
//		ButtonListener l = new ButtonListener();
//		reg.setOnClickListener(l);
//		hangout.setOnClickListener(l);
//				
//	}
	
//	class ButtonListener implements OnClickListener{
//
//		@Override
//		public void onClick(View v) {
//			if(v.getTag().equals("reg")){
//				if(regForm.getVisibility() !=  View.VISIBLE){
//					mTextView.setVisibility(View.GONE);
//					regForm.setVisibility(View.VISIBLE);
//					phoneView.requestFocus();
//					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//					
//					return;
//				}
//				
//				if(!checkInput()) return ;
//				else{
//					map = new HashMap<String, String>();
//					map.put("mobileNo", phone);
//					map.put("password", password);
//					map.put("userName", nickname);
//					
//					String url = Const.PATH + "mobileRegister";
//					new regTask().execute(url);
//				}
//			}else{
//				EasterActivity.this.finish();
//			}
//		}
//		
//	}
	
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
				DialogHelper.showFinishDialog(EasterActivity.this, "注册成功！");
			}else if(result.equals("ERR")){
				DialogHelper.showFinishDialog(EasterActivity.this, "出错了，请与我们联系，谢谢");
			}else if(result.equals("10")){
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
	
//	class CursorAdapter extends SimpleCursorAdapter{
//		
//		private LayoutInflater inflater;
//
//		public CursorAdapter(Context context, int layout, Cursor c,
//				String[] from, int[] to, int flags) {
//			super(context, layout, c, from, to, flags);
//			inflater = (LayoutInflater)context.getSystemService("layout_inflater");
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			if (!this.mDataValid) {
//	            throw new IllegalStateException("this should only be called when the cursor is valid");
//	        }
//	        if (!this.mCursor.moveToPosition(position)) {
//	            throw new IllegalStateException("couldn't move cursor to position " + position);
//	        }
//	        View v;
//	        
//	        if(position < 39){
//	        	v = inflater.inflate(R.layout.directory, parent, false);
//	        }else{
//	        	v = inflater.inflate(R.layout.directory1, parent, false);
//	        }
//	        bindView(v, this.mContext, this.mCursor);
//	        return v;
//			
//		}
//
//	}
//	



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//		 Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.easter, menu);
		reg = menu.findItem(R.id.action_reg);
		reg.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if(regForm.getVisibility() !=  View.VISIBLE){
					mTextView.setVisibility(View.GONE);
					regForm.setVisibility(View.VISIBLE);
					phoneView.requestFocus();
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
					reg.setTitle("确定");
					
					return false;
				}
				
				if(!checkInput()) return false;
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
		
//		SearchManager searchManager =
//		        (SearchManager) IndexActivity.this.getSystemService(Context.SEARCH_SERVICE);
//		SupportMenuItem searchMenuItem = ((SupportMenuItem) menu.findItem(R.id.action_serach));
//		SearchView searchView = (SearchView) searchMenuItem.getActionView();
//		searchView.setSearchableInfo(searchManager.getSearchableInfo(IndexActivity.this.getComponentName()));
		
		
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
	    super.onPostCreate(savedInstanceState);
	    //有了这句，actionbar标题栏图标才会出现汉堡（本来是箭头）
	    drawerToggle.syncState();
	}
	 
//	@Override
//	public void onConfigurationChanged(Configuration newConfig) {
//	    super.onConfigurationChanged(newConfig);
//	    drawerToggle.onConfigurationChanged(newConfig);
//	}
//	 
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//有了这句，点击顶部汉堡才能出现drawer
	    if (drawerToggle.onOptionsItemSelected(item)) {
	    	return true;
	    }
	 
	    return super.onOptionsItemSelected(item);
	}
	
	
}
