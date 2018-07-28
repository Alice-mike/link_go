package com.link.cloud.activity;
import android.app.Activity;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.link.cloud.bean.Lockdata;
import com.link.cloud.bean.PushMessage;
import com.link.cloud.bean.RestResponse;
import com.link.cloud.contract.IsopenCabinet;
import com.link.cloud.contract.SendLogMessageTastContract;
import com.link.cloud.core.BaseAppCompatActivity;
import com.link.cloud.fragment.BindVeinMainFragment;
import com.link.cloud.fragment.FirstFragment;
import com.link.cloud.fragment.MainFragment;
import com.link.cloud.fragment.SecondFragment;
import com.link.cloud.fragment.ThirdFragment;
//import com.link.cloud.greendao.gen.PersonDao;

import com.link.cloud.message.MessageEvent;
import com.link.cloud.model.MdFvHelper;
import com.link.cloud.setting.TtsSettings;
import com.link.cloud.utils.CountDownTimer;
import com.link.cloud.utils.Finger_identify;
import com.link.cloud.view.ExitAlertDialog;
import com.link.cloud.view.LazyViewPager;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android_serialport_api.SerialPort;
import butterknife.Bind;
import butterknife.ButterKnife;
import md.com.sdk.MicroFingerVein;

/**
 * Created by 30541 on 2018/3/12.
 */
public class LockActivity extends BaseAppCompatActivity implements SendLogMessageTastContract.sendLog{
    @Bind(R.id.bing_main_page)
    LazyViewPager viewPager;
    @Bind(R.id.main_bt_01)
    Button button_01;
    @Bind(R.id.main_bt_02)
    Button button_02;
    @Bind(R.id.main_bt_03)
    Button button_03;
    @Bind(R.id.layout_three_main)
    LinearLayout layout_three;
    @Bind(R.id.layout_one)
    LinearLayout layout_one;
    @Bind(R.id.text_number)
    TextView text_number;
    @Bind(R.id.edit_01)
    EditText clearlock;
    @Bind(R.id.edit_02)
    EditText openlock;
    @Bind(R.id.openlock_other)
    Button openlock_other;
    @Bind(R.id.openlock_all)
    Button openlock_all;
    @Bind(R.id.clean_other)
    Button clean_other;
    @Bind(R.id.clean_all)
    Button clean_all;
    @Bind(R.id.adminmessage)
    LinearLayout adminmessage;
    @Bind(R.id.head_text_01)
    TextView head_text_01;
    @Bind(R.id.head_text_02)
    TextView head_text_02;
    @Bind(R.id.head_text_03_main)
    TextView head_text_03;
    PushMessage pushMessage;
    private ArrayList<Fragment> mFragmentList = new ArrayList<Fragment>();
    int[] pos = new int[1];
    boolean flog=true;
    String deviceId,uid;
    MesReceiver mesReceiver;
    OpenDoorUtil openDoorUtil;
    public static boolean isStart=false;
   public BaseApplication baseApplication;
    BindVeinMainFragment bindVeinMainFragment;
    MainFragment mainFragment;
    FirstFragment firstFragment;
    SecondFragment secondFragment;
    ThirdFragment thirdFragment;
    String timedata=null;

    private final static int MSG_SHOW_LOG=0;
    public MicroFingerVein microFingerVein;
    public static final String ACTION_UPDATEUI = "com.link.cloud.updateTiemStr";
    public static final String ACTION_UPDATE = "com.link.cloud.updateTiemData";
    private static String ACTION_USB_PERMISSION = "com.android.USB_PERMISSION";
    Intent intent;
    public SerialPort serialpprt_wk1=null;
    public SerialPort serialpprt_wk2=null;
    public SerialPort serialpprt_wk3=null;
    public boolean bopen=false;
    public volatile boolean bRun=false;
    private UsbDeviceConnection usbDevConn;
    public SendLogMessageTastContract sendLogMessageTastContract;
    // 语音合成对象
    public SpeechSynthesizer mTts;
    // 默认本地发音人
    public static String voicerLocal="xiaoyan";
    // 本地发音人列表
    private String[] localVoicersEntries;
    private String[] localVoicersValue ;
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    private SharedPreferences mSharedPreferences;
    String TAG="LockActivity";
   public  ExitAlertDialog exitAlertDialog;
    int type;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        BaseApplication.setMainActivity(this);
        openDoorUtil=new OpenDoorUtil();
        baseApplication=(BaseApplication) getApplication();
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
//                if(exitAlertDialog.isShowing()){
//                }else {
//                    exitAlertDialog.show();
//                }
            }
        });
        WorkService.setActactivity(this);
        try {
            serialpprt_wk1=new SerialPort(new File("/dev/ttysWK1"),9600,0);
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            serialpprt_wk2=new SerialPort(new File("/dev/ttysWK2"),9600,0);
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            serialpprt_wk3=new SerialPort(new File("/dev/ttysWK3"),9600,0);
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        setupExtra();
        sendLogMessageTastContract=new SendLogMessageTastContract();
        sendLogMessageTastContract.attachView(this);
        // 初始化合成对象
        mTts = SpeechSynthesizer.createSynthesizer(this, mTtsInitListener);
        mSharedPreferences = getSharedPreferences(TtsSettings.PREFER_NAME, Activity.MODE_PRIVATE);

        mEngineType =  SpeechConstant.TYPE_LOCAL;
        mTts.startSpeaking(getResources().getString(R.string.initialization_successful),mTtsListener);
    }
    private void setupExtra() {
        Intent intent=new Intent(this,WorkService.class);
        if(!bindService(intent,mdSrvConn,Service.BIND_AUTO_CREATE)){
            Log.e(TAG,"bind MdUsbService failed,can't get microFingerVein object.");
            handler.removeCallbacksAndMessages(null);
            finish();
        }
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
    Toast mToast;
    public void showTip(final String str){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mToast.setText(str);
                mToast.show();
            }
        });
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
    @Override
    protected void initData() {
    }

   public void setupParam(){
        bRun=true;
        Thread mdWorkThread=new Thread(runnablemol);
        mdWorkThread.start();
    }
    boolean ret = false;
    boolean time_start;
    Runnable  runnablemol=new Runnable() {
        @Override
        public void run() {
            int state=0;
            int[] tipTimes = {0, 0};//后两次次建模时用了不同手指，重复提醒限制3次
            int modOkProgress = 0;
            Logger.e("FirstFragment"+"activity.bRun"+bRun+"activity.bopen"+bopen);
            while (bRun) {
                if(!bopen) {
                Logger.e("FirstFragment"+"activity.bRun"+bRun+"activity.bopen"+bopen);
                modOkProgress++;
                bopen = microFingerVein.fvdev_open();//开启指定索引的设备
                int cnt = microFingerVein.fvdev_get_count();
                if(cnt == 0){
                    continue;
                }
                if (modOkProgress>10){
//                       Utils.showPromptToast(getContext(),"请重启设备再试。。。");
                    bRun=false;
                }
                continue;
            }
                state = microFingerVein.fvdev_get_state();
                //设备连接正常则进入正常建模或认证流程
//                Logger.e("BindActivty===========state"+state);
                if (state != 0) {
                    time_start=false;
                    if (timer!=null) {
                        timer.cancel();
                    }
                    Logger.e("FirstFragment===========state" + state);
                    byte[] img= MdFvHelper.tryGetFirstBestImg(microFingerVein,0,5);
                    Logger.e("FirstFragment===========img" + img);
                    if (img == null) {
                        continue;
                    }
                   String userUid= Finger_identify.Finger_identify(LockActivity.this,img);
                    if (userUid!=null){
                        if (handler != null) {
//                            handler.obtainMessage(MSG_SHOW_SUCCESS,"验证成功").sendToTarget();
                        }
                       try {
                           Thread.sleep(1000);
                       } catch (InterruptedException e) {
                           e.printStackTrace();
                       }
                    } else {
                        if (handler != null) {
//                            Log.e("Identify failed,", "ret=" + ret + ",pos=" + pos[0] + ", score=" + score[0]);
                            handler.obtainMessage(MSG_SHOW_LOG,getResources().getString(R.string.check_failed)).sendToTarget();
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else {
                    if (time_start==false&&timer!=null) {
                                try {
                                    timer.start();
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                    }
                    if (handler != null&&LockActivity.this!=null) {
                        handler.obtainMessage(MSG_SHOW_LOG,getResources().getString(R.string.finger_right)).sendToTarget();
                    }
                }
            }
        }
    };
    CountDownTimer timer;
    String timeStr;
    private void time_out() {
        /**
         * 倒计时60秒，一次1秒
         */
        timer = new CountDownTimer(40 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // TODO Auto-generated method stub
                    timeStr=millisUntilFinished / 1000 + "";
            }
            @Override
            public void onFinish() {
            }
        };
    }
    private ServiceConnection mdSrvConn=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            WorkService.MyBinder myBinder=(WorkService.MyBinder)service;
            if(myBinder!=null){
                microFingerVein=myBinder.getMicroFingerVeinInstance();
                myBinder.setOnUsbMsgCallback(mdUsbMsgCallback);
                Log.e(TAG,"microFingerVein initialized OK,get microFingerVein from MdUsbService success.");
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG,"disconnect MdUsbService.");
        }
    };
    private WorkService.UsbMsgCallback mdUsbMsgCallback=new WorkService.UsbMsgCallback(){
        @Override
        public void onUsbConnSuccess(String usbManufacturerName, String usbDeviceName) {
//            String newUsbInfo="USB厂商："+usbManufacturerName+"  \nUSB节点："+usbDeviceName;
//            handler.obtainMessage(MSG_SHOW_LOG,newUsbInfo).sendToTarget();
        }
        @Override
        public void onUsbDisconnect() {
//            handler.obtainMessage(MSG_SHOW_LOG,"usb disconnected.").sendToTarget();
//            deviceTouchState=2;
            if(microFingerVein!=null) {
                microFingerVein.close();
            }
            bopen=false;
//            bt_model.setText("开始建模");
//            bt_identify.setText("开始认证");
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
    public void onError(ApiException e) {
        String reg = "[^\u4e00-\u9fa5]";
        String syt=e.getMessage().replaceAll(reg, "");
        EventBus.getDefault().post(new MessageEvent(type+3,syt));
        Logger.e("LockActivity"+syt);
    }

    @Override
    public void onResultError(ApiException e) {
        onError(e);
    }

    @Override
    public void onPermissionError(ApiException e) {
        onError(e);
    }

    @Override
    public void sendLogSuccess(RestResponse resultResponse) {

    }
    @Override
    protected void initViews(Bundle savedInstanceState) {
        bindVeinMainFragment=new BindVeinMainFragment();
        mFragmentList.add(bindVeinMainFragment);
        FragmentManager fm=getSupportFragmentManager();
       SectionsPagerAdapter mfpa=new SectionsPagerAdapter(fm,mFragmentList); //new myFragmentPagerAdater记得带上两个参数
        viewPager.setAdapter(mfpa);
        viewPager.setCurrentItem(0);
    }
    private Thread mdWorkThread=null;//进行建模或认证的全局工作线程
//    public void setupParam(Runnable runnablemol) {
//        bRun=true;
//        mdWorkThread=new Thread(runnablemol);
//        mdWorkThread.start();
//    }
     String sql;
     Cursor cursor;
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        ArrayList<Fragment> list;
        public SectionsPagerAdapter(FragmentManager fm,ArrayList<Fragment> mFragmentList) {
            super(fm);
            this.list=mFragmentList;
        }
        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }
        @Override
        public int getCount() {
            return list.size();
        }
    }
    @Override
    protected void initListeners() {
    }
    @Override
    protected int getLayoutId() {
        return R.layout.lock_main_layout;
    }
    @Override
    protected void initToolbar(Bundle savedInstanceState) {
    }

    String titile;
    public void setTitle(String text){
        titile=text;
    }

    public PushMessage toJsonArray(String json) {
        try {
            pushMessage = new PushMessage();
            JSONObject object=new JSONObject(json);
            pushMessage.setAppid(object.getString("appid"));
            pushMessage.setShopId(object.getString("shopId"));
            pushMessage.setUid(object.getString("uid"));
            pushMessage.setSendTime(object.getString("sendTime"));
            pushMessage.setMessageId(object.getString("messageId"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return pushMessage;
    }
    int openType;
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
//                case 0:
////                    text_error.setText("请正确放置手指...");
//                    break;
//                case 1:
////                    text_error.setText("验证成功");
//                    PersonDao personDao=BaseApplication.getInstances().getDaoSession().getPersonDao();
//                    SharedPreferences userinfo=getSharedPreferences("user_info",0);
//                    deviceId=userinfo.getString("deviceId","");
//                    QueryBuilder qb = personDao.queryBuilder();
//                    List<Person> users = qb.where(PersonDao.Properties.Id.eq((long)pos[0]+1)).list();
//                    uid=users.get(0).getUid();
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            isopenCabinet.isopen(openType,deviceId,uid,"vein");
//                        }
//                    }).start();
//                    break;
//                case 7:
////                    text_error.setText("验证失败...");
//                    break;
//                case 8:
////                    text_error.setText("请移开手指");
//                    break;
                case MicroFingerVein.USB_HAS_REQUST_PERMISSION:
                {
                    UsbDevice  usbDevice=(UsbDevice) msg.obj;
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
                        UsbDevice  usbDevice=(UsbDevice) msg.obj;
//                        Logger.e(usbDevice.getManufacturerName()+"  节点："+usbDevice.getDeviceName());
                    }
                }
                break;
                case MicroFingerVein.USB_DISCONNECT:{
//                    microFingerVein.close();
                }
                break;
                case MicroFingerVein.UsbDeviceConnection: {
                    if(msg.obj!=null) {
                        usbDevConn=(UsbDeviceConnection)msg.obj;
                        //----------------------------------------
                        if(LockActivity.this.isFinishing()||LockActivity.this.isDestroyed()) {
                            //修复bug:启动activity几十毫秒内用户快速关闭activity，此时尚未收到usbDeviceConnection对象导致usb不能正常关闭
                            usbDevConn.close();
                        }
                    }
                }
                break;
            }
        }
    };
    @Override
    public void onBackPressed() {
        // super.onBackPressed();
    }
    @Override
    protected void onResume() {

        super.onResume();
    }
    @Override
    protected void onStart() {
        super.onStart();
        mesReceiver=new MesReceiver();

    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        bRun=false;
        if(usbDevConn==null){
        }else{
            usbDevConn.close();
        }
        if( null != mTts ){
            mTts.stopSpeaking();
            // 退出时释放连接
            mTts.destroy();
        }
        if(exitAlertDialog != null) {
            exitAlertDialog.dismiss();
        }
        unbindService(mdSrvConn);
        ButterKnife.unbind(this);
        super.onDestroy();
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
    /**
     * 广播接收器
     *
     * @author kevin
     */
    public class MesReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            head_text_02.setText(intent.getStringExtra("timeStr"));
            head_text_01.setText(intent.getStringExtra("getData"));
            timedata=intent.getStringExtra("getData");
            if (context == null) {
                context.unregisterReceiver(this);
            }
        }
    }
}
