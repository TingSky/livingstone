package com.joker.livingstone;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.joker.livingstone.util.DBHelper;
import com.joker.livingstone.util.DialogHelper;

public class SectionActivity extends ActionBarActivity {

	private ActionBarDrawerToggle drawerToggle;
	private DrawerLayout drawerLayout;

	private String[] menuList;
	private ListView drawerList;

	private ActionBar bar;

	private ViewPager mPager;
	private ArrayList<View> pageView;
	
	private int bookId;
	private String bookName;
	private int chapterNo;
	
	private int sectionNo;
	private String query;
	
	private MyPagerAdapter adapter;
	private TextView hint;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DialogHelper.dismiss();
		
		setContentView(R.layout.activity_section);
		hint = (TextView)findViewById(R.id.hint);
		mPager = (ViewPager)findViewById(R.id.viewPager);
		
		
		getDataFromIntent();
		initDrawerAndActionBar();
		loadSectionData();
		

	}

	private void getDataFromIntent() {
		Intent i = getIntent();
		bookId = i.getIntExtra("bookId", 1);
		bookName = i.getStringExtra("bookName");
		chapterNo = i.getIntExtra("chapterNo", 1);
		
		sectionNo = i.getIntExtra("sectionNo", 0);
		query = i.getStringExtra("query");
	}

	private String initTitle(){
		return bookName + " · " + chapterNo;
		
	}
	/**
	 * 初始化Drawer和ActionBar
	 */
	private void initDrawerAndActionBar() {
		menuList = getResources().getStringArray(R.array.menu);
		setTitle(initTitle());

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
				setTitle(initTitle());
				supportInvalidateOptionsMenu(); // creates call to
												// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				setTitle(R.string.drawer_open);
				supportInvalidateOptionsMenu(); // creates call to
												// onPrepareOptionsMenu()
			}
		};
		drawerLayout.setDrawerListener(drawerToggle);

		bar.setDisplayHomeAsUpEnabled(true);
		bar.setHomeButtonEnabled(true);

	}

	private void loadSectionData() {
		String sql = "SELECT seqId as _id , chapterNo from chapter "
				+ "where bookId = ? "
				+ "order by chapterIndex ";
		Cursor c = DBHelper.get().rawQuery(sql, new String[]{bookId + ""});
		pageView = new ArrayList<View>();
		while(c.moveToNext()){
			ListView listView = (ListView)
					(getLayoutInflater().inflate(R.layout.pager_listview,null).findViewById(R.id.listview));
			pageView.add(listView);
		}
		
		

		mPager.setOnPageChangeListener(new MyPagerChangeListener());
		adapter = new MyPagerAdapter(pageView);
		mPager.setAdapter(adapter);
		mPager.setCurrentItem(chapterNo - 1);
	}
	
	private ListView loadPage(int chapterNo , ListView target){
		
		String sql2 = "SELECT seqId as _id , sectionNo , sectionText , title  from section "
				+ "where bookId = ? and chapterNo = ? "
				+ "order by sectionIndex ";
		Cursor c2 = DBHelper.get().rawQuery(sql2, new String[]{bookId+"" , chapterNo + ""});
		
		// SimpleCursorAdapter adapter = new SimpleCursorAdapter(
		MyAdapter adapter2 = new MyAdapter(
				SectionActivity.this, 
				new int[] {R.layout.section, R.layout.section_title1,R.layout.section_title2 }, 
				c2, 
				new String[] { "sectionNo",	"sectionText" },
				new int[] { R.id.sectionNo, R.id.sectionText },
				MyAdapter.NO_SELECTION);
		
		target.setAdapter(adapter2);
		target.setDivider(null);
		target.setOnItemClickListener(new ItemClickListener());	
		if(chapterNo == this.chapterNo){
			scroll(target , adapter2 , this.sectionNo);
		}
		return target;
	}
	

	


	class MyPagerChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int arg0) {
			SectionActivity.this.chapterNo = arg0 + 1;
			SectionActivity.this.setTitle(initTitle());
		}

	}
	public class MyPagerAdapter extends PagerAdapter {
		public List<View> mListViews = new ArrayList<View>();

		public MyPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(View container, int position, Object arg2) {
			Log.d("destroyItem", "" + container + " " + position);
			((ViewPager) container).removeView(mListViews.get(position));
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(View container, int position) {
//			Log.d("instantiateItem", "" + container + " " + position);
			try {
				View v =  mListViews.get(position);
				if (v != null && ((ListView)v).getAdapter() != null ){
					((ViewPager) container).addView(mListViews.get(position), 0);
				}
				else {
					// 很难理解新添加进来的view会自动绑定一个父类，由于一个儿子view不能与两个父类相关，所以得解绑
					// 不这样做否则会产生 viewpager java.lang.IllegalStateException: The
					// specified child already has a parent. You must call
					// removeView() on the child's parent first.
					// 还有一种方法是viewPager.setOffscreenPageLimit(3); 这种方法不用判断parent
					// 是不是已经存在，但多余的listview不能被destroy

//					((ViewGroup) mListViews.get(position -1).getParent())
//							.removeView(mListViews.get(position -1));
//					((ViewPager) container)
//							.addView(mListViews.get(position -1), position - 1);
//					if(mListViews.get(position -1) == null)
					Log.e("tag",position+"");
//					if(position == 0){
//						initListView(container, position);
//						initListView(container, position + 1);
//					}else if(position == mListViews.size()){
//						initListView(container, position - 1);
//						initListView(container, position);
//					}else{
//						initListView(container, position - 1);
					
					((ViewGroup) v.getParent()).removeView(v);
					v = loadPage(position + 1, (ListView)v);
					mListViews.set(position, v);
					((ViewPager) container).addView(mListViews.get(position), 0);
					
//					((ViewGroup) mListViews.get(position).getParent())
//							.removeView(mListViews.get(position));
//					((ViewPager) container)
//							.addView(mListViews.get(position), position);
//					
//					((ViewGroup) mListViews.get(position).getParent())
//							.removeView(mListViews.get(position));
//					((ViewPager) container)
//							.addView(mListViews.get(position), position +1);

				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.d("parent=", "" + mListViews.get(position).getParent());
				e.printStackTrace();
			}
			return mListViews.get(position);
		}
		

		@Override
		public boolean isViewFromObject(View container, Object position) {
			return container == (position);
		}

		@Override
		public void restoreState(Parcelable container, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View container) {
		}
	}
	
	//搜索时滑到相应位置
	private void scroll(ListView target ,MyAdapter adapter , int sectionNo) {
		if(sectionNo != 0){
			for(int i = 1; i< adapter.getCount() ; i++){
				Cursor cur = (Cursor)adapter.getItem(i);
				if(cur.getInt(1) == sectionNo ){
					target.setSelection(i);
					break;
				}
			}
		}
	}

	class MyAdapter extends CursorAdapter {

		private int[] mFrom;
		private int[] mTo;
		private int[] mLayout;
		private LayoutInflater inflater;

		public MyAdapter(Context context, int[] layout, Cursor c,
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
			View v = newView(this.mContext, this.mCursor, parent);
			bindView(v, this.mContext, this.mCursor);
			
			return v;
		}

		@Override
		public View newView(Context context, Cursor cursor,
				ViewGroup parent) {
			int type = cursor.getInt(3);
			return inflater.inflate(mLayout[type], parent, false);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			int count = this.mTo.length;
			int[] from = this.mFrom;
			int[] to = this.mTo;
			
//			Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/xingothic.otf");
			
			int type = cursor.getInt(3);
			//最不靠谱的一段，毫无复用性
			if(type > 0){
				TextView v = (TextView) view.findViewById(R.id.title);
				v.setText(cursor.getString(2));
//				v.setTypeface(tf);
				return ;
			}

			for (int i = 0; i < count; i++) {
				View v = view.findViewById(to[i]);
				if (v != null) {

					String text = cursor.getString(from[i]);
					if (text == null) {
						text = "";
					}

					if ((v instanceof TextView)){
						highlight(v, text , query);
//						((TextView) v).setTypeface(tf);
					}
					// setViewText((TextView) v, text);
					// else if ((v instanceof ImageView))
					// setViewImage((ImageView) v, text);
					else
						throw new IllegalStateException(
								v.getClass().getName()
										+ " is not a "
										+ " view that can be bounds by this SimpleCursorAdapter");
				}
			}
		}
		
		// 高亮查询的内容
		private void highlight(View v, String text , String query) {
			TextView t = (TextView) v;
			try{
				int start = text.indexOf(query);
				int end = start + query.length();
				
				SpannableStringBuilder style = new SpannableStringBuilder(text);
				style.setSpan(new ForegroundColorSpan(Color.RED), start, end,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				t.setText(style);
			}catch(Exception e){
				t.setText(text);
			}
		}

		private void findColumns(String[] from) {
			if (this.mCursor != null) {
				int count = from.length;
				if ((this.mFrom == null) || (this.mFrom.length != count)) {
					this.mFrom = new int[count];
				}
				for (int i = 0; i < count; i++)
					this.mFrom[i] = this.mCursor.getColumnIndexOrThrow(from[i]);
			} else {
				this.mFrom = null;
			}
		}

	}

	class ItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
//			TextView t = (TextView) view.findViewById(R.id.bookName);
//			Toast.makeText(SectionActivity.this, position + "",
//					Toast.LENGTH_LONG).show();

			if(hint.getVisibility() == View.VISIBLE){
				hint.setVisibility(View.INVISIBLE);
			}else{
				Cursor c = (Cursor) parent.getItemAtPosition(position);
				hint.setText(c.getString(2));
				
//			hint.setText(c.getString(c.getColumnIndex("sectionText ")));
				hint.setVisibility(View.VISIBLE);
				
			}
			
		}

	}
	@Override
	public void onBackPressed() {
		if(hint.getVisibility() == View.VISIBLE){
			hint.setVisibility(View.INVISIBLE);
		}else{
			super.onBackPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.index, menu);

		// SearchManager searchManager =
		// (SearchManager)
		// IndexActivity.this.getSystemService(Context.SEARCH_SERVICE);
		// SupportMenuItem searchMenuItem = ((SupportMenuItem)
		// menu.findItem(R.id.action_serach));
		// SearchView searchView = (SearchView) searchMenuItem.getActionView();
		// searchView.setSearchableInfo(searchManager.getSearchableInfo(IndexActivity.this.getComponentName()));

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// 有了这句，actionbar标题栏图标才会出现汉堡（本来是箭头）
		drawerToggle.syncState();
	}

	// @Override
	// public void onConfigurationChanged(Configuration newConfig) {
	// super.onConfigurationChanged(newConfig);
	// drawerToggle.onConfigurationChanged(newConfig);
	// }
	//
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// 有了这句，点击顶部汉堡才能出现drawer
		if (drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

}
