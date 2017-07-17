package com.techscan.dvq;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.techscan.dvq.R.id;

import org.apache.http.ParseException;
import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

//import com.techscan.dvq.StockMoveScan.ButtonOnClickDelconfirm;
//import com.techscan.dvq.StockMoveScan.ButtonOnClick;

public class PurStockInDetail extends Activity {

    private GetInvBaseInfo objInvBaseInfo = null;
    private HashMap<String, Object> m_mapInvBaseInfo = null;
    private SplitBarcode m_cSplitBarcode = null;
    String fileNameScan = null;
    String ScanedFileName = null;
    String UserID = null;
    File fileScan = null;
    String ReScanBody = "1";
    private writeTxt writeTxt;        //保存LOG文件

    String m_BillNo = "";
    String m_BillID = "";

    String tmpWHStatus = "";
    String tmpBillStatus = "";
    String tmpWarehouseID = "";
    String tmpposID = "";
    private ArrayList<String> ScanedBarcode = new ArrayList<String>();
    JSONObject jsHead;
    JSONObject jsBody;

    // ADD CAIXY TEST START
//	private SoundPool sp;// 声明一个SoundPool
//	private int MainLogin.music;// 定义一个int来设置suondID
//	private int MainLogin.music2;// 定义一个int来设置suondID
    // ADD CAIXY TEST END

    Inventory currentObj; // 当前扫描到的存货信息

    JSONObject jsBoxTotal;
    JSONObject jsSerino;

    EditText txtBarcode;
    EditText txtInvName;
    EditText txtInvSerino;
    EditText txtInvCode;
    EditText txtBatch;
    // Map<String, Object> map;

    TextView tvPurcount;
    Button btnTask;
    Button btnDetail;
    Button btnExit;

    EditText txtPurType;
    EditText txtPurSpec;
    EditText txtPurNumber;
    EditText txtPurWeight;
    EditText txtPurTotal;
    EditText txtPurUnit;

    Double ishouldinnum;
    Double iinnum;

    List<Map<String, Object>> lstTaskBody = null;

    private AlertDialog DeleteButton = null;
    private ButtonOnClick buttonDelOnClick = new ButtonOnClick(0);
    private AlertDialog SelectButton = null;

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {// 拦截meu键事件 //do something...
            return false;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {// 拦截返回按钮事件 //do something...
            return false;
        }
        return true;
    }

    private void LoadPurOrder() throws ParseException, IOException {
        if (m_BillNo == null || m_BillNo.equals("")) {
            Toast.makeText(this, "请先确认需要扫描的订单号", Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            return;
        }

        JSONObject para = new JSONObject();
        String FunctionName = "";
        FunctionName = "CommonQuery";
        // 获取表头
        try {
            para.put("BillHead", this.m_BillNo);
            para.put("accId", "A");
            para.put("FunctionName", "GetInBoundBillHeadNoCompanyCode");
            para.put("TableName", "PurGood");
        } catch (JSONException e2) {
            e2.printStackTrace();

            Toast.makeText(PurStockInDetail.this, e2.getMessage(),
                    Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
        }
        try {
            if (!MainLogin.getwifiinfo()) {
                Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG)
                        .show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                return;
            }
            jsHead = Common.DoHttpQuery(para, FunctionName, "");
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            return;
        }
        // 表头获得完毕
        para = new JSONObject();
        FunctionName = "CommonQuery";
        try {
            para.put("BillID", m_BillID);
            para.put("accId", "A");
            para.put("FunctionName", "GetInBoundBillDetail");
            para.put("TableName", "PurBody");
            if (!MainLogin.getwifiinfo()) {
                Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG)
                        .show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                return;
            }
            jsBody = Common.DoHttpQuery(para, FunctionName, "");
            Log.d(TAG, "AAA: "+jsBody.toString());
        } catch (JSONException e2) {
            e2.printStackTrace();
            Toast.makeText(this, e2.getMessage(), Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
        }

        try {
            if (jsHead == null || jsBody == null) {
                Toast.makeText(this, "网络操作出现问题!", Toast.LENGTH_LONG).show();
                // ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                // ADD CAIXY TEST END
                return;
            }
            if (!jsHead.has("Status")) {
                Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG)
                        .show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                return;
            }
            if (!jsHead.getBoolean("Status")) {
                String errMsg = "";
                if (jsHead.has("ErrMsg")) {
                    errMsg = jsHead.getString("ErrMsg");
                } else {
                    errMsg = getString(R.string.WangLuoChuXianWenTi);
                }
                Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show();
                // ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                // ADD CAIXY TEST END
                return;
            }
            if (!jsBody.getBoolean("Status")) {
                String errMsg = "";
                if (jsBody.has("ErrMsg")) {
                    errMsg = jsBody.getString("ErrMsg");
                } else {
                    errMsg = getString(R.string.WangLuoChuXianWenTi);
                }
                Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show();
                // ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                // ADD CAIXY TEST END
                return;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            return;
        }
    }

    private boolean ScanSerial(String serino, String Free1, String TotalBox)
            throws JSONException {
        if (jsSerino == null)
            jsSerino = new JSONObject();

        if (!jsSerino.has("Serino")) {
            JSONArray serinos = new JSONArray();
            jsSerino.put("Serino", serinos);
            JSONObject temp = new JSONObject();
            temp.put("serino", serino);
            temp.put("box", TotalBox);
            temp.put("invcode", m_mapInvBaseInfo.get("invcode").toString());
            temp.put("invname", m_mapInvBaseInfo.get("invname").toString());
            temp.put("batch", m_mapInvBaseInfo.get("batch").toString());
            temp.put("sno", m_mapInvBaseInfo.get("serino").toString());
            // caixy 需要增加产地
            temp.put("vfree1", Free1);

            serinos.put(temp);
            return true;
        } else {
            JSONArray serinos = jsSerino.getJSONArray("Serino");

            for (int i = 0; i < serinos.length(); i++) {
                JSONObject temp = new JSONObject();
                temp = serinos.getJSONObject(i);
                if (temp.getString("serino").equals(serino)) {
                    //temp.put("box", TotalBox);
                    // Toast.makeText(this, "该条码已经被扫描过了,不能再次扫描",
                    // Toast.LENGTH_LONG).show();
                    // //ADD CAIXY TEST START
                    // MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    // //ADD CAIXY TEST END
                    // return false;
                    return true;
                }
            }
            JSONObject temp = new JSONObject();
            temp.put("serino", serino);
            temp.put("box", TotalBox);
            temp.put("invcode", m_mapInvBaseInfo.get("invcode").toString());
            temp.put("invname", m_mapInvBaseInfo.get("invname").toString());
            temp.put("batch", m_mapInvBaseInfo.get("batch").toString());
            temp.put("sno", m_mapInvBaseInfo.get("serino").toString());
            // caixy 需要增加产地
            temp.put("vfree1", Free1);

            serinos.put(temp);
        }

        return true;
    }

    private boolean ScanBoxTotal(String serino, String total, String current)
            throws JSONException {
        if (jsBoxTotal == null)
            jsBoxTotal = new JSONObject();

        int stotal = Integer.valueOf(total);

        if (!jsBoxTotal.has("BoxList")) {
            JSONArray boxs = new JSONArray();
            jsBoxTotal.put("BoxList", boxs);

            JSONObject temp = new JSONObject();
            temp.put("serial", serino);
            temp.put("total", total);
            temp.put("current", 1);
            temp.put("invcode", currentObj.getInvCode());
            temp.put("batch", currentObj.GetBatch());
            boxs.put(temp);

            if (stotal == 1)
                return true;
            else
                return false;
        }
        JSONArray boxs = jsBoxTotal.getJSONArray("BoxList");
        for (int i = 0; i < boxs.length(); i++) {
            JSONObject temp = boxs.getJSONObject(i);
            if (temp.getString("serial").equals(serino)
                    && temp.getString("invcode")
                    .equals(currentObj.getInvCode())
                    && temp.getString("batch").equals(currentObj.GetBatch())) {
                int icurrent;
                int itotal;
                icurrent = temp.getInt("current");
                itotal = temp.getInt("total");

//				if (icurrent == itotal) {
////					Toast.makeText(this, "该条码已经被扫描过了,不能再次扫描", Toast.LENGTH_LONG)
////							.show();
////					// ADD CAIXY TEST START
////					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
////					// ADD CAIXY TEST END
////					return false;
//				}
                if (itotal == icurrent + 1) {
                    temp.put("current", icurrent + 1);
                    return true;
                }

                temp.put("current", icurrent + 1);
                return false;
                // 13770529941
            }
        }

        JSONObject temp = new JSONObject();
        temp.put("serial", serino);
        temp.put("total", total);
        temp.put("invcode", currentObj.getInvCode());
        temp.put("batch", currentObj.GetBatch());
        temp.put("current", 1);
        boxs.put(temp);

        if (stotal == 1)
            return true;
        else
            return false;
    }

    private boolean ScanDetail(String Scanbarcode)
    {
        if (Scanbarcode == null || Scanbarcode.equals(""))
            return false;

        if (!MainLogin.getwifiinfo()) {
            Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return false;
        }

        SplitBarcode bar = new SplitBarcode(Scanbarcode);
        if (bar.creatorOk == false) {
            Toast.makeText(this, "扫描的不是正确货品条码", Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            return false;
        }

        m_cSplitBarcode = bar;

        if(!bar.BarcodeType.equals("C") && !bar.BarcodeType.equals("TC")
                && !bar.BarcodeType.equals("Y"))
            bar.creatorOk = false;

        String FinishBarCode = bar.FinishBarCode;

        if (ScanedBarcode != null || ScanedBarcode.size() > 0) {
            for (int si = 0; si < ScanedBarcode.size(); si++) {
                String BarCode = ScanedBarcode.get(si).toString();

                if (BarCode.equals(FinishBarCode)) {
                    Toast.makeText(this, "该条码已经被扫描过了,不能再次扫描", Toast.LENGTH_LONG)
                            .show();
                    // ADD CAIXY TEST START
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    // ADD CAIXY TEST END
                    return false;
                }
            }
        }

        IniDetail();
        try {
            //currentObj = new Inventory(bar.cInvCode, "BADV", bar.AccID);
            objInvBaseInfo = new GetInvBaseInfo(bar, mHandler);
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            return false;
        }
        return true;
    }

    private boolean ScanedToGet() {
//        if (Scanbarcode == null || Scanbarcode.equals(""))
//            return false;
//
//        if (!MainLogin.getwifiinfo()) {
//            Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG).show();
//            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//            return false;
//        }
//
//        SplitBarcode bar = new SplitBarcode(Scanbarcode);
//
//        if (bar.creatorOk == false) {
//            Toast.makeText(this, "扫描的不是正确货品条码", Toast.LENGTH_LONG).show();
//            //ADD CAIXY TEST START
//            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//            //ADD CAIXY TEST END
//            return false;
//        }
//
//        String FinishBarCode = bar.FinishBarCode;
//
//        if (ScanedBarcode != null || ScanedBarcode.size() > 0) {
//            for (int si = 0; si < ScanedBarcode.size(); si++) {
//                String BarCode = ScanedBarcode.get(si).toString();
//
//                if (BarCode.equals(FinishBarCode)) {
//                    Toast.makeText(this, "该条码已经被扫描过了,不能再次扫描", Toast.LENGTH_LONG)
//                            .show();
//                    // ADD CAIXY TEST START
//                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//                    // ADD CAIXY TEST END
//                    return false;
//                }
//            }
//        }

//        IniDetail();
        SplitBarcode bar = m_cSplitBarcode;
        try {
//            try {
//                //currentObj = new Inventory(bar.cInvCode, "BADV", bar.AccID);
//                objInvBaseInfo = new GetInvBaseInfo(bar, mHandler);
//            } catch (Exception ex) {
//                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
//                // ADD CAIXY TEST START
//                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//                // ADD CAIXY TEST END
//                return false;
//            }

            //加载到页面各个控件上
            //SetInvBaseToUI();
            //return true;

//			currentObj.SetSerino(bar.cSerino);
//			currentObj.SetBatch(bar.cBatch);
//			currentObj.SetcurrentID(bar.currentBox);
//			currentObj.SettotalID(bar.TotalBox);
//			currentObj.SetAccID(bar.AccID);

//			if (currentObj.getErrMsg() != null
//					&& !currentObj.getErrMsg().equals("")) {
//				Toast.makeText(this, currentObj.getErrMsg(), Toast.LENGTH_LONG)
//						.show();
//				// ADD CAIXY TEST START
//				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//				// ADD CAIXY TEST END
//				return false;
//			}

//            Log.d("TAG", "CheckSerino");
            // 校验流水号
//            if (!ConformGetSERINO(bar.CheckBarCode, bar)) {
//                txtBarcode.setText("");
//                txtBarcode.requestFocus();
//                return false;
//            }

//            if (tmpWHStatus.equals("Y") && tmpBillStatus.equals("N")) {
//                if (!FindInvnBinStockInfo()) {
//                    txtBarcode.setText("");
//                    txtBarcode.requestFocus();
//                    return false;
//                }
//            } else if (tmpWHStatus.equals("N") && tmpBillStatus.equals("N")) {
//                if (!FindInvnNoBinStockInfo()) {
//                    txtBarcode.setText("");
//                    txtBarcode.requestFocus();
//                    return false;
//                }
//            }

            // 获得存货完毕，校验上游单据
            JSONArray bodys = jsBody.getJSONArray("PurBody");
            Log.d("TAG", "PurBody: " + bodys);
            boolean isFind = false;
            for (int i = 0; i < bodys.length(); i++) {
                JSONObject temp = bodys.getJSONObject(i);
                if (temp.getString("invcode").equals(m_mapInvBaseInfo.get("invcode").toString())) {
                    isFind = true;
                    //ShowDetail();

                    //String Free1 = temp.getString("vfree1");
                    String Free1 = "";
                    // 寻找到了对应存货
                    Double doneqty = 0.0;
                    if(!temp.getString("nconfirmnum").isEmpty() &&
                            !temp.getString("nconfirmnum").toLowerCase().equals("null")) {
                        doneqty = temp.getDouble("nconfirmnum");
//                        if (doneqty  >= temp.getInt("nordernum")) {
                        if (doneqty  >= temp.getInt("tasknum")) {
                            Toast.makeText(this, "这个存货已经超过应收数量了,不允许收!",
                                    Toast.LENGTH_LONG).show();
                            // ADD CAIXY TEST START
                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            // ADD CAIXY TEST END

                            txtBarcode.setText("");
                            txtBarcode.requestFocus();
                            return false;
                        }
                    }

                    if(bar.BarcodeType.equals("C") || bar.BarcodeType.equals("TC")) {
                        Double ldTotal = (Double) m_mapInvBaseInfo.get("quantity") * (Integer)m_mapInvBaseInfo.get("number");
                        txtPurTotal.setText(ldTotal.toString());
                    }

                    if (ScanSerial(bar.FinishBarCode, Free1, txtPurTotal.getText().toString()) == false) {
                        txtBarcode.setText("");
                        txtBarcode.requestFocus();
                        return false;
                    }
                    ScanedBarcode.add(bar.FinishBarCode);
                    MainLogin.sp.play(MainLogin.music2, 1, 1, 0, 0, 1);
                    //SaveScanedBody();//写入本地

                    // 判断判断箱子是否装满。
//					if (ScanBoxTotal(bar.cSerino, bar.TotalBox, bar.currentBox) == false) {
//						txtBarcode.setText("");
//						txtBarcode.requestFocus();
//						return false;
//					}

//                    int doneqty = temp.getInt("doneqty");
//                    temp.put("doneqty", doneqty + 1);

                    //Double doneqty = temp.getDouble("doneqty");
                    //temp.put("doneqty", doneqty + Double.parseDouble(txtPurTotal.getText().toString()));
                    temp.put("nconfirmnum", doneqty + Double.parseDouble(txtPurTotal.getText().toString()));
                    break;
                }
            }

            if (isFind == false) {
                Toast.makeText(this, "这个存货不在本次扫描任务中", Toast.LENGTH_LONG).show();
                // ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                // ADD CAIXY TEST END
                return false;
            }

        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return false;
        }

        JSONArray arrays;
        try {
            arrays = jsBody.getJSONArray("PurBody");
            ishouldinnum = 0.0;
            iinnum = 0.0;
            for (int i = 0; i < arrays.length(); i++) {
//                String sshouldinnum = ((JSONObject) (arrays.get(i)))
//                        .getString("nordernum");
                String sshouldinnum = ((JSONObject) (arrays.get(i))).getString("tasknum");
                String sinnum = ((JSONObject) (arrays.get(i)))
                        .getString("nconfirmnum");

                ishouldinnum = ishouldinnum + Double.valueOf(sshouldinnum);
                if(!sinnum.toLowerCase().equals("null") && !sinnum.isEmpty())
                    iinnum = iinnum + Double.valueOf(sinnum);
            }
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();

            Toast.makeText(this, e1.getMessage(), Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
        }
        tvPurcount.setText("总量" + ishouldinnum + " | " + "已扫" + iinnum
                + " | " + "未扫" + (ishouldinnum - iinnum) );

        txtBarcode.requestFocus();
        txtBarcode.setText("");
        txtBarcode.setSelectAllOnFocus(true);

        return true;
    }

    private boolean FindInvnBinStockInfo() throws JSONException,
            ParseException, IOException {

        JSONObject para = new JSONObject();
        para.put("FunctionName", "GetBinStockByID");
        para.put("InvID", this.currentObj.Invmandoc());

        para.put("BinID", this.tmpposID);

        para.put("LotB", this.currentObj.GetBatch());
        para.put("TableName", "Stock");
        if (!MainLogin.getwifiinfo()) {
            Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return false;
        }
        JSONObject StockInfo = Common.DoHttpQuery(para, "CommonQuery", "A");

        if (StockInfo == null) {
            Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return false;
        }

        if (!StockInfo.has("Status")) {
            Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return false;
        }

        if (StockInfo.getBoolean("Status")) {
            JSONArray val = StockInfo.getJSONArray("Stock");
            if (val.length() < 1) {
                Toast.makeText(this, "获取货位库存数据信息失败,货位上没有该货品,不能调出",
                        Toast.LENGTH_LONG).show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                return false;
            }

            double stockCount = 0.0;

            for (int iv = 0; iv < val.length(); iv++) {
                JSONObject temp = val.getJSONObject(iv);
                if (Double.valueOf(temp.getString("nnum")).doubleValue() > 0.0) {
                    stockCount = stockCount
                            + Double.valueOf(temp.getString("nnum"))
                            .doubleValue();
                }
            }

            double sancount = getScanCount(currentObj.Invmandoc(),
                    currentObj.GetBatch());
            sancount += 1.0;
            if (sancount > stockCount) {
                Toast.makeText(this, "该货品在该货位的发出库存已经不足了,不能发出",
                        Toast.LENGTH_LONG).show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                return false;
            }

        } else {
            Toast.makeText(this, "该货品在该仓库中没有库存信息,不能发出", Toast.LENGTH_LONG)
                    .show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return false;
        }

        return true;
    }

    private boolean FindInvnNoBinStockInfo() throws JSONException,
            ParseException, IOException {

        JSONObject para = new JSONObject();
        para.put("FunctionName", "SearchGetStockInfo");
        para.put("TableName", "InvInfo");
        para.put("InvCode", currentObj.getInvCode());
        para.put("InvBatch", currentObj.GetBatch());

        String CompanyCode = "1001";
        if (tmpBillStatus.equals("Y")) {
            CompanyCode = "1001";
        }

        para.put("CompanyCode", CompanyCode);

        para.put("accId", "A");

        // para.put("LotB", this.currentObj.GetBatch());
        // para.put("TableName", "Stock");
        if (!MainLogin.getwifiinfo()) {
            Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return false;
        }
        JSONObject StockInfo = Common.DoHttpQuery(para, "CommonQuery", "A");

        if (StockInfo == null) {
            Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return false;
        }

        if (!StockInfo.has("Status")) {
            Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return false;
        }

        if (StockInfo.getBoolean("Status")) {
            JSONArray val = StockInfo.getJSONArray("InvInfo");
            if (val.length() < 1) {
                Toast.makeText(this, "获取货位库存数据信息失败,货位上没有该货品,不能调出",
                        Toast.LENGTH_LONG).show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                return false;
            }

            double stockCount = 0.0;

            for (int iv = 0; iv < val.length(); iv++) {
                JSONObject temp = val.getJSONObject(iv);

                if (temp.getString("pk_stordoc").equals(tmpWarehouseID)) {
                    if (Double.valueOf(temp.getString("nnum")).doubleValue() > 0.0) {
                        stockCount = stockCount
                                + Double.valueOf(temp.getString("nnum"))
                                .doubleValue();
                    }
                }

            }

            double sancount = getScanCount(currentObj.Invmandoc(),
                    currentObj.GetBatch());
            sancount += 1.0;
            if (sancount > stockCount) {
                Toast.makeText(this, "该货品在该货位的发出库存已经不足了,不能发出",
                        Toast.LENGTH_LONG).show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                return false;
            }

        } else {
            Toast.makeText(this, "该货品在该仓库中没有库存信息,不能发出", Toast.LENGTH_LONG)
                    .show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return false;
        }

        return true;
    }

    double getScanCount(String InvID, String BatchCode) {
        double spacenum = 0.0;
        if (jsBody == null) {
            return spacenum;
        }

        JSONArray arrays;

        try {
            arrays = jsBody.getJSONArray("PurBody");

            // ishouldinnum = 0;
            // iinnum = 0;
            for (int i = 0; i < arrays.length(); i++) {
                // String sshouldinnum =
                // ((JSONObject)(arrays.get(i))).getString("nshouldinnum");
                // String sinnum =
                // ((JSONObject)(arrays.get(i))).getString("doneqty");
                //
                // ishouldinnum = ishouldinnum + Integer.valueOf(sshouldinnum);
                // iinnum = iinnum + Integer.valueOf(sinnum);

                if ((((JSONObject) (arrays.get(i))).get("cinventoryid")
                        .equals(InvID))
                        && (((JSONObject) (arrays.get(i))).get("vbatchcode")
                        .equals(BatchCode))) {
                    String sinnum = ((JSONObject) (arrays.get(i)))
                            .getString("doneqty");
                    spacenum += Double.valueOf(sinnum);
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return spacenum;

        // for(int i = 0; i < lstSaveBody.size(); i++)
        // {
        // Map<String, Object> values = lstSaveBody.get(i);
        // if((values.get("cinventoryid").equals(InvID)) &&
        // (values.get("vbatchcode").equals(BatchCode)))
        // {
        // spacenum +=
        // Double.valueOf((String)values.get("spacenum")).doubleValue();
        // }
        // }

    }

    // 确认存货流水号是否和上游单据一致
    private boolean ConformGetSERINO(String barcode, SplitBarcode bar)
            throws JSONException, ParseException, IOException {
        if (m_BillID.equals("")) {
            Toast.makeText(this, R.string.MeiYouZhaoDaoCanZhao, Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            return false;
        }

        // 获得当前单据的流水号
        JSONObject SERINOList = null;
        JSONObject para = new JSONObject();

        para.put("FunctionName", "GetSERINO");
        para.put("CBUSNO", m_BillID);
        para.put("INVCODE", bar.cInvCode);
        para.put("CLOT", bar.cBatch);
        para.put("TableName", "SERINO");

        if (!MainLogin.getwifiinfo()) {
            Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return false;
        }
        SERINOList = Common.DoHttpQuery(para, "CommonQuery", bar.AccID);
        Log.d("TAG", "ConformGetSERINO: " + SERINOList);

        if (SERINOList == null) {
            Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return false;
        }

        if (!SERINOList.getBoolean("Status")) {
            return true;
        }

        JSONArray jsarray = SERINOList.getJSONArray("SERINO");

        for (int i = 0; i < jsarray.length(); i++) {
            String SERINO = ((JSONObject) jsarray.getJSONObject(i)).getString(
                    "serino").toString();
            if (SERINO.equals(bar.cSerino)) {
                Toast.makeText(this, "该存货的流水号已经被保存,请确认流水号是否重复！",
                        Toast.LENGTH_LONG).show();
                // ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                // ADD CAIXY TEST END
                return false;
            }

        }

        return true;

    }

    /**
     * 网络请求后的线程通信
     * msg.obj 是从子线程传递过来的数据
     */
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    JSONObject json = (JSONObject) msg.obj;
                    if (json != null) {
                        try {

                            Log.d("TAG", "handleMessage: TEST");
                            Log.d("TAG", "json: " + json);
                            objInvBaseInfo.SetInvBaseToParam(json);
                            m_mapInvBaseInfo = objInvBaseInfo.mapInvBaseInfo;
                            SetInvBaseToUI();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.d("TAG", "handleMessage: NULL");
                        return;
                    }
                    break;
            }
        }
    };

    //把取得的数据加载到页面控件上
    private void SetInvBaseToUI() {
        txtInvCode.setText(m_mapInvBaseInfo.get("invcode").toString());
        txtInvName.setText(m_mapInvBaseInfo.get("invname").toString());
        txtPurType.setText(m_mapInvBaseInfo.get("invtype").toString());
        txtPurSpec.setText(m_mapInvBaseInfo.get("invspec").toString());
        txtBatch.setText(m_mapInvBaseInfo.get("batch").toString());
        txtInvSerino.setText(m_mapInvBaseInfo.get("serino").toString());
        txtPurUnit.setText(m_mapInvBaseInfo.get("measname").toString());
        txtBarcode.setText(m_mapInvBaseInfo.get("barcode").toString());
        txtPurWeight.setText(m_mapInvBaseInfo.get("quantity").toString());
        txtPurNumber.setText(m_mapInvBaseInfo.get("number").toString());

        Double ldTotal = (Double) m_mapInvBaseInfo.get("quantity") * (Integer)m_mapInvBaseInfo.get("number");
        txtPurTotal.setText(ldTotal.toString());
        m_mapInvBaseInfo.put("total",ldTotal);
        if(m_mapInvBaseInfo.get("barcodetype").toString().equals("TC")) {
            txtBatch.setFocusableInTouchMode(false);
            txtBatch.setFocusable(false);
            txtPurNumber.setFocusableInTouchMode(false);
            txtPurNumber.setFocusable(false);
            txtPurTotal.setFocusableInTouchMode(false);
            txtPurTotal.setFocusable(false);
            ScanedToGet();
        }
        else if(m_mapInvBaseInfo.get("barcodetype").toString().equals("C") ) {
            txtBatch.setFocusableInTouchMode(false);
            txtBatch.setFocusable(false);
            txtPurNumber.setFocusableInTouchMode(true);
            txtPurNumber.setFocusable(true);
            txtPurTotal.setFocusableInTouchMode(false);
            txtPurTotal.setFocusable(false);
            txtPurNumber.requestFocus();
            txtPurNumber.selectAll();
        }
        else if(m_mapInvBaseInfo.get("barcodetype").toString().equals("Y")){
            txtBatch.setFocusableInTouchMode(true);
            txtBatch.setFocusable(true);
            txtBatch.requestFocus();
            txtBatch.selectAll();
            txtPurTotal.setFocusableInTouchMode(true);
            txtPurTotal.setFocusable(true);
            txtPurNumber.setFocusableInTouchMode(false);
            txtPurNumber.setFocusable(false);
            //txtPurNumber.setVisibility();
        }
    }

    private class ButtonOnClick implements DialogInterface.OnClickListener {
        public int index;

        public ButtonOnClick(int index) {
            this.index = index;
        }

        @Override
        public void onClick(DialogInterface dialog, int whichButton) {
            if (whichButton >= 0) {
                index = whichButton;
                // dialog.cancel();
            } else {

                if (dialog.equals(DeleteButton)) {
                    if (whichButton == DialogInterface.BUTTON_POSITIVE) {
                        return;
                    } else if (whichButton == DialogInterface.BUTTON_NEGATIVE) {
                        // 这里进行数据删除处理
                        // ConfirmDelItem(index);
                    }
                }
            }

        }

    }

    private void ShowDetail() {
        if (currentObj == null)
            return;

        txtInvName.setText(currentObj.getInvName());
        txtInvCode.setText(currentObj.getInvCode());
        txtBatch.setText(currentObj.GetBatch());
        txtInvSerino.setText(currentObj.GetSerino());

        txtBarcode.requestFocus();
        txtBarcode.setSelectAllOnFocus(true);
    }

    private void IniDetail() {
        currentObj = null;
        txtInvName.setText("");
        txtInvCode.setText("");
        txtBatch.setText("");
        txtInvSerino.setText("");

        txtPurType.setText("");
        txtPurSpec.setText("");
        txtPurNumber.setText("");
        txtPurWeight.setText("");
        txtPurTotal.setText("");
        txtPurUnit.setText("");
    }

    private Button.OnClickListener myButtonListner = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case id.btnPurTask:
                    try {
                        ShowTaskDig();
                    } catch (JSONException e) {
                        e.printStackTrace(); // ADD CAIXY TEST START
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        // ADD CAIXY TEST END
                    }
                    break;
                case id.btnPurDetail:
                    try {
                        ShowDetailDig();
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        // ADD CAIXY TEST START
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        // ADD CAIXY TEST END
                    }

                    break;
                case id.btnPurReturn:
                    Return();
                    break;
            }
        }
    };

    private void Return() {
//        if (jsBoxTotal != null && jsSerino != null) {
        if (jsSerino != null) {
            Intent intent = new Intent();

//            intent.putExtra("box", jsBoxTotal.toString());
            //Log.d("TAG", "ReturnjsBoxTotal: " + jsBoxTotal);
            intent.putExtra("box", "");
            intent.putExtra("head", jsHead.toString());
            Log.d("TAG", "ReturnScanedhead: " + jsHead);
            intent.putExtra("body", jsBody.toString());
            Log.d("TAG", "ReturnScanedbody: " + jsBody);
            intent.putExtra("serino", jsSerino.toString());
            Log.d("TAG", "ReturnScanedSerino: " + jsSerino);
            intent.putStringArrayListExtra("ScanedBarcode", ScanedBarcode);
            Log.d("TAG", "ReturnScanedBarcode: " + ScanedBarcode);
            PurStockInDetail.this.setResult(1, intent);// 设置回传数据。resultCode值是1，这个值在主窗口将用来区分回传数据的来源，以做不同的处理
            PurStockInDetail.this.finish();

        } else {
            Intent intent = new Intent();
            PurStockInDetail.this.setResult(2, intent);// 设置回传数据。resultCode值是1，这个值在主窗口将用来区分回传数据的来源，以做不同的处理
            PurStockInDetail.this.finish();
        }
    }

    private void  ShowDetailDig() throws JSONException {
        lstTaskBody = new ArrayList<Map<String, Object>>();
        Log.d("TAG", "jsSerino: " + jsSerino);
        Map<String, Object> map;
        if (jsSerino == null || !jsSerino.has("Serino")) {
            Toast.makeText(this, "还没有扫描到的记录", Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            return;
        }
        JSONArray arrays = jsSerino.getJSONArray("Serino");

        for (int i = 0; i < arrays.length(); i++) {
            map = new HashMap<String, Object>();

            String sSerial = ((JSONObject) (arrays.get(i))).getString("sno");
            String sBatch = ((JSONObject) (arrays.get(i))).getString("batch");
            String sInvCode = ((JSONObject) (arrays.get(i)))
                    .getString("invcode");
            String serino = ((JSONObject) (arrays.get(i))).getString("serino");
            String sTotal = ((JSONObject) (arrays.get(i))).getString("box");

            map.put("invcode", sInvCode);
            map.put("invname",
                    ((JSONObject) (arrays.get(i))).getString("invname"));
            map.put("batch", sBatch);
            map.put("sno", sSerial);
            map.put("serino", serino);
            map.put("okflg", " ");
            map.put("total", sTotal);
//            JSONArray boxs = jsBoxTotal.getJSONArray("BoxList");
//            for (int x = 0; x < boxs.length(); x++) {
//                JSONObject temp = boxs.getJSONObject(x);
//                if (temp.getString("serial").equals(sSerial)
//                        && temp.getString("invcode").equals(sInvCode)
//                        && temp.getString("batch").equals(sBatch)) {
//                    int icurrent;
//                    int itotal;
//                    icurrent = temp.getInt("current");
//                    itotal = temp.getInt("total");
//
//                    if (icurrent == itotal) {
//                        map.put("okflg", " ");
//                    } else {
//                        map.put("okflg", "分包未完");
//                    }
//                }
//            }
            lstTaskBody.add(map);
        }
        // jsBoxTotal
        Log.d("TAG", "lstTaskBody: " + lstTaskBody);
        SimpleAdapter listItemAdapter = new SimpleAdapter(
                PurStockInDetail.this, lstTaskBody,// 数据源
                R.layout.vlistpurdetail,// ListItem的XML实现
                // 动态数组与ImageItem对应的子项
                new String[]{"invname", "invcode", "batch", "serino", "okflg", "total"},
                // ImageItem的XML文件里面的一个ImageView,两个TextView ID
                new int[]{R.id.listpurdetailinvName,
                        R.id.listpurdetailinvCode, R.id.listpurdetailinvBatch,
                        R.id.listpurdetailSno, R.id.listpurdetailinvok, R.id.listpurdetailTotal});

        DeleteButton = new AlertDialog.Builder(this).setTitle(getString(R.string.SaoMiaoMingXiXinXi))
                .setSingleChoiceItems(listItemAdapter, 0, buttonDelOnClick)
                .setPositiveButton(R.string.QueRen, null).create();
        // MOD CAIXY END

        DeleteButton.getListView().setOnItemLongClickListener(
                new OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> arg0,
                                                   View arg1, int arg2, long arg3) {
                        // TODO Auto-generated method stub
                        // When clicked, show a toast with the TextView text

                        ConfirmDelItem(arg2);

                        return false;
                    }
                });

        DeleteButton.show();

    }

    private void ConfirmDelItem(int index) {
        ButtonOnClickDelconfirm buttondel = new ButtonOnClickDelconfirm(index);
        SelectButton = new AlertDialog.Builder(this).setTitle(R.string.QueRenShanChu)
                .setMessage(R.string.NiQueRenShanChuGaiXingWeiJiLuMa).setPositiveButton(R.string.QueRen, buttondel)
                .setNegativeButton(R.string.QuXiao, null).show();
    }

    private class ButtonOnClickDelconfirm implements
            DialogInterface.OnClickListener {
        public int index;

        public ButtonOnClickDelconfirm(int index) {
            this.index = index;
        }

        @Override
        public void onClick(DialogInterface dialog, int whichButton) {
            if (whichButton >= 0) {
                index = whichButton;
            } else {

                if (whichButton == DialogInterface.BUTTON_POSITIVE) {

                    Map<String, Object> mapTemp = (Map<String, Object>) lstTaskBody
                            .get(index);
                    //
                    // //new String[]
                    // {"invname","invcode","batch","sno","okflg"},
                    String invcode = (String) mapTemp.get("invcode");
                    String batch = (String) mapTemp.get("batch");
                    String sno = (String) mapTemp.get("sno");
                    String serino = (String) mapTemp.get("serino");
                    Double ScanedTotal = Double.valueOf(mapTemp.get("total").toString());

                    if (ScanedBarcode != null || ScanedBarcode.size() > 0) {
                        for (int si = 0; si < ScanedBarcode.size(); si++) {
                            String RemoveBarCode = ScanedBarcode.get(si).toString();
                            int iBarlenth = RemoveBarCode.length() - 6;
                            String RemoveBarCodeF = RemoveBarCode.substring(0, iBarlenth);

                            if (RemoveBarCodeF.equals(serino)) {
                                ScanedBarcode.remove(si);
                                si--;
                            }
                        }
                    }

                    JSONArray arrays;
                    try {
                        arrays = jsSerino.getJSONArray("Serino");

                        HashMap<String, Object> Temp = new HashMap<String, Object>();
                        JSONArray serinos = new JSONArray();

                        for (int i = 0; i < arrays.length(); i++) {
                            String serino1 = ((JSONObject) (arrays.get(i)))
                                    .getString("serino");
                            if (!serino1.equals(serino)) {
                                JSONObject temp = new JSONObject();
                                temp = arrays.getJSONObject(i);
                                serinos.put(temp);
                            }
                        }

                        jsSerino = new JSONObject();

                        if (serinos.length() > 0) {
                            jsSerino.put("Serino", serinos);
                        }

                        int it = 0;
//                        JSONArray boxs = jsBoxTotal.getJSONArray("BoxList");
//                        JSONArray newboxs = new JSONArray();
//                        for (int x = 0; x < boxs.length(); x++) {
//                            JSONObject tempBox = new JSONObject();
//                            JSONObject temp = boxs.getJSONObject(x);
//                            if (temp.getString("serial").equals(sno)
//                                    && temp.getString("invcode")
//                                    .equals(invcode)
//                                    && temp.getString("batch").equals(batch)) {
//                                int icurrent;
//                                int itotal;
//                                icurrent = temp.getInt("current") - 1;
//                                itotal = temp.getInt("total");
//                                if (icurrent + 1 == itotal) {
//                                    it = 1;
//                                }
////								if (icurrent != 0)
////								{
////									tempBox.put("serial", sno);
////									tempBox.put("total", itotal);
////									tempBox.put("current", icurrent);
////									tempBox.put("invcode", invcode);
////									tempBox.put("batch", batch);
////									newboxs.put(tempBox);
////
////								}
//                            } else {
//
//                                tempBox = temp;
//                                newboxs.put(tempBox);
//                            }
//
//                        }

                        jsBoxTotal = new JSONObject();

//                        if (serinos.length() > 0) {
//                            jsBoxTotal.put("BoxList", newboxs);
//                        }

                        //if (it == 1) {

                            JSONArray bodys = jsBody.getJSONArray("PurBody");
                            JSONArray bodynews = new JSONArray();
                            // JSONArray serinos = new JSONArray();
                            for (int i = 0; i < bodys.length(); i++) {
                                JSONObject temp = bodys.getJSONObject(i);

                                String invcodeold = ((JSONObject) (bodys.get(i)))
                                        .getString("invcode");
                                String batchcodeold = ((JSONObject) (bodys
                                        .get(i))).getString("vbatchcode");

                                if (invcodeold.equals(invcode)
                                        && batchcodeold.equals(batch)) {
                                    Double doneqty = temp.getDouble("nconfirmnum");
                                    temp.put("nconfirmnum", doneqty - ScanedTotal);
                                }

                                bodynews.put(temp);
                            }

                            jsBody = new JSONObject();
                            jsBody.put("Status", "true");
                            jsBody.put("PurBody", bodynews);

                        //}

                        JSONArray arraysCount;
                        try {
                            arraysCount = jsBody.getJSONArray("PurBody");
                            ishouldinnum = 0.0;
                            iinnum = 0.0;
                            for (int i = 0; i < arraysCount.length(); i++) {
//                                String sshouldinnum = ((JSONObject) (arraysCount
//                                        .get(i))).getString("nordernum");
                                String sshouldinnum = ((JSONObject) (arraysCount.get(i))).getString("tasknum");
                                String sinnum = ((JSONObject) (arraysCount
                                        .get(i))).getString("nconfirmnum");

                                ishouldinnum = ishouldinnum
                                        + Double.valueOf(sshouldinnum);
                                if(!sinnum.toLowerCase().equals("null") && !sinnum.isEmpty())
                                    iinnum = iinnum + Double.valueOf(sinnum);
                            }
                        } catch (JSONException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        tvPurcount.setText("总量" + ishouldinnum + " | " + "已扫"
                                + iinnum + " | " + "未扫"
                                + (ishouldinnum - iinnum) );
                        //SaveScanedBody();//写入本地
                        IniDetail();

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Toast.makeText(PurStockInDetail.this, e.getMessage(),
                                Toast.LENGTH_LONG).show();
                        // ADD CAIXY TEST START
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        // ADD CAIXY TEST END
                    }

                    DeleteButton.cancel();

                } else if (whichButton == DialogInterface.BUTTON_NEGATIVE) {
                    return;
                }
            }
        }
    }

    private void ShowTaskDig() throws JSONException {
        lstTaskBody = new ArrayList<Map<String, Object>>();
        // purBody
        Map<String, Object> map;

        if (jsBody == null) {
            Toast.makeText(this, R.string.MeiYouDeDaoBiaoTiShuJu, Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            return;
        }
        JSONArray arrays = jsBody.getJSONArray("PurBody");

        for (int i = 0; i < arrays.length(); i++) {
            map = new HashMap<String, Object>();
            map.put("BillCode", m_BillNo);
            map.put("InvName",
                    ((JSONObject) (arrays.get(i))).getString("invname"));
            map.put("InvCode",
                    ((JSONObject) (arrays.get(i))).getString("invcode"));
//            String batchs = ((JSONObject) (arrays.get(i)))
//                    .getString("vbatchcode");
//            if (batchs == null || batchs.equals("") || batchs.equals("null")) {
//                batchs = "批次未指定";
//            }
            map.put("Batch", "");// test caixy
            String sinnum = ((JSONObject) (arrays.get(i))).getString("nconfirmnum");
            if(sinnum.toLowerCase().equals("null") || sinnum.isEmpty())
                sinnum = "0.0";
//            map.put("InvNum",
//                    sinnum + " / " + Double.valueOf(((JSONObject) (arrays.get(i))).getString("nordernum")));
            map.put("InvNum",
                   sinnum + " / " + Double.valueOf(((JSONObject) (arrays.get(i))).getString("tasknum")));
            // map.put("DoneQty", )
            lstTaskBody.add(map);
        }

        SimpleAdapter listItemAdapter = new SimpleAdapter(
                PurStockInDetail.this, lstTaskBody,// 数据源
                R.layout.vlistpurstockintask,// ListItem的XML实现
                // 动态数组与ImageItem对应的子项
                new String[]{"BillCode", "InvCode", "InvName", "InvNum"},
                // ImageItem的XML文件里面的一个ImageView,两个TextView ID
                new int[]{
                        id.txtpurinorder, id.txtpurininvcode,
                       id.txtpurininvname, id.txtpurinnumber});
        new AlertDialog.Builder(PurStockInDetail.this).setTitle("源单信息")
                .setAdapter(listItemAdapter, null)
                .setPositiveButton(R.string.QueRen, null).show();

    }

    private TextWatcher watchers = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
            // m_OutPosID="";
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

        }

    };
    private OnKeyListener myTxtListener = new OnKeyListener() {
        @Override
        public boolean onKey(View v, int arg1, KeyEvent arg2) {
            switch (v.getId()) {
                case id.txtPurBarcode:
                    if (arg1 == 66 && arg2.getAction() == KeyEvent.ACTION_UP) {
                        ScanDetail(txtBarcode.getText().toString());
                        txtBarcode.requestFocus();
                        txtBarcode.setText("");
                        return true;
                    }
                case id.txtPurNumber:
                    if (arg1 == 66 && arg2.getAction() == KeyEvent.ACTION_UP) {
                        m_mapInvBaseInfo.put("number",Integer.valueOf(txtPurNumber.getText().toString()));
                        ScanedToGet();
                        return true;
                    }
                case id.txtPurBatch:
                    if (arg1 == 66 && arg2.getAction() == KeyEvent.ACTION_UP) {
                        if(txtBatch.getText().toString().equals(""))
                                return false;
                        m_cSplitBarcode.cBatch = txtBatch.getText().toString().replace("\r", "").replace("\n", "");
                        m_mapInvBaseInfo.put("batch",txtBatch.getText().toString().replace("\r", "").replace("\n", ""));
                        txtPurTotal.requestFocus();
                        return true;
                    }
                case id.txtPurTotal:
                    if (arg1 == 66 && arg2.getAction() == KeyEvent.ACTION_UP) {
                        ScanedToGet();
                        return true;
                    }

            }
             return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pur_stock_in_detail);

        String tag = this.getIntent().getStringExtra("Tag");
        // savedInstanceState.getString("Tag");
        this.m_BillNo = this.getIntent().getStringExtra("BillNo");
        this.m_BillID = this.getIntent().getStringExtra("BillID");

        tmpWHStatus = getIntent().getStringExtra("tmpWHStatus");
        tmpBillStatus = getIntent().getStringExtra("tmpBillStatus");

        tmpWarehouseID = getIntent().getStringExtra("tmpWarehouseID");
        ScanedBarcode = getIntent().getStringArrayListExtra("ScanedBarcode");

        if (tmpWHStatus.equals("Y")) {
            tmpposID = getIntent().getStringExtra("tmpposID");
        }

        // ADD CAIXY START
//		sp = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);// 第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
//		MainLogin.music = MainLogin.sp.load(this, R.raw.xxx, 1); // 把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
//		MainLogin.music2 = MainLogin.sp.load(this, R.raw.yyy, 1);
//		// ADD CAIXY END

        //ADD BY WUQIONG END
        UserID = MainLogin.objLog.UserID;
        //String LogName = BillType + UserID + dfd.format(day)+".txt";
        ScanedFileName = "45Scan" + UserID + ".txt";
        fileNameScan = "/sdcard/DVQ/45Scan" + UserID + ".txt";
        fileScan = new File(fileNameScan);

        // this.btnDetail.

        //if (tag.equals("1")) {

            try {
                String temp = this.getIntent().getStringExtra("head");
                jsHead = new JSONObject(temp);
                Log.d("TAG", "GetDetailHead: " + temp);

                temp = this.getIntent().getStringExtra("body");
                jsBody = new JSONObject(temp);
                Log.d("TAG", "GetDetailBody: " + temp);

                if (tag.equals("1")) {
                    temp = this.getIntent().getStringExtra("serino");
                    jsSerino = new JSONObject(temp);
                }

                //temp = this.getIntent().getStringExtra("box");
                //jsBoxTotal = new JSONObject(temp);
                jsBoxTotal = null;

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(PurStockInDetail.this, e.getMessage(),
                        Toast.LENGTH_LONG).show();
                // ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                // ADD CAIXY TEST END
            }

        //}

        txtPurType = (EditText)findViewById(R.id.txtPurType);
        txtPurSpec = (EditText)findViewById(R.id.txtPurSpec);
        txtPurNumber = (EditText)findViewById(R.id.txtPurNumber);
        txtPurWeight = (EditText)findViewById(R.id.txtPurWeight);
        txtPurTotal = (EditText)findViewById(R.id.txtPurTotal);
        txtPurUnit = (EditText)findViewById(R.id.txtPurUnit);

        tvPurcount = (TextView) findViewById(R.id.tvPurcount);
        txtBarcode = (EditText) findViewById(R.id.txtPurBarcode);
        txtInvName = (EditText) findViewById(R.id.txtPurInvName);
        txtInvSerino = (EditText) findViewById(R.id.txtPurSerino);
        txtInvCode = (EditText) findViewById(R.id.txtPurInvCode);
        txtBatch = (EditText) findViewById(R.id.txtPurBatch);

        btnTask = (Button) findViewById(R.id.btnPurTask);

        btnExit = (Button) findViewById(R.id.btnPurReturn);

        btnTask.setOnClickListener(myButtonListner);
        btnExit.setOnClickListener(myButtonListner);

        btnDetail = (Button) findViewById(R.id.btnPurDetail);
        btnDetail.setOnClickListener(myButtonListner);

        this.txtBarcode.addTextChangedListener(watchers);
        txtBarcode.setOnKeyListener(myTxtListener);
        txtPurNumber.setOnKeyListener(myTxtListener);
        txtBatch.setOnKeyListener(myTxtListener);
        txtPurTotal.setOnKeyListener(myTxtListener);

        ActionBar actionBar = this.getActionBar();
        actionBar.setTitle("采购入库扫描明细");
        // Drawable TitleBar =
        // this.getResources().getDrawable(R.drawable.bg_barbackgroup);
        // actionBar.setBackgroundDrawable(TitleBar);
        // actionBar.show();
        // 获得并创建传递过来的参数

        // ===============
        try {
            if (jsHead == null && jsBody == null)
                LoadPurOrder();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(PurStockInDetail.this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(PurStockInDetail.this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
        }

        JSONArray arrays;
        try {
            ishouldinnum = 0.0;
            iinnum = 0.0;
            if (jsBody == null || !jsBody.has("PurBody")) {
                Common.ReScanErr = true;
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                ReScanErr();
                return;
            }
            arrays = jsBody.getJSONArray("PurBody");
            for (int i = 0; i < arrays.length(); i++) {
//                String sshouldinnum = ((JSONObject) (arrays.get(i)))
//                        .getString("nordernum");
                String sshouldinnum = ((JSONObject) (arrays.get(i))).getString("tasknum");
                String sinnum = ((JSONObject) (arrays.get(i)))
                        .getString("nconfirmnum");

                ishouldinnum = ishouldinnum + Double.valueOf(sshouldinnum);
                if(!sinnum.toLowerCase().equals("null") && !sinnum.isEmpty())
                    iinnum = iinnum + Double.valueOf(sinnum);
            }
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            Toast.makeText(PurStockInDetail.this, e1.getMessage(),
                    Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
        }
        tvPurcount.setText("总量" + ishouldinnum + " | " + "已扫" + iinnum
                + " | " + "未扫" + (ishouldinnum - iinnum));

        //ReScanBody();
    }


    private void SaveScanedBody() {


        if (Common.ReScanErr == true) {
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            ReScanErr();
            return;
        }

        if (ReScanBody.equals("0")) {
            return;
        }

        if (ScanedBarcode == null || ScanedBarcode.size() < 1) {
            if (fileScan.exists()) {
                fileScan.delete();
            }
            return;
        }

        String BillBarCode = "";

        writeTxt = new writeTxt();

        //记录扫描数据
        String ScanedBar = "";


        for (int i = 0; i < ScanedBarcode.size(); i++) {

            if (i == ScanedBarcode.size() - 1)
                BillBarCode = BillBarCode + ScanedBarcode.get(i).toString();
            else
                BillBarCode = BillBarCode + ScanedBarcode.get(i).toString() + ",";
        }
        ScanedBar = BillBarCode;


        if (fileScan.exists()) {
            fileScan.delete();
        }

        writeTxt.writeTxtToFile(ScanedFileName, ScanedBar);

    }

    private void ReScanErr() {
        AlertDialog.Builder bulider =
                new AlertDialog.Builder(this).setTitle(R.string.CuoWu).setMessage("数据加载出现错误" + "\r\n" + "退出该模块并且再次尝试加载缓存");

        bulider.setPositiveButton(R.string.QueRen, listenExit).setCancelable(false).create().show();
        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
        return;
    }

    private DialogInterface.OnClickListener listenExit = new
            DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,
                                    int whichButton) {
                    finish();
                    System.gc();
                }
            };

    private void ReScanBody() {
        String res = "";


        if (ScanedBarcode.size() > 0) {
            ReScanBody = "1";
            return;
        }


        if (!fileScan.exists()) {
            ReScanBody = "1";
            return;
        }

        try {

            FileInputStream fin = new FileInputStream(fileNameScan);

            int length = fin.available();

            byte[] buffer = new byte[length];

            fin.read(buffer);

            res = EncodingUtils.getString(buffer, "UTF-8");

            res = res.substring(0, res.length() - 2);

            fin.close();

            ArrayList<String> ScanedBillBar = new ArrayList<String>();

            String[] Bars;
            if (res.contains(",")) {
                ReScanBody = "0";
                Bars = res.split("\\,");

                for (int i = 0; i < Bars.length; i++) {
                    ScanedBillBar.add(Bars[i]);
                }
            } else {
                ReScanBody = "0";
                ScanedBillBar.add(res);
            }


            if (ScanedBillBar.size() < 1) {
                ReScanBody = "1";
                return;
            }

            int x = 0;

            for (int i = 0; i < ScanedBillBar.size(); i++) {

                if (x > 10) {
//        			AlertDialog alertDialog = null;
//        			alertDialog = new AlertDialog.Builder(this).create();
//        	        alertDialog.setTitle("数据加载出现错误");
//        	        alertDialog.setMessage("请在保持网络畅通的情况下尝试再次加载缓存数据");
//        	        alertDialog.show();
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    Common.ReScanErr = true;
                    ReScanErr();
                    return;
                }

                ScanDetail(ScanedBillBar.get(i).toString());

                String OKflg = "ng";

                if (ScanedBarcode == null || ScanedBarcode.size() < 1) {
                    x++;
                    i--;
                } else {
                    for (int j = 0; j < ScanedBarcode.size(); j++) {

                        String AAA = ScanedBarcode.get(j);
                        if (ScanedBillBar.get(i).toString().equals(AAA)) {
                            OKflg = "ok";
                        }

                    }
                    if (!OKflg.equals("ok")) {
                        x++;
                        i--;
                    }
                }
            }

            this.txtBarcode.requestFocus();
            ReScanBody = "1";

        } catch (Exception e) {

            e.printStackTrace();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pur_stock_in_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Changeline();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static AlertDialog SelectLine = null;
    private buttonOnClickC buttonOnClickC = new buttonOnClickC(0);
    static String[] LNameList = new String[2];

    private void Changeline() {

        int lsindex = 0;
        if (Common.lsUrl.equals(MainLogin.objLog.LoginString2)) {
            lsindex = 1;
        }

        LNameList[0] = getString(R.string.ZhuWebDiZhi);
        LNameList[1] = getString(R.string.FuWebDiZhi);

        SelectLine = new AlertDialog.Builder(this).setTitle(R.string.QieHuanDiZhi)
                .setSingleChoiceItems(LNameList, lsindex, buttonOnClickC)
                .setPositiveButton(R.string.QueRen, buttonOnClickC)
                .setNegativeButton(R.string.QuXiao, buttonOnClickC).show();
    }

    private void ShowLineChange(String WebName) {

        String CommonUrl = Common.lsUrl;
        CommonUrl = CommonUrl.replace("/service/nihao", "");

        AlertDialog.Builder bulider = new AlertDialog.Builder(this).setTitle(
                R.string.QieHuanChengGong).setMessage(R.string.YiJingQieHuanZhi + WebName + "\r\n" + CommonUrl);

        bulider.setPositiveButton(R.string.QueRen, null).setCancelable(false).create()
                .show();
        return;
    }

    private class buttonOnClickC implements DialogInterface.OnClickListener {
        public int index;

        public buttonOnClickC(int index) {
            this.index = index;
        }

        @Override
        public void onClick(DialogInterface dialog, int whichButton) {
            if (whichButton >= 0) {
                index = whichButton;
            } else {

                if (dialog.equals(SelectLine)) {
                    if (whichButton == DialogInterface.BUTTON_POSITIVE) {
                        if (index == 0) {

                            Common.lsUrl = MainLogin.objLog.LoginString;
                            ShowLineChange(LNameList[0]);
                            System.gc();
                        } else if (index == 1) {
                            Common.lsUrl = MainLogin.objLog.LoginString2;
                            ShowLineChange(LNameList[1]);
                            System.gc();
                        }
                        return;
                    } else if (whichButton == DialogInterface.BUTTON_NEGATIVE) {
                        return;
                    }
                }
            }
        }
    }


}
