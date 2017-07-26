package com.techscan.dvq;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.techscan.dvq.common.Common;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;



public class LoadImg extends Activity {
	
	Button btLoadImgReturn;
	Bitmap bmImg;  
	ImageView imView;  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_load_img);
		
		ActionBar actionBar = this.getActionBar();
		actionBar.setTitle(R.string.CunHuoTuPian);
		
		btLoadImgReturn = (Button)findViewById(R.id.btLoadImgReturn);
		btLoadImgReturn.setOnClickListener(myBtnListner);
		imView = (ImageView) findViewById(R.id.LoadImgiView); 
		
		String LogInStr = Common.lsUrl;
		
		Intent myintent =getIntent();
		//tmpAccID = myintent.getStringExtra("AccID");
		String InvCode = myintent.getStringExtra("InvCode");
		
		//String imageUrl = "http://192.168.0.210:5557/DVQImg/InvImg.jpg";  
		String imageUrl = LogInStr.substring(0,LogInStr.length()-13)+"/DVQ/DVQImg/"+InvCode+".jpg";
		imView.setImageBitmap(returnBitMap(imageUrl));
		

	}
	
	public Bitmap returnBitMap(String url) {  
		URL myFileUrl = null;  
		Bitmap bitmap = null;  
		try {  
			myFileUrl = new URL(url);  
		} catch (MalformedURLException e) {  
			e.printStackTrace();  
		}  
		try {  
			HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();  
			conn.setDoInput(true);  
			conn.connect();  
			InputStream is = conn.getInputStream();  
			bitmap = BitmapFactory.decodeStream(is);  
			is.close();  
		} catch (IOException e) {  
		  e.printStackTrace();  
		  }  
		  return bitmap;  
		}  
	
	
	
	//创建对话框的按钮事件侦听	
    private Button.OnClickListener  myBtnListner = new 
    		Button.OnClickListener()
    {
		@Override
		public void onClick(View v) 
		{
			switch(v.getId())
			{			
				case R.id.btLoadImgReturn:
				{
					finish();
				}
			}
		}
    };
    


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.load_img, menu);
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
