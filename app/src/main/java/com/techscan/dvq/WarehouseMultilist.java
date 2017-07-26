package com.techscan.dvq;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.techscan.dvq.R.id;
import com.techscan.dvq.common.Common;
import com.techscan.dvq.login.MainLogin;

import org.apache.http.ParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class WarehouseMultilist extends Activity {

	protected static final String PREFERENCE_SETTING = null;
	static TextView tvMulCount = null;
	static ListView WHmultilist = null;
	Button btnWHMListClear = null;
	Button btnWHMListOK = null;
	int localid = 0;
	ArrayList<String> listStr = null;
	ArrayList<HashMap<String, String>> array = null;
	ArrayList<String> listCode = null;
	private List<Map<String, Object>> mData;
	private Handler handler = null;
	private List<HashMap<String, Object>> MultiList = null;
	private MyAdapter adapter;
	private static Adapter ItemAdapter = null;
	private Map<String, Object> ItemMap = null;
	private static Map<String, Object> ItemMap2 = null;

	private Map<String, Object> ResultMap = new ArrayMap<String, Object>();

	private List<Map<String, Object>> ResultList = new ArrayList<Map<String, Object>>();
	private static List<Map<String, Object>> ResultListSave = new ArrayList<Map<String, Object>>();

	// ADD CAIXY TEST START
//	private SoundPool sp;// 声明一个SoundPool
//	private int MainLogin.music;// 定义一个int来设置suondID
	// ADD CAIXY TEST END
	Button btWarehouseMultilistReturn = null;

	private String sWhCode2 = "";
	private HashSet<String> itemSelected = new HashSet<String>();

	// ArrayList<HashMap<String,Integer>> localidList =null
	// ArrayList<Integer> localidList = new ArrayList<Integer>();
	List<Integer> localidListSave = new ArrayList<Integer>();
	List<Integer> localidList = new ArrayList<Integer>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_warehouse_multilist);

		ActionBar actionBar = this.getActionBar();
		actionBar.setTitle(R.string.CangKuMingXi);

		// ADD CAIXY START
//		sp = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);// 第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
//		MainLogin.music = MainLogin.sp.load(this, R.raw.xxx, 1); // 把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
		// ADD CAIXY END

		// Drawable TitleBar =
		// this.getResources().getDrawable(R.drawable.bg_barbackgroup);
		// actionBar.setBackgroundDrawable(TitleBar);
		// actionBar.setDisplayHomeAsUpEnabled(true);
		// actionBar.setDisplayShowHomeEnabled(true);
		// actionBar.show();

		btWarehouseMultilistReturn = (Button) findViewById(R.id.btWarehouseMultilistReturn);
		btWarehouseMultilistReturn.setOnClickListener(ButtonOnClickListener);

		sWhCode2 = this.getIntent().getStringExtra("storcode");

		tvMulCount = (TextView) findViewById(R.id.tvMulCount);
		WHmultilist = (ListView) findViewById(R.id.WHmultilist);
		btnWHMListClear = (Button) findViewById(R.id.btnWHMListClear);
		btnWHMListOK = (Button) findViewById(R.id.btnWHMListOK);
		// getListCheckedItemIds();

		localidListSave = this.getIntent().getIntegerArrayListExtra("slocalid");

		// test

		// for(int i=0;i<=localidList.size();i++)
		// {
		// int listid = localidList.get(i);
		// WHmultilist.setItemChecked(5, true);
		// WHmultilist.setSelection(5);

		// isSelected.put(5, true);

		// }

		// String jasstr= this.getIntent().getStringExtra("myData");
		// try
		// {
		//
		// JSONObject jas=new JSONObject(jasstr);
		// mData = getData(jas);
		// ListView list = (ListView) findViewById(R.id.WHmultilist);
		//
		// SimpleAdapter listItemAdapter = new SimpleAdapter(this,mData,//数据源
		// R.layout.vmultilistwh,//ListItem的XML实现
		// //动态数组与ImageItem对应的子项
		// new String[] {"storcode","storname"},
		// //ImageItem的XML文件里面的一个ImageView,两个TextView ID
		// new int[] {R.id.vmultilistwhcode,R.id.vmultilistwhname}
		// );
		//
		// list.setOnItemClickListener((OnItemClickListener) itemListener);
		//
		// list.setAdapter(listItemAdapter);
		// }
		// catch (JSONException e)
		// {
		// e.printStackTrace();
		// }

		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());

		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
				.penaltyLog().penaltyDeath().build());

		try {
			showCheckBoxListView();

			if (localidListSave != null) {
				ResultList = new ArrayList();
				localidList = new ArrayList();

				adapter.initSelect(localidListSave);
				adapter.notifyDataSetChanged();
				WHmultilist.setAdapter(adapter);

				ResultList = ResultListSave;
				localidList = localidListSave;

				tvMulCount.setText(getString(R.string.YiXuanZhong) + ResultList.size() + getString(R.string.Xiang));
				localidListSave = new ArrayList();
				ResultListSave = new ArrayList();

			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 确定按钮
		btnWHMListOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				/*
				 * SerializableMap tmpmap=new SerializableMap();
				 * tmpmap.setMap(ResultMap); Bundle ResultBundle = new Bundle();
				 * ResultBundle.putSerializable("resultinfo", tmpmap);
				 * intent.putExtras(ResultBundle);// 把返回数据存入Intent
				 */

				// SerializableList tmplist = new SerializableList();
				// tmplist.setList(ResultList);

				SerializableList tmplist = new SerializableList();

				tmplist.setList(ResultList);
				Bundle ResultBundle = new Bundle();
				ResultBundle.putSerializable("resultinfo", tmplist);
				intent.putExtras(ResultBundle);
				// test
				intent.putIntegerArrayListExtra("slocalid",
						(ArrayList<Integer>) localidList);

				WarehouseMultilist.this.setResult(1, intent);

				WHmultilist.setItemChecked(localid, true);
				WHmultilist.setSelection(localid);
				finish();

			}
		});

		btWarehouseMultilistReturn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				finish();

			}
		});

		// 全消除按钮
		btnWHMListClear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				adapter.noCheckAll();
				ResultList.clear();
				listStr.clear();
				localidList.clear();
				ResultListSave.clear();
				adapter.notifyDataSetChanged();
				tvMulCount.setText(R.string.YiXuanZhong0Xiang);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.warehouse_multilist, menu);
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

	private OnClickListener ButtonOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) { // btnSDScanReturn
			case id.btWarehouseMultilistReturn:
				finish();
				break;
			}
		}
	};

	// private List<Map<String, Object>> getData(JSONObject jas) throws
	// JSONException
	// {
	// List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	// Map<String, Object> map;
	//
	// JSONObject tempJso = null;
	//
	// if(!jas.has("Status"))
	// {
	// Toast.makeText(this, "网络操作出现问题!请稍后再试", Toast.LENGTH_LONG).show();
	// MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
	// return null;
	// }
	//
	// if(!jas.getBoolean("Status"))
	// {
	// String errMsg = "";
	// if(jas.has("ErrMsg"))
	// {
	// errMsg = jas.getString("ErrMsg");
	// }
	// else
	// {
	// errMsg = "网络操作出现问题!请稍后再试";
	// }
	// Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show();
	//
	// Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show();
	// //ADD CAIXY TEST START
	// MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
	// //ADD CAIXY TEST END
	// return null;
	// }
	// JSONArray jsarray= jas.getJSONArray("warehouse");
	//
	// for(int i = 0;i<jsarray.length();i++)
	// {
	// tempJso = jsarray.getJSONObject(i);
	// map = new HashMap<String, Object>();
	// map.put("pk_stordoc", tempJso.getString("pk_stordoc"));
	// map.put("storcode", tempJso.getString("storcode"));
	// map.put("storname", tempJso.getString("storname"));
	// map.put("accid", tempJso.getString("accid"));
	//
	// list.add(map);
	// }
	// return list;
	// }

	// 显示带有checkbox的listview
	public void showCheckBoxListView() throws ParseException, IOException,
			JSONException {
		MultiList = new ArrayList<HashMap<String, Object>>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map;
		try {
			JSONObject para = new JSONObject();

			para.put("FunctionName", "GetWareHouseListAB");
			para.put("CompanyCode", MainLogin.objLog.CompanyCode);
			para.put("STOrgCode", MainLogin.objLog.STOrgCode);
			para.put("TableName", "warehouse");
			if (!MainLogin.getwifiinfo()) {
				Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG)
						.show();
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				return;
			}
			JSONObject rev = Common.DoHttpQuery(para, "GetWareHouseListAB", "");

			if (rev == null) {
				// 网络通讯错误
				Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG)
						.show();
				// ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				// ADD CAIXY TEST END
				return;
			}

			if (!rev.has("Status")) {
				Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG)
						.show();
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				return;
			}

			if (rev.getBoolean("Status")) {
				JSONArray val = rev.getJSONArray("warehouse");

				array = new ArrayList<HashMap<String, String>>();

				JSONArray valA = new JSONArray();
				JSONArray valB = new JSONArray();

				for (int i = 0; i < val.length(); i++) {

					if (val.getJSONObject(i).get("accid").toString()
							.equals("A")) {
						valA.put(val.getJSONObject(i));
					} else {
						valB.put(val.getJSONObject(i));
					}
				}

				for (int i = 0; i < valA.length(); i++) {
					map = new HashMap<String, Object>();

					String HWCodeA = valA.getJSONObject(i).get("storcode")
							.toString();
					String HWNameA = valA.getJSONObject(i).get("storname")
							.toString();
					map.put("pk_stordocA",
							valA.getJSONObject(i).get("pk_stordoc").toString());
					map.put("pk_stordocB", "");
					map.put("storcode", valA.getJSONObject(i).get("storcode")
							.toString());
					map.put("storname", valA.getJSONObject(i).get("storname")
							.toString());
					map.put("accid", "A");

					for (int x = 0; x < valB.length(); x++) {
						if (HWCodeA.equals(valB.getJSONObject(x)
								.get("storcode").toString())
								&& HWNameA.equals(valB.getJSONObject(x)
										.get("storname").toString())) {
							map.put("pk_stordocB",
									valB.getJSONObject(x).get("pk_stordoc")
											.toString());
							map.put("accid", "A&B");
						}
					}

					list.add(map);

				}

				for (int x = 0; x < valB.length(); x++) {
					JSONObject tempJsoB = valB.getJSONObject(x);

					String HWCodeB = tempJsoB.getString("storcode");
					String HWNameB = tempJsoB.getString("storname");
					String PutOk = "NG";
					for (int y = 0; y < list.size(); y++) {

						Map<String, Object> tmpmap = list.get(y);
						JSONObject json = Common.MapTOJSONOBject(tmpmap);
						String HWCodeA = ((JSONObject) json).getString(
								"storcode").toString();
						String HWNameA = ((JSONObject) json).getString(
								"storname").toString();

						if (HWCodeB.equals(HWCodeA) && HWNameB.equals(HWNameA)) {
							PutOk = "OK";
							break;
						}
					}
					if (PutOk.equals("NG")) {
						map = new HashMap<String, Object>();
						map.put("pk_stordocA", "");
						map.put("pk_stordocB",
								valB.getJSONObject(x).get("pk_stordoc")
										.toString());
						map.put("storcode",
								valB.getJSONObject(x).get("storcode")
										.toString());
						map.put("storname",
								valB.getJSONObject(x).get("storname")
										.toString());
						map.put("accid", "B");
						list.add(map);
					}

				}

				// for(int i=0;i< val.length();i++)
				// {
				// map = new HashMap<String, Object>();
				//
				// map.put("pk_stordoc",
				// val.getJSONObject(i).get("pk_stordoc").toString());
				// map.put("storcode",
				// val.getJSONObject(i).get("storcode").toString());
				// map.put("storname",
				// val.getJSONObject(i).get("storname").toString());
				// map.put("accid", val.getJSONObject(i).get("accid"));
				//
				// //array.add(map);
				// list.add(map);
				//
				// }

				adapter = new MyAdapter(this, list, R.layout.vmultilistwh,
						new String[] { "storname", "storcode", "accid" },
						new int[] { R.id.vmultilistwhcode,
								R.id.vmultilistwhname, R.id.tvaccidtype });
				WHmultilist.setAdapter(adapter);
				listStr = new ArrayList<String>();

			} else {
				String Errmsg = rev.getString("ErrMsg");
				Toast.makeText(this, Errmsg, Toast.LENGTH_LONG).show();
				// ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				// ADD CAIXY TEST END
			}

		} catch (Exception e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
		}

		WHmultilist.setOnItemClickListener(new OnItemClickListener() {

			Integer listid = null;

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				ViewHolder holder = (ViewHolder) view.getTag();
				ItemAdapter = arg0.getAdapter();
				ItemMap = (Map<String, Object>) ItemAdapter.getItem(position);
				ArrayList<String> selectedlist = null;
				// HashMap item = (HashMap<String,
				// Object>)arg0.getItemAtPosition(position);

				holder.cb.toggle();// 在每次获取点击的item时改变checkbox的状态
				MyAdapter.isSelected.put(position, holder.cb.isChecked()); // 同时修改map的值保存状态
				listid = position;
				if (holder.cb.isChecked() == true) {
					ResultList.add(ItemMap);
					listStr.add(holder.tvwarehousecode.getText().toString());

					localidList.add(listid);

				} else {
					ResultList.remove(ItemMap);

					listStr.remove(holder.tvwarehousecode.getText().toString());

					localidList.remove(listid);

				}

				tvMulCount.setText("已选中" + ResultList.size() + "项");

			}

		});

		// 判断是否要被选中
		Map<String, Object> WHMultiMap = null;
		Map<String, Object> SelectedMap = null;
		String WHMultiwarehousecode;
		String WHMultiwarehousename;
		String Selectedwarehousecode;
		String Selectedwarehousename;
		if (Data.getSelListAdapter() == null)

			return;
		for (int i = 0; i < adapter.getCount(); i++) {
			WHMultiMap = (Map<String, Object>) adapter.getItem(i);
			WHMultiwarehousecode = WHMultiMap.get("storcode").toString(); // 仓库号
			WHMultiwarehousename = WHMultiMap.get("storname").toString(); // 仓库名
			for (int j = 0; j < Data.getSelListAdapter().getCount(); j++)
			// for(int j=0;j<WHmultilist.getCount();j++)
			{
				SelectedMap = (Map<String, Object>) Data.getSelListAdapter()
						.getItem(j);
				// SelectedMap = (Map<String, Object>)
				// WHmultilist.getChildAt(j);
				Selectedwarehousecode = SelectedMap.get("storcode").toString(); // 仓库号
				Selectedwarehousename = SelectedMap.get("storname").toString(); // 仓库名
				if (WHMultiwarehousecode.equals(Selectedwarehousecode)
						&& WHMultiwarehousename.equals(Selectedwarehousename)) {
					ViewHolder holder = (ViewHolder) adapter.getView(i, null,
							null).getTag();
					holder.cb.toggle();// 在每次获取点击的item时改变checkbox的状态
					MyAdapter.isSelected.put(i, holder.cb.isChecked()); // 同时修改map的值保存状态
					ResultList.add(SelectedMap);
					// listCode.add("'"
					// +holder.tvwarehousecode.getText().toString()+"'");
					listStr.add(holder.tvwarehousecode.getText().toString());
					// adapter.getView(i, null,
					// null).setBackgroundColor(Color.parseColor("#33b5e5"));
				}
			}
		}
		tvMulCount.setText("已选中" + listStr.size() + "项");
	}

	// public void isSelect()
	// {
	// if(listStr.size()==0)
	// return;
	// for(int i=0;i<=WHmultilist.getCount();i++)
	// {
	// Cursor cursor = (Cursor) adapter.getItem(i);
	// String idstr = cursor.getString(ID_COLUMN_INDEX);
	// itemSelected.add(idstr);
	// }
	// }
	public void setInit(ListAdapter initListAdapter) {
		Data.setSelListAdapter(initListAdapter);
	}

	// 全局变量赋值
	public static class Data {
		private static ListAdapter SelListAdapter = null;

		public static ListAdapter getSelListAdapter() {
			return SelListAdapter;
		}

		public static void setSelListAdapter(ListAdapter pListAdapter) {
			SelListAdapter = pListAdapter;
		}
	}

	// 为listview自定义适配器内部类
	public static class MyAdapter extends BaseAdapter {
		public static HashMap<Integer, Boolean> isSelected;
		private Context context = null;
		private LayoutInflater inflater = null;
		private List<Map<String, Object>> list = null;
		private String keyString[] = null;
		private String itemString0 = null; // 记录每个item中textview的值
		private String itemString1 = null;
		private String itemString2 = null;
		// private String itemString2 = null;
		private int idValue[] = null;// id值

		public MyAdapter(Context context, List<Map<String, Object>> list,
				int resource, String[] from, int[] to) {
			this.context = context;
			this.list = list;
			keyString = new String[from.length];
			idValue = new int[to.length];
			System.arraycopy(from, 0, keyString, 0, from.length);
			System.arraycopy(to, 0, idValue, 0, to.length);
			inflater = LayoutInflater.from(context);
			init();

			// initDate();
		}

		// // 初始化isSelected的数据
		// private void initDate(){
		// for(int i=0; i<list.size();i++) {
		// getIsSelected().put(i,false);
		// }
		// }
		//
		// public static HashMap<Integer,Boolean> getIsSelected() {
		// return isSelected;
		// }
		//
		// public static void setIsSelected(HashMap<Integer,Boolean> isSelected)
		// {
		// MyAdapter.isSelected = isSelected;
		// }

		// 初始化 设置所有checkbox都为未选择
		public void init() {
			isSelected = new HashMap<Integer, Boolean>();
			for (int i = 0; i < list.size(); i++) {
				isSelected.put(i, false);
			}
		}

		public void initSelect(List<Integer> localidList) {

			for (int t = 0; t < localidList.size(); t++) {
				int A = localidList.get(t);
				ItemMap2 = (Map<String, Object>) ItemAdapter.getItem(A);
				ResultListSave.add(ItemMap2);

				isSelected.put(A, true);
				Map<String, Object> map = list.get(A);
				// ResultList.add(ItemMap);
				// listStr.add(holder.tvwarehousecode.getText().toString());
				// tvMulCount.setText("已选中"+listStr.size()+"项");

			}

			notifyDataSetChanged();

		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View view, ViewGroup arg2) {
			ViewHolder holder = null;
			if (holder == null) {
				holder = new ViewHolder();
				if (view == null) {
					view = inflater.inflate(R.layout.vmultilistwh, null);
				}
				// holder.tvpdorder = (TextView)
				// view.findViewById(R.id.mlistpdorder);
				// holder.tvfromware = (TextView)
				// view.findViewById(R.id.mlistfromware);
				// holder.tvtoware = (TextView)
				// view.findViewById(R.id.mlisttoware);
				// holder.cb = (CheckBox)
				// view.findViewById(R.id.chbmitemselect);
				holder.tvwarehousecode = (TextView) view
						.findViewById(R.id.vmultilistwhcode);
				holder.tvwarehousename = (TextView) view
						.findViewById(R.id.vmultilistwhname);
				holder.tvaccid = (TextView) view.findViewById(R.id.tvaccidtype);
				holder.cb = (CheckBox) view.findViewById(R.id.cmulitemselect);
				view.setTag(holder);

			} else
				holder = (ViewHolder) view.getTag();

			Map<String, Object> map = list.get(position);
			if (map != null) {
				itemString1 = (String) map.get(keyString[1]);
				itemString0 = (String) map.get(keyString[0]);
				itemString2 = (String) map.get(keyString[2]);

				holder.tvwarehousecode.setText(itemString1);
				holder.tvwarehousename.setText(itemString0);
				holder.tvaccid.setText(itemString2);

			}
			holder.cb.setChecked(isSelected.get(position));
			return view;
		}

		public void noCheckAll() {
			for (int i = 0; i < list.size(); i++) {
				isSelected.put(i, false);
				Map<String, Object> map = list.get(i);
			}
			notifyDataSetChanged();
		}

	}
}
