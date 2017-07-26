package com.techscan.dvq.module.otherIn;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.techscan.dvq.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class OtherInAct extends Activity {

    @InjectView(R.id.ed_bill_num)
    EditText    edBillNum;
    @InjectView(R.id.ed_bill_date)
    EditText    edBillDate;
    @InjectView(R.id.ed_wh)
    EditText    edWh;
    @InjectView(R.id.btn_refer_wh)
    ImageButton btnReferWh;
    @InjectView(R.id.ed_organization)
    EditText    edOrganization;
    @InjectView(R.id.btn_refer_organization)
    ImageButton btnReferOrganization;
    @InjectView(R.id.ed_lei_bie)
    EditText    edLeiBie;
    @InjectView(R.id.btn_refer_lei_bie)
    ImageButton btnReferLeiBie;
    @InjectView(R.id.ed_department)
    EditText    edDepartment;
    @InjectView(R.id.btn_refer_department)
    ImageButton btnReferDepartment;
    @InjectView(R.id.textView)
    TextView    textView;
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
        setContentView(R.layout.activity_other_in);
        ButterKnife.inject(this);
        init();
    }

    private void init() {
        ActionBar actionBar = this.getActionBar();
        actionBar.setTitle("∆‰À˚»Îø‚");
    }

    @OnClick({R.id.btn_refer_wh, R.id.btn_refer_organization, R.id.btn_refer_lei_bie,
            R.id.btn_refer_department, R.id.btn_scan, R.id.btn_save, R.id.btn_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_refer_wh:
                break;
            case R.id.btn_refer_organization:
                break;
            case R.id.btn_refer_lei_bie:
                break;
            case R.id.btn_refer_department:
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
