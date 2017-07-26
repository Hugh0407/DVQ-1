package com.techscan.dvq.module.otherOut;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.techscan.dvq.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class OtherOutAct extends Activity {

    @InjectView(R.id.ed_bill_num)
    EditText    edBillNum;
    @InjectView(R.id.ed_bill_date)
    EditText    edBillDate;
    @InjectView(R.id.ed_wh)
    EditText    edWh;
    @InjectView(R.id.btn_refer_wh)
    ImageButton btnReferWh;
    @InjectView(R.id.ed_org)
    EditText    edOrg;
    @InjectView(R.id.btn_refer_org)
    ImageButton btnReferOrg;
    @InjectView(R.id.ed_lei_bie)
    EditText    edLeiBie;
    @InjectView(R.id.btn_refer_lei_bie)
    ImageButton btnReferLeiBie;
    @InjectView(R.id.ed_dep)
    EditText    edDep;
    @InjectView(R.id.btn_refer_dep)
    ImageButton btnReferDep;
    @InjectView(R.id.ed_remark)
    EditText    edRemark;
    @InjectView(R.id.btn_scan)
    Button      btnScan;
    @InjectView(R.id.btn_save)
    Button      btnSave;
    @InjectView(R.id.btn_back)
    Button      btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_out);
        ButterKnife.inject(this);
        init();
    }

    private void init() {
        ActionBar actionBar = this.getActionBar();
        actionBar.setTitle("ÆäËû³ö¿â");
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick({R.id.btn_refer_wh, R.id.btn_refer_org, R.id.btn_refer_lei_bie,
            R.id.btn_refer_dep, R.id.btn_scan, R.id.btn_save, R.id.btn_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_refer_wh:
                break;
            case R.id.btn_refer_org:
                break;
            case R.id.btn_refer_lei_bie:
                break;
            case R.id.btn_refer_dep:
                break;
            case R.id.btn_scan:
                break;
            case R.id.btn_save:
                break;
            case R.id.btn_back:
                finish();
                break;
        }
    }
}
