package com.techscan.dvq.module.saleout;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.techscan.dvq.DateCompare;
import com.techscan.dvq.R;
import com.techscan.dvq.login.MainLogin;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


/**
 * Created by Hugh on 2017/6/28.
 */

public class SaleChooseTime extends Activity {
    @Nullable
    @InjectView(R.id.et_BeginDate)
    EditText txtBeginDate;
    @Nullable
    @InjectView(R.id.et_EndDate)
    EditText txtEndDate;
    @Nullable
    @InjectView(R.id.bt_Search)
    Button btSearch;
    @Nullable
    @InjectView(R.id.et_BillCode)
    EditText txtBillCode;

    int year;
    int month;
    int day;
    int year_c;
    int month_c;
    int day_c;

    Calendar mycalendar;
    @NonNull
    String sEndDate   = "";
    @NonNull
    String sBillCodes = "";
    @NonNull
    String sBeginDate = "";

    String months;
    String dayys;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_time);
        this.setTitle("单据查询");
        ButterKnife.inject(this);
        initView();

    }
    @OnClick({R.id.et_BillCode, R.id.et_BeginDate, R.id.et_EndDate,R.id.bt_Search})
    public void onViewClicked(@NonNull View view) {
        switch(view.getId()){
            case R.id.et_BeginDate:
                year_c = mycalendar.get(Calendar.YEAR); //获取Calendar对象中的年
                month_c = mycalendar.get(Calendar.MONTH);//获取Calendar对象中的月
                day_c = mycalendar.get(Calendar.DAY_OF_MONTH);//获取这个月的第几天
                DatePickerDialog dpd = new DatePickerDialog(SaleChooseTime.this, Datelister_s, year_c, month_c, day_c);
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
                String beginDate = txtBeginDate.getText().toString();
                String endDate  = txtEndDate.getText().toString();
                DateCompare dateCompare = new DateCompare();
                if ((beginDate.equals("")||beginDate==null)&&(endDate.equals("")||endDate==null)){
                    Intent in = this.getIntent();
                    sBillCodes  = txtBillCode.getText().toString();
                    sBeginDate =  txtBeginDate.getText().toString();
                    sEndDate = txtEndDate.getText().toString();
                    in.putExtra("sBillCodes",sBillCodes);
                    in.putExtra("sBeginDate",sBeginDate);
                    in.putExtra("sEndDate",sEndDate);
                    SaleChooseTime.this.setResult(4,in);
                    finish();
                }
                else
                {
                if(dateCompare.timeCompare(beginDate,endDate)){
                    Intent in = this.getIntent();
                    sBillCodes  = txtBillCode.getText().toString();
                    sBeginDate =  txtBeginDate.getText().toString();
                    sEndDate = txtEndDate.getText().toString();
                    in.putExtra("sBillCodes",sBillCodes);
                    in.putExtra("sBeginDate",sBeginDate);
                    in.putExtra("sEndDate",sEndDate);
                    SaleChooseTime.this.setResult(4,in);
                    finish();
                }else{
                    Toast.makeText(this, R.string.dateCompare,Toast.LENGTH_SHORT).show();
                }
                }
                break;
            default:
                break;

        }
    }



    /**
     * 单据开始
     */

    @NonNull
    private DatePickerDialog.OnDateSetListener Datelister_s = new DatePickerDialog.OnDateSetListener() {
        @NonNull
        String mo="";
        @NonNull
        String days="";
        /**params：view：该事件关联的组件
         * params：myyear：当前选择的年
         * params：monthOfYear：当前选择的月
         * params：dayOfMonth：当前选择的日
         */
        @Override
        public void onDateSet(DatePicker view, int myear, int monthOfYear, int dayOfMonth) {
            //修改year、month、day的变量值，以便以后单击按钮时，DatePickerDialog上显示上一次修改后的值
            year_c = myear;
            month_c = monthOfYear;
            if (month_c<9){
                mo = "0"+(month_c+1);
            }else{
                mo = (month_c+1)+"";
            }
            day_c = dayOfMonth;
            if (day_c<10){
                days = "0"+(day_c);
            }else{
                days = day_c +"";
            }

            updateDates();


        }

        //当DatePickerDialog关闭时，更新日期显示
        private void updateDates() {
            //在TextView上显示日期
            txtBeginDate.setText(year_c + "-" + mo+ "-" + days);
        }
    };

    /**
     * 单据日期结束
     */

    @NonNull
    private DatePickerDialog.OnDateSetListener Datelistener = new DatePickerDialog.OnDateSetListener() {
        @NonNull
        String mo="";
        @NonNull
        String days="";
        /**params：view：该事件关联的组件
         * params：myyear：当前选择的年
         * params：monthOfYear：当前选择的月
         * params：dayOfMonth：当前选择的日
         */
        @Override
        public void onDateSet(DatePicker view, int myyear, int monthOfYear, int dayOfMonth) {
            //修改year、month、day的变量值，以便以后单击按钮时，DatePickerDialog上显示上一次修改后的值
            year = myyear;
            month = monthOfYear;
            if (month<9){
                mo = "0"+(month+1);
            }else{
                mo = (month+1)+"";
            }
            day = dayOfMonth;
            if (day<10){
                days = "0"+(day);
            }else{
                days = day+"";
            }
            updateDate();

        }

        //当DatePickerDialog关闭时，更新日期显示
        private void updateDate() {
            //在TextView上显示日期
            txtEndDate.setText(year + "-" + mo + "-" + days);
        }

    };


    @NonNull
    private View.OnFocusChangeListener myFocusListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {
                DatePickerDialog dpd = new DatePickerDialog(SaleChooseTime.this, Datelistener, year, month, day);
                dpd.show();//显示DatePickerDialog组件
            }
        }
    };

    @NonNull
    private View.OnFocusChangeListener myFocusListeners = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {
                DatePickerDialog dpds = new DatePickerDialog(SaleChooseTime.this, Datelister_s, year_c, month_c, day_c);
                dpds.show();//显示DatePickerDialog组件
            }
        }
    };

/**
 * 回车事件
 */

@NonNull
private View.OnKeyListener mOnKeyListener = new View.OnKeyListener(){
        @Override
        public boolean onKey(@NonNull View v, int keyCode, @NonNull KeyEvent event) {
             if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                switch (v.getId()){
                    case  R.id.et_BillCode:
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
          year_c = mycalendar.get(Calendar.YEAR); //获取Calendar对象中的年
          month_c = mycalendar.get(Calendar.MONTH);//获取Calendar对象中的月
          day_c = mycalendar.get(Calendar.DAY_OF_MONTH);//获取这个月的第几天
          txtBeginDate.setOnFocusChangeListener(myFocusListeners);
          txtEndDate.setOnFocusChangeListener(myFocusListener);
          txtBillCode.setOnKeyListener(mOnKeyListener);
          txtBeginDate.setOnKeyListener(mOnKeyListener);
//        if (month<9){
//            months = "0"+(month+1);
//        }else{
//            months = (month+1)+"";
//        }
//        if (day<10){
//            dayys = "0"+(day);
//        }else{
//            dayys = day+"";
//        }
//          txtBeginDate.setText((year+"")+"-"+months+"-"+dayys);
//          txtEndDate.setText((year+"")+"-"+months+"-"+dayys);
          txtBeginDate.setText(MainLogin.appTime);
          txtEndDate.setText(MainLogin.appTime);

    }


}
