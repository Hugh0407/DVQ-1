package com.techscan.dvq;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.techscan.dvq.login.MainLogin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContentItemView extends Activity {
	
	//ADD CAIXY TEST START
//	private SoundPool sp;//声明一个SoundPool
//	private int MainLogin.music;//定义一个int来设置suondID
	//ADD CAIXY TEST END 
	Button btcontent_itemReturn =null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_content_item_view);
		//ADD CAIXY START
		//sp= new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
		//MainLogin.music = MainLogin.sp.load(this, R.raw.xxx, 1); //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
		//ADD CAIXY END
		btcontent_itemReturn = (Button)findViewById(R.id.btcontent_itemReturn);
		btcontent_itemReturn.setOnClickListener(ButtonOnClickListener);

	}

	private OnClickListener ButtonOnClickListener = new OnClickListener()
    {
  		
		@Override
		public void onClick(View v) 
		{
			switch(v.getId())
  			{			//btnSDScanReturn
//	  			case id.btnmainmenuReturn:
//	  				finish();					
//					break;
  			}
		}
    	
    };
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.content_item_view, menu);
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
	
	private List<Map<String, Object>> getData(JSONObject jas) 
			throws JSONException 
	{
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map;
		
		JSONObject tempJso = null;

		if(!jas.getBoolean("Status"))
		{
			String errMsg = jas.getString("ErrMsg");

			//Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return null;
		}
		JSONArray jsarray= jas.getJSONArray("dbHead");
		
		for(int i = 0;i<jsarray.length();i++)
		{
			tempJso = jsarray.getJSONObject(i);
			map = new HashMap<String, Object>();
			map.put("No", tempJso.getString("cinventoryid"));
			map.put("From", tempJso.getString("cinventoryname"));
			map.put("To",tempJso.getString("vbatchcode"));
			list.add(map);
		}
		return list;
	}
	
	
	JSONObject mData;
	private void ShowData()
	{
		String jasstr= this.getIntent().getStringExtra("myData");
		try 
		{
			
			JSONObject jas=new JSONObject(jasstr);
			if(!jas.getBoolean("Status"))
			{
				Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				return;
			}
			
			//mData = getData(jas);
		} 
		catch (JSONException e) 
		{
			return;
			
		}
		
	}
}
