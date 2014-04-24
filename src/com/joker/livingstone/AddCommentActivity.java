package com.joker.livingstone;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.joker.livingstone.service.SendCommentService;


public class AddCommentActivity extends BaseActivity{
	
    
    
    private EditText mEditText;
    private int bookId;
    private String bookName;
    private int chapterNo;
    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_comment);
		mEditText = (EditText) findViewById(R.id.mEditText);
		
		
		getDataFromIntent();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        

	}
	
	private void getDataFromIntent(){
		Intent i = getIntent();
		bookId = i.getIntExtra("bookId", 1);
		bookName = i.getStringExtra("bookName");
		chapterNo = i.getIntExtra("chapterNo" , 1);
	}
	



	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_comment, menu);
		menu.findItem(R.id.send).setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if(mEditText.getText().toString().equals("")){
					Toast.makeText(AddCommentActivity.this, "评论内容不能为空哦～", Toast.LENGTH_LONG).show();
					return false;
				}
				send();
				return true;
			}
		});
		return super.onCreateOptionsMenu(menu);
	}
	
	private void send() {
		Intent i = new Intent(this, SendCommentService.class);
		i.putExtra("bookId", bookId);
		i.putExtra("chapterNo", chapterNo);
		i.putExtra("bookName", bookName);
		i.putExtra("content", mEditText.getText().toString());
		startService(i);
		finish();
	}

	 

}
