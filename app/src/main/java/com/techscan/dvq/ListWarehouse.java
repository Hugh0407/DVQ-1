package com.techscan.dvq;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.techscan.dvq.R.id;
import com.techscan.dvq.login.MainLogin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListWarehouse extends Activity {
    //ADD CAIXY TEST START
//	private SoundPool sp;//声明一个SoundPool
//	private int MainLogin.music;//定义一个int来设置suondID
    //ADD CAIXY TEST END
    @Nullable
    Button btlist_warehouseReturn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_warehouse);
        ActionBar actionBar = this.getActionBar();
        actionBar.setTitle("仓库列表");
        //ADD CAIXY START
//		sp= new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
//		MainLogin.music = MainLogin.sp.load(this, R.raw.xxx, 1); //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
//		//ADD CAIXY END

        String jasstr = this.getIntent().getStringExtra("myData");
        btlist_warehouseReturn = (Button) findViewById(R.id.btlist_warehouseReturn);
        btlist_warehouseReturn.setOnClickListener(ButtonOnClickListener);

        try {

            JSONObject jas = new JSONObject(jasstr);
            mData = getData(jas);
            ListView list = (ListView) findViewById(R.id.listwarehouse);

            SimpleAdapter listItemAdapter = new SimpleAdapter(this, mData,//数据源
                    R.layout.vlistwh,//ListItem的XML实现
                    //动态数组与ImageItem对应的子项
                    new String[]{"storname", "storcode"},
                    //ImageItem的XML文件里面的一个ImageView,两个TextView ID
                    new int[]{R.id.vlistwarehousename, R.id.vlistwarehousecode}
            );

            list.setOnItemClickListener(itemListener);

            list.setAdapter(listItemAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
            //Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
        }
    }

    @NonNull
    private OnClickListener ButtonOnClickListener = new OnClickListener() {

        @Override
        public void onClick(@NonNull View v) {
            switch (v.getId()) {            //btnSDScanReturn
                case id.btlist_warehouseReturn:
                    finish();
                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list_warehouse, menu);
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

    @Nullable
    public List<Map<String, Object>> mData;

    private List<Map<String, Object>> getData(@NonNull JSONObject jas) throws JSONException {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;

        JSONObject tempJso = null;

//		if(!jas.getBoolean("Status"))
//		{
//			String errMsg = jas.getString("ErrMsg");
//
//			Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show();
//			return null;
//		}
        //不知道为什么注释的，现在仅仅判断节点是否存在
        if (!jas.has("warehouse")) {
            Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return null;
        }
        JSONArray jsarray = jas.getJSONArray("warehouse");

        for (int i = 0; i < jsarray.length(); i++) {
            tempJso = jsarray.getJSONObject(i);
            map = new HashMap<String, Object>();
            map.put("pk_stordoc", tempJso.getString("pk_stordoc"));
            map.put("storcode", tempJso.getString("storcode"));
            map.put("storname", tempJso.getString("storname"));

            list.add(map);
        }
        return list;
    }

    @NonNull
    private ListView.OnItemClickListener itemListener = new
            ListView.OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull AdapterView<?> arg0, View arg1, int arg2,
                                        long arg3) {
                    // TODO Auto-generated method stub
                    Adapter adapter = arg0.getAdapter();
                    @SuppressWarnings("unchecked")
                    Map<String, Object> map = (Map<String, Object>) adapter.getItem(arg2);

                    String warehousePK1 = map.get("pk_stordoc").toString();
                    String warehousecode = map.get("storcode").toString();
                    String warehouseName = map.get("storname").toString();

                    Intent intent = new Intent();
                    intent.putExtra("result1", warehousePK1);// 把返回数据存入Intent
                    intent.putExtra("result2", warehousecode);// 把返回数据存入Intent
                    intent.putExtra("result3", warehouseName);// 把返回数据存入Intent
                    ListWarehouse.this.setResult(13, intent);// 设置回传数据。resultCode值是1，这个值在主窗口将用来区分回传数据的来源，以做不同的处理
                    ListWarehouse.this.finish();// 关闭子窗口ChildActivity

                }
            };
}
