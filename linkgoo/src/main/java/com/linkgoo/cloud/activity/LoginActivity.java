package com.soonvein.cloud.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.renderscript.Long2;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.baidu.tts.client.SynthesizerTool;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.orhanobut.logger.Logger;
import com.soonvein.cloud.BaseApplication;
import com.soonvein.cloud.R;
import com.soonvein.cloud.base.ApiException;
import com.soonvein.cloud.base.MyToast;
import com.soonvein.cloud.bean.UpdateMessage;
import com.soonvein.cloud.constant.Constant;
import com.soonvein.cloud.contract.LoginTaskContract;
import com.soonvein.cloud.contract.VersoinUpdateContract;
import com.soonvein.cloud.utils.ToastUtils;
import com.soonvein.cloud.utils.Utils;

import org.apache.commons.logging.Log;
import org.w3c.dom.Text;

import java.util.Date;

import androidkun.com.versionupdatelibrary.entity.VersionUpdateConfig;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/8/28.
 */

public class LoginActivity extends Activity implements LoginTaskContract.LoginView,VersoinUpdateContract.VersoinUpdate{
    @Bind(R.id.deviceID)
    EditText etDeviceID;
    @Bind(R.id.devicePwd)
    EditText etDevicePwd;
    @Bind(R.id.error_ID)
    TextView error_ID;
    @Bind(R.id.error_PWD)
    TextView error_PWD;
    @Bind(R.id.login)
    Button login;
    @Bind(R.id.tv_device)
    TextView tv_device;
    @Bind(R.id.tv_pwd)
    TextView tv_pwd;
    @Bind(R.id.versoin_tv)
    TextView versoin_tv;
    @Bind(R.id.service_tv)
    TextView service_tv;
    @Bind(R.id.tracerout_rootview)
    LinearLayout linearLayout;
    MediaPlayer mediaPlayer;
    private LoginTaskContract presenter;
    private VersoinUpdateContract versoinUpdateContract;
    private String fileName,versoin,remark,createTime;
    String url,deviceID;
    private SharedPreferences userInfo;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        ButterKnife.bind(LoginActivity.this);
        mediaPlayer = new MediaPlayer();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }
    @Override
    protected void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        userInfo = LoginActivity.this.getSharedPreferences("user_info", 0);
        deviceID = userInfo.getString("DeviceID", "");
        Logger.e("LoginActivity"+deviceID);
        etDeviceID.setText(deviceID);
        client.connect();
        versoinUpdateContract=new VersoinUpdateContract();
        this.versoinUpdateContract.attachView(this);
        etDeviceID.setClickable(true);
        etDevicePwd.setClickable(true);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }
    @Override
    public void updateVersoin(UpdateMessage updateMessage) {
        fileName=updateMessage.getFileName();
        versoin=updateMessage.getVersion();
        createTime=updateMessage.getCreateTime();
        remark=updateMessage.getRemark();
        url=updateMessage.getUrl();
    }
    @Override
    public void onPermissionError(ApiException e) {
        this.showToast(e.getDisplayMessage(), Toast.LENGTH_LONG);
    }
    @Override
    public void onError(ApiException e) {
        this.showToast(e.getDisplayMessage());
    }
    @Override
    public void onResultError(ApiException e) {
    }
    @Override
    public void onLoginSuccess() {
        Intent intent = new Intent(LoginActivity.this, NewMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }
    @Override
    protected void onResume() {
        super.onResume();
        etDevicePwd.getText().clear();
        Logger.e("LoginActivity======onResume");
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onStop() {
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
    }
    @Override
    protected void onDestroy() {
//        this.presenter.detachView();
        super.onDestroy();
        this.etDevicePwd.setText("");
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }
    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }
    @OnClick({R.id.login, R.id.deviceID, R.id.devicePwd, R.id.versoin_tv,R.id.service_tv})
    public void onClick(View view) {
        Animation shakeAnim = AnimationUtils.loadAnimation(LoginActivity.this,
                R.anim.shake_xx);
        switch (view.getId()) {
            case R.id.login:
            this.presenter = new LoginTaskContract();
            this.presenter.attachView(this);
                String deviceId = etDeviceID.getText().toString().trim();
                String devicecode = etDevicePwd.getText().toString().trim();
                etDeviceID.setError(null);
                etDevicePwd.setError(null);
                View focusView = null;
                boolean cancel = false;
                if (TextUtils.isEmpty(deviceId)) {
                    error_ID.setText("请输入设备ID");
                    error_ID.startAnimation(shakeAnim);
                    focusView = etDeviceID;
                    cancel = true;
                } else if (TextUtils.isEmpty(devicecode)) {
                    error_PWD.setText("请输入密码！！！");
                    error_PWD.startAnimation(shakeAnim);
                    focusView = etDevicePwd;
                    cancel = true;
                } else {
                    if (!devicecode.equals("888888")) {
                        error_PWD.setText("密码错误！！！");
                        error_PWD.startAnimation(shakeAnim);
                        mediaPlayer.start();
                        focusView = etDevicePwd;
                        cancel = true;
                    }
                }
                if (cancel) {
                    focusView.requestFocus();
                } else {
                    SharedPreferences userInfo = this.getSharedPreferences("user_info", 0);
                    userInfo.edit().putString("DeviceID", etDeviceID.getText().toString()).commit();
                    SharedPreferences userInfo1 = this.getSharedPreferences("user_info", 1);
                    userInfo1.edit().putString("DevicePWD", etDeviceID.getText().toString()).commit();
                    this.presenter.login(deviceId, devicecode);
                }
                break;
            case R.id.tracerout_rootview:
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                Toast.makeText(LoginActivity.this, "=========", Toast.LENGTH_LONG).show();
                break;
            case R.id.deviceID:
                error_ID.setText("");
                break;
            case R.id.devicePwd:
                error_PWD.setText("");
                break;
            case R.id.service_tv:
//                ShowChoise();
                break;
            case R.id.versoin_tv:

                versoinUpdateContract.deviceUpgrade(etDeviceID.getText().toString());
                dialog1();
                break;
        }
    }
private void ShowChoise() {

    final String items[] = {"正式环境: http://39.108.13.171:8082/vein-api/","测试环境: http://120.24.169.32:8082/vein-api/","旧版本地址:http://biocloud.wedonetech.com/vein-api/"};
    AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle("选择服务器地址")//设置对话框的标题
            .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                case 0:
                    Constant.REST_API_URL = "http://39.108.13.171:8082/vein-api/";
                    android.util.Log.i("ip", Constant.REST_API_URL);
                    break;
                case 1:
                    Constant.REST_API_URL = "http://120.24.169.32:8082/vein-api/";
                    android.util.Log.i("ip", Constant.REST_API_URL);
                    break;
                case 2:
                     Constant.REST_API_URL = "http://biocloud.wedonetech.com/vein-api/";
                     android.util.Log.i("ip", Constant.REST_API_URL);
                     break;
               }
             }
          })
            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            })
            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create();
    dialog.show();
}
    //软件更新dialog
    private void dialog1(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);  //先得到构造器
        builder.setTitle("软件版本更新"); //设置标题
        builder.setMessage(fileName+ '\n' +versoin+'\n'+
                remark+'\n'+createTime+'\n'); //设置内容
//        builder.setMessage( "设置内容"+fileName); //设置内容
        builder.setIcon(R.drawable.app_icon);//设置图标，图片id即可
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                update();
                Toast.makeText(LoginActivity.this, "确认" + which, Toast.LENGTH_SHORT).show();
                dialog.dismiss(); //关闭dialog
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(LoginActivity.this, "取消" + which, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNeutralButton("忽略", new DialogInterface.OnClickListener() {//设置忽略按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(LoginActivity.this, "忽略" + which, Toast.LENGTH_SHORT).show();
            }
        });
        //参数都设置完成了，创建并显示出来
        builder.create().show();
    }
    public void update(){
        VersionUpdateConfig.getInstance()//获取配置实例
                .setContext(LoginActivity.this)//设置上下文
                .setDownLoadURL(url)//设置文件下载链接
                .setNotificationIconRes(R.drawable.app_icon)//设置通知大图标
                .setNotificationSmallIconRes(R.drawable.app_icon_small)//设置通知小图标
                .setNotificationTitle("版本升级")//设置通知标题
                .startDownLoad();//开始下载
    }
    public void showToast(String msg) {
        this.showToast(msg, Toast.LENGTH_LONG);
    }

    public void showToast(String msg, int duration) {
        if (msg == null) return;
        if (duration == Toast.LENGTH_SHORT || duration == Toast.LENGTH_LONG) {
            ToastUtils.show(LoginActivity.this, msg, duration);
        } else {
            ToastUtils.show(LoginActivity.this, msg, ToastUtils.LENGTH_SHORT);
        }
    }
    @Override
    public void onBackPressed() {
        // super.onBackPressed();
    }
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Login Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }
}
