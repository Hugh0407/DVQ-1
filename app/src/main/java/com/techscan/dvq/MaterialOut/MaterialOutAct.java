package com.techscan.dvq.MaterialOut;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.techscan.dvq.R;
import com.techscan.dvq.VlistRdcl;

import org.apache.http.ParseException;
import org.json.JSONException;

import java.io.IOException;

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
    @InjectView(R.id.refer_remark)
    ImageButton mReferRemark;
    @InjectView(R.id.btnPurInScan)
    Button mBtnPurInScan;
    @InjectView(R.id.btnPurinSave)
    Button mBtnPurinSave;
    @InjectView(R.id.btnBack)
    Button mBtnBack;
    private String TAG = this.getClass().getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_in);
        ButterKnife.inject(this);

//        JSONObject para = new JSONObject();
//
//        try {
//            para.put("FunctionName", "GetInvBaseInfo");
//            para.put("CompanyCode", "4100");
//            para.put("InvCode", "a");
//            para.put("TableName", "inventory");
//            JSONObject rev = Common.DoHttpQuery(para, "CommonQuery", "a1");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }


    @OnClick({R.id.refer_bill_num, R.id.refer_bill_date, R.id.refer_wh, R.id.refer_organization, R.id.refer_lei_bie, R.id.refer_remark, R.id.btnPurInScan, R.id.btnPurinSave, R.id.btnBack})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.refer_bill_num:
                break;
            case R.id.refer_bill_date:
                break;
            case R.id.refer_wh:
                try {
                    btnRdclClick("");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.refer_organization:
                break;
            case R.id.refer_lei_bie:
                break;
            case R.id.refer_remark:
                break;
            case R.id.btnPurInScan:
                Intent in = new Intent(MaterialOutAct.this, MaterialOutScanAct.class);
                startActivity(in);
                break;
            case R.id.btnPurinSave:
                break;
            case R.id.btnBack:
                finish();
                break;
        }
    }

    private void btnRdclClick(String Code) throws ParseException, IOException,
            JSONException {
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
}
