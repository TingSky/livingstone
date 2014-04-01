package com.joker.livingstone;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.joker.livingstone.DiscussActivity.LoadPageListener;
import com.joker.livingstone.util.Const;
import com.joker.livingstone.util.DeviceUtil;
import com.joker.livingstone.util.DialogHelper;
import com.joker.livingstone.util.HttpHelper;


@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled", "NewApi" })
public class EasterActivity extends BaseActivity{
	
	private ActionBarDrawerToggle drawerToggle;
	private DrawerLayout drawerLayout;
	
	private String[] menuList;
    private ListView drawerList;
    
    private ActionBar bar;
    
    private int eggId;
    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_easter);

		initDrawerAndActionBar("感谢神！");
        getRank();
        

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
		String imei = DeviceUtil.getImei(this);
		String url = Const.PATH + "applyEgg?imei=" + imei;
		new AsyncTask<String, Void, Integer>() {

			@Override
			protected Integer doInBackground(String... url) {
				String data = HttpHelper.getString(url[0]);
				JSONObject json;
				try {
					json = new JSONObject(data);
					if (json.getInt("rtn") == 0) {
						return json.getJSONObject("data").getInt("eggId");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return 0;
			}
			
			@Override
			protected void onPostExecute(Integer result) {
				TextView t = (TextView)findViewById(R.id.text);
				String s = getResources().getString(R.string.reg_tag);
				t.setText(s.format(s, result));
				super.onPostExecute(result);
			}
			
		}.execute(url);
		
		
		
		
		TextView t = (TextView)findViewById(R.id.text);
		String s = getResources().getString(R.string.reg_tag);
		
		
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
	


//	class ItemClickListener implements OnItemClickListener {
//
//		@Override
//		public void onItemClick(AdapterView<?> parent, View view, int position,
//				long id) {
////			TextView t = (TextView)view.findViewById(R.id.bookName);
////			Toast.makeText(ChapterActivity.this, position + "", Toast.LENGTH_LONG).show();
//			
//			Cursor c = (Cursor)parent.getItemAtPosition(position);
//			Intent i = new Intent(EasterActivity.this , SectionActivity.class);
//			i.putExtra("chapterNo", c.getInt(1));
//			i.putExtra("bookName", bookName);
//			i.putExtra("bookId", bookId);
//			Log.d("123", c.getString(1) + bookName + bookId);
////			i.putExtra("chapterNo", c.getString(c.getColumnIndex("chapterNo")));
//			EasterActivity.this.startActivity(i);
////			dialog = ProgressDialog.show(ChapterActivity.this , "活石" ,"正在加载...");
//			DialogHelper.showDialog(EasterActivity.this);
//		}
//
//	}
//	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.index, menu);
		
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
