package com.techscan.dvq.materialOut;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.techscan.dvq.materialOut.scan.Cargo;
import com.techscan.dvq.materialOut.scan.MaterialOutScanAct;

import org.apache.http.ParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_in);
        ButterKnife.inject(this);
        mOrganization.setText("C00");   // TODO: 2017/6/21 暂时默认设置
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

                break;
            case R.id.btnBack:
                finish();
                break;
            case R.id.refer_department:
                btnReferDepartment();
                break;
        }
    }

    /**
     * 获取部门列表信息的网络请求
     */
    private void btnReferDepartment() {
        MyThread myThread = new MyThread();
        new Thread(myThread).start();
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
        //部门名称的回传数据 <----ListWarehouse.class
        if (requestCode == 97 && resultCode == 13) {
            String warehousePK1 = data.getStringExtra("result1");
            String warehousecode = data.getStringExtra("result2");
            String warehouseName = data.getStringExtra("result3");
            mWh.setText(warehouseName);
        }
        // 收发类别的回传数据 <----VlistRdcl.class
        if (requestCode == 98 && resultCode == 2) {
            String code = data.getStringExtra("Code");
            String name = data.getStringExtra("Name");
            String AccID = data.getStringExtra("AccID");
            String RdIDA = data.getStringExtra("RdIDA");
            String RdIDB = data.getStringExtra("RdIDB");
            mLeiBie.setText(name);
        }
        //部门信息的回传数据 <----DepartmentListAct.class
        if (requestCode == 96 && resultCode == 4) {
            String deptname = data.getStringExtra("deptname");
            String pk_deptdoc = data.getStringExtra("pk_deptdoc");
            String deptcode = data.getStringExtra("deptcode");
            mDepartment.setText(deptname);
        }

        //扫描明细的回传数据 <----MaterialOutScanAct.class
        if (requestCode == 95 && resultCode == 5) {
            Bundle bundle = data.getExtras();
            List<Cargo> tempList = bundle.getParcelableArrayList("overViewList");
        }
    }

    /**
     * 获取部门信息的线程
     */
    private class MyThread implements Runnable {
        @Override
        public void run() {
            JSONObject para = new JSONObject();
            try {
                para.put("FunctionName", "GetDeptList");
                para.put("CompanyCode", MainLogin.objLog.CompanyCode);
                para.put("TableName", "department");
                JSONObject rev = Common.DoHttpQuery(para, "CommonQuery", "");
                if (rev.getBoolean("Status")) {
                    JSONArray val = rev.getJSONArray("department");
                    JSONObject temp = new JSONObject();
                    temp.put("department", val);
                    Intent ViewGrid = new Intent(MaterialOutAct.this, DepartmentListAct.class);
                    ViewGrid.putExtra("myData", temp.toString());
                    startActivityForResult(ViewGrid, 96);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
