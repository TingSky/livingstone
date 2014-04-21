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

		initDrawerAndActionBar("��л��");
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
	 * ��ʼ��Drawer��ActionBar
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
		//������ش���˲ʵ���žͲ��ӷ�����������
		String eggid = DeviceUtil.get(this,"EGGID");
		if(eggid == null ||eggid.equals("")){
			getRankFromServer();
		}else{
			displayRank(eggid);
		}
		
	}
	
	/*
	 * չʾ�ҵ��ʵ�������
	 * ����Ѿ�ע������ʾע����Ϣ
	 */
	private void displayRank(String rank){
		content.setVisibility(View.VISIBLE);
		String s = getResources().getString(R.string.reg_tag);
		mTextView.setText(s.format(s, rank));
		
		//�Ѿ�ע���
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
				//�û����δע�᷵��useridΪ0
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
			Toast.makeText(this, "�ֻ��������������������������", Toast.LENGTH_LONG).show();
			return false;
		}
		try {
			Long.parseLong(phone);
		} catch (Exception e) {
			Toast.makeText(this, "�ֻ��������������������������", Toast.LENGTH_LONG).show();
			return false;
		}
		
		if(password.length()<6){
			Toast.makeText(this, "���볤��̫�̣�����������", Toast.LENGTH_LONG).show();
			return false;
		}
		
		if(password.length()>20){
			Toast.makeText(this, "��������Ҳ̫���˰ɣ��ĸ��򵥵İ�", Toast.LENGTH_LONG).show();
			return false;
		}
		
		if(nickname.equals("")){
			Toast.makeText(this, "�ǳƲ���Ϊ��Ŷ���������Ժ�����޸ĵġ�", Toast.LENGTH_LONG).show();
			return false;
		}
		
		if(nickname.length()>10){
			Toast.makeText(this, "�����ǳ�Ҳ̫���˰ɣ��ĸ��򵥵İ�", Toast.LENGTH_LONG).show();
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
				DialogHelper.showFinishDialog(EasterActivity.this, "ע��ɹ���");
			}else if(result.equals("ERR")){
				DialogHelper.showFinishDialog(EasterActivity.this, "�����ˣ�����������ϵ��лл");
			}else if(result.equals("10")){
				DialogHelper.showFinishDialog(EasterActivity.this, "imeiΪ��");	
			}else if(result.equals("11")){
				DialogHelper.showFinishDialog(EasterActivity.this, "�ֻ�����Ϊ��" , false);	
			}else if(result.equals("12")){
				DialogHelper.showFinishDialog(EasterActivity.this, "�ֻ����벻�Ϸ�Ŷ" , false);	
			}else if(result.equals("13")){
				DialogHelper.showFinishDialog(EasterActivity.this, "�û��ǳ�Ϊ��" , false);	
			}else if(result.equals("14")){
				DialogHelper.showFinishDialog(EasterActivity.this, "�����ˣ�����������ϵ��лл");	
			}else if(result.equals("15")){
				DialogHelper.showFinishDialog(EasterActivity.this, "���ֻ������Ѿ�ע����Ŷ" , false);	
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
					reg.setTitle("ȷ��");
					
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
	    //������䣬actionbar������ͼ��Ż���ֺ����������Ǽ�ͷ��
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
		//������䣬��������������ܳ���drawer
	    if (drawerToggle.onOptionsItemSelected(item)) {
	    	return true;
	    }
	 
	    return super.onOptionsItemSelected(item);
	}
	
	
}
