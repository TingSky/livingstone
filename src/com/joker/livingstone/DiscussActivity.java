package com.joker.livingstone;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.joker.livingstone.util.Const;
import com.joker.livingstone.util.DBHelper;
import com.joker.livingstone.util.DialogHelper;
import com.joker.livingstone.util.HttpHelper;
import com.joker.livingstone.util.SearchProvider;
import com.umeng.analytics.MobclickAgent;


public class DiscussActivity extends BaseActivity{
	
	private ActionBarDrawerToggle drawerToggle;
	private DrawerLayout drawerLayout;
	
	private String[] menuList;
	private ListView drawerList;
    
    private ActionBar bar;
    
	private int bookId;
	private String bookName;
	private int chapterNo;
    private ListView mListView;
    
    private String query;
    
    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		
		mListView = (ListView) findViewById(R.id.listView);
//		mListView.setDivider(null);
		
		getDataFromIntent();
		initDrawerAndActionBar(bookName + " · " + chapterNo + " · 讨论");
		loadSearchData();
		
		
//		SearchRecentSuggestions suggestions=new SearchRecentSuggestions(this,  
//				SearchProvider.AUTHORITY, SearchProvider.MODE); 
//		suggestions.saveRecentQuery(query, null);
		

	}
	


	
	private void getDataFromIntent() {
		Intent i = getIntent();
		bookId = i.getIntExtra("bookId", 1);
		bookName = i.getStringExtra("bookName");
		chapterNo = i.getIntExtra("chapterNo", 1);
		
	}
	
	
	/**
	 * 初始化Drawer和ActionBar
	 */
	private void initDrawerAndActionBar(final String title) {
		menuList = getResources().getStringArray(R.array.menu);
		setTitle(title);

		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerList = (ListView) findViewById(R.id.left_drawer);
		drawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, menuList));

		bar = getSupportActionBar();
		drawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		drawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
				bar.setTitle(title);
				supportInvalidateOptionsMenu(); // creates call to
												// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				bar.setTitle(R.string.drawer_open);
				supportInvalidateOptionsMenu(); // creates call to
												// onPrepareOptionsMenu()
			}
		};
		drawerLayout.setDrawerListener(drawerToggle);

		bar.setDisplayHomeAsUpEnabled(true);
		bar.setHomeButtonEnabled(true);

	}
	
	
	private void loadSearchData() {
		String url = Const.PATH + "pullComment?bookId=" + bookId + "&chapterNo=" + chapterNo;
		String data = HttpHelper.getString(url);
		JSONObject json;
		try {
			json = new JSONObject(data);
			if(json.get("rtn").equals(0)){
				json.getJSONArray("list");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

//		// SimpleCursorAdapter adapter = new SimpleCursorAdapter(
//		MyAdapter adapter = new MyAdapter(
//				DiscussActivity.this, 
//				R.layout.search , 
//				c, 
//				new String[] {  "directory", "sectionText" },
//				new int[] { R.id.directory , R.id.content },
//				MyAdapter.NO_SELECTION);
//		mListView.setAdapter(adapter);
//		mListView.setOnItemClickListener(new ItemClickListener());

	}

	class MyAdapter extends CursorAdapter {

		private int[] mFrom;
		private int[] mTo;
		private int mLayout;
		private LayoutInflater inflater;

		public MyAdapter(Context context, int layout, Cursor c,
				String[] from, int[] to, int flags) {
			super(context, c, flags);
			this.mTo = to;
			findColumns(from);
			this.mLayout = layout;

			// super(context, layout, c, from, to, flags);
			inflater = (LayoutInflater) context.getSystemService("layout_inflater");
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (!this.mDataValid) {
				throw new IllegalStateException(
						"this should only be called when the cursor is valid");
			}
			if (!this.mCursor.moveToPosition(position))
				throw new IllegalStateException(
						"couldn't move cursor to position " + position);
			View v;
			if (convertView == null)
				v = newView(this.mContext, this.mCursor, parent);
			else {
				v = convertView;
			}
			bindView(v, this.mContext, this.mCursor);
			TextView text = (TextView)v.findViewById(R.id.No);
			text.setText(position+1 + "");
			return v;
		}

		
		
		public void bindView(View view, Context context, Cursor cursor) {
			int count = this.mTo.length;
			int[] from = this.mFrom;
			int[] to = this.mTo;
			
			for (int i = 0; i < count; i++) {
				View v = view.findViewById(to[i]);
				if (v != null) {
					if(v instanceof TextView){
						if(from[i] == -256){
							String s = cursor.getString(2) + " · " + cursor.getString(3) +"章" + cursor.getString(4) +"节";
							((TextView) v).setText(s);
							continue;
						}
						
						String text = cursor.getString(from[i]);
						if (text == null) {
							text = "";
						}
						highlight(v, text);
					}else{
						throw new IllegalStateException(
								v.getClass().getName()
								+ " is not a "
								+ " view that can be bounds by this SimpleCursorAdapter");
					}
				}
			}
		}
		
		//高亮查询的内容
		private void highlight(View v , String text){
			TextView t = (TextView) v;
			SpannableStringBuilder style = new SpannableStringBuilder(text);
			int start = text.indexOf(query);
			int end = start + query.length();
//	        style.setSpan(new BackgroundColorSpan(Color.YELLOW),start,end,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	        style.setSpan(new ForegroundColorSpan(Color.RED),start,end,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			t.setText(style);
		}
		
		private void findColumns(String[] from) {
			if (this.mCursor != null) {
				int count = from.length;
				if ((this.mFrom == null) || (this.mFrom.length != count)) {
					this.mFrom = new int[count];
				}
				for (int i = 0; i < count; i++){
					if(from[i].equals("directory")){
						this.mFrom[i] = -256;
						continue;
					}
					this.mFrom[i] = this.mCursor.getColumnIndexOrThrow(from[i]);
				}
			} else {
				this.mFrom = null;
			}
		}
		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent){
			return this.inflater.inflate(this.mLayout, parent, false);
		}


	}
	


	class ItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
//			TextView t = (TextView)view.findViewById(R.id.bookName);
//			Toast.makeText(IndexActivity.this, position + "", Toast.LENGTH_LONG).show();
			
			Cursor c = (Cursor)parent.getItemAtPosition(position);
			Intent i = new Intent(DiscussActivity.this , SectionActivity.class);
			i.putExtra("bookId", c.getInt(1));
//			i.putExtra("bookId", c.getString(c.getColumnIndex("_id")));
			i.putExtra("bookName", c.getString(2));
//			i.putExtra("bookId", c.getString(c.getColumnIndex("bookName")));
			i.putExtra("chapterNo", c.getInt(3));
//			i.putExtra("chapterNo", c.getString(c.getColumnIndex("chapterNo")));
			i.putExtra("sectionNo", c.getInt(4));
//			i.putExtra("sectionNo", c.getString(c.getColumnIndex("sectionNo")));
			i.putExtra("query" , query);
			
			DiscussActivity.this.startActivity(i);
//			dialog = ProgressDialog.show(SearchActivity.this , "活石" ,"正在加载...");
			DialogHelper.showDialog(DiscussActivity.this);
		}

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.index, menu);
		
		
		
		
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
