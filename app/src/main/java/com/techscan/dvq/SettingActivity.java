package com.techscan.dvq;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SettingActivity extends Activity implements OnClickListener {

	public final static String PREFERENCE_SETTING = "Setting";
	@Override
	public void onClick(View v)
	{
		if(v.getId() ==R.id.btSAOkS)
		{
			EditText text1 =(EditText)findViewById(R.id.eTAddress);
			//EditText text2 =(EditText)findViewById(R.id.eTAddress2);

			
			EditText textCp=(EditText)findViewById(R.id.eTCpy);
			
			EditText OrgCode=(EditText)findViewById(R.id.txtstorgcode);
			EditText WhCode=(EditText)findViewById(R.id.txtstwhcode);
			
			EditText AccId=(EditText)findViewById(R.id.txtstaccid);
			
			EditText WIFIMin=(EditText)findViewById(R.id.WIFIMin);
			EditText WIFIMax=(EditText)findViewById(R.id.WIFIMax);
			
			
			String Address1=text1.getText().toString();
			//String Address2=text2.getText().toString();
			//String AddressB=textB.getText().toString();
			
			String CpCode=textCp.getText().toString();
			
			String SOrgCode=OrgCode.getText().toString();
			String SWhCode=WhCode.getText().toString();
			String SAccId=AccId.getText().toString();
			
            String sWIFIMin = WIFIMin.getText().toString();
            String sWIFIMax = WIFIMax.getText().toString();
			
			if(!isNum(sWIFIMin)) 
			{
				AlertDialog.Builder localBuilder2 = new AlertDialog.Builder(this);
				localBuilder2.setTitle(R.string.TiXing).setMessage("信号强度必须输入数字").setPositiveButton(R.string.QueDing, null).create().show();
				return;
            }
			
            if(!isNum(sWIFIMax)) 
            {
                AlertDialog.Builder localBuilder2 = new AlertDialog.Builder(this);
                localBuilder2.setTitle(R.string.TiXing).setMessage("信号强度必须输入数字").setPositiveButton(R.string.QueDing, null).create().show();
                return;
            }
			
			SharedPreferences mySharedPreferences = getSharedPreferences(
					PREFERENCE_SETTING, Activity.MODE_PRIVATE);
			if(!Address1.equals("") && !CpCode.equals("")&&!SWhCode.equals("")&&!SOrgCode.equals("")&&!SAccId.equals(""))
			{
				SharedPreferences.Editor editor = mySharedPreferences.edit();
				editor.putString("Address", Address1);
				editor.putString("CompanyCode", CpCode);
				editor.putString("OrgCode", SOrgCode);
				editor.putString("WhCode", SWhCode);
				editor.putString("AccId", SAccId);
                editor.putString("WIFIMin", sWIFIMin);
                editor.putString("WIFIMax", sWIFIMax);
				editor.commit();

				this.finish();
			}
			else
			{
				AlertDialog.Builder bulider = new AlertDialog.Builder(this).setTitle(R.string.TiXing).
						setMessage("请输入正确的公司,连接信息,仓库,库存组织!");
				   bulider.setPositiveButton(R.string.QueDing, null).create().show();
			}
			
		}
		else if(v.getId()==R.id.btSettingCancel)
		{
			this.finish();
		}
	}
	
	  public static boolean isNum(String paramString)
	  {
	    return paramString.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
	  }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
        ActionBar actionBar = this.getActionBar();
		actionBar.setTitle(R.string.JiChuSheZhi);
//		Drawable TitleBar = this.getResources().getDrawable(R.drawable.bg_barbackgroup);
//		actionBar.setBackgroundDrawable(TitleBar);
//		actionBar.setDisplayHomeAsUpEnabled(true);
//		actionBar.setDisplayShowHomeEnabled(true);
//		actionBar.show();
		
		Button btSet=(Button)findViewById(R.id.btSAOkS);
		btSet.setOnClickListener(this);
		Button btExit =(Button)findViewById(R.id.btSettingCancel);
		btExit.setOnClickListener(this);
		SharedPreferences mySharedPreferences = getSharedPreferences(
				SettingActivity.PREFERENCE_SETTING, Activity.MODE_PRIVATE);
		
		EditText address=(EditText)findViewById(R.id.eTAddress);
		//EditText address2=(EditText)findViewById(R.id.eTAddress2);
		address.setText(mySharedPreferences.getString("Address", ""));
		//address2.setText(mySharedPreferences.getString("Address2", ""));
		
		if(address.getText().toString().equals(""))
		{
			address.setText("http://58.211.61.69:8180/service/nihao");
		}
		
		//if(address2.getText().toString().equals(""))
		//{
		//	address2.setText("http://192.168.0.210:5557/service/nihao");
		//}
		
		EditText WIFIMin=(EditText)findViewById(R.id.WIFIMin);
		EditText WIFIMax=(EditText)findViewById(R.id.WIFIMax);
		
		WIFIMin.setText(mySharedPreferences.getString("WIFIMin", "30"));
		WIFIMax.setText(mySharedPreferences.getString("WIFIMax", "90"));
		
		
	    if (WIFIMin.getText().toString().equals(""))
	    	WIFIMin.setText("30");
	    
	    if (WIFIMax.getText().toString().equals(""))
	    	WIFIMax.setText("85");
	      
//		addressB.setText(mySharedPreferences.getString("AddressB", ""));
//		if(addressB.getText().toString().equals(""))
//		{
//			addressB.setText("http://192.168.0.210:5557/service/nihao");
//		}
//		
		EditText CompanyCode=(EditText)findViewById(R.id.eTCpy);
		CompanyCode.setText(mySharedPreferences.getString("CompanyCode", "101"));
		
		EditText OrgCode=(EditText)findViewById(R.id.txtstorgcode);
		OrgCode.setText(mySharedPreferences.getString("OrgCode", "1"));
		
		EditText WhCode=(EditText)findViewById(R.id.txtstwhcode);
		WhCode.setText(mySharedPreferences.getString("WhCode", ""));
		
		EditText AccId=(EditText)findViewById(R.id.txtstaccid);
		AccId.setText(mySharedPreferences.getString("AccId", ""));
		address.requestFocus();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setting, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
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
}
