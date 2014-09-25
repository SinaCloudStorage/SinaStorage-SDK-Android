package com.sample;

import java.util.ArrayList;
import java.util.HashSet;

import com.sample.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

	@SuppressWarnings("serial")
	ArrayList<String> listItems = new ArrayList<String>(){
		{
			//Service操作
			add("Service操作---");
			add("列所有Bucket");
			//Bucket操作
			add("Bucket操作---");
			add("列Bucket中所有object");
			add("创建Bucket");
			add("删除Bucket");
			add("获取Bucket ACL");
			add("设置Bucket ACL");
			add("获取Bucket Info");
			//Object操作
			add("Object操作---");
			add("获取Object信息");
			add("下载Object");
			add("上传Object");
			add("Object秒传");
			add("设置Object Metadata");
			add("获取Object Metadata");
			add("删除Object");
			add("获取Object ACL");
			add("设置Object ACL");
			add("获取Object Info");
			//文件分片上传
			add("分片上传---");
			add("文件分片上传");
			add("获取已上传的分片列表");
			//生成URL文件地址
			add("URL文件地址---");
			add("生成URL文件地址");
		}
	};
	
	@SuppressWarnings("serial")
	HashSet<Integer> separatorPositions = new HashSet<Integer>(){
		{
			add(0);
			add(2);
			add(9);
			add(20);
			add(23);
		}
	};
	
	public static final int TYPE_MAIN = 0;
	public static final int TYPE_SEPARATOR = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		ListView list = (ListView)findViewById(R.id.list);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int itemType = adapter.getItemViewType(position);
				if(itemType == TYPE_MAIN){
					Intent intent = new Intent(MainActivity.this, SCSResultActivity.class);
					intent.putExtra("position", position);
					intent.putExtra("title", MainActivity.this.listItems.get(position));
					startActivity(intent);
					
				}
				
			}
		});
	}
	
	@SuppressLint("ResourceAsColor")
	BaseAdapter adapter = new BaseAdapter() {
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

            int type = getItemViewType(position);
            if (convertView == null) {
                switch (type) {
                    case TYPE_MAIN:{
                    	LinearLayout cellView = new LinearLayout(MainActivity.this);
            			cellView.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, 50));
            			TextView tv = new TextView(MainActivity.this);
            			tv.setPadding(10, 10, 10, 5);
            			tv.setTag("label");
            			tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
            			cellView.addView(tv);
            			cellView.setBackgroundResource(R.drawable.list_selector);
            			convertView = cellView;
            			break;
        			}
                    case TYPE_SEPARATOR:{
                    	LinearLayout cellView = new LinearLayout(MainActivity.this);
            			cellView.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, 20));
            			TextView tv = new TextView(MainActivity.this);
            			tv.setTextSize(10);
            			tv.setBackgroundColor(R.color.separator_background);
            			tv.setTag("separator");
            			tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
            			cellView.addView(tv);
            			cellView.setBackgroundResource(R.drawable.list_separator_selector);
            			
            			convertView = cellView;
                        break;
                    }
                }
            }
            
            switch (type) {
            case TYPE_MAIN:
            	((TextView)convertView.findViewWithTag("label")).setText(MainActivity.this.listItems.get(position));
                break;
            case TYPE_SEPARATOR:
            	((TextView)convertView.findViewWithTag("separator")).setText(MainActivity.this.listItems.get(position));
                break;
            }
            
            return convertView;
		}
		
		@Override
		public long getItemId(int position) {
			return position;
		}
		
		@Override
		public Object getItem(int position) {
			return MainActivity.this.listItems.get(position);
		}
		
		@Override
		public int getCount() {
			return MainActivity.this.listItems.size();
		}
		
		@Override
		public int getItemViewType(int position)
		{
			return MainActivity.this.separatorPositions.contains(position)?1:0;
		}
		
		@Override
		public int getViewTypeCount() {
	        return 2;
	    }
	};
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
}
