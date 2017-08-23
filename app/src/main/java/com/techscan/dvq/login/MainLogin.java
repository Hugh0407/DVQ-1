package com.techscan.dvq.login;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.techscan.dvq.MyHttpClient;
import com.techscan.dvq.R;
import com.techscan.dvq.common.Base64Encoder;
import com.techscan.dvq.common.Common;
import com.techscan.dvq.common.SoundHelper;
import com.techscan.dvq.common.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EncodingUtils;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;
import static com.techscan.dvq.MainActivity.LoginUser;
import static com.techscan.dvq.common.Common.lsUrl;

public class MainLogin extends Activity {
    // protected static final Context context = null;
    // �ؼ��Ķ���
    Button btnLogin, btnExit;
    String LoginString, LoginString2, CompanyCode, OrgCode, WhCode, WhCodeB;
    String newVerName       = "";// �°汾����
    String UPDATE_SERVERAPK = "dvq.apk";

    public static String tempFilePath = "";

    public static String      appTime      = "";
    //static JSONArray arysUserRole = new JSONArray();
    //static AlertDialog LGalertDialog = null;
    static        WifiManager wifi_service = null;
    static        String      sWIFIMin     = null;
    static        String      sWIFIMax     = null;


    int            newVerCode = -1;// �°汾��
    ProgressDialog pd         = null;


    String months;
    String dayys;
    EditText user = null;
    EditText pwds = null;

    TextView tvVer;

    public static SoundPool sp;// ����һ��SoundPool
    public static int       music;// ����һ��int������suondID
    public static int       music2;// ����һ��int������suondID


    public static Common objLog = new Common();
    @Nullable
    @InjectView(R.id.ed_time)
    EditText edTime;

    //objLog.LoginString = LoginString;
    // �����û���
    private void LoadSettingForm() {
        Intent abcd = new Intent(MainLogin.this, SettingActivity.class);
        startActivityForResult(abcd, R.layout.activity_setting);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {// ����meu���¼�
            return false;
        }
        return keyCode != KeyEvent.KEYCODE_BACK;
    }


    private DialogInterface.OnClickListener listenExit = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int whichButton) {
            MainLogin.this.finish();
        }
    };

    // ȡ����ť�Ի����¼�
    private void Exit() {
        AlertDialog.Builder bulider = new AlertDialog.Builder(this).setTitle(
                R.string.XunWen).setMessage(R.string.NiQueDingYaoTuiChuMa);
        bulider.setNegativeButton(R.string.QuXiao, null);
        bulider.setPositiveButton(R.string.QueDing, listenExit).create().show();
    }

    // private AlertDialog DeleteButton=null;
    private void Login() throws ParseException, IOException, JSONException {

        SharedPreferences mySharedPreferences = getSharedPreferences(
                SettingActivity.PREFERENCE_SETTING, Context.MODE_PRIVATE);

        LoginString = mySharedPreferences.getString("Address", "");        //����ַ
        LoginString2 = mySharedPreferences.getString("Address2", "");    //����ַ
        CompanyCode = mySharedPreferences.getString("CompanyCode", "");
        // TODO: 2017/8/15  XUHU
//        OrgCode = mySharedPreferences.getString("OrgCode", "");
        OrgCode = mySharedPreferences.getString("CompanyCode", "");
        WhCode = mySharedPreferences.getString("WhCode", "");// ��ȡA��˾�Ĺ��˲ֿ�
        // AccId=mySharedPreferences.getString("AccId", "A");
        WhCodeB = mySharedPreferences.getString("AccId", "");// ��ȡB��˾�Ĺ��˲ֿ�

        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("account",user.getText().toString());
        editor.apply();

        // EditText user=(EditText)findViewById(R.id.txtUserName);
        // EditText pwds=(EditText)findViewById(R.id.txtPassword);

        if (LoginString.equals("") || CompanyCode.equals("")
                || OrgCode.equals("") || WhCode.equals("")
                || WhCodeB.equals("")) {

            AlertDialog.Builder bulider = new AlertDialog.Builder(this)
                    .setTitle(R.string.TiXing).setMessage(R.string.HaiMeiYouPeiZhiJIChuLianJIe);
            bulider.setPositiveButton(R.string.QueDing, null).create().show();
            return;
        }
        String userName = user.getText().toString().replace("\n", "");
        String password = pwds.getText().toString().replace("\n", "");
        if (userName.equals("") || password.equals("")) {

            AlertDialog.Builder bulider = new AlertDialog.Builder(this)
                    .setTitle(R.string.TiXing).setMessage("�û����������벻��Ϊ��");

            bulider.setPositiveButton(R.string.QueDing, null).create().show();
            return;
        }

        // Temp
        // LoginString = "http://192.168.6.221/service/nihao";
        // LoginString ="http://192.168.0.203:5556/mb/nihao";
        // LoginString = "http://192.168.0.203:5556/service/nihao";
        // LoginString = "http://192.168.93.121/service/nihao";
        final String Version = "" + this.getVerCode(this);
        //
        // LoginString = "http://192.168.66.231:8180/service/nihao";
        if (lsUrl.equals("")) {
            lsUrl = LoginString;
        }
        Log.d("TAG", "DoHttpQuery: " + lsUrl);
//��ʱ�ر�
//		if(UrlErr > 0)
//		{
//			if(lsUrl.equals(LoginString))
//			{
//				lsUrl = LoginString2;
//			}
//			else if(lsUrl.equals(LoginString2))
//			{
//				lsUrl = LoginString;
//			}
//		}	

        String user_name = Base64Encoder.encode(userName.getBytes("gb2312"));
        // ���÷���
        /*********************************************************************/
        // ���ύ
        RequestBody        formBody = new FormBody.Builder().build();
        final OkHttpClient client   = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(lsUrl)
                .addHeader("Self-Test", "V")
                .addHeader("User-Code", user_name)
                .addHeader("User-Pwd", password)
                .addHeader("User-Company", CompanyCode)
                .addHeader("Data-Source", "A")
                .addHeader("Org-Code", OrgCode)
                .addHeader("Version-Code", Version)
                .post(formBody)
                .build();
        Observable.create(new ObservableOnSubscribe<Response>() {
            @Override
            public void subscribe(ObservableEmitter<Response> e) throws Exception {
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    Utils.showToast(MainLogin.this, "���������쳣");
                    return;
                }
                e.onNext(response);
                e.onComplete();
            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Response>() {
                    @Override
                    public void accept(Response response) throws Exception {

                        if (response.code() == 200) {
                            /**
                             * GB2312���й��涨�ĺ��ֱ��룬Ҳ����˵�Ǽ������ĵ��ַ�������;
                             * GBK �� GB2312����չ ,���˼���GB2312�⣬��������ʾ��������
                             */
                            String res = new String(response.body().bytes(), "GBK");
//                            res = new String(res.getBytes("iso-8859-1"), "GBK");
                            JSONObject jas = new JSONObject(res);

                            if (!jas.has("Status")) {
                                Utils.showToast(MainLogin.this, R.string.WangLuoChuXianWenTi);
                                SoundHelper.playWarning();
                                return;
                            }

                            boolean loginStatus = jas.getBoolean("Status");
                            if (loginStatus == true) {

                                objLog.LoginString = LoginString;
                                objLog.LoginString2 = LoginString2;
                                objLog.LoginUser = user.getText().toString().replace("\n", "");
                                objLog.Password = pwds.getText().toString().replace("\n", "");
                                objLog.CompanyCode = CompanyCode;

                                objLog.UserID = jas.getString("userid");
                                objLog.UserName = LoginUser;
                                objLog.STOrgCode = OrgCode;
//                                ************// TODO: 2017/8/15 XUHU
//                                objLog.STOrgCode = CompanyCode;
                                objLog.WhCodeA = WhCode;
                                objLog.WhCodeB = WhCodeB;

                                objLog.UserIDB = jas.getString("useridb");
                                objLog.VersionCode = Version;
                                //���� ����ҳ��
                                SimpleDateFormat f    = new SimpleDateFormat("yyyy-MM-dd");
                                Date             date = new Date();
                                objLog.LoginDate = f.format(date);
                                if (!GetInfo()) {
                                    return;
                                }
                                Intent MenuForm = new Intent(MainLogin.this, MainMenu.class);
                                startActivity(MenuForm);

                            } else {
                                String ErrMsg = jas.getString("ErrMsg");
                                Log.d("TAG", "accept: " + ErrMsg);
                                Utils.showToast(MainLogin.this, ErrMsg);
                                SoundHelper.playWarning();
                            }
                        }
                    }
                });

//
//        /*********************************************************************/
//
//        HttpPost httpPost = new HttpPost(lsUrl);
//        httpPost.addHeader("Self-Test", "V");
//        httpPost.addHeader("User-Code", user_name);
//        httpPost.addHeader("User-Pwd", password);
//        httpPost.addHeader("User-Company", CompanyCode);
//        httpPost.addHeader("Data-Source", "A");
//        httpPost.addHeader("Org-Code", OrgCode);
//        httpPost.addHeader("Version-Code", Version);
//
////        Log.d("TAG", "CompanyCode: " + CompanyCode);
////        Log.d("TAG", "user_name: " + user_name);
////        Log.d("TAG", "OrgCode: " + OrgCode);
////        Log.d("TAG", "Version: " + Version);
//        // WhCodeB
//        HttpResponse httpResponse = null;
//        HttpClient   defaults     = null;
//
//        try {
//            defaults = MyHttpClient.getSaveHttpClient();
//            // DefaultHttpClient defaults = new DefaultHttpClient();
//            // HttpConnectionParams.setConnectionTimeout(defaults.getParams(),
//            // 30000);
//            // HttpConnectionParams.setSoTimeout(defaults.getParams(),30000);
//            httpResponse = defaults.execute(httpPost);
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
//            // ADD CAIXY TEST START
//            sp.play(music, 1, 1, 0, 0, 1);
//
//            // ADD CAIXY TEST END
//
//            return;
//        } catch (IOException e) {
//            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
//            sp.play(music, 1, 1, 0, 0, 1);
//            return;
////			//�����л����õ�ַ
////			if(UrlErr>2)
////			{
////				UrlErr = 0;
////				e.printStackTrace();
////				Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
////				// ADD CAIXY TEST START
////
////				sp.play(music, 1, 1, 0, 0, 1);
////				// ADD CAIXY TEST END
////				return;
////			}
////			else
////			{
////				UrlErr++;
////				Login();
////			}
//
//        } catch (Exception e) {
//            e.printStackTrace();
//
//            Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
//            // ADD CAIXY TEST START
//            sp.play(music, 1, 1, 0, 0, 1);
//            // ADD CAIXY TEST END
//            return;
//        }
//
//        // ��������֤������������
//        int v = 0;
//        if (httpResponse != null) {
//            v = httpResponse.getStatusLine().getStatusCode();
//        }
//
//        if (v == 200) {
//            // Toast.makeText(this,"�����֤ͨ����׼������", Toast.LENGTH_SHORT).show();
//            String result = EntityUtils.toString(httpResponse.getEntity());
//            String jasstr = EncodingUtils.getString(EncodingUtils.getBytes(result, "ISO8859-1"), "gb2312");
//
//            JSONObject jas = new JSONObject(jasstr);
//
//            if (!jas.has("Status")) {
//                Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG)
//                        .show();
//                sp.play(music, 1, 1, 0, 0, 1);
//
//                return;
//            }
//        })
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<Response>() {
//                    @Override
//                    public void accept(Response response) throws Exception {
//
//            boolean loginStatus = jas.getBoolean("Status");
//            if (loginStatus == true) {
//
//                objLog = new Common();
//                objLog.LoginString = LoginString;
//                objLog.LoginString2 = LoginString2;
//                objLog.LoginUser = user.getText().toString().replace("\n", "");
//                objLog.Password = pwds.getText().toString().replace("\n", "");
//                objLog.CompanyCode = CompanyCode;
//
//                objLog.UserID = jas.getString("userid");
//                objLog.UserName = jas.getString("username");
//                Log.d("tag", "Login: " + objLog.UserName);
//                objLog.STOrgCode = OrgCode;
//                objLog.WhCodeA = WhCode;
//                objLog.WhCodeB = WhCodeB;
//
//                objLog.UserIDB = jas.getString("useridb");
//                objLog.VersionCode = Version;
//                //���� ����ҳ��
//
//                                objLog.UserID = jas.getString("userid");
//                                objLog.UserName = LoginUser;
//                                objLog.STOrgCode = OrgCode;
//                                objLog.WhCodeA = WhCode;
//                                objLog.WhCodeB = WhCodeB;
//
//                SimpleDateFormat f    = new SimpleDateFormat("yyyy-MM-dd");
//                Date             date = new Date();
//                objLog.LoginDate = f.format(date);
//
//                if (!GetInfo()) {
//                    return;
//                }
//
//        /*********************************************************************/
//
//                Intent MenuForm = new Intent(this, MainMenu.class);
//                startActivity(MenuForm);
//
//            } else {
//                String ErrMsg = jas.getString("ErrMsg");
//                Toast.makeText(this, ErrMsg, Toast.LENGTH_LONG).show();
//                // ADD CAIXY TEST START
//
//                sp.play(music, 1, 1, 0, 0, 1);
//                // ADD CAIXY TEST END
//                return;
//            }
//        }
//		else {
//			Toast.makeText(this, "���ʴ���", Toast.LENGTH_LONG).show();
//			// ADD CAIXY TEST START
//			
//			sp.play(music, 1, 1, 0, 0, 1);
//			// ADD CAIXY TEST END
//			return;
//		}

        //

    }

    // �����Ի���İ�ť�¼�����

    private Button.OnClickListener myListner = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnLogin: {
                    //ShowLoading();
                    Login111();
//                    pwds.setText("");
                    break;
                }
                case R.id.btnExit: {
                    Exit();
                    break;
                }
                case R.id.tvVer: {
                    ScanBarcode();
                    break;
                }

            }
        }
    };

    private boolean GetInfo() throws JSONException, ParseException, IOException {
        // SearchmainActivity search= new SearchmainActivity
        JSONObject para = new JSONObject();
        para.put("FunctionName", "GetComanyInfo");
        para.put("CompanyCode", CompanyCode);
        para.put("TableName", "LoginInfo");

        JSONObject LoginInfo = null;

        LoginInfo = Common.DoHttpQuery(para, "CommonQuery", "A");

        if (LoginInfo == null) {
            Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG)
                    .show();
            sp.play(music, 1, 1, 0, 0, 1);

            return false;
        }

        if (!LoginInfo.has("Status")) {
            Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG)
                    .show();
            sp.play(music, 1, 1, 0, 0, 1);

            return false;
        }

        if (!LoginInfo.getBoolean("Status")) {

            Toast.makeText(this, "���������еĹ�˾Code���ò���ȷ,����",
                           Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            sp.play(music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            return false;
        }

        String GetCompanyName = "";

        JSONArray arys = LoginInfo.getJSONArray("LoginInfo");
        for (int i = 0; i < arys.length(); i++) {
            GetCompanyName = (String) arys.getJSONObject(i).get(
                    "unitname");
        }

        objLog.CompanyName = GetCompanyName;

        para.put("FunctionName", "GetSTOrgList");

        LoginInfo = null;

        LoginInfo = Common.DoHttpQuery(para, "CommonQuery", "A");

        if (LoginInfo == null) {
            Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG)
                    .show();
            sp.play(music, 1, 1, 0, 0, 1);
            return false;
        }

        if (!LoginInfo.has("Status")) {
            Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG)
                    .show();
            sp.play(music, 1, 1, 0, 0, 1);

            return false;
        }

        if (!LoginInfo.getBoolean("Status")) {
            Toast.makeText(this, "���������еĿ����֯���ò���ȷ,���飡",
                           Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            sp.play(music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            return false;
        }

        String GetSTOrg    = "";
        String GetSTOrgFlg = "N";

        JSONArray arys2 = LoginInfo.getJSONArray("LoginInfo");
        for (int i = 0; i < arys2.length(); i++) {
            GetSTOrg = (String) arys2.getJSONObject(i).get("bodycode");
            if (GetSTOrg.equals(OrgCode)) {
                GetSTOrgFlg = "Y";
            }
        }

        if (GetSTOrgFlg.equals("N")) {

            Toast.makeText(this, "���������еĿ����֯���ò���ȷ,���飡",
                           Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            sp.play(music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            return false;
        }

//walter
        JSONObject paraRole = new JSONObject();
        paraRole.put("FunctionName", "GetUserRole");
        paraRole.put("TableName", "UserRole");
        paraRole.put("UserIDA", objLog.UserID);
        paraRole.put("UserIDB", objLog.UserIDB);

        JSONObject UserRole = null;

        UserRole = Common.DoHttpQuery(paraRole, "CommonQuery", "");

        if (UserRole == null) {

            Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG)
                    .show();
            sp.play(music, 1, 1, 0, 0, 1);
            return false;
        }

        if (!UserRole.has("Status")) {

            Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG)
                    .show();
            sp.play(music, 1, 1, 0, 0, 1);
            return false;
        }

        if (!UserRole.getBoolean("Status")) {

            Toast.makeText(this, "ȡ���û�Ȩ��ʧ��",
                           Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            sp.play(music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            return false;
        }


        MainLogin.objLog.arysUserRole = UserRole.getJSONArray("UserRole");


        JSONObject paraWHRole = new JSONObject();
        paraWHRole.put("FunctionName", "GetUserWHRole");
        paraWHRole.put("TableName", "UserWHRole");
        paraWHRole.put("UserIDA", objLog.UserID);
        paraWHRole.put("UserIDB", objLog.UserIDB);

        JSONObject UserWHRole = null;

        UserWHRole = Common.DoHttpQuery(paraWHRole, "CommonQuery", "");

        if (UserWHRole == null) {

            Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG)
                    .show();
            sp.play(music, 1, 1, 0, 0, 1);
            return false;
        }

        if (!UserWHRole.has("Status")) {

            Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG)
                    .show();
            sp.play(music, 1, 1, 0, 0, 1);
            return false;
        }

        try {
            if (!UserWHRole.getBoolean("Status")) {

                String ErrMsg = UserWHRole.getString("ErrMsg");
                if (ErrMsg.equals("û�п�������")) {
                    MainLogin.objLog.arysUserWHRole = null;
                } else {
                    Toast.makeText(this, "ȡ���û��ֿ�Ȩ��ʧ��",
                                   Toast.LENGTH_LONG).show();
                    // ADD CAIXY TEST START
                    sp.play(music, 1, 1, 0, 0, 1);
                    // ADD CAIXY TEST END
                    return false;
                }

            } else {
                try {
                    MainLogin.objLog.arysUserWHRole = UserWHRole.getJSONArray("UserWHRole");
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(this, "ȡ��Ȩ��ʧ��",
                                   Toast.LENGTH_LONG).show();
                    // ADD CAIXY TEST START
                    sp.play(music, 1, 1, 0, 0, 1);
                    // ADD CAIXY TEST END
                    return false;
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, "ȡ��Ȩ��ʧ��",
                           Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            sp.play(music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            return false;
        }
////        del walter
        return true;

    }

    private void ScanBarcode() {
        // SearchmainActivity search= new SearchmainActivity


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 23) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    String BarCode = bundle.getString("BarCode");
                    user.setText(BarCode);
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);
        ButterKnife.inject(this);
        //����ʱ��Ϊ��ǰϵͳʱ��
        mycalendar = Calendar.getInstance();//��ʼ��Calendar��������
        year = mycalendar.get(Calendar.YEAR); //��ȡCalendar�����е���
        month = mycalendar.get(Calendar.MONTH);//��ȡCalendar�����е���
        day = mycalendar.get(Calendar.DAY_OF_MONTH);//��ȡ����µĵڼ���
                if (month<9){
            months = "0"+(month+1);
        }else{
            months = (month+1)+"";
        }
        if (day<10){
            dayys = "0"+(day);
        }else{
            dayys = day+"";
        }
//        appTime = year + "-" + (month + 1) + "-" + day;
        appTime = year + "-" + months + "-" + dayys;
        edTime.setText(Utils.formatTime(System.currentTimeMillis()));
        edTime.setOnFocusChangeListener(myFocusListener);
        edTime.setInputType(InputType.TYPE_NULL);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnExit = (Button) findViewById(R.id.btnExit);

        user = (EditText) findViewById(R.id.txtUserName);
        pwds = (EditText) findViewById(R.id.txtPassword);
        // btnSet = (Button)findViewById(R.id.btnSetting);
        //LGalertDialog = new AlertDialog.Builder(this).create();
//        Drawable TitleBar = this.getResources().getDrawable(
//                R.color.white);
        ActionBar actionBar = this.getActionBar();
        actionBar.setTitle(R.string.title);
//        actionBar.setBackgroundDrawable(TitleBar);
        actionBar.show();
//        user.setText("����");
//        pwds.setText("1234aS~");
        btnLogin.setOnClickListener(myListner);
        btnExit.setOnClickListener(myListner);
        // btnSet.setOnClickListener(myListner);

        // ADD CAIXY START 15/04/15
        tvVer = (TextView) findViewById(R.id.tvVer);
        tvVer.setText("Ver : " + this.getVerName(this));
        tvVer.setOnClickListener(myListner);
        // ADD CAIXY END 15/04/15
        // ADD CAIXY TEST
        sp = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);// ��һ������Ϊͬʱ���������������������ڶ����������ͣ�����Ϊ��������
        music = sp.load(this, R.raw.xxx, 1); // ����������زķŵ�res/raw���2��������Ϊ��Դ�ļ�����3��Ϊ���ֵ����ȼ�
        music2 = sp.load(this, R.raw.yyy, 1); // ����������زķŵ�res/raw���2��������Ϊ��Դ�ļ�����3��Ϊ���ֵ����ȼ�
        // ADD CAIXY TEST

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                                           .detectDiskReads().detectDiskWrites().detectNetwork()
                                           .penaltyLog().build());

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                                       .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                                       .penaltyLog().penaltyDeath().build());

        // doNewVersionUpdate(this.LoginString);

        wifi_service = (WifiManager) getSystemService(WIFI_SERVICE);
        // String sWIFIMin = mySharedPreferences.getString("WIFIMin", "");
        // String sWIFIMax = mySharedPreferences.getString("WIFIMax", "");

        SharedPreferences mySharedPreferences = getSharedPreferences(
                SettingActivity.PREFERENCE_SETTING, Activity.MODE_PRIVATE);

        sWIFIMin = mySharedPreferences.getString("WIFIMin", "");
        sWIFIMax = mySharedPreferences.getString("WIFIMax", "");
        String account = mySharedPreferences.getString("account", "");
        user.setText(account);
        this.LoginString = mySharedPreferences.getString("Address", "");
        this.LoginString2 = mySharedPreferences.getString("Address2", "");
        if (LoginString.equals("") || LoginString == null || LoginString2.equals("") || LoginString2 == null) {

        } else {
            if (getServerVer()) {
                int vercode = getVerCode(this);
                if (newVerCode > vercode) {
                    doNewVersionUpdate(LoginString);// �����°汾����
                } else {
                    // Toast.makeText(getApplicationContext(),"Ŀǰ�����°汾����л����֧��",Toast.LENGTH_LONG).show();//û���°汾
                }
            }// �Ӻ�������ȡ�汾��
        }

    }

    int      year;
    int      month;
    int      day;
    Calendar mycalendar;

    @OnClick(R.id.ed_time)
    public void onViewClicked() {
        DatePickerDialog dpd = new DatePickerDialog(MainLogin.this, Datelistener, year, month, day);
        dpd.show();//��ʾDatePickerDialog���
    }

    public static boolean getwifiinfo() {
        WifiInfo wifiInfo = wifi_service.getConnectionInfo();
        int      iWIFIMin = 30;
        int      iWIFIMax = 85;

        if (sWIFIMin != null && !sWIFIMin.equals("")) {
            iWIFIMin = Integer.valueOf(sWIFIMin).intValue();
        }

        if (sWIFIMax != null && !sWIFIMax.equals("")) {
            iWIFIMax = Integer.valueOf(sWIFIMax).intValue();
        }

        // int iWIFIMin = Integer.valueOf(sWIFIMin).intValue();
        // int iWIFIMax = Integer.valueOf(sWIFIMax).intValue();
        int Rssi = wifiInfo.getRssi() * -1;
        return (iWIFIMin < Rssi) && (Rssi < iWIFIMax);

    }

    /**
     * ��ð汾��
     */
    public int getVerCode(Context context) {
        int verCode = -1;
        try {
            verCode = context.getPackageManager().getPackageInfo(
                    "com.techscan.dvq", 0).versionCode;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            Log.e(getString(R.string.BanBenHaoHuoQuYiChang), e.getMessage());
        }
        return verCode;
    }

    /**
     * ��ð汾����
     */
    public String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo(
                    "com.techscan.dvq", 0).versionName;
        } catch (NameNotFoundException e) {
            Log.e(getString(R.string.BanBenHaoHuoQuYiChang), e.getMessage());
        }
        return verName;
    }

    /**
     * �ӷ������˻�ð汾����汾����
     *
     * @return
     */

    //int UrlErr = 0;    //��ʱ�ر�
    public boolean getServerVer() {
        try {

            HttpGet httpget = new HttpGet();


            lsUrl = LoginString;

            URI uri = new URI(lsUrl);

            httpget.setURI(uri);
            HttpResponse httpResponse = null;
            HttpClient   defaults     = null;
            try {
                // DefaultHttpClient defaults=new DefaultHttpClient();
                // HttpConnectionParams.setConnectionTimeout(defaults.getParams(),
                // 30000);
                // HttpConnectionParams.setSoTimeout(defaults.getParams(),30000);

                defaults = MyHttpClient.getSaveHttpClient();

                httpResponse = defaults.execute(httpget);

            } catch (ClientProtocolException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                sp.play(music, 1, 1, 0, 0, 1);
                return false;
            } catch (Exception e) {


                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG)
                        .show();
                sp.play(music, 1, 1, 0, 0, 1);
                return false;


            }
            if (httpResponse.getStatusLine().getStatusCode() == 200) {

                //UrlErr = 0;
                String result = EntityUtils.toString(httpResponse.getEntity());
                String jasstr = EncodingUtils.getString(
                        EncodingUtils.getBytes(result, "ISO8859-1"), "gb2312");

                String json = jasstr;// bReader.readLine();
                //mod walter
                //JSONArray array = new JSONArray(json);
                //JSONObject jsonObj = array.getJSONObject(0);
                //newVerCode = Integer.parseInt(jsonObj.getString("verCode"));
                //newVerName = jsonObj.getString("verName");
                newVerCode = 1;
                newVerName = "1.0.00";
                //mod walter
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            sp.play(music, 1, 1, 0, 0, 1);
            return false;
        }
        return true;
    }

    /**
     * �����°汾
     */
    public void notNewVersionUpdate() {
        int          verCode = this.getVerCode(this);
        String       verName = this.getVerName(this);
        StringBuffer sb      = new StringBuffer();
        sb.append("��ǰ�汾��");
        sb.append(verName);
        sb.append(" Code:");
        sb.append(verCode);
        sb.append("\n�������°汾���������");
        Dialog dialog = new AlertDialog.Builder(this).setTitle(R.string.RuanJianGengXin)
                .setMessage(sb.toString())
                .setPositiveButton(R.string.QueDing, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        finish();
                    }
                }).create();
        dialog.show();
    }

    /**
     * ���°汾
     */
    public void doNewVersionUpdate(final String DownStr) {
        int          verCode = this.getVerCode(this);
        String       verName = this.getVerName(this);
        StringBuffer sb      = new StringBuffer();
        sb.append("��ǰ�汾��");
        sb.append(verName);
        sb.append(" Code:");
        sb.append(verCode);
        sb.append(",���ְ汾��");
        sb.append(newVerName);
        sb.append(" Code:");
        sb.append(verCode);
        sb.append(",�Ƿ����");
        Dialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.RuanJianGengXin)
                .setMessage(sb.toString())
                .setPositiveButton("����", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        pd = new ProgressDialog(MainLogin.this);
                        pd.setTitle("��������");
                        pd.setMessage("������...");
                        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        String downfile = DownStr.substring(0,
                                                            DownStr.length() - 13);
                        downfile = downfile + "DVQ/" + UPDATE_SERVERAPK;
                        downFile(downfile);

                    }
                })
                .setNegativeButton("�ݲ�����",
                                   new DialogInterface.OnClickListener() {

                                       @Override
                                       public void onClick(DialogInterface dialog,
                                                           int which) {
                                           // TODO Auto-generated method stub
                                           finish();
                                       }
                                   }).create();
        // ��ʾ���¿�
        dialog.show();
    }

    /**
     * ����apk
     */
    public void downFile(final String url) {
        pd.show();
        new Thread() {
            public void run() {
                HttpClient client = new DefaultHttpClient();

                // HttpClient client = MyHttpClient.getSaveHttpClient();

                HttpGet      get = new HttpGet(url);
                HttpResponse response;
                try {
                    response = client.execute(get);
                    HttpEntity entity = response.getEntity();
                    // long length = entity.getContentLength();
                    // InputStream is = entity.getContent();

                    down();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(MainLogin.this, e.getMessage(),
                                   Toast.LENGTH_LONG).show();
                    // ADD CAIXY TEST START
                    sp.play(music, 1, 1, 0, 0, 1);
                    // ADD CAIXY TEST END
                    e.printStackTrace();
                }
            }
        }.start();
    }


    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            pd.cancel();
            update();
        }
    };

    /**
     * ������ɣ�ͨ��handler�����ضԻ���ȡ��
     */
    public void down() {
        new Thread() {
            public void run() {
                Message message = handler.obtainMessage();
                handler.sendMessage(message);
            }
        }.start();
    }

    /**
     * ��װӦ��
     */
    public void update() {
        File   file   = new File(tempFilePath);
        long   ab     = file.length();
        Intent intent = new Intent(Intent.ACTION_VIEW);

        // intent.setDataAndType(Uri.fromFile(new
        // File(tempFilePath))//Environment.getExternalStorageDirectory(),UPDATE_SERVERAPK
        // , "application/vnd.android.package-archive");
        intent.setAction("android.intent.action.VIEW");
        String downfile = LoginString.substring(0, LoginString.length() - 13);
        downfile = downfile + "DVQ/" + UPDATE_SERVERAPK;
        Uri content_url = Uri.parse(downfile);
        intent.setData(content_url);

        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_login, menu);
        // MenuInflater inflater = getMenuInflater();
        // inflater.inflate(R.menu.activity_main_login, menu);
        // return super.onCreateOptionsMenu(menu);
        // MenuItem SetItem = menu.add(1,1,1,"����");
        // SetItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

//	private void getOverflowMenu() {
//		try {
//			ViewConfiguration config = ViewConfiguration.get(this);
//			Field menuKeyField = ViewConfiguration.class
//					.getDeclaredField("sHasPermanentMenuKey");
//			if (menuKeyField != null) {
//				menuKeyField.setAccessible(true);
//				menuKeyField.setBoolean(config, false);
//			}
//		} catch (Exception e) {
//			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
//			// ADD CAIXY TEST START
//			sp.play(music, 1, 1, 0, 0, 1);
//			// ADD CAIXY TEST END
//			e.printStackTrace();
//		}
//	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.menu_settings) {
            LoadSettingForm();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void Login111() {
        try {
            Login();
//            //mod walte
//            //Login();
//            Intent MenuForm1 = new Intent(this, MainMenu.class);
//            startActivity(MenuForm1);
//            //return;
//            //mod walte
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private DatePickerDialog.OnDateSetListener Datelistener = new DatePickerDialog.OnDateSetListener() {
        /**params��view�����¼����������
         * params��myyear����ǰѡ�����
         * params��monthOfYear����ǰѡ�����
         * params��dayOfMonth����ǰѡ�����
         */
        @Override
        public void onDateSet(DatePicker view, int myyear, int monthOfYear, int dayOfMonth) {
            //�޸�year��month��day�ı���ֵ���Ա��Ժ󵥻���ťʱ��DatePickerDialog����ʾ��һ���޸ĺ��ֵ
            year = myyear;
            month = monthOfYear;
            day = dayOfMonth;
            if (month<9){
            months = "0"+(month+1);
           }else{
            months = (month+1)+"";
           }
           if (day<10){
            dayys = "0"+(day);
        }else{
            dayys = day+"";
        }
            updateDate();
        }

        //��DatePickerDialog�ر�ʱ������������ʾ
        private void updateDate() {
            appTime = year + "-" + months + "-" + dayys;
            Log.d(TAG, "updateDate: "+appTime);
            //��TextView����ʾ����
            edTime.setText(appTime);
        }
    };


    private View.OnFocusChangeListener myFocusListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {
                DatePickerDialog dpd = new DatePickerDialog(MainLogin.this, Datelistener, year, month, day);
                dpd.show();//��ʾDatePickerDialog���
            }
        }
    };



	/*
     * ArrayList<HashMap<String,String>> am=new
	 * ArrayList<HashMap<String,String>>(); HashMap<String,String> ab=new
	 * HashMap<String,String>(); ab.put("a", "a"); HashMap<String,String>
	 * abc=new HashMap<String,String>(); abc.put("a", "a"); am.add(ab);
	 * am.add(abc); SimpleAdapter listItemAdapter = new
	 * SimpleAdapter(this,am,//����Դ
	 * android.R.layout.simple_list_item_single_choice,//ListItem��XMLʵ��
	 * //��̬������ImageItem��Ӧ������ new String[] {"a"},
	 * //ImageItem��XML�ļ������һ��ImageView,����TextView ID new int[]
	 * {android.R.id.text1} );
	 *
	 * OnItemSelectedListener abs=new OnItemSelectedListener() { //
	 * ��position�ѡ��ʱ�����÷�����
	 *
	 * @Override public void onItemSelected(AdapterView<?> parent, View view,
	 * int position, long id) { for(int i=0;i<parent.getCount();i++){ View
	 * v=parent.getChildAt(parent.getCount()-1-i); if (position == i) {
	 * v.setBackgroundColor(Color.RED); } else {
	 * v.setBackgroundColor(Color.TRANSPARENT); } } }
	 *
	 * @Override public void onNothingSelected(AdapterView<?> parent) { } };
	 * DeleteButton =new
	 * AlertDialog.Builder(this).setTitle("ɨ����ϸ��Ϣ").setSingleChoiceItems
	 * (listItemAdapter, 0, null).setNegativeButton("ɾ��",
	 * null).setPositiveButton("ȷ��",null).create();
	 * DeleteButton.getListView().setOnItemClickListener(new
	 * OnItemClickListener() {
	 * 
	 * @Override public void onItemClick(AdapterView<?> arg0, View arg1, int
	 * arg2,
	 * 
	 * long arg3)
	 * 
	 * {
	 * 
	 * for(int i=0;i<arg0.getCount();i++){ View v=arg0.getChildAt(i);
	 * if(v.equals(arg1)) { v.setBackgroundColor(Color.RED); } else {
	 * v.setBackgroundColor(Color.TRANSPARENT); } } } });
	 * 
	 * DeleteButton.show();
	 */
}
