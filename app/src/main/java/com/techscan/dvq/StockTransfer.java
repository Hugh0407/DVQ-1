package com.techscan.dvq;

import android.app.ActionBar;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;

public class StockTransfer extends TabActivity {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stock_transfer);
		
		ActionBar actionBar = this.getActionBar();
		actionBar.setTitle(R.string.DiaoBo);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
//		Drawable TitleBar = this.getResources().getDrawable(R.drawable.bg_barbackgroup);
//		actionBar.setBackgroundDrawable(TitleBar);
//		actionBar.show();
		
		StockTransContent cStockTransContent = new StockTransContent();

		Intent OutIntent = new Intent(this,cStockTransContent.getClass());
		OutIntent.putExtra("TransType", "OUT");
		
		Intent InIntent = new Intent(this,cStockTransContent.getClass());
		InIntent.putExtra("TransType", "IN");
		
		TabHost tabHost = getTabHost();
		//tabHost.setup();
		tabHost.addTab(tabHost.newTabSpec("tab1")
	                .setIndicator(getString(R.string.DiaoBoChuKu))
	                .setContent(OutIntent));
	    tabHost.addTab(tabHost.newTabSpec("tab2")
	        		.setIndicator(getString(R.string.DiaoBoRuKu))
	        		.setContent(InIntent));
	    
	    if(Data.getDataTransType().equals("OUT"))
			tabHost.setCurrentTab(0);
		else
			tabHost.setCurrentTab(1);	    

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.stock_transfer, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		//返回
		if(id == android.R.id.home)
        {
            finish();
            return true;
        }
		return super.onOptionsItemSelected(item);
	}
	
	public void setTransType(String sType)
	{
		Data.setDataTransType(sType);
	}
	
	//全局变量赋值
	public static class Data
	{  
	    private static String fsTransType ="";  
	      
	    public static String getDataTransType() {  
	        return fsTransType;  
	    }  
	      
	    public static void setDataTransType(String sType) {  
	    	fsTransType = sType;  
	    }  
	}
	
	
}
