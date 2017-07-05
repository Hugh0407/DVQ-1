package com.techscan.dvq.statusChange;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.techscan.dvq.OtherOrderList;
import com.techscan.dvq.R;
import com.techscan.dvq.common.Utils;
import com.techscan.dvq.statusChange.scan.SCScanAct;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class StatusChangeAct extends Activity {

    @InjectView(R.id.ed_bill_type)
    EditText mEdBillType;
    @InjectView(R.id.btn_bill_type)
    ImageButton mBtnBillType;
    @InjectView(R.id.ed_source_bill)
    EditText mEdSourceBill;
    @InjectView(R.id.btn_source_bill)
    ImageButton mBtnSourceBill;
    @InjectView(R.id.ed_select_wh)
    EditText mEdSelectWh;
    @InjectView(R.id.btn_select_wh)
    ImageButton mBtnSelectWh;

    @InjectView(R.id.sacn)
    Button mSacn;
    @InjectView(R.id.save)
    Button mSave;
    @InjectView(R.id.back)
    Button mBack;

    String result = "";
    String OrderNo = "";
    String OrderID = "";
    String WarehouseName = "";
    String WarehouseID = "";
    String AccID = "";
    String pk_corp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_change);
        ButterKnife.inject(this);
        ActionBar actionBar = this.getActionBar();
        actionBar.setTitle("形态转换");

    }


    @OnClick({R.id.btn_bill_type, R.id.btn_source_bill, R.id.btn_select_wh,
            R.id.sacn, R.id.save, R.id.back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_bill_type:
                break;
            case R.id.btn_source_bill:
                ShowOrderList("");
                break;
            case R.id.btn_select_wh:
                break;
            case R.id.sacn:
                ShowScanDetail();
                break;
            case R.id.save:
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 回传数据<----OtherOrderList.class，从ShowOrderList(); 中跳过去
        if (requestCode == 99 && resultCode == 1) {
            result = data.getStringExtra("result");
            OrderNo = data.getStringExtra("OrderNo");
            OrderID = data.getStringExtra("OrderID");
            WarehouseName = data.getStringExtra("WarehouseName");
            WarehouseID = data.getStringExtra("WarehouseID");
            AccID = data.getStringExtra("AccID");
            pk_corp = data.getStringExtra("pk_corp");
            mEdSourceBill.setText(OrderNo);
            mEdSelectWh.setText(WarehouseName);
        }
        // 回传数据<----OtherStockInDetail.class，从ShowScanDetail(); 中跳过去
        if (requestCode == 97 && resultCode == 3) {

        }
    }

    /**
     * 选择需要转换的order
     *
     * @param lsBillCode
     */
    private void ShowOrderList(String lsBillCode) {
        if (this.mEdBillType.getText().toString().equals("")) {
            Utils.showToast(StatusChangeAct.this, "请选择单据类型");
            return;
        }
        Intent otherOrder = new Intent(this, OtherOrderList.class);
        otherOrder.putExtra("OrderType", "4N");
        otherOrder.putExtra("Typename", this.mEdBillType.getText().toString());
        otherOrder.putExtra("BillCode", lsBillCode);
        startActivityForResult(otherOrder, 99);
    }

    //打开扫描界面
    private void ShowScanDetail() {

        if (OrderID == null || OrderID.equals("")) {
            Utils.showToast(StatusChangeAct.this, "请选择来源的单据号");
        } else {
            Intent otherOrderDetail = new Intent(this, SCScanAct.class);
            otherOrderDetail.putExtra("OrderID", OrderID);
            otherOrderDetail.putExtra("BillNo", OrderNo);
            otherOrderDetail.putExtra("OrderType", "4N");
            otherOrderDetail.putExtra("AccID", AccID);
            otherOrderDetail.putExtra("m_WarehouseID", WarehouseID);
            otherOrderDetail.putExtra("pk_corp", pk_corp);
            startActivityForResult(otherOrderDetail, 93);
        }
    }
}

