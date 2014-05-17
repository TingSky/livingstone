package com.joker.livingstone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.view.ActionMode;
import android.text.ClipboardManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.joker.livingstone.provider.DynamicShareActionProvider;
import com.joker.livingstone.util.DBHelper;
import com.joker.livingstone.util.DeviceUtil;
import com.joker.livingstone.util.DialogHelper;
import com.umeng.analytics.MobclickAgent;

public class SectionActivity extends BaseActivity {
	public static final String TAG = "SectionActivity";

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
	private TextView tmpHighLight;
	private ActionMode actionMode;
	private String[] shareContent = new String[100];
	private DynamicShareActionProvider mShareActionProvider;
	private List<View> shareItems = new ArrayList<View>();
	private String content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DialogHelper.dismiss();
		
		setContentView(R.layout.activity_section);
		hint = (TextView)findViewById(R.id.hint);
		mPager = (ViewPager)findViewById(R.id.viewPager);
		
		
		getDataFromIntent();
//		initDrawerAndActionBar();
		setTitle(initTitle());
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
		
		String sql2 = "SELECT seqId as _id , sectionNo , sectionText , noteText , title  from section "
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
		target.setOnItemLongClickListener(new ItemLongClickListener());
		if(chapterNo == this.chapterNo){
			scroll(target , adapter2 , this.sectionNo);
		}
		return target;
	}
	
	class MyPagerChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			if(hint.getVisibility() == View.VISIBLE) {
				hideHint();
			}
			if(actionMode != null){
				actionMode.finish();
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int arg0) {
			
			SectionActivity.this.chapterNo = arg0 + 1;
			SectionActivity.this.setTitle(initTitle());
			
			HashMap<String,String> map = new HashMap<String, String>();
	        map.put("书编号", bookId + "");
	        map.put("章节号", chapterNo + "");
	        MobclickAgent.onEvent(SectionActivity.this, "查看章", map);
			
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
					
					((ViewGroup) v.getParent()).removeView(v);
					v = loadPage(position + 1, (ListView)v);
					mListViews.set(position, v);
					((ViewPager) container).addView(mListViews.get(position), 0);
					

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
//			int type = cursor.getInt(3);
			int type = cursor.getInt(cursor.getColumnIndex("title"));
			return inflater.inflate(mLayout[type], parent, false);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			int count = this.mTo.length;
			int[] from = this.mFrom;
			int[] to = this.mTo;
			
//			Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/xingothic.otf");
			
			int type = cursor.getInt(cursor.getColumnIndex("title"));
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
	/*
	 * 隐藏注释浮层，并取消高亮编号
	 */
	private void hideHint() {
		hint.setVisibility(View.INVISIBLE);
		if(tmpHighLight != null){
			tmpHighLight.setTextColor(Color.parseColor("#88444444"));
		}
	}
	
	
	private void setShareContent(View v , int position){
		if(shareContent[position] == null){
			TextView content = (TextView)v.findViewById(R.id.sectionText);
			TextView no;
			if(content == null){
				content = (TextView)v.findViewById(R.id.title);
			}else{
				no = (TextView)v.findViewById(R.id.sectionNo);
				no.setTextColor(Color.parseColor("#ff000000"));
			}
			content.setTextColor(Color.parseColor("#ff000000"));
			shareItems.add(v);
			shareContent[position] =  content.getText().toString();
		}else{
			discheckShareContent(v);
			shareItems.remove(v);
			shareContent[position] = null;
		}
	}
	/*
	 * 进入分享选择模式后，反选经文颜色变化
	 */
	private void discheckShareContent(View v){
		TextView content = (TextView)v.findViewById(R.id.sectionText);
		TextView no;
		if(content == null){
			content = (TextView)v.findViewById(R.id.title);
		}else{
			no = (TextView)v.findViewById(R.id.sectionNo);
			no.setTextColor(Color.parseColor("#88444444"));
		}
		content.setTextColor(Color.parseColor("#ff444444"));
	}
	

	class ItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			if(actionMode != null){
				setShareContent(view, position);
				return ;
			}
			
			if(hint.getVisibility() == View.VISIBLE){
				hideHint();
			}else{
				Cursor c = (Cursor) parent.getItemAtPosition(position);
				String note = c.getString(c.getColumnIndex("noteText"));
				if(note.equals("")) {
					note = "暂无注释。";
				}
				hint.setText(note);
				
				//高亮编号
				tmpHighLight = (TextView) view.findViewById(R.id.sectionNo);
				if(tmpHighLight != null){
					tmpHighLight.setTextColor(Color.parseColor("#FF000000"));
				}
				
//			hint.setText(c.getString(c.getColumnIndex("sectionText ")));
				HashMap<String,String> map = new HashMap<String, String>();
		        map.put("节编号", c.getInt(0) + "");
		        MobclickAgent.onEvent(SectionActivity.this, "查看注释", map);
				
				hint.setVisibility(View.VISIBLE);
				
			}
		}
	}
	
	class ItemLongClickListener implements OnItemLongClickListener{
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			if(actionMode != null) {
				return false;
			}
			actionMode = startSupportActionMode(mActionModeCallback);  //开启ActionMode菜单
			setShareContent(view , position);
//			view.setSelected(true);
			return true;
		}
	}
	
	@Override
	public void onBackPressed() {
		if(hint.getVisibility() == View.VISIBLE){
			hideHint();
		}else{
			super.onBackPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.

		getMenuInflater().inflate(R.menu.section, menu);
		MenuItem discuss = menu.findItem(R.id.action_discuss);
		discuss.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if(DeviceUtil.get(SectionActivity.this , "USERID").equals("")){
					Intent i = new Intent(SectionActivity.this, LoginActivity.class);
					SectionActivity.this.startActivity(i);
					return true;
				}
				
				Intent i = new Intent(SectionActivity.this, DiscussActivity.class);
				i.putExtra("bookId", bookId);
				i.putExtra("chapterNo", chapterNo);
				i.putExtra("bookName", bookName);
				SectionActivity.this.startActivity(i);
				return true;
			}
		});


		return super.onCreateOptionsMenu(menu);
	}

	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

	    // Called when the action mode is created; startActionMode() was called
	    @Override
	    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	        // Inflate a menu resource providing context menu items
	        MenuInflater inflater = mode.getMenuInflater();
	        inflater.inflate(R.menu.section_long_press, menu);
	        return true;
	    }

	    // Called each time the action mode is shown. Always called after onCreateActionMode, but
	    // may be called multiple times if the mode is invalidated.
	    @Override
	    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
	    	hideHint();
	    	shareItems.clear();
	        return true; // Return false if nothing is done
	    }

	    // Called when the user selects a contextual menu item
	    @Override
	    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
	    	if(item.getItemId() == R.id.action_share){
	    		mShareActionProvider = (DynamicShareActionProvider) MenuItemCompat.getActionProvider(item);
	    		mShareActionProvider.setShareDataType("text/plain");
		        mShareActionProvider.setOnShareIntentUpdateListener(new DynamicShareActionProvider.OnShareIntentUpdateListener() {
					
					@Override
					public Bundle onShareIntentExtrasUpdate() {
						Bundle bundle = new Bundle();
						getShareContent();
						bundle.putString(Intent.EXTRA_TEXT, content);    
					    return bundle;
					}
				});
//                mode.finish(); // Action picked, so close the CAB
                return true;
	        }else{
	        	ClipboardManager c = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
				c.setText(getShareContent());
				Toast.makeText(SectionActivity.this, "选中经文内容已复制到剪贴板～", Toast.LENGTH_LONG).show();
	        	return false;
	        }
	    }

	    // Called when the user exits the action mode
	    @Override
	    public void onDestroyActionMode(ActionMode mode) {
	    	shareContent = new String[100];
	    	for (View v : shareItems) {
				discheckShareContent(v);
			}
	        actionMode = null;
	    }
	};
	
	public String getShareContent() {
		content = "";
		for (String s : shareContent) {
			if(s == null) continue;
			content += s + "\n";
		}
		content += "——分享自“活石”";
		return content;
	}
	

}
