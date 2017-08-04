package com.techscan.dvq;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.techscan.dvq.R.id;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchmainActivity extends Activity {

    @Nullable
    GridView gridview            = null;
    @Nullable
    Button   btnSearchMainReturn = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchmain);
		
		gridview = (GridView)findViewById(R.id.gvSearchMain);	
		
		
		btnSearchMainReturn = (Button)findViewById(R.id.btnSearchMainReturn);
		btnSearchMainReturn .setOnClickListener(ButtonOnClickListener);
		
		ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();  
 
        HashMap<String, Object> map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.icon_inv);
        map.put("ItemText", "按存货查询");
        lstImageItem.add(map);
        
        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.icon_carg);
        map.put("ItemText", "按货位查询");
        lstImageItem.add(map);
        
        SimpleAdapter saImageItems = new SimpleAdapter(this,
                lstImageItem,//数据来源   
                R.layout.girdviewitem,    
                new String[] {"ItemImage","ItemText"}, 
                new int[] {R.id.gvimgItem,R.id.gvtvItem});  
        gridview.setAdapter(saImageItems); 
		gridview.setOnItemClickListener(new ItemClickListener());
		
	}
	
	class  ItemClickListener implements OnItemClickListener  
	{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			switch(arg2)
			{
				case 0:
					Intent intents0 =new Intent(SearchmainActivity.this,SbinvActivity.class);
				    startActivity(intents0);
					break;
				case 1:
					Intent intents1 =new Intent(SearchmainActivity.this,SbbinActivity.class);
				    startActivity(intents1);
					break;
			}
			
		}  
		 
	}


    @NonNull
    private OnClickListener ButtonOnClickListener = new OnClickListener()
    {
  		
		@Override
		public void onClick(@NonNull View v)
		{
			switch(v.getId())
  			{			//btnSDScanReturn
	  			case id.btnSearchMainReturn:
					finish();					
					break;
  			}
		}	    	
    };

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.searchmain, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
