package com.techscan.dvq.module.pur;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.techscan.dvq.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class PurStockInScanAct extends Activity {

    @InjectView(R.id.ed_barcode)
    EditText edBarcode;
    @InjectView(R.id.ed_invcode)
    EditText edInvcode;
    @InjectView(R.id.ed_name)
    EditText edName;
    @InjectView(R.id.ed_type)
    EditText edType;
    @InjectView(R.id.ed_spec)
    EditText edSpec;
    @InjectView(R.id.ed_batch)
    EditText edBatch;
    @InjectView(R.id.ed_serino)
    EditText edSerino;
    @InjectView(R.id.ed_number)
    EditText edNumber;
    @InjectView(R.id.ed_weight)
    EditText edWeight;
    @InjectView(R.id.ed_qty)
    EditText edQty;
    @InjectView(R.id.ed_unit)
    EditText edUnit;
    @InjectView(R.id.tvPurcount)
    TextView tvPurcount;
    @InjectView(R.id.btn_task)
    Button   btnTask;
    @InjectView(R.id.btn_detail)
    Button   btnDetail;
    @InjectView(R.id.btn_back)
    Button   btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pur_stock_in_scan);
        ButterKnife.inject(this);
    }

    @OnClick({R.id.btn_task, R.id.btn_detail, R.id.btn_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_task:
                break;
            case R.id.btn_detail:
                break;
            case R.id.btn_back:
                break;
        }
    }
}
