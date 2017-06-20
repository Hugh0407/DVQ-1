package com.techscan.dvq.MaterialOut;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.techscan.dvq.Common;
import com.techscan.dvq.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MaterialOutAct extends Activity implements View.OnClickListener {


    private String TAG = this.getClass().getSimpleName();

    Button btnPurInScan, btnPurinSave, btnBack;
    EditText txtPurOrderNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_in);
        initView();

        JSONObject para = new JSONObject();

        try {
            para.put("FunctionName", "GetInvBaseInfo");
            para.put("CompanyCode", "4100");
            para.put("InvCode", "a");
            para.put("TableName", "inventory");
            JSONObject rev = Common.DoHttpQuery(para, "CommonQuery", "a1");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void initView() {
        txtPurOrderNo = (EditText) findViewById(R.id.txtPurOrderNo);
        btnPurInScan = (Button) findViewById(R.id.btnPurInScan);
        btnPurinSave = (Button) findViewById(R.id.btnPurinSave);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnPurInScan.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //扫描按钮
            case R.id.btnPurInScan:
                Intent in = new Intent(MaterialOutAct.this, MaterialOutScanAct.class);
                startActivity(in);
                break;
            //保存按钮
            case R.id.btnPurinSave:

                break;
            //退出按钮
            case R.id.btnBack:
                finish();
                break;
            default:

                break;
        }
    }
}
