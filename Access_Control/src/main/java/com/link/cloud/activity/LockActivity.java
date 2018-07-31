package com.link.cloud.activity;
import android.app.Activity;
import android.app.ActivityManager;
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
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.cloopen.rest.rest.sdk.utils.encoder.BASE64Decoder;

import com.hotelmanager.xzy.util.HotelUtil;
import com.hotelmanager.xzy.util.OpenDoorUtil;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.util.ResourceUtil;
import com.link.cloud.BaseApplication;
import com.link.cloud.R;
import com.link.cloud.base.ApiException;
import com.link.cloud.bean.Code_Message;
import com.link.cloud.bean.DownLoadData;
import com.link.cloud.bean.Lockdata;
import com.link.cloud.bean.PagesInfoBean;
import com.link.cloud.bean.PushMessage;
import com.link.cloud.bean.RestResponse;
import com.link.cloud.bean.Sign_data;
import com.link.cloud.bean.SyncFeaturesPage;
import com.link.cloud.bean.UpDateBean;
import com.link.cloud.component.EditTextChangeListener;
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
import com.link.cloud.setting.TtsSettings;
import com.link.cloud.utils.APKVersionCodeUtils;
import com.link.cloud.utils.FileUtils;
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
public class LockActivity extends BaseAppCompatActivity implements IsopenCabinet.isopen,SyncUserFeature.syncUser,SendLogMessageTastContract.sendLog,DownloadFeature.download{
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
    String gpiotext="";
    String TAG="LockActivity";
    private UsbDeviceConnection usbDevConn;
    ExitAlertDialog exitAlertDialog;
    // 语音合成对象
    public SpeechSynthesizer mTts;
    // 默认本地发音人
    public static String voicerLocal="xiaoyan";
    // 本地发音人列表
    private String[] localVoicersEntries;
    private String[] localVoicersValue ;
    // 云端/本地选择按钮
    private RadioGroup mRadioGroup;
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    DownloadFeature downloadFeature;
    private Toast mToast;
    private SharedPreferences mSharedPreferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        personDao= BaseApplication.getInstances().getDaoSession().getPersonDao();
        baseApplication=(BaseApplication)getApplication();
        EventBus.getDefault().register(this);
        // 初始化合成对象
        mTts = SpeechSynthesizer.createSynthesizer(this, mTtsInitListener);
        mSharedPreferences = getSharedPreferences(TtsSettings.PREFER_NAME, Activity.MODE_PRIVATE);
        sendLogMessageTastContract=new SendLogMessageTastContract();
        sendLogMessageTastContract.attachView(this);
//        TTSUtils.getInstance().init(this);
        exitAlertDialog=new ExitAlertDialog(this);
        exitAlertDialog.setCanceledOnTouchOutside(false);
        exitAlertDialog.setCancelable(false);
        BaseApplication.setMainActivity(this);
        WorkService.setActactivity(this);
        downloadFeature=new DownloadFeature();
        downloadFeature.attachView(this);
        setupExtra();
        sendLogMessageTastContract=new SendLogMessageTastContract();
        sendLogMessageTastContract.attachView(this);
        setParam();
        mEngineType =  SpeechConstant.TYPE_LOCAL;
        mTts.startSpeaking("初始化成功", mTtsListener);
    }
    /**
     * 初始化监听。
     */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(TAG, "InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip(getResources().getString(R.string.mTts_stating_error)+code);
            } else {
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
            }
        }
    };
    /**
     * 参数设置
     * @return
     */
    public  void setParam(){
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
            //设置使用本地引擎
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            //设置发音人资源路径
            mTts.setParameter(ResourceUtil.TTS_RES_PATH,getResourcePath());
            //设置发音人
            mTts.setParameter(SpeechConstant.VOICE_NAME,voicerLocal);
        //设置合成语速
        mTts.setParameter(SpeechConstant.SPEED, mSharedPreferences.getString("speed_preference", "50"));
        //设置合成音调
        mTts.setParameter(SpeechConstant.PITCH, mSharedPreferences.getString("pitch_preference", "50"));
        //设置合成音量
        mTts.setParameter(SpeechConstant.VOLUME, mSharedPreferences.getString("volume_preference", "50"));
        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, mSharedPreferences.getString("stream_preference", "3"));

        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/tts.wav");
    }
    //获取发音人资源路径
    private String getResourcePath(){
        StringBuffer tempBuffer = new StringBuffer();
        //合成通用资源
        tempBuffer.append(ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, "tts/common.jet"));
        tempBuffer.append(";");
        //发音人资源
        tempBuffer.append(ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, "tts/"+LockActivity.voicerLocal+".jet"));
        return tempBuffer.toString();
    }
    public void showTip(final String str){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mToast.setText(str);
                mToast.show();
            }
        });
    }
    private void setupExtra() {
        Intent intent=new Intent(this,WorkService.class);
        if(!bindService(intent,mdSrvConn, Service.BIND_AUTO_CREATE)){
            handler.removeCallbacksAndMessages(null);
            finish();
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
    EditText code_mumber;
    @Override
    protected void initData() {
        TextView textView=findView(R.id.versionName);
        textView.setText( APKVersionCodeUtils.getVerName(this));
        code_mumber=(EditText) findViewById(R.id.code_mumber1);
        code_mumber.setFocusable(true);
        code_mumber.setCursorVisible(true);
        code_mumber.setFocusableInTouchMode(true);
        code_mumber.requestFocus();
        /**
          * EditText编辑框内容发生变化时的监听回调
          */
        code_mumber.addTextChangedListener(new EditTextChangeListener());
    }
    public class EditTextChangeListener implements TextWatcher {
        long lastTime;
        /**
         * 编辑框的内容发生改变之前的回调方法
         */
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            Logger.e("MyEditTextChangeListener"+"beforeTextChanged---" + charSequence.toString());
        }
        /**
         * 编辑框的内容正在发生改变时的回调方法 >>用户正在输入
         * 我们可以在这里实时地 通过搜索匹配用户的输入
         */
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            Logger.e("MyEditTextChangeListener"+"onTextChanged---" + charSequence.toString());
        }
        /**
         * 编辑框的内容改变以后,用户没有继续输入时 的回调方法
         */
        @Override
        public void afterTextChanged(Editable editable) {
            String str=code_mumber.getText().toString();
//            Logger.e("MyEditTextChangeListener"+ "afterTextChanged---"+code_mumber.getText().toString());
            if (str.contains("\n")) {
                    if(System.currentTimeMillis()-lastTime<1500){
                        code_mumber.setText("");
                        return;
                    }
                    lastTime=System.currentTimeMillis();
                    userinfo = getSharedPreferences("user_info", MODE_MULTI_PROCESS);
                    deviceId = userinfo.getString("deviceId", "");
                    connectivityManager =(ConnectivityManager)LockActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);//获取当前网络的连接服务
                    NetworkInfo info =connectivityManager.getActiveNetworkInfo(); //获取活动的网络连接信息
                    if (info != null) {   //当前没有已激活的网络连接（表示用户关闭了数据流量服务，也没有开启WiFi等别的数据服务）
                        isopenCabinet.memberCode(deviceId, code_mumber.getText().toString());
                 }else {
                        mTts.startSpeaking(getResources().getString(R.string.network_error),mTtsListener);
                    }
                    code_mumber.setText("");
            }
        }
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
                    long lasttime=System.currentTimeMillis();
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
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    userUid = Finger_identify.Finger_identify(LockActivity.this, img);
                    istext=true;
                    if (userUid!=null){
                        bRun=false;
                        isopen=0;
                        EventBus.getDefault().post(new MessageEvent(1,getResources().getString(R.string.check_successful)));
                    }else {
                        isopen=0;
                        bRun=false;
                        EventBus.getDefault().post(new MessageEvent(0,getResources().getString(R.string.check_failed)));

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
    long starttime,lasttime;
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageEventBus(MessageEvent event){
//        Logger.e("FirstFragment"+"========messageEventBus+type="+event.type+"isopen=="+isopen);
        if (event.type==1&&isopen<1) {
            userinfo=getSharedPreferences("user_info",MODE_MULTI_PROCESS);
            deviceId=userinfo.getString("deviceId","");
            String gpio=userinfo.getString("gpiotext",null);
            if (gpio==null){
                userinfo.edit().putString("gpiotext","1067").commit();
            }
            gpiotext=userinfo.getString(gpiotext,"");
            Gpio.gpioInt(gpiotext);
            Gpio.set(gpiotext,48);
            text_error.setText(R.string.check_successful);
             starttime=System.currentTimeMillis();
                connectivityManager =(ConnectivityManager)LockActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);//获取当前网络的连接服务
                NetworkInfo info =connectivityManager.getActiveNetworkInfo(); //获取活动的网络连接信息
                if (info != null) {   //当前没有已激活的网络连接（表示用户关闭了数据流量服务，也没有开启WiFi等别的数据服务）
                    if (starttime-lasttime>500) {
                        isopenCabinet.isopen(deviceId, userUid, "vein");
                    lasttime = System.currentTimeMillis();
                    }
                }else {
                    mTts.startSpeaking(getResources().getString(R.string.network_error),mTtsListener);
                }

        }else if (event.type==0&&isopen<1){
            isopen=0;
            if(istext){
//                TTSUtils.getInstance().speak("验证失败");
            }
            mTts.startSpeaking(getResources().getString(R.string.check_failed),mTtsListener);
            if(handler!=null){
                handler.sendEmptyMessageDelayed(10,1000);
            }
            text_error.setText(R.string.check_failed);
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
    long start=0,end=0;
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    text_error.setText(R.string.finger_right);
                    break;
                case 1:
//                    userinfo=getSharedPreferences("user_info",MODE_MULTI_PROCESS);
//                    String gpio=userinfo.getString("gpiotext",null);
//                    deviceId=userinfo.getString("deviceId","");
//                    if (gpio==null){
//                        userinfo.edit().putString("gpiotext","1067").commit();
//                    }
//                    gpiotext=userinfo.getString(gpiotext,"");
//                    Gpio.gpioInt(gpiotext);
//                    Gpio.set(gpiotext,48);
                    text_error.setText(R.string.check_successful);

                    connectivityManager =(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);//获取当前网络的连接服务
                    NetworkInfo info =connectivityManager.getActiveNetworkInfo(); //获取活动的网络连接信息
                    if (info != null) {   //当前没有已激活的网络连接（表示用户关闭了数据流量服务，也没有开启WiFi等别的数据服务）
                         start=System.currentTimeMillis();
                        if (start-end>2000) {
                            isopenCabinet.isopen(deviceId, userUid, "vein");
                            end=System.currentTimeMillis();
                        }
                    }else {
                        mTts.startSpeaking(getResources().getString(R.string.network_error),mTtsListener);
                    }
                    break;
                case 7:
//                    TTSUtils.getInstance().speak("验证失败");
                    mTts.startSpeaking(getResources().getString(R.string.check_failed), mTtsListener);
                    text_error.setText(R.string.check_failed);
                    break;
                case 8:
                    text_error.setText(R.string.move_finger);
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
//                        Logger.e(usbDevice.getManufacturerName()+"  节点："+usbDevice.getDeviceName());
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
                    removeMessages(10);
                    bRun=true;
                    mdWorkThread.start();
                    break;
            }
        }
    };
    ConnectivityManager connectivityManager;
    @OnClick({ R.id.button02, R.id.button1, R.id.button2, R.id.button3, R.id.button4,R.id.button5, R.id.head_text_02})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button02:
                userinfo=getSharedPreferences("user_info",0);
                userinfo.edit().putString("gpiotext","1067").commit();
                gpiostr=userinfo.getString("gpiotext","");
                Gpio.gpioInt(gpiostr);
                Toast.makeText(LockActivity.this, getResources().getString(R.string.configure_io), Toast.LENGTH_SHORT).show();
                break;
            case R.id.button1:
                Gpio.set(gpiostr, 49);
                Toast.makeText(LockActivity.this, getResources().getString(R.string.set_hight), Toast.LENGTH_SHORT).show();
                break;
            case R.id.button2:
                Gpio.set(gpiostr, 48);
                Toast.makeText(LockActivity.this, getResources().getString(R.string.set_low), Toast.LENGTH_SHORT).show();
                break;
            case R.id.button3:
                textView1.setText(Gpio.get(gpiostr)+"");
                Toast.makeText(LockActivity.this, getResources().getString(R.string.set_stating), Toast.LENGTH_SHORT).show();
                break;
            case R.id.button4:
                deviceId=userinfo.getString("deviceId","");
                textView2.setText(deviceId);
                break;
            case R.id.head_text_02:
                connectivityManager =(ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);//获取当前网络的连接服务
                NetworkInfo info =connectivityManager.getActiveNetworkInfo(); //获取活动的网络连接信息
                if (info != null) {   //当前没有已激活的网络连接（表示用户关闭了数据流量服务，也没有开启WiFi等别的数据服务）
                    exitAlertDialog.show();
                    deviceId=userinfo.getString("deviceId","");
//                    downloadFeature.getPagesInfo(deviceId);
                    syncUserFeature.syncUser(deviceId);
                    syncUserFeature.syncSign(deviceId);
                }else {
                    mTts.startSpeaking(getResources().getString(R.string.network_error),mTtsListener);
                    Toast.makeText(this, getResources().getString(R.string.network_error), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.button5:
                Intent intent = new Intent();
                // 为Intent设置Action、Category属性
                intent.setAction(Intent.ACTION_MAIN);// "android.intent.action.MAIN"
                intent.addCategory(Intent.CATEGORY_HOME); //"android.intent.category.HOME"
                startActivity(intent);
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
    public void qrCodeSuccess(Code_Message code_message) {
//        TTSUtils.getInstance().speak("验证成功");
        mTts.startSpeaking(getResources().getString(R.string.successful_open),mTtsListener);
        SharedPreferences sharedPreferences=getSharedPreferences("user_info",0);
        gpiostr=sharedPreferences.getString("gpiotext","");
        Logger.e("LockAcitvity"+"==========="+gpiostr);
        try {
            Gpio.gpioInt(gpiostr);
            Thread.sleep(400);
            Gpio.set(gpiostr,48);
//            TTSUtils.getInstance().speak("门已开");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Gpio.set(gpiostr,49);
        mdWorkThread=null;
        startupParam();
//        if(handler!=null){
//            handler.sendEmptyMessageDelayed(10,1000);
//        }

    }

    @Override
    public void syncSignUserSuccess(Sign_data downLoadData) {
    SignUserDao signUserDao=BaseApplication.getInstances().getDaoSession().getSignUserDao();
    if (downLoadData.getData().size()>0){
        signUserDao.deleteAll();
        signUserDao.insertInTx(downLoadData.getData());
    }
    }
    @Override
    public void syncUserSuccess(DownLoadData resultResponse) {
        personDao = BaseApplication.getInstances().getDaoSession().getPersonDao();
        Person person = new Person();
        if (resultResponse.getData().size() > 0) {
            personDao.deleteAll();
            personDao.insertInTx(resultResponse.getData());
            Toast.makeText(LockActivity.this, getResources().getString(R.string.syn_data), Toast.LENGTH_SHORT).show();
        }
        mTts.startSpeaking(getResources().getString(R.string.syn_data),mTtsListener);
        exitAlertDialog.dismiss();
    }

    @Override
    public void downloadNotReceiver(DownLoadData resultResponse) {

    }

    @Override
    public void downloadApK(UpDateBean resultResponse) {

    }

    @Override
    public void downloadSuccess(DownLoadData resultResponse) {

    }

    ArrayList<Person> SyncFeaturesPages = new ArrayList<>();
    int totalPage=0,currentPage=0,downloadPage=0;
    @Override
    public void getPagesInfo(PagesInfoBean resultResponse) {
        if (resultResponse.getData().getCount()>0) {
            totalPage = resultResponse.getData().getPageCount();
            for (int x = 0; x < 8; x++) {
                if (x > totalPage - 1) {
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        currentPage++;
                        Logger.e(currentPage + "currentPage");
                        downloadFeature.syncUserFeaturePages(FileUtils.loadDataFromFile(LockActivity.this, "deviceId.text"), currentPage);
                    }
                }).start();
            }
        }else {
            exitAlertDialog.dismiss();
        }
    }

    @Override
    public void syncUserFeaturePagesSuccess(SyncFeaturesPage resultResponse) {
        if (resultResponse.getData().size()>0) {
            downloadPage++;
            Logger.e(downloadPage + "downloadPage");
            if (totalPage > 8 && currentPage < totalPage) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        currentPage++;
                        Logger.e(currentPage + "currentPage");
                        downloadFeature.syncUserFeaturePages(FileUtils.loadDataFromFile(LockActivity.this, "deviceId.text"), currentPage);
                    }
                }).start();
            }
            SyncFeaturesPages.addAll(resultResponse.getData());
            if (downloadPage == totalPage) {
                PersonDao personDao = BaseApplication.getInstances().getDaoSession().getPersonDao();
                personDao.insertInTx(resultResponse.getData());
                Logger.e(SyncFeaturesPages.size() + getResources().getString(R.string.syn_data));
                NetworkInfo info = connectivityManager.getActiveNetworkInfo(); //获取活动的网络连接信息
                if (info != null) {   //当前没有已激活的网络连接（表示用户关闭了数据流量服务，也没有开启WiFi等别的数据服务）
                    downloadFeature.appUpdateInfo(FileUtils.loadDataFromFile(LockActivity.this, "deviceId.text"));
                } else {
                    mTts.startSpeaking(getResources().getString(R.string.network_error),mTtsListener);
                    Toast.makeText(LockActivity.this, getResources().getString(R.string.network_error), Toast.LENGTH_LONG).show();
                }
                exitAlertDialog.dismiss();
            }
        }else {
            exitAlertDialog.dismiss();
        }
    }

    @Override
    protected void onResume() {
        Logger.e("resume");
        userinfo=getSharedPreferences("user_info",MODE_MULTI_PROCESS);
        String gpio=userinfo.getString("gpiotext",null);
        deviceId=userinfo.getString("deviceId","");
        if (gpio==null){
            userinfo.edit().putString("gpiotext","1067").commit();
        }
        gpiotext=userinfo.getString(gpiotext,"");
        Gpio.gpioInt(gpiotext);
        Gpio.set(gpiotext,48);
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
        mTts.startSpeaking(getResources().getString(R.string.successful_open), mTtsListener);
        SharedPreferences sharedPreferences=getSharedPreferences("user_info",0);
        gpiostr=sharedPreferences.getString("gpiotext","");
        Logger.e("LockAcitvity"+"==========="+gpiostr);
        try {
            Gpio.gpioInt(gpiostr);
            Thread.sleep(400);
            Gpio.set(gpiostr,48);
//            TTSUtils.getInstance().speak("门已开");
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
        mTts.startSpeaking(syt,mTtsListener);
        if(handler!=null){
            handler.sendEmptyMessageDelayed(10,1000);
        }
    }
    /**
     * 合成回调监听。
     */
    public SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
        }
        @Override
        public void onSpeakPaused() {
        }
        @Override
        public void onSpeakResumed() {
        }
        @Override
        public void onSpeakProgress(int i, int i1, int i2) {
        }
        @Override
        public void onCompleted(SpeechError speechError) {
        }
        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {
        }
        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
        }
    };
    @Override
    public void sendLogSuccess(RestResponse resultResponse) {
    }
    private WorkService.UsbMsgCallback mdUsbMsgCallback=new WorkService.UsbMsgCallback(){
        @Override
        public void onUsbConnSuccess(String usbManufacturerName, String usbDeviceName) {
//            String newUsbInfo="USB厂商："+usbManufacturerName+"  \nUSB节点："+usbDeviceName;
//            handler.obtainMessage(MSG_SHOW_LOG,newUsbInfo).sendToTarget();
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
//            handler.obtainMessage(MSG_SHOW_LOG,"md usb device connection ok.").sendToTarget();
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
        unregisterReceiver(mesReceiver);
        Logger.e("LockActivity"+"onDestroy");
//        TTSUtils.getInstance().release();
        if(usbDevConn==null){
        }else{
            usbDevConn.close();
        }
        if( null != mTts ){
            mTts.stopSpeaking();
            // 退出时释放连接
            mTts.destroy();
        }
        EventBus.getDefault().unregister(this);
        unbindService(mdSrvConn);
        super.onDestroy();

    }
}
