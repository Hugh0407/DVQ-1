package com.techscan.dvq.module.otherOut;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.techscan.dvq.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class OtherOutScanAct extends Activity {

    @InjectView(R.id.ed_bar_code)
    EditText edBarCode;
    @InjectView(R.id.ed_invcode)
    EditText edInvcode;
    @InjectView(R.id.ed_name)
    EditText edName;
    @InjectView(R.id.ed_type)
    EditText edType;
    @InjectView(R.id.ed_spectype)
    EditText edSpectype;
    @InjectView(R.id.ed_batch)
    EditText edBatch;
    @InjectView(R.id.ed_manual)
    EditText edManual;
    @InjectView(R.id.ed_cost_object)
    EditText edCostObject;
    @InjectView(R.id.ed_num)
    EditText edNum;
    @InjectView(R.id.ed_weight)
    EditText edWeight;
    @InjectView(R.id.ed_qty)
    EditText edQty;
    @InjectView(R.id.ed_unit)
    EditText edUnit;
    @InjectView(R.id.btn_overview)
    Button   btnOverview;
    @InjectView(R.id.btn_detail)
    Button   btnDetail;
    @InjectView(R.id.btn_back)
    Button   btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_out_scan);
        ButterKnife.inject(this);
        init();
    }

    private void init() {
        ActionBar actionBar = this.getActionBar();
        actionBar.setTitle("ÆäËû³ö¿âÉ¨Ãè");
    }

    @OnClick({R.id.btn_overview, R.id.btn_detail, R.id.btn_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_overview:
                break;
            case R.id.btn_detail:
                break;
            case R.id.btn_back:
                finish();
                break;
        }
    }
}
