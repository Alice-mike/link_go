package com.link.cloud.activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.TtsMode;
import com.cloopen.rest.rest.sdk.utils.encoder.BASE64Decoder;
import com.example.TTSUtils;
import com.hotelmanager.xzy.util.HotelUtil;
import com.hotelmanager.xzy.util.OpenDoorUtil;
import com.link.cloud.BaseApplication;
import com.link.cloud.R;
import com.link.cloud.base.ApiException;
import com.link.cloud.bean.DownLoadData;
import com.link.cloud.bean.Lockdata;
import com.link.cloud.bean.PushMessage;
import com.link.cloud.bean.RestResponse;
import com.link.cloud.contract.AdminopenCabinet;
import com.link.cloud.contract.ClearCabinetContract;
import com.link.cloud.contract.DownloadFeature;
import com.link.cloud.contract.IsopenCabinet;
import com.link.cloud.contract.SendLogMessageTastContract;
import com.link.cloud.contract.SyncUserFeature;
import com.link.cloud.core.BaseAppCompatActivity;
import com.link.cloud.gpiotest.Gpio;
import com.link.cloud.greendao.gen.PersonDao;
import com.link.cloud.greendao.gen.SignUserDao;
import com.link.cloud.greendaodemo.Person;
import com.link.cloud.greendaodemo.SignUser;
import com.link.cloud.message.MessageEvent;
import com.link.cloud.model.MdFvHelper;
import com.link.cloud.utils.APKVersionCodeUtils;
import com.link.cloud.utils.Finger_identify;
import com.link.cloud.view.ExitAlertDialog;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import butterknife.Bind;
import butterknife.OnClick;
import md.com.sdk.MicroFingerVein;

import static com.alibaba.sdk.android.ams.common.util.HexUtil.hexStringToByte;
/**
 * Created by 30541 on 2018/3/12.
 */
public class LockActivity extends BaseAppCompatActivity implements IsopenCabinet.isopen,SyncUserFeature.syncUser,SendLogMessageTastContract.sendLog{
    @Bind(R.id.head_text_01)
    TextView head_text_01;
    @Bind(R.id.head_text_02)
    TextView head_text_02;
    @Bind(R.id.head_text_03)
    TextView head_text_03;
    @Bind(R.id.button02)
    Button button02;
    @Bind(R.id.button1)
    Button button1;
    @Bind(R.id.button2)
    Button button2;
    @Bind(R.id.button3)
    Button button3;
    @Bind(R.id.button4)
    Button button4;
    @Bind(R.id.textView1)
    TextView textView1;
    @Bind(R.id.textView2)
    TextView textView2;
    @Bind(R.id.text_error)
    TextView text_error;
    IsopenCabinet isopenCabinet;
    private ArrayList<Fragment> mFragmentList = new ArrayList<Fragment>();
    int state =0;
    boolean ret = false;
    private PersonDao personDao;
    SyncUserFeature syncUserFeature;
    String deviceId;
    ConnectivityManager connectivityManager;
  public MesReceiver mesReceiver;
    private final static int MSG_SHOW_LOG=0;
    BaseApplication baseApplication;
   public  MicroFingerVein microFingerVein;
    String gpiostr;
    String userUid;
   public SendLogMessageTastContract sendLogMessageTastContract;
    public static final String ACTION_UPDATEUI = "com.link.cloud.dataTime";
    private static String ACTION_USB_PERMISSION = "com.android.USB_PERMISSION";
    private final static float IDENTIFY_SCORE_THRESHOLD=0.63f;//认证通过的得分阈值，超过此得分才认为认证通过；
    SharedPreferences userinfo;
    ExitAlertDialog exitAlertDialog;
    String gpiotext="";
    String TAG="LockActivity";
    private UsbDeviceConnection usbDevConn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        personDao= BaseApplication.getInstances().getDaoSession().getPersonDao();
        baseApplication=(BaseApplication)getApplication();
        EventBus.getDefault().register(this);
        sendLogMessageTastContract=new SendLogMessageTastContract();
        sendLogMessageTastContract.attachView(this);
        TTSUtils.getInstance().init(this);
        BaseApplication.setMainActivity(this);
        WorkService.setActactivity(this);
        exitAlertDialog=new ExitAlertDialog(this);
        exitAlertDialog.setCanceledOnTouchOutside(false);
        exitAlertDialog.setCancelable(false);
        baseApplication.setDownLoadListner(new BaseApplication.downloafinish() {
            @Override
            public void finish() {
                exitAlertDialog.dismiss();
            }
            @Override
            public void start() {
            }
        });
        setupExtra();
        sendLogMessageTastContract=new SendLogMessageTastContract();
        sendLogMessageTastContract.attachView(this);
    }
    private void setupExtra() {
        Intent intent=new Intent(this,WorkService.class);
        if(!bindService(intent,mdSrvConn, Service.BIND_AUTO_CREATE)){
            handler.removeCallbacksAndMessages(null);
            finish();
        }
        connectivityManager =(ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);//获取当前网络的连接服务
        NetworkInfo info =connectivityManager.getActiveNetworkInfo(); //获取活动的网络连接信息
        if (info != null) {
            exitAlertDialog.show();
        }
    }
    private ServiceConnection mdSrvConn=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            WorkService.MyBinder myBinder=(WorkService.MyBinder)service;
            if(myBinder!=null){
                microFingerVein=myBinder.getMicroFingerVeinInstance();
                myBinder.setOnUsbMsgCallback(mdUsbMsgCallback);
                startupParam();
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };
    @Override
    protected void initData() {
        TextView textView=findView(R.id.versionName);
        textView.setText( APKVersionCodeUtils.getVerName(this));
    }
    boolean bopen=false;
    boolean bRun=false;
    private Thread mdWorkThread=null;
    private void startupParam() {
        Logger.e("LockActivity"+"====startupParam===");
        mdWorkThread=null;
            bRun = true;
            mdWorkThread = new Thread(runnable);
            mdWorkThread.start();
    }
    boolean istext=false;
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            boolean ret = false;
            int[] tipTimes = {0, 0};//后两次次建模时用了不同手指，重复提醒限制3次
            int modOkProgress = 0;
            while (bRun) {
                if(!bopen&&microFingerVein!=null) {
                    int cnt = microFingerVein.fvdev_get_count();
                    if(cnt == 0) continue;
                    bopen = microFingerVein.fvdev_open();//开启指定索引的设备
                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                state = microFingerVein.fvdev_get_state();
                //设备连接正常则进入正常建模或认证流程
                if (state != 0&&istext==false) {
                    Logger.e("FirstFragment===========state" + state);
                    if (state == 1 || state == 2) {
                        continue;
                    } else if (state == 3) {

                    }

                    byte[] img= MdFvHelper.tryGetFirstBestImg(microFingerVein,0,5);
                    Logger.e("FirstFragment===========img" + img);
                    if (img == null) {
                        continue;
                    }

                   userUid=Finger_identify.Finger_identify(LockActivity.this,img);

                    istext=true;
                    if (userUid!=null){
                        bRun=false;
                        isopen=0;
                        EventBus.getDefault().post(new MessageEvent(1,"验证成功"));
                    }else {
                        isopen=0;
                        bRun=false;
                        EventBus.getDefault().post(new MessageEvent(0,"验证失败"));
                    }
                }
                else {
                    istext=false;
                    if (handler != null) {
                        Message message = new Message();
                        message.what = 0;
                        handler.sendMessage(message);
                    }
                }
            }
        }
    };
    int isopen=0;
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageEventBus(MessageEvent event){
        Logger.e("FirstFragment"+"========messageEventBus+type="+event.type+"isopen=="+isopen);
        if (event.type==1&&isopen<1) {
            userinfo=getSharedPreferences("user_info",0);
            String gpio=userinfo.getString("gpiotext",null);
            deviceId=userinfo.getString("deviceId","");
            if (gpio==null){
                userinfo.edit().putString("gpiotext","1067").commit();
            }
            gpiotext=userinfo.getString(gpiotext,"");
            Gpio.gpioInt(gpiotext);
            Gpio.set(gpiotext,48);
            text_error.setText("验证成功");
            isopenCabinet.isopen(deviceId,userUid,"vein");
        }else if (event.type==0&&isopen<1){
            isopen=0;
            if(istext){
                TTSUtils.getInstance().speak("验证失败");
            }

            if(handler!=null){
                handler.sendEmptyMessageDelayed(10,1000);
            }
            text_error.setText("验证失败...");
        }
        isopen++;
    }
    //认证一个手指模板,当比对成功且得分大于自定义认证阈值时返回true，否则返回false;

    @Override
    protected void initViews(Bundle savedInstanceState) {
    }
    @Override
    protected void initListeners() {
    }
    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }
    @Override
    protected void initToolbar(Bundle savedInstanceState) {
    }
    int openType;
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    text_error.setText("请正确放置手指...");
                    break;
                case 1:
                    userinfo=getSharedPreferences("user_info",0);
                    String gpio=userinfo.getString("gpiotext",null);
                    deviceId=userinfo.getString("deviceId","");
                    if (gpio==null){
                        userinfo.edit().putString("gpiotext","1067").commit();
                    }
                    gpiotext=userinfo.getString(gpiotext,"");
                    Gpio.gpioInt(gpiotext);
                    Gpio.set(gpiotext,48);
                    TTSUtils.getInstance().speak("验证成功");
                    text_error.setText("验证成功");
                   isopenCabinet.isopen(deviceId,userUid,"vein");
                    break;
                case 7:
                    TTSUtils.getInstance().speak("验证失败");
                    text_error.setText("验证失败...");
                    break;
                case 8:
                    text_error.setText("请移开手指");
                    break;
                case MicroFingerVein.USB_HAS_REQUST_PERMISSION:
                {
                    UsbDevice usbDevice=(UsbDevice) msg.obj;
                    UsbManager mManager=(UsbManager)getSystemService(Context.USB_SERVICE);
                    PendingIntent mPermissionIntent = PendingIntent.getBroadcast(LockActivity.this, 0, new Intent(ACTION_USB_PERMISSION), 0);
                    if(mManager == null)
                    {
                        mManager=(UsbManager)getSystemService(Context.USB_SERVICE);
                        IntentFilter filter = new IntentFilter();
                    }
                    mManager.requestPermission(usbDevice,mPermissionIntent);
                }
                break;
                case MicroFingerVein.USB_CONNECT_SUCESS: {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        UsbDevice usbDevice=(UsbDevice) msg.obj;
                        Logger.e(usbDevice.getManufacturerName()+"  节点："+usbDevice.getDeviceName());
                    }
                }
                break;
                case MicroFingerVein.USB_DISCONNECT:{
                    Logger.e("NewMAinActivity=========="+ret);
                }
                break;
                case MicroFingerVein.UsbDeviceConnection: {
                    if(msg.obj!=null) {
                        UsbDeviceConnection usbDevConn=(UsbDeviceConnection)msg.obj;
                        if(LockActivity.this.isFinishing()||LockActivity.this.isDestroyed()) {
                            //修复bug:启动activity几十毫秒内用户快速关闭activity，此时尚未收到usbDeviceConnection对象导致usb不能正常关闭
                            usbDevConn.close();
                        }
                    }
                }
                break;
                case 10:
                    bRun=true;
                    mdWorkThread.start();
                    break;
            }
        }
    };
    @OnClick({ R.id.button02, R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.head_text_02})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button02:
                userinfo=getSharedPreferences("user_info",0);
                userinfo.edit().putString("gpiotext","1067").commit();
                gpiostr=userinfo.getString("gpiotext","");
                Gpio.gpioInt(gpiostr);
                Toast.makeText(LockActivity.this, "配置2成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.button1:
                Gpio.set(gpiostr, 49);
                Toast.makeText(LockActivity.this, "设置高电平", Toast.LENGTH_SHORT).show();
                break;
            case R.id.button2:
                Gpio.set(gpiostr, 48);
                Toast.makeText(LockActivity.this, "设置低电平", Toast.LENGTH_SHORT).show();
                break;
            case R.id.button3:
                textView1.setText(Gpio.get(gpiostr)+"");
                Toast.makeText(LockActivity.this, "返回1为当前gpio口为高，返回0为低，返回-1为当前GPIO口不可用", Toast.LENGTH_SHORT).show();
                break;
            case R.id.button4:
                deviceId=userinfo.getString("deviceId","");
                textView2.setText(deviceId);
                break;
            case R.id.head_text_02:
                connectivityManager =(ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);//获取当前网络的连接服务
                NetworkInfo info =connectivityManager.getActiveNetworkInfo(); //获取活动的网络连接信息
                if (info != null) {
                    exitAlertDialog.show();
                    syncUserFeature.syncUser(deviceId);
                }
            break;
            default:
                break;
        }
    }
//    public static String desEncrypt(String datacode) throws Exception {
//        try
//        {
//            String data = "LU8wzgej7Uzw2EGHRJuTT62zQ9kuyVCg4z0S1vg/1VR3cQdilIgnsAYouHksGcDl";
//            String key = "rocketbird!@sjs!";
//            String iv = "kiPqmEVXtZrgaVkf";
//            byte[] encrypted1 = new BASE64Decoder().decodeBuffer(datacode);
//            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
//            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
//            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
//            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
//            byte[] original = cipher.doFinal(encrypted1);
//            String originalString = new String(original);
//            return originalString;
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
    @Override
    public void syncSignUserSuccess(DownLoadData downLoadData) {
        Cursor cursor=null;
        String sql;
        sql = "select UID from PERSON";
        String [] Uids=new String[cursor.getCount()];
        int i=0;
        String [] downUid=new String[downLoadData.getDown_userInfo().length];
        cursor = BaseApplication.getInstances().getDaoSession().getDatabase().rawQuery(sql,null);
        while (cursor.moveToNext()){
            Uids[i]=cursor.getString(cursor.getColumnIndex("UID"));
            i++;
        }
        SignUserDao signUserDao=BaseApplication.getInstances().getDaoSession().getSignUserDao();
        if (downLoadData.getDown_userInfo().length>Uids.length){
            for (int j=0;j<downLoadData.getDown_userInfo().length;j++){
                SignUser signPerson =new SignUser();
                signPerson.setId((long) j);
                signPerson.setPos(j+"");
                signPerson.setUserId(downLoadData.getDown_userInfo()[j].getUid());
                signUserDao.insert(signPerson);
            }
        }
    }
    @Override
    public void syncUserSuccess(DownLoadData resultResponse) {
        personDao = BaseApplication.getInstances().getDaoSession().getPersonDao();
        Person person = new Person();
        if (resultResponse.getDown_userInfo().length > 0) {
            personDao.deleteAll();
            for (int i = 0; i < resultResponse.getDown_userInfo().length; i++) {
                person.setId((long) i);
                person.setUid(resultResponse.getDown_userInfo()[i].getUid());
                person.setNumber(resultResponse.getDown_userInfo()[i].getUserName());
                person.setPos(i+"");
                person.setFingermodel(resultResponse.getDown_userInfo()[i].getFeature());
                personDao.insert(person);
            }
            Toast.makeText(LockActivity.this, "数据同步成功", Toast.LENGTH_SHORT).show();
        }
        exitAlertDialog.dismiss();
    }
    @Override
    protected void onResume() {
        userinfo=getSharedPreferences("user_info",0);
        deviceId=userinfo.getString("deviceId","");
        TTSUtils.getInstance().speak("初始化成功");
        super.onResume();
    }
    @Override
    protected void onStart() {
        super.onStart();
        mesReceiver = new MesReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LockActivity.ACTION_UPDATEUI);
        registerReceiver(mesReceiver, intentFilter);
        isopenCabinet=new IsopenCabinet();
        isopenCabinet.attachView(this);
        syncUserFeature=new SyncUserFeature();
        syncUserFeature.attachView(this);
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    public void isopenSuccess(Lockdata resultResponse) {
        TTSUtils.getInstance().speak("验证成功");
        SharedPreferences sharedPreferences=getSharedPreferences("user_info",0);
        gpiostr=sharedPreferences.getString("gpiotext","");
        Logger.e("LockAcitvity"+"==========="+gpiostr);
        try {
            Gpio.gpioInt(gpiostr);
            Thread.sleep(100);
            Gpio.set(gpiostr,48);
            TTSUtils.getInstance().speak("门已开");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Gpio.set(gpiostr,49);
      if(handler!=null){
          handler.sendEmptyMessageDelayed(10,1000);
      }

    }
    @Override
    public void onError(ApiException e) {
        String reg = "[^\u4e00-\u9fa5]";
        String syt=e.getMessage().replaceAll(reg, "");
        Logger.e("BindActivity"+syt);
        TTSUtils.getInstance().speak(syt);
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                    text_error.setText(syt);
                    try {
                    Thread.sleep(2000);
                    startupParam();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }
    @Override
    public void sendLogSuccess(RestResponse resultResponse) {
    }
    private WorkService.UsbMsgCallback mdUsbMsgCallback=new WorkService.UsbMsgCallback(){
        @Override
        public void onUsbConnSuccess(String usbManufacturerName, String usbDeviceName) {
            String newUsbInfo="USB厂商："+usbManufacturerName+"  \nUSB节点："+usbDeviceName;
            handler.obtainMessage(MSG_SHOW_LOG,newUsbInfo).sendToTarget();
        }
        @Override
        public void onUsbDisconnect() {
            handler.obtainMessage(MSG_SHOW_LOG,"usb disconnected.").sendToTarget();
            if(microFingerVein!=null) {
                microFingerVein.close();
            }
            bopen=false;
        }
        @Override
        public void onUsbDeviceConnection(UsbDeviceConnection usbDevConn) {
            handler.obtainMessage(MSG_SHOW_LOG,"md usb device connection ok.").sendToTarget();
            LockActivity.this.usbDevConn=usbDevConn;
            if(LockActivity.this.isFinishing()||LockActivity.this.isDestroyed()) {
//                Log.e(TAG,"√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√");
                LockActivity.this.usbDevConn.close();
            }
        }
    };
    @Override
    public void onResultError(ApiException e) {
        onError(e);
    }
    @Override
    public void onPermissionError(ApiException e) {
        onError(e);
    }
    /**
     * 广播接收器
     *
     * @author kevin
     */
    public class MesReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            head_text_03.setText(intent.getStringExtra("timeStr"));
            head_text_01.setText(intent.getStringExtra("timeData"));
            if (context == null) {
                context.unregisterReceiver(this);
            }
        }
    }
    @Override
    protected void onDestroy() {
        Logger.e("LockActivity"+"onDestroy");
        TTSUtils.getInstance().release();
        if(usbDevConn==null){
        }else{
            usbDevConn.close();
        }
        EventBus.getDefault().unregister(this);
        unregisterReceiver(mesReceiver);
        unbindService(mdSrvConn);
        super.onDestroy();

    }
}
