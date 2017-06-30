package com.techscan.dvq;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by XUHU on 2017/6/30.
 * 单据日期查询比较
 */

public class DateCompare {

    public boolean timeCompare(String date1,String date2){

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
            Date beginDate = sdf.parse(date1);
            Date endDate   = sdf.parse(date2);
            if (endDate.getTime()>=beginDate.getTime()){
               return true;
            }else{
              return  false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }


}
