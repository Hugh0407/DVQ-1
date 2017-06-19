package com.techscan.dvq;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Second extends Activity implements OnClickListener {

	@Override
	public void onClick(View v)
	{
		if(v.getId()==R.id.btSdBack)
		{
			setResult(21);
			this.finish();
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second);
		//textView textViews =(textView)findByViewId(R.id.textView1); 
//		TextView textViews =(TextView)findViewById(R.id.txtWebAddress); 
		//textViews.setText(text)
//		Bundle bundle =  getIntent().getExtras();
//		textViews.setText(bundle.get("hello").toString());
		Button bt = (Button)findViewById(R.id.btSdBack);
		bt.setOnClickListener(this);
		this.setTitle(getString(R.string.ZhuJieMian));
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.second, menu);
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
		return super.onOptionsItemSelected(item);
	}
}
