package com.joker.livingstone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.content.Context;
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
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.joker.livingstone.util.Const;
import com.joker.livingstone.util.DialogHelper;
import com.joker.livingstone.util.HttpHelper;

public class DiscussActivity extends BaseActivity {


	private int bookId;
	private String bookName;
	private int chapterNo;
	private ListView mListView;

	private ArrayList<HashMap<String, Object>> dataList = new ArrayList<HashMap<String, Object>>();
	private SimpleAdapter adapter;
	
	//用户目前处于评论第几页
	private int currentPage;
	//剩下多少页评论
	private int pageTotal;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		mListView = (ListView) findViewById(R.id.listView);
		// mListView.setDivider(null);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getDataFromIntent();
		setTitle(bookName + " · " + chapterNo + " · 讨论");
		loadPageData(++currentPage);


	}

	private void getDataFromIntent() {
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(Const.SEND_COMMENT_SUCCESS_ID);
		
		Intent i = getIntent();
		bookId = i.getIntExtra("bookId", 1);
		bookName = i.getStringExtra("bookName");
		chapterNo = i.getIntExtra("chapterNo", 1);
		
		Log.d("123",bookName + bookId + "|" +chapterNo);

	}

	
	//第一次加载页面
	private void loadPageData(int pageNo) {
		Map<String , String> postData = new HashMap<String, String>();
		postData.put("bookId", bookId +"");
		postData.put("chapterNo", chapterNo +"");
		postData.put("pageNo", pageNo +"");
		new PageTask().execute(postData);


	}

	class PageTask extends AsyncTask<Map<String,String>, Void, String> {

		@Override
		protected String doInBackground(Map<String,String>... postData) {
			String url = Const.PATH + "mpullComment";
			String data = HttpHelper.getString(DiscussActivity.this , url ,postData[0]);
			JSONObject json;
			try {
				json = new JSONObject(data);
				if (json.getInt("rtn") == 0) {
					pageTotal = json.getJSONObject("data").getInt("pageTotal"); 
					final JSONArray list = json.getJSONObject("data").getJSONArray("list");
					
					for (int i = 0; i < list.length(); i++) {
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("seqId", list.getJSONObject(i).getInt("seqId" + ""));
						map.put("nickName",
								list.getJSONObject(i).getString("userName"));
						map.put("addr", list.getJSONObject(i).getString("addr"));
						map.put("content",
								list.getJSONObject(i).getString("content"));
						map.put("createTime",
								list.getJSONObject(i).getString("createTime"));
						Vote v = new Vote();
						v.seqid = list.getJSONObject(i).getString("seqId");
						v.isVoted = list.getJSONObject(i).getString("voted");
						v.agree = list.getJSONObject(i).getInt("totalVote");
						map.put("agree", v);
//						cid = list.getJSONObject(i).getInt("seqId");
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
			if(adapter == null){
				if(dataList.size() == 0){
					mListView.setVisibility(View.GONE);
					TextView v = (TextView) findViewById(R.id.hint);
					v.setText(R.string.comment_not_found);
					v.setVisibility(View.VISIBLE);
					return ;
				}
				adapter = new SimpleAdapter(DiscussActivity.this, dataList,
						R.layout.discuss, 
						new String[] { "nickName", "addr","content", "createTime","agree" }, 
						new int[] { R.id.nickname, R.id.ip, R.id.comment, R.id.date, R.id.agree });
				mListView.setAdapter(adapter);
				mListView.setOnScrollListener(new LoadPageListener());
			}else{
				adapter.notifyDataSetChanged();
			}
			SimpleAdapter.ViewBinder binder = new ViewBinder() {
				@Override
				public boolean setViewValue(View view, Object data,String textRepresentation) {
					if(view instanceof ToggleButton){
						Vote vote = (Vote)data;
						ToggleButton button = (ToggleButton)view;
						button.setTextOn(vote.agree + "赞");
						button.setTextOff(vote.agree + "赞");
						button.setChecked(vote.isVoted.equals("1"));
						view.setOnClickListener(new VoteListener(vote));
						return true;
					}
					return false;
				}
			};
			adapter.setViewBinder(binder);
			super.onPostExecute(result);
		}
	}
	
	class VoteListener implements OnClickListener{
		Vote vote;
		public VoteListener(Vote vote) {
			this.vote = vote;
		}
		@Override
		public void onClick(View v) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("cid", vote.seqid);
			map.put("vote", ((ToggleButton)v).isChecked()?"1":"0");
			new VoteTask().execute(map , v , vote);
		}
		
	}
	
	class VoteTask extends AsyncTask<Object, Void, String> {
		ToggleButton button;
		Vote vote;
		@Override
		protected String doInBackground(Object... obj) {
			Map<String,String> map = (Map<String, String>) obj[0];
			button = (ToggleButton) obj[1];
			vote = (Vote) obj[2];
			String url = Const.PATH + "mvoteComment";
			String data = HttpHelper.getString(DiscussActivity.this , url ,map);
			JSONObject json;
			try {
				json = new JSONObject(data);
				if (json.getInt("rtn") == 0) {
					return map.get("vote");
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		}
		
		@Override
		protected void onPostExecute(String result) {
			if(result.equals("0")){
				vote.agree--;
//				button.setChecked(false);
//				button.setTextOff(vote.agree + "赞");
			}else if(result.equals("1")){
				vote.agree++;
//				button.setChecked(true);
//				button.setTextOn(vote.agree + "赞");
			}else{
				return ;
			}
			vote.isVoted = (vote.isVoted.equals("1")) ? "0" : "1";
			
			for (Map<String,Object> map : dataList) {
				if(map.get("seqId").toString().equals(vote.seqid + "")){
Log.d(map.get("seqId").toString(),vote.seqid+"");
					((Vote) map.get("agree")).isVoted =vote.isVoted;
					((Vote) map.get("agree")).agree =vote.agree;
Log.d(vote.isVoted + "",vote.agree+"");
//					v.isVoted = vote.isVoted;
//					v.agree = vote.agree;
//					map.put("agree", v);
				}
			}
			adapter.notifyDataSetChanged();
		}
		
	}
	
	class Vote{
		String seqid ;
		String isVoted;
		int agree;
	}

	class LoadPageListener implements OnScrollListener {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// 当不滚动时
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
				// 判断是否滚动到底部
				if (view.getLastVisiblePosition() == view.getCount() - 1 ) {
					if(pageTotal == currentPage){
						Toast.makeText(DiscussActivity.this, "已经是最后一条评论了哦～", Toast.LENGTH_SHORT).show();
						return ;
					}
					loadPageData(++currentPage);
					
				}
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			// TODO Auto-generated method stub

		}

	}


	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//		 Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.discuss, menu);
		MenuItem addComment = menu.findItem(R.id.action_add_comment);
		addComment.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent i = new Intent(DiscussActivity.this , AddCommentActivity.class);
				i.putExtra("bookId", bookId);
				i.putExtra("chapterNo", chapterNo);
				i.putExtra("bookName", bookName);
				startActivity(i);
				return true;
			}
		});
		return super.onCreateOptionsMenu(menu);
	}


}
