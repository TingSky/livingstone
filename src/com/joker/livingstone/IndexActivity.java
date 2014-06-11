package com.joker.livingstone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.joker.livingstone.util.Const;
import com.joker.livingstone.util.DBHelper;
import com.joker.livingstone.util.DeviceUtil;
import com.joker.livingstone.util.DialogHelper;
import com.joker.livingstone.util.HttpHelper;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;


public class IndexActivity extends BaseActivity{
	public static final String TAG = "IndexActivity";

	
	private ActionBarDrawerToggle drawerToggle;
	private DrawerLayout drawerLayout;
	
    private ListView drawerList;
    private DrawerAdapter adapter;
    private CharSequence title;
    
    private ActionBar bar;
    
    private GridView mGridView;
    
    private MenuItem searchMenuItem;
    private SearchView search;
    private String userid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//DeviceUtil.set(this, "USERID", 10001+"");
		setContentView(R.layout.activity_index);
		mGridView = (GridView) findViewById(R.id.gridView);
		if(Const.FIRST){
			UmengUpdateAgent.setUpdateCheckConfig(false);
			UmengUpdateAgent.silentUpdate(this);
			UmengUpdateAgent.setUpdateCheckConfig(false);
			Const.FIRST = false;
		}
		
		initDrawerAndActionBar();
		loadBibleData();
        

	}
	
	/**
	 * 初始化Drawer和ActionBar
	 */
	private void initDrawerAndActionBar() {
		title = getTitle();
		
		drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
		drawerList = (ListView) findViewById(R.id.left_drawer);
		adapter = new DrawerAdapter(); 
		drawerList.setAdapter(adapter);
		drawerList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				drawerLayout.closeDrawers();
				if(position == 0){
					if(DeviceUtil.get(IndexActivity.this, "USERID").equals("")){
						Intent i = new Intent(IndexActivity.this, LoginActivity.class);
						IndexActivity.this.startActivity(i);
					}else{
						DialogHelper.showLogoutDialog(IndexActivity.this);
					}
//				}else if(position == 1){
//					Toast.makeText(IndexActivity.this, "『志愿者计划』页面暂未开放，敬请期待...", Toast.LENGTH_LONG).show();
				}else if(position == 1){
			    	startActivity(new Intent(IndexActivity.this , FeedbackActivity.class));
			    }else if(position == 2){
			    	Intent i = new Intent(IndexActivity.this , AboutActivity.class);
			    	IndexActivity.this.startActivity(i);
				}
			}
		});
		
		bar = getSupportActionBar();
		drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
            	bar.setTitle(R.string.app_name);
            	supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
            	bar.setTitle(R.string.drawer_open);
            	adapter.notifyDataSetChanged();
            	userid = DeviceUtil.get(IndexActivity.this, "USERID");
            	if(!userid.equals("")){
            		new SyncUserTask().execute(null,null,null);
            	}
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);

        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeButtonEnabled(true);
	}
	
	class SyncUserTask extends AsyncTask<Void, Void, Void>{
		HashMap<String , String> map = new HashMap<String, String>();
		String url = Const.PATH + "mobileSyncUser";
		
		@Override
		protected Void doInBackground(Void...v) {
			String data = HttpHelper.getString(IndexActivity.this , url , map);
			JSONObject json;
			try {
				json = new JSONObject(data);
				if (json.getInt("rtn") == 0) {
//					DeviceUtil.set(IndexActivity.this, "USERID" , json.getJSONObject("data").getJSONObject("user").getString("userId"));
					DeviceUtil.set(IndexActivity.this, "NICKNAME" , json.getJSONObject("data").getJSONObject("user").getString("userName"));
					DeviceUtil.set(IndexActivity.this, "PHONE" , json.getJSONObject("data").getJSONObject("user").getString("mobileNo"));
					DeviceUtil.set(IndexActivity.this, "VOTE" , json.getJSONObject("data").getInt("vote") + "");
					return null ;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			adapter.notifyDataSetChanged();
			super.onPostExecute(result);
		}

	}
	
	
	class DrawerAdapter extends BaseAdapter{

		List<View> data = new ArrayList<View>();
		LayoutInflater mInflater = (LayoutInflater) IndexActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		String[] item = IndexActivity.this.getResources().getStringArray(R.array.menu);
		
		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(position == 0){
				RelativeLayout layout = (RelativeLayout) mInflater.inflate(R.layout.drawer_user, parent , false);
				userid = DeviceUtil.get(IndexActivity.this, "USERID");
				if(userid.equals("")) return layout;
				
				View userLogin = layout.findViewById(R.id.user_login);
				userLogin.setVisibility(View.GONE);
				View userInfo = layout.findViewById(R.id.user_info);
				userInfo.setVisibility(View.VISIBLE);
				
				
				TextView text = (TextView) layout.findViewById(R.id.user_nickname);
				text.setText(DeviceUtil.get(IndexActivity.this, "NICKNAME"));
				TextView vote = (TextView) layout.findViewById(R.id.user_thanks);
				vote.setText(DeviceUtil.get(IndexActivity.this, "VOTE") + "赞");
				
//				ImageView image = (ImageView) layout.findViewById(R.id.user_photo);
//				image.
				return layout;
			}else{
				TextView view = (TextView)mInflater.inflate(R.layout.drawer_list_item, parent , false);
				view.setText(item[position-1]);
				return view;
			}
		}

		
	}
	
	
	private void loadBibleData(){
		String sql = "SELECT seqId as _id , bookName , chapterCount , isNew from book order by bookNo";
		Cursor c = DBHelper.get().rawQuery(sql, null);
		
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(
//		CursorAdapter adapter = new CursorAdapter(
				IndexActivity.this,
				R.layout.directory,
				c,
				new String[] {"bookName" , "chapterCount"},
				new int[] {R.id.bookName , R.id.chapter},
				CursorAdapter.NO_SELECTION
		);
		mGridView.setAdapter(adapter);
		mGridView.setOnItemClickListener(new ItemClickListener());
	}
	
	class CursorAdapter extends SimpleCursorAdapter{
		
		private LayoutInflater inflater;

		public CursorAdapter(Context context, int layout, Cursor c,
				String[] from, int[] to, int flags) {
			super(context, layout, c, from, to, flags);
			inflater = (LayoutInflater)context.getSystemService("layout_inflater");
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (!this.mDataValid) {
	            throw new IllegalStateException("this should only be called when the cursor is valid");
	        }
	        if (!this.mCursor.moveToPosition(position)) {
	            throw new IllegalStateException("couldn't move cursor to position " + position);
	        }
	        View v;
	        
	        if(position < 39){
	        	v = inflater.inflate(R.layout.directory, parent, false);
	        }else{
	        	v = inflater.inflate(R.layout.directory1, parent, false);
	        }
	        bindView(v, this.mContext, this.mCursor);
	        return v;
			
		}

	}
	


	class ItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			TextView count = (TextView)view.findViewById(R.id.chapter);
			Cursor c = (Cursor)parent.getItemAtPosition(position);
			if(count.getText().toString().equals("1")){
				Intent i = new Intent(IndexActivity.this , SectionActivity.class);
				i.putExtra("bookId", c.getInt(0));
//				i.putExtra("bookId", c.getString(c.getColumnIndex("_id")));
				i.putExtra("bookName", c.getString(1));
//				i.putExtra("bookId", c.getString(c.getColumnIndex("bookName")));
				IndexActivity.this.startActivity(i);
			}else{
				Intent i = new Intent(IndexActivity.this , ChapterActivity.class);
				i.putExtra("bookId", c.getInt(0));
//				i.putExtra("bookId", c.getString(c.getColumnIndex("_id")));
				i.putExtra("bookName", c.getString(1));
//				i.putExtra("bookId", c.getString(c.getColumnIndex("bookName")));
				IndexActivity.this.startActivity(i);
				
			}
			
		}

	}
	
	
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.index, menu);
		
		SearchManager SManager =  (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.action_search);
        search = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        search.setSearchableInfo(SManager.getSearchableInfo(getComponentName()));
        
        search.setQueryHint(getResources().getString(R.string.search_hint));
//        search.setOnSearchClickListener(new menuClickListener());
        search.setOnQueryTextListener(new searchListener());
        
//        search.setIconifiedByDefault(true);
		
		
		
		return true;
	}
	
	class searchListener implements SearchView.OnQueryTextListener{


		@Override
		public boolean onQueryTextSubmit(String text) {
//			Toast.makeText(IndexActivity.this, text, Toast.LENGTH_LONG).show();
			
	        Intent i = new Intent(IndexActivity.this , SearchActivity.class);
	        i.putExtra(SearchManager.QUERY, text);
	        
	        
	        MenuItemCompat.collapseActionView(searchMenuItem);
	        search.setQuery(null, false);
	        search.clearFocus();
	        
	        startActivity(i);
	        
	        HashMap<String,String> map = new HashMap<String, String>();
	        map.put("搜索内容", text);
	        MobclickAgent.onEvent(IndexActivity.this, "首页搜索提交", map);
	        
//	        i.putExtra(SearchManager.APP_DATA, value)
//			IndexActivity.this.onSearchRequested();
			return true;
		}

		@Override
		public boolean onQueryTextChange(String paramString) {
			return true;
		}
		
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
	    super.onPostCreate(savedInstanceState);
	    //有了这句，actionbar标题栏图标才会出现汉堡（本来是箭头）
	    drawerToggle.syncState();
	}
	 
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//有了这句，点击顶部汉堡才能出现drawer
	    if (drawerToggle.onOptionsItemSelected(item)) {
	    	return true;
	    }
	    return super.onOptionsItemSelected(item);
	}

	

}
