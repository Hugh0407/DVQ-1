package com.techscan.dvq.materialOut;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.techscan.dvq.Common;
import com.techscan.dvq.ListWarehouse;
import com.techscan.dvq.MainLogin;
import com.techscan.dvq.R;
import com.techscan.dvq.VlistRdcl;
import com.techscan.dvq.materialOut.scan.Goods;
import com.techscan.dvq.materialOut.scan.MaterialOutScanAct;

import org.apache.http.ParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MaterialOutAct extends Activity {


    @InjectView(R.id.bill_num)
    EditText mBillNum;
    @InjectView(R.id.refer_bill_num)
    ImageButton mReferBillNum;
    @InjectView(R.id.bill_date)
    EditText mBillDate;
    @InjectView(R.id.refer_bill_date)
    ImageButton mReferBillDate;
    @InjectView(R.id.wh)
    EditText mWh;
    @InjectView(R.id.refer_wh)
    ImageButton mReferWh;
    @InjectView(R.id.organization)
    EditText mOrganization;
    @InjectView(R.id.refer_organization)
    ImageButton mReferOrganization;
    @InjectView(R.id.lei_bie)
    EditText mLeiBie;
    @InjectView(R.id.refer_lei_bie)
    ImageButton mReferLeiBie;
    @InjectView(R.id.department)
    EditText mDepartment;
    @InjectView(R.id.remark)
    EditText mRemark;
    @InjectView(R.id.btnPurInScan)
    Button mBtnPurInScan;
    @InjectView(R.id.btnPurinSave)
    Button mBtnPurinSave;
    @InjectView(R.id.btnBack)
    Button mBtnBack;
    @InjectView(R.id.refer_department)
    ImageButton mReferDepartment;
    private String TAG = this.getClass().getSimpleName();
    public static final int a = 10;
    List<Goods> tempList;

    String CDISPATCHERID;//收发类别code
    String CDPTID;  //部门id
    String CUSER;   //登录员工id
    String CWAREHOUSEID;    //库存组织
    String PK_CALBODY;      //仓库id
    String PK_CORP;         //公司
    String VBILLCOD;        //单据号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_in);
        ButterKnife.inject(this);
        mOrganization.setText("C00");   // TODO: 2017/6/21 暂时默认设置
        mLeiBie.setText("0105");
        mDepartment.setText("物流部");
        ActionBar actionBar = this.getActionBar();
        actionBar.setTitle("材料出库");
        mBillDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    String time = Utils.formatTime(System.currentTimeMillis());
                    mBillDate.setText(time);
                }
            }
        });
    }

    /**
     * 所有的点击事件
     *
     * @param view
     */
    @OnClick({R.id.refer_bill_num, R.id.refer_bill_date, R.id.refer_wh, R.id.refer_organization,
            R.id.refer_lei_bie, R.id.btnPurInScan, R.id.btnPurinSave,
            R.id.btnBack, R.id.refer_department})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.refer_bill_num:
                break;
            case R.id.refer_bill_date:
                break;
            case R.id.refer_wh:
                try {
                    btnWarehouseClick();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.refer_organization:

//                btnReferSTOrgList(MainLogin.objLog.CompanyCode);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject object = new JSONObject();
                        try {
                            object.put("FunctionName", "GetSTOrgList");
                            object.put("CompanyCode", MainLogin.objLog.CompanyCode);
                            object.put("TableName", "STOrg");
                            JSONObject rev = Common.DoHttpQuery(object, "CommonQuery", "");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            case R.id.refer_lei_bie:
                try {
                    btnRdclClick("");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btnPurInScan:
                Intent in = new Intent(MaterialOutAct.this, MaterialOutScanAct.class);
                startActivityForResult(in, 95);
                break;
            case R.id.btnPurinSave:
                if (tempList != null && tempList.size() > 0) {
                    try {
                        SaveInfo(tempList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.btnBack:
                finish();
                break;
            case R.id.refer_department:
                btnReferDepartment(MainLogin.objLog.CompanyCode);
                break;
        }
    }

    /**
     * 保存单据信息
     *
     * @param goodsList
     * @throws JSONException
     */
    private void SaveInfo(List<Goods> goodsList) throws JSONException {
        final JSONObject table = new JSONObject();
        JSONObject tableHead = new JSONObject();
        tableHead.put("CDISPATCHERID", CDISPATCHERID);
        tableHead.put("CDPTID", CDPTID);
        tableHead.put("CUSER", MainLogin.objLog.UserID);
        tableHead.put("CWAREHOUSEID", CWAREHOUSEID);
//        tableHead.put("PK_CALBODY", MainLogin.objLog.STOrgCode);
        tableHead.put("PK_CORP", MainLogin.objLog.STOrgCode);
        tableHead.put("VBILLCODE", mBillNum.getText().toString());
        table.put("Head", tableHead);
        JSONObject tableBody = new JSONObject();
        JSONArray bodyArray = new JSONArray();
        for (Goods c : goodsList) {
            JSONObject object = new JSONObject();
            object.put("CINVBASID", c.getPk_invbasdoc());
            object.put("CINVENTORYID", c.getPk_invmandoc());
            object.put("NOUTNUM", c.getQty());
//            object.put("PK_BODYCALBODY", MainLogin.objLog.STOrgCode);
            object.put("PK_CORP", MainLogin.objLog.STOrgCode);
            object.put("VBATCHCODE", c.getLot());
            bodyArray.put(object);
        }
        tableBody.put("ScanDetails", bodyArray);
        table.put("Body", tableBody);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jas = MainLogin.objLog.DoHttpQuery(table, "SaveMaterialOut", "A");

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


        Log.d(TAG, "onViewClicked: " + table.toString());
    }


    // 打开收发类别画面
    private void btnRdclClick(String Code) throws ParseException, IOException, JSONException {
        Intent ViewGrid = new Intent(this, VlistRdcl.class);
        ViewGrid.putExtra("FunctionName", "GetRdcl");
        // ViewGrid.putExtra("AccID", "A");
        // ViewGrid.putExtra("rdflag", "1");
        // ViewGrid.putExtra("rdcode", "202");
        ViewGrid.putExtra("AccID", "");
        ViewGrid.putExtra("rdflag", "1");
        ViewGrid.putExtra("rdcode", "");
        startActivityForResult(ViewGrid, 98);
    }

    /**
     * 点击仓库列表参照
     *
     * @throws JSONException
     */
    private void btnWarehouseClick() throws JSONException {
        String lgUser = MainLogin.objLog.LoginUser;
        String lgPwd = MainLogin.objLog.Password;
        String LoginString = MainLogin.objLog.LoginString;

        JSONObject para = new JSONObject();

        para.put("FunctionName", "GetWareHouseList");
        para.put("CompanyCode", MainLogin.objLog.CompanyCode);
        para.put("STOrgCode", MainLogin.objLog.STOrgCode);
        para.put("TableName", "warehouse");

        try {
            if (!MainLogin.getwifiinfo()) {
                Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG).show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                return;
            }

            JSONObject rev = Common.DoHttpQuery(para, "CommonQuery", "");
            if (rev == null) {
                // 网络通讯错误
                Toast.makeText(this, "错误！网络通讯错误", Toast.LENGTH_LONG).show();
                // ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                // ADD CAIXY TEST END
                return;
            }
            if (rev.getBoolean("Status")) {
                JSONArray val = rev.getJSONArray("warehouse");

                JSONObject temp = new JSONObject();
                temp.put("warehouse", val);

                Intent ViewGrid = new Intent(this, ListWarehouse.class);
                ViewGrid.putExtra("myData", temp.toString());

                startActivityForResult(ViewGrid, 97);
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

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //仓库的回传数据 <----ListWarehouse.class
        if (requestCode == 97 && resultCode == 13) {
            String warehousePK1 = data.getStringExtra("result1");
            String warehousecode = data.getStringExtra("result2");
            String warehouseName = data.getStringExtra("result3");
            CWAREHOUSEID = warehousePK1;
            mWh.setText(warehouseName);
        }
        // 收发类别的回传数据 <----VlistRdcl.class
        if (requestCode == 98 && resultCode == 2) {
            String code = data.getStringExtra("Code");
            String name = data.getStringExtra("Name");
            String AccID = data.getStringExtra("AccID");
            String RdIDA = data.getStringExtra("RdIDA");    //需要回传的id
            String RdIDB = data.getStringExtra("RdIDB");
            CDISPATCHERID = RdIDA;
            mLeiBie.setText(name);
        }
        //部门信息的回传数据 <----DepartmentListAct.class
        if (requestCode == 96 && resultCode == 4) {
            String deptname = data.getStringExtra("deptname");
            String pk_deptdoc = data.getStringExtra("pk_deptdoc");
            String deptcode = data.getStringExtra("deptcode");
            CDPTID = pk_deptdoc;
            mDepartment.setText(deptname);
        }

        //扫描明细的回传数据 <----MaterialOutScanAct.class
        if (requestCode == 95 && resultCode == 5) {
            Bundle bundle = data.getExtras();
            tempList = bundle.getParcelableArrayList("overViewList");
        }
    }


    /**
     * 获取库存组织的网络请求
     */
    private void btnReferSTOrgList(String companyCode) {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("FunctionName", "GetSTOrgList");
        parameter.put("CompanyCode", companyCode);
        parameter.put("TableName", "STOrg");
        RequestThread requestThread = new RequestThread(parameter, mHandler, 2);
        Thread td = new Thread(requestThread);
        td.start();
    }

    /**
     * 获取部门列表信息的网络请求
     */
    private void btnReferDepartment(String companyCode) {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("FunctionName", "GetDeptList");
        parameter.put("CompanyCode", companyCode);
        parameter.put("TableName", "department");
        RequestThread requestThread = new RequestThread(parameter, mHandler, 1);
        Thread td = new Thread(requestThread);
        td.start();
    }

    /**
     * 网络请求后的线程通信
     * msg.obj 是从子线程传递过来的数据
     */
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    JSONObject json = (JSONObject) msg.obj;
                    try {
                        if (json.getBoolean("Status")) {
                            JSONArray val = json.getJSONArray("department");
                            JSONObject temp = new JSONObject();
                            temp.put("department", val);
                            Intent ViewGrid = new Intent(MaterialOutAct.this, DepartmentListAct.class);
                            ViewGrid.putExtra("myData", temp.toString());
                            startActivityForResult(ViewGrid, 96);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    JSONObject storg = (JSONObject) msg.obj;
                    try {
                        if (storg.getBoolean("Status")) {
                            JSONArray val = storg.getJSONArray("STOrg");
                            JSONObject temp = new JSONObject();
                            temp.put("STOrg", val);
                            Intent StorgList = new Intent(MaterialOutAct.this, DepartmentListAct.class);
                            StorgList.putExtra("STOrg", temp.toString());
                            startActivityForResult(StorgList, 94);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };
}
