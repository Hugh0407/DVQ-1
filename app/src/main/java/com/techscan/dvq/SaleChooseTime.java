package com.techscan.dvq;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static android.content.ContentValues.TAG;


/**
 * Created by Hugh on 2017/6/28.
 */

public class SaleChooseTime extends Activity {
    @InjectView(R.id.et_BeginDate)
    EditText txtBeginDate;
    @InjectView(R.id.et_EndDate)
    EditText txtEndDate;
    @InjectView(R.id.bt_Search)
    Button btSearch;
    @InjectView(R.id.et_BillCode)
    EditText txtBillCode;

    int year;
    int month;
    int day;
    Calendar mycalendar;
    String sEndDate = "";
    String sBillCodes = "";
    String sBeginDate = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_time);
        this.setTitle("单据查询");
        ButterKnife.inject(this);
        initView();

    }
    @OnClick({R.id.et_BillCode, R.id.et_BeginDate, R.id.et_EndDate,R.id.bt_Search})
    public void onViewClicked(View view) {
        switch(view.getId()){
            case R.id.et_BeginDate:
                year = mycalendar.get(Calendar.YEAR); //获取Calendar对象中的年
                month = mycalendar.get(Calendar.MONTH);//获取Calendar对象中的月
                day = mycalendar.get(Calendar.DAY_OF_MONTH);//获取这个月的第几天
                DatePickerDialog dpd = new DatePickerDialog(SaleChooseTime.this, Datelister_s, year, month, day);
                dpd.show();//显示DatePickerDialog组件
                break;
            case R.id.et_EndDate:
                year = mycalendar.get(Calendar.YEAR); //获取Calendar对象中的年
                month = mycalendar.get(Calendar.MONTH);//获取Calendar对象中的月
                day = mycalendar.get(Calendar.DAY_OF_MONTH);//获取这个月的第几天
                DatePickerDialog dpds = new DatePickerDialog(SaleChooseTime.this, Datelistener, year, month, day);
                dpds.show();//显示DatePickerDialog组件
                break;
            case R.id.bt_Search:
                Intent in = this.getIntent();
                sBillCodes  = txtBillCode.getText().toString();
                sBeginDate =  txtBeginDate.getText().toString();
                sEndDate = txtEndDate.getText().toString();
                Log.d(TAG, "onActivityResult: "+sBillCodes);
                Log.d(TAG, "onActivityResult: "+sBeginDate);
                Log.d(TAG, "onActivityResult: "+sEndDate);
                in.putExtra("sBillCodes",sBillCodes);
                in.putExtra("sBeginDate",sBeginDate);
                in.putExtra("sEndDate",sEndDate);
                SaleChooseTime.this.setResult(4,in);
                finish();
                break;
            default:
                break;

        }
    }

    /**
     * 单据开始
     */

    private  DatePickerDialog.OnDateSetListener Datelister_s = new DatePickerDialog.OnDateSetListener() {
        String mo="";
        /**params：view：该事件关联的组件
         * params：myyear：当前选择的年
         * params：monthOfYear：当前选择的月
         * params：dayOfMonth：当前选择的日
         */
        @Override
        public void onDateSet(DatePicker view, int myear, int monthOfYear, int dayOfMonth) {
            //修改year、month、day的变量值，以便以后单击按钮时，DatePickerDialog上显示上一次修改后的值
            year = myear;
            month = monthOfYear;

            if (month<10){
                mo = "0"+(month+1);
            }else{
                mo = (month+1)+"";
            }
            day = dayOfMonth;
            updateDates();
            txtEndDate.requestFocus();

        }

        //当DatePickerDialog关闭时，更新日期显示
        private void updateDates() {
            //在TextView上显示日期
            txtBeginDate.setText(year + "-" + mo+ "-" + day);
        }
    };

    /**
     * 单据日期结束
     */

    private DatePickerDialog.OnDateSetListener Datelistener = new DatePickerDialog.OnDateSetListener() {
        String mo;
        /**params：view：该事件关联的组件
         * params：myyear：当前选择的年
         * params：monthOfYear：当前选择的月
         * params：dayOfMonth：当前选择的日
         */
        @Override
        public void onDateSet(DatePicker view, int myyear, int monthOfYear, int dayOfMonth) {
            //修改year、month、day的变量值，以便以后单击按钮时，DatePickerDialog上显示上一次修改后的值
            if (month<10){
                mo = "0"+(month+1);
            }else{
                mo = (month+1)+"";
            }
            year = myyear;
            month = monthOfYear;
            day = dayOfMonth;
            updateDate();

        }

        //当DatePickerDialog关闭时，更新日期显示
        private void updateDate() {
            //在TextView上显示日期
            txtEndDate.setText(year + "-" + mo + "-" + day);
        }

    };


    private View.OnFocusChangeListener myFocusListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {
                DatePickerDialog dpd = new DatePickerDialog(SaleChooseTime.this, Datelistener, year, month, day);
                dpd.show();//显示DatePickerDialog组件
            }
        }
    };

    private View.OnFocusChangeListener myFocusListeners = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {
                DatePickerDialog dpds = new DatePickerDialog(SaleChooseTime.this, Datelister_s, year, month, day);
                dpds.show();//显示DatePickerDialog组件
            }
        }
    };

/**
 * 回车事件
 */

    private View.OnKeyListener mOnKeyListener = new View.OnKeyListener(){
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
             if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                switch (v.getId()){
                    case  R.id.et_BillCode:
                        Log.d(TAG, "onKey: "+"kkk");
                        txtBeginDate.requestFocus();
                        return true;
//                    case  R.id.et_BeginDate:
//                        txtEndDate.requestFocus();
//                        return true;
                }
             }
            return false;
        }
    };


    /**
     * 初始化界面
     * 初始化数据
     */
    private void initView(){
          mycalendar = Calendar.getInstance();//初始化Calendar日历对象
          year = mycalendar.get(Calendar.YEAR); //获取Calendar对象中的年
          month = mycalendar.get(Calendar.MONTH);//获取Calendar对象中的月
          day = mycalendar.get(Calendar.DAY_OF_MONTH);//获取这个月的第几天
          txtBeginDate.setOnFocusChangeListener(myFocusListeners);
          txtBeginDate.setOnKeyListener(mOnKeyListener);
          txtEndDate.setOnFocusChangeListener(myFocusListener);
          txtEndDate.setOnKeyListener(mOnKeyListener);

           }


}
