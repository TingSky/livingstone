package com.joker.livingstone;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
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

import com.joker.livingstone.util.DBHelper;
import com.joker.livingstone.util.DialogHelper;


public class ChapterActivity extends ActionBarActivity{
	
	private ActionBarDrawerToggle drawerToggle;
	private DrawerLayout drawerLayout;
	
	private String[] menuList;
    private ListView drawerList;
    
    private ActionBar bar;
    
    private GridView mGridView;
    private int bookId;
    private String bookName;
    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chapter);
		mGridView = (GridView) findViewById(R.id.gridView);
		
		
		getDataFromIntent();
		initDrawerAndActionBar(bookName);
		loadChapterData();
        
        

	}
	
	private void getDataFromIntent(){
		Intent i = getIntent();
		bookId = i.getIntExtra("bookId", 1);
		bookName = i.getStringExtra("bookName");
	}
	
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
	
	
	private void loadChapterData(){
		String sql = "SELECT seqId as _id , chapterNo from chapter where bookId = " + 
				bookId + " order by seqId ";
		Cursor c = DBHelper.get().rawQuery(sql, null);
		
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(
//		CursorAdapter adapter = new CursorAdapter(
				ChapterActivity.this,
				R.layout.chapter,
				c,
				new String[] {"chapterNo" },
				new int[] {R.id.chapterNo},
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
//			Toast.makeText(ChapterActivity.this, position + "", Toast.LENGTH_LONG).show();
			
			Cursor c = (Cursor)parent.getItemAtPosition(position);
			Intent i = new Intent(ChapterActivity.this , SectionActivity.class);
			i.putExtra("chapterNo", c.getInt(1));
			i.putExtra("bookName", bookName);
			i.putExtra("bookId", bookId);
			Log.d("123", c.getString(1) + bookName + bookId);
//			i.putExtra("chapterNo", c.getString(c.getColumnIndex("chapterNo")));
			ChapterActivity.this.startActivity(i);
//			dialog = ProgressDialog.show(ChapterActivity.this , "活石" ,"正在加载...");
			DialogHelper.showDialog(ChapterActivity.this);
		}

	}
	
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
