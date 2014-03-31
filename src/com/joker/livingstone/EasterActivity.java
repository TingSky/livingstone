package com.joker.livingstone;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.TextView;

import com.joker.livingstone.util.Const;
import com.joker.livingstone.util.DialogHelper;


@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled", "NewApi" })
public class EasterActivity extends BaseActivity{
	
	private ActionBarDrawerToggle drawerToggle;
	private DrawerLayout drawerLayout;
	
	private String[] menuList;
    private ListView drawerList;
    
    private ActionBar bar;
    
    private WebView mWebView;
    private int bookId;
    private String bookName;
    private Handler h = new Handler();
    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_easter);
//		mWebView = (WebView) findViewById(R.id.webView);
//		mWebView.getSettings().setJavaScriptEnabled(true);
//		mWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
//		
//		mWebView.addJavascriptInterface(new runJavaScript(), "wst");
//		
//		mWebView.loadUrl("file:///android_asset/easter.html");
//		
//		getDataFromIntent();
		initDrawerAndActionBar("��л��");
//		loadChapterData();
        
        

	}
	
	
	 final class runJavaScript{//���Java �����ǰ�����һ���߳���ģ�
		 @SuppressLint("JavascriptInterface")
		 public void getRankURL(){
			 h.post(new Runnable(){
				@Override
				public void run() {//����Ӧ���ر�ע���
//					TextView show = (TextView) findViewById(R.id.show);
//					show.setText("This is a message from javascript:"+str); 
					TelephonyManager tm = (TelephonyManager) EasterActivity.this.getSystemService(TELEPHONY_SERVICE);
	            	String imei = tm.getDeviceId();
	            	String url = Const.PATH + "applyEgg?imei=" + imei;
	            	mWebView.loadUrl("javascript:getRank('" + url + "')");
	            	Log.d("aaa", url);
				}
	    
			 });
		 }
	}
//	@SuppressLint("JavascriptInterface")
//	public void getRankURL(){
//		Log.d("aaa", "12311231231");
//		runOnUiThread(new Runnable() {  
//			  
//            @Override  
//            public void run() { 
//            	TelephonyManager tm = (TelephonyManager) EasterActivity.this.getSystemService(TELEPHONY_SERVICE);
//            	String imei = tm.getDeviceId();
//            	String url = Const.PATH + "applyEgg?imei=" + imei;
//            	mWebView.loadUrl("javascript:getRank('" + url + "')");
//            	Log.d("aaa", url);
//            }
//		});
//	}
	
	
	private void getDataFromIntent(){
		Intent i = getIntent();
		bookId = i.getIntExtra("bookId", 1);
		bookName = i.getStringExtra("bookName");
	}
	
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
	
	
//	private void loadChapterData(){
//		String sql = "SELECT seqId as _id , chapterNo from chapter where bookId = " + 
//				bookId + " order by seqId ";
//		Cursor c = DBHelper.get().rawQuery(sql, null);
//		
//		SimpleCursorAdapter adapter = new SimpleCursorAdapter(
////		CursorAdapter adapter = new CursorAdapter(
//				EasterActivity.this,
//				R.layout.chapter,
//				c,
//				new String[] {"chapterNo" },
//				new int[] {R.id.chapterNo},
//				CursorAdapter.NO_SELECTION
//		);
//		mGridView.setAdapter(adapter);
//		mGridView.setOnItemClickListener(new ItemClickListener());
//		
//	}
	
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
			Intent i = new Intent(EasterActivity.this , SectionActivity.class);
			i.putExtra("chapterNo", c.getInt(1));
			i.putExtra("bookName", bookName);
			i.putExtra("bookId", bookId);
			Log.d("123", c.getString(1) + bookName + bookId);
//			i.putExtra("chapterNo", c.getString(c.getColumnIndex("chapterNo")));
			EasterActivity.this.startActivity(i);
//			dialog = ProgressDialog.show(ChapterActivity.this , "��ʯ" ,"���ڼ���...");
			DialogHelper.showDialog(EasterActivity.this);
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
