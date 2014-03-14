package com.joker.livingstone;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.joker.livingstone.util.DBHelper;

public class SectionActivity extends ActionBarActivity {

	private ActionBarDrawerToggle drawerToggle;
	private DrawerLayout drawerLayout;

	private String[] menuList;
	private ListView drawerList;

	private ActionBar bar;

	private ListView mListView1;
	private ListView mListView2;
	private ListView mListView3;
	private ViewPager mPager;
	private ArrayList<View> pageView;
	
	private int bookId;
	private String bookName;
	private int chapterNo;
	
	private int sectionNo;
	private int chapterId;
	private String query;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_section);
		mPager = (ViewPager)findViewById(R.id.viewPager);
		
		mListView1 = (ListView)
                (getLayoutInflater().inflate(R.layout.pager_listview,null).findViewById(R.id.listview));
		mListView2 = (ListView)
				(getLayoutInflater().inflate(R.layout.pager_listview,null).findViewById(R.id.listview));
		mListView3 = (ListView)
				(getLayoutInflater().inflate(R.layout.pager_listview,null).findViewById(R.id.listview));
		mListView1.setDivider(null);
		mListView2.setDivider(null);
		mListView3.setDivider(null);
		
		getDataFromIntent();
		initDrawerAndActionBar(bookName + " �� " + chapterNo);
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

	/**
	 * ��ʼ��Drawer��ActionBar
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

	private void loadSectionData() {
		String sql2 = "SELECT seqId as _id , sectionNo , sectionText , title , chapterId from section "
				+ "where bookId = ? and chapterNo = ? "
				+ "order by sectionIndex ";
		Log.d("123", sql2);
		Cursor c2 = DBHelper.get().rawQuery(sql2, new String[]{bookId+"" , chapterNo + ""});
		c2.moveToNext();
		chapterId = c2.getInt(4);
		
		// SimpleCursorAdapter adapter = new SimpleCursorAdapter(
		MyAdapter adapter2 = new MyAdapter(
				SectionActivity.this, 
				new int[] {R.layout.section, R.layout.section_title1,R.layout.section_title2 }, 
				c2, 
				new String[] { "sectionNo",	"sectionText" },
				new int[] { R.id.sectionNo, R.id.sectionText },
				MyAdapter.NO_SELECTION);
		mListView2.setAdapter(adapter2);
		mListView2.setOnItemClickListener(new ItemClickListener());		
		scroll(adapter2 , this.sectionNo);
		pageView = new ArrayList<View>();
//		pageView.add(mListView2);
		
		loadPrePage(chapterId);
		loadNextPage(chapterId);
		//ǰһ��
//		int chapterId = c.getInt(c.getColumnIndex("chapterId"));
//		String sql1 = "SELECT seqId as _id , sectionNo , sectionText , title , chapterId from section "
//				+ "where chapterId = ? "
//				+ "order by sectionIndex ";
//		Log.d("123", sql1);
//		Cursor c1 = DBHelper.get().rawQuery(sql1, new String[]{chapterId - 1 + ""});
//		MyAdapter adapter1 = new MyAdapter(
//				SectionActivity.this, 
//				new int[] {R.layout.section, R.layout.section_title1,R.layout.section_title2 }, 
//				c1, 
//				new String[] { "sectionNo",	"sectionText" },
//				new int[] { R.id.sectionNo, R.id.sectionText },
//				MyAdapter.NO_SELECTION);
//		mListView1.setAdapter(adapter1);
////		pageView.add(mListView1);
//		
//		
//		//��һ��
////		int chapterId = c.getInt(c.getColumnIndex("chapterId"));
//		String sql3 = "SELECT seqId as _id , sectionNo , sectionText , title , chapterId from section "
//				+ "where chapterId = ? "
//				+ "order by sectionIndex ";
//		Log.d("123", sql3);
//		Cursor c3 = DBHelper.get().rawQuery(sql1, new String[]{chapterId + 1 + ""});
//		MyAdapter adapter3 = new MyAdapter(
//				SectionActivity.this, 
//				new int[] {R.layout.section, R.layout.section_title1,R.layout.section_title2 }, 
//				c3, 
//				new String[] { "sectionNo",	"sectionText" },
//				new int[] { R.id.sectionNo, R.id.sectionText },
//				MyAdapter.NO_SELECTION);
//		mListView3.setAdapter(adapter3);
		
		
		pageView.add(mListView1);
		pageView.add(mListView2);
		pageView.add(mListView3);
		

		mPager.setOnPageChangeListener(new MyPagerChangeListener());
		mPager.setAdapter(new MyPagerAdapter(pageView));
		mPager.setCurrentItem(1);
	}
	private void loadPrePage(int chapterId) {
		String sql = "SELECT seqId as _id , sectionNo , sectionText , title , chapterId from section "
				+ "where chapterId = ? "
				+ "order by sectionIndex ";
		Log.d("123", sql);
		Cursor c = DBHelper.get().rawQuery(sql, new String[]{chapterId - 1 + ""});
		MyAdapter adapter1 = new MyAdapter(
				SectionActivity.this, 
				new int[] {R.layout.section, R.layout.section_title1,R.layout.section_title2 }, 
				c, 
				new String[] { "sectionNo",	"sectionText" },
				new int[] { R.id.sectionNo, R.id.sectionText },
				MyAdapter.NO_SELECTION);
		mListView1.setAdapter(adapter1);
	}
	
	private void loadNextPage(int chapterId) {
		String sql = "SELECT seqId as _id , sectionNo , sectionText , title , chapterId from section "
				+ "where chapterId = ? "
				+ "order by sectionIndex ";
		Log.d("123", sql);
		Cursor c3 = DBHelper.get().rawQuery(sql, new String[]{chapterId + 1 + ""});
		MyAdapter adapter3 = new MyAdapter(
				SectionActivity.this, 
				new int[] {R.layout.section, R.layout.section_title1,R.layout.section_title2 }, 
				c3, 
				new String[] { "sectionNo",	"sectionText" },
				new int[] { R.id.sectionNo, R.id.sectionText },
				MyAdapter.NO_SELECTION);
		mListView3.setAdapter(adapter3);
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
			Toast.makeText(SectionActivity.this, arg0 +"", Toast.LENGTH_LONG).show();
			if(arg0 == 0){
				loadPrePage(chapterId - 2);
			}
			if(arg0 == 2){
				loadNextPage(chapterId + 2);
			}
		}

	}
	public class MyPagerAdapter extends PagerAdapter {
		public List<View> mListViews;

		public MyPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			Log.d("destroyItem", "" + arg0 + " " + arg1);
			((ViewPager) arg0).removeView(mListViews.get(arg1));
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			Log.d("instantiateItem", "" + arg0 + " " + arg1);
			try {
				if (mListViews.get(arg1).getParent() == null)
					((ViewPager) arg0).addView(mListViews.get(arg1), 0);
				else {
					// �����������ӽ�����view���Զ���һ�����࣬����һ������view����������������أ����Եý��
					// ���������������� viewpager java.lang.IllegalStateException: The
					// specified child already has a parent. You must call
					// removeView() on the child's parent first.
					// ����һ�ַ�����viewPager.setOffscreenPageLimit(3); ���ַ��������ж�parent
					// �ǲ����Ѿ����ڣ��������listview���ܱ�destroy
					((ViewGroup) mListViews.get(arg1).getParent())
							.removeView(mListViews.get(arg1));
					((ViewPager) arg0).addView(mListViews.get(arg1), 0);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.d("parent=", "" + mListViews.get(arg1).getParent());
				e.printStackTrace();
			}
			return mListViews.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}
	}
	
	//����ʱ������Ӧλ��
	private void scroll(MyAdapter adapter , int sectionNo) {
		if(sectionNo != 0){
			for(int i = 1; i< adapter.getCount() ; i++){
				Cursor cur = (Cursor)adapter.getItem(i);
				if(cur.getInt(1) == sectionNo){
					mListView2.setSelection(i);
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
			//����׵�һ�Σ����޸�����
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
		
		// ������ѯ������
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
			TextView t = (TextView) view.findViewById(R.id.bookName);
			Toast.makeText(SectionActivity.this, position + "",
					Toast.LENGTH_LONG).show();

			Cursor c = (Cursor) parent.getItemAtPosition(position);
			// Intent i = new Intent(IndexActivity.this , );
			Log.d("123", c.getString(0));
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
		// ������䣬actionbar������ͼ��Ż���ֺ����������Ǽ�ͷ��
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
		// ������䣬��������������ܳ���drawer
		if (drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

}
