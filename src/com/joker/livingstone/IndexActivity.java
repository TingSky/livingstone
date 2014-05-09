package com.joker.livingstone;

import java.util.HashMap;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.joker.livingstone.util.DBHelper;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;


public class IndexActivity extends BaseActivity{
	public static final String TAG = "IndexActivity";

	
	private ActionBarDrawerToggle drawerToggle;
	private DrawerLayout drawerLayout;
	
	private String[] menuList;
    private ListView drawerList;
    private CharSequence title;
    
    private ActionBar bar;
    
    private GridView mGridView;
    
    private MenuItem searchMenuItem;
    private SearchView search;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_index);
		mGridView = (GridView) findViewById(R.id.gridView);
		UmengUpdateAgent.setUpdateCheckConfig(false);
		UmengUpdateAgent.silentUpdate(this);
		
		initDrawerAndActionBar();
		loadBibleData();
        

	}
	
	/**
	 * 初始化Drawer和ActionBar
	 */
	private void initDrawerAndActionBar() {
		menuList = getResources().getStringArray(R.array.menu);
		title = getTitle();
		
		drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
		drawerList = (ListView) findViewById(R.id.left_drawer);
		drawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, menuList));
		drawerList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				drawerLayout.closeDrawers();
			    if(position == 1){
			    	startActivity(new Intent(IndexActivity.this , FeedbackActivity.class));
			    }else if(position == 2){
			    	Toast.makeText(IndexActivity.this, "『关于』页面暂未开放，敬请期待...", Toast.LENGTH_LONG).show();
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
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);

        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeButtonEnabled(true);

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
//			TextView t = (TextView)view.findViewById(R.id.bookName);
//			Toast.makeText(IndexActivity.this, position + "", Toast.LENGTH_LONG).show();
			
			Cursor c = (Cursor)parent.getItemAtPosition(position);
			Intent i = new Intent(IndexActivity.this , ChapterActivity.class);
			i.putExtra("bookId", c.getInt(0));
//			i.putExtra("bookId", c.getString(c.getColumnIndex("_id")));
			i.putExtra("bookName", c.getString(1));
//			i.putExtra("bookId", c.getString(c.getColumnIndex("bookName")));
			IndexActivity.this.startActivity(i);
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
