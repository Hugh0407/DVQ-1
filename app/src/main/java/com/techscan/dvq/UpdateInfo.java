package com.techscan.dvq;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.techscan.dvq.R.id;





public class UpdateInfo extends Activity {

    @Nullable
    Button btnUpdateInfoReturn =null;

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_info);
		Button btnUpdateInfoReturn =null;
        ActionBar actionBar = this.getActionBar();
		actionBar.setTitle(R.string.GengXinRiZhi);
		
		
		btnUpdateInfoReturn = (Button)findViewById(R.id.btnUpdateInfoReturn);
		btnUpdateInfoReturn.setOnClickListener(ButtonOnClickListener);
		

		
		SetUpdateInfo();

		

	}

	private void SetUpdateInfo()
    {

		
    }



    @NonNull
    private OnClickListener ButtonOnClickListener = new OnClickListener()
    {
  		
		@Override
		public void onClick(@NonNull View v)
		{
			switch(v.getId())
  			{			//btnSDScanReturn
	  			case id.btnUpdateInfoReturn:
	  				finish();
					break;
  			}
		}	    	
    };
    
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.update_info, menu);
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
