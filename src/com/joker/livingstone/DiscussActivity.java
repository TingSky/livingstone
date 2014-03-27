package com.joker.livingstone;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.joker.livingstone.util.Const;
import com.joker.livingstone.util.DialogHelper;
import com.joker.livingstone.util.HttpHelper;

public class DiscussActivity extends BaseActivity {

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
	private ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
	private SimpleAdapter adapter;
	
	//最后一条评论的seqid
	private int cid;
	//剩下多少页评论
	private int pageTotal;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		mListView = (ListView) findViewById(R.id.listView);
		// mListView.setDivider(null);

		getDataFromIntent();
		initDrawerAndActionBar(bookName + " · " + chapterNo + " · 讨论");
		loadSearchData();

		// SearchRecentSuggestions suggestions=new SearchRecentSuggestions(this,
		// SearchProvider.AUTHORITY, SearchProvider.MODE);
		// suggestions.saveRecentQuery(query, null);

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

		new PageTask().execute(url);

		// // SimpleCursorAdapter adapter = new SimpleCursorAdapter(
		// MyAdapter adapter = new MyAdapter(
		// DiscussActivity.this,
		// R.layout.search ,
		// c,
		// new String[] { "directory", "sectionText" },
		// new int[] { R.id.directory , R.id.content },
		// MyAdapter.NO_SELECTION);
		// mListView.setAdapter(adapter);
		// mListView.setOnItemClickListener(new ItemClickListener());

	}

	class PageTask extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			DialogHelper.showDialog(DiscussActivity.this);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... url) {
			String data = HttpHelper.getString(url[0]);
			JSONObject json;
			try {
				json = new JSONObject(data);
				if (json.getInt("rtn") == 0) {
					pageTotal = json.getJSONObject("data").getInt("pageTotal"); 
					JSONArray list = json.getJSONObject("data").getJSONArray("list");
					
					for (int i = 0; i < list.length(); i++) {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("nickName",
								list.getJSONObject(i).getString("nickName"));
						map.put("addr", list.getJSONObject(i).getString("addr"));
						map.put("content",
								list.getJSONObject(i).getString("content"));
						map.put("createTime",
								list.getJSONObject(i).getString("createTime"));
						// map.put("ip", list.getJSONObject(i).getString("ip"));
						
						cid = list.getJSONObject(i).getInt("seqId");
						dataList.add(map);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return data;
		}

		@Override
		protected void onPostExecute(String result) {
			DialogHelper.dismiss();
			if(adapter == null){
				adapter = new SimpleAdapter(DiscussActivity.this, dataList,
						R.layout.discuss, 
						new String[] { "nickName", "addr","content", "createTime" }, 
						new int[] {R.id.nickname, R.id.ip, R.id.comment, R.id.date });
				mListView.setAdapter(adapter);
				mListView.setOnScrollListener(new LoadPageListener());
			}else{
				adapter.notifyDataSetChanged();
			}
			super.onPostExecute(result);
		}
	}

	class LoadPageListener implements OnScrollListener {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// 当不滚动时
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
				// 判断是否滚动到底部
				if (view.getLastVisiblePosition() == view.getCount() - 1 ) {
					if(pageTotal < 2){
						Toast.makeText(DiscussActivity.this, "已经是最后一条评论了哦～", Toast.LENGTH_SHORT).show();
						return ;
					}
					String url = Const.PATH + "pullComment?bookId=" + bookId
							+ "&chapterNo=" + chapterNo + "&cid=" + cid;
					new PageTask().execute(url);
					
				}
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			// TODO Auto-generated method stub

		}

	}

	class ItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TextView t = (TextView)view.findViewById(R.id.bookName);
			// Toast.makeText(IndexActivity.this, position + "",
			// Toast.LENGTH_LONG).show();

			Cursor c = (Cursor) parent.getItemAtPosition(position);
			Intent i = new Intent(DiscussActivity.this, SectionActivity.class);
			i.putExtra("bookId", c.getInt(1));
			// i.putExtra("bookId", c.getString(c.getColumnIndex("_id")));
			i.putExtra("bookName", c.getString(2));
			// i.putExtra("bookId", c.getString(c.getColumnIndex("bookName")));
			i.putExtra("chapterNo", c.getInt(3));
			// i.putExtra("chapterNo",
			// c.getString(c.getColumnIndex("chapterNo")));
			i.putExtra("sectionNo", c.getInt(4));
			// i.putExtra("sectionNo",
			// c.getString(c.getColumnIndex("sectionNo")));
			i.putExtra("query", query);

			DiscussActivity.this.startActivity(i);
			// dialog = ProgressDialog.show(SearchActivity.this , "活石"
			// ,"正在加载...");
			DialogHelper.showDialog(DiscussActivity.this);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.index, menu);

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
