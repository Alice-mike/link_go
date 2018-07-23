package com.link.cloud.activity;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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


import com.cloopen.rest.rest.sdk.utils.encoder.BASE64Decoder;
import com.hotelmanager.xzy.util.HotelUtil;
import com.hotelmanager.xzy.util.OpenDoorUtil;
import com.link.cloud.BaseApplication;
import com.link.cloud.R;
import com.link.cloud.base.ApiException;
import com.link.cloud.bean.DownLoadData;
import com.link.cloud.bean.Lockdata;
import com.link.cloud.bean.PushMessage;
import com.link.cloud.contract.AdminopenCabinet;
import com.link.cloud.contract.ClearCabinetContract;
import com.link.cloud.contract.DownloadFeature;
import com.link.cloud.contract.IsopenCabinet;
import com.link.cloud.contract.SyncUserFeature;
import com.link.cloud.core.BaseAppCompatActivity;
import com.link.cloud.gpiotest.Gpio;
import com.link.cloud.greendao.gen.PersonDao;
import com.link.cloud.greendaodemo.Person;
import com.orhanobut.logger.Logger;

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
public class LockActivity extends BaseAppCompatActivity implements IsopenCabinet.isopen,SyncUserFeature.syncUser{
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
    PushMessage pushMessage;
    DownloadFeature feature;
    IsopenCabinet isopenCabinet;
    AdminopenCabinet adminopenCabinet;
    ClearCabinetContract clearCabinetContract;
    private ArrayList<Fragment> mFragmentList = new ArrayList<Fragment>();
    byte[] featuer = null;
    int state =0;
    byte[] img1 = null;
    boolean ret = false;
    int[] pos = new int[1];
    float[] score = new float[1];
    private PersonDao personDao;
    SyncUserFeature syncUserFeature;
    private HotelUtil hotelUtil;
    boolean flog=true;
    String deviceId,uid;
  public MesReceiver mesReceiver;
    OpenDoorUtil openDoorUtil;
    public static boolean isStart=false;
    BaseApplication baseApplication;
    MicroFingerVein microFingerVein;
    String timedata=null,gpiostr;
    public static final String ACTION_UPDATEUI = "action.dataTime";
    private static String ACTION_USB_PERMISSION = "com.android.USB_PERMISSION";
    SharedPreferences userinfo;
    String gpiotext="";
    Intent intent;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        personDao= BaseApplication.getInstances().getDaoSession().getPersonDao();
        intent = new Intent(this, WorkService.class);
        startService(intent);
        WorkService.setActactivity(this);
        setupParam();
    }
    @Override
    protected void initData() {
        userinfo=getSharedPreferences("user_info",0);
        String gpio=userinfo.getString("gpiotext",null);
        if (gpio==null){
            userinfo.edit().putString("gpiotext","1066").commit();
        }
        gpiotext=userinfo.getString(gpiotext,"");
        Gpio.gpioInt(gpiotext);
        Gpio.set(gpiotext,48);
        microFingerVein= MicroFingerVein.getInstance(this);
        deviceId=userinfo.getString("deviceId","");
    }
    byte[]  executeSql() {
        byte[] nFeatuer=null;
        personDao= BaseApplication.getInstances().getDaoSession().getPersonDao();
        personDao.loadAll();
        int i=0;
        Cursor cursor;
        String sql;
        sql = "select FINGERMODEL from PERSON" ;
        cursor = BaseApplication.getInstances().getDaoSession().getDatabase().rawQuery(sql,null);
        byte[][] feature=new byte[cursor.getCount()][];
        while (cursor.moveToNext()){
            int nameColumnIndex = cursor.getColumnIndex("FINGERMODEL");
            String strValue=cursor.getString(nameColumnIndex);
            feature[i]=hexStringToByte(strValue);
            i++;
        }
        int len = 0;
        // 计算一维数组长度
        if(feature.length>0) {
            for (byte[] element : feature) {
                len += element.length;
            }
            // 复制元素
            nFeatuer = new byte[len];
            int index = 0;
            for (byte[] element : feature) {
                for (byte element2 : element) {
                    nFeatuer[index++] = element2;
                }
            }
        }
        return nFeatuer;
    }
    boolean bopen=false;
    boolean bRun=false;
    private Thread mdWorkThread=null;
    private void setupParam() {
        bRun=true;
        mdWorkThread=new Thread(runnable);
        mdWorkThread.start();
    }
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            boolean ret = false;
            int[] tipTimes = {0, 0};//后两次次建模时用了不同手指，重复提醒限制3次
            int modOkProgress = 0;
            while (bRun) {
                state = microFingerVein.fvdev_get_state();
                //设备连接正常则进入正常建模或认证流程
//                Logger.e("BindActivty===========state"+state);
                if (state != 0) {
//                    time_start=false;
//                    timer.cancel();
                    featuer=executeSql();
                    Logger.e("FirstFragment===========state" + state);
                    if (state == 1 || state == 2) {
                        continue;
                    } else if (state == 3) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    byte[] img = microFingerVein.fvdev_grab();
                    Logger.e("FirstFragment===========img" + img);
                    if (img == null) {
                        continue;
                    }
                    if (featuer!=null) {
                        ret = microFingerVein.fv_index(featuer, featuer.length/ 3352, img, pos, score);
                        Logger.e("FirstFragment_count"+"===========featuer.length"+featuer.length/3352+"pos"+pos[0]+"ret="+ret);
                    }else {
                        if (handler != null) {
                            Message message = new Message();
                            message.what = 2;
                            handler.sendMessage(message);
                        }
                    }
                    if (ret == true && score[0] > 0.63) {
                        Logger.e("Identify success,"+"pos=" + pos[0] + ", score=" + score[0]);
                        if (handler != null) {
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                        }
                    } else {
                        if (handler != null) {
                            Log.e("Identify failed,", "ret=" + ret + ",pos=" + pos[0] + ", score=" + score[0]);
                            Message message = new Message();
                            message.what = 7;
                            handler.sendMessage(message);
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else {
                    if (handler != null) {
                        Message message = new Message();
                        message.what = 0;
                        handler.sendMessage(message);
                    }
                }
            }
        }
    };
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
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    text_error.setText("请正确放置手指...");
                    break;
                case 1:
                    text_error.setText("验证成功");
                    QueryBuilder qb = personDao.queryBuilder();
                    int value=pos[0]+1;
                    List<Person> users = qb.where(PersonDao.Properties.Id.notEq(value)).list();
                    uid=users.get(0).getUid();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            isopenCabinet.isopen(deviceId,uid,"vein");
                        }
                    }).start();
                    break;
                case 7:
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
//                    microFingerVein.close();
//                    bt_model.setText("开始建模");
//                    bt_identify.setText("开始认证");
                }
                break;
                case MicroFingerVein.UsbDeviceConnection: {
                    if(msg.obj!=null) {
                        UsbDeviceConnection usbDevConn=(UsbDeviceConnection)msg.obj;
                    }
                }
                break;
            }
        }
    };
    @OnClick({ R.id.button02, R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.head_text_02})
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.button01:
//                userinfo=getSharedPreferences("user_info",0);
//                userinfo.edit().putString("gpiotext","1067").commit();
//                gpiostr=userinfo.getString("gpiotext","");
//                Gpio.gpioInt(gpiostr);
//                setupParam();
//                Toast.makeText(LockActivity.this, "配置1成功", Toast.LENGTH_SHORT).show();
//                break;
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
                syncUserFeature.syncUser(deviceId);
            break;
            default:
                break;
        }
    }
    public static String desEncrypt(String datacode) throws Exception {
        try
        {
            String data = "LU8wzgej7Uzw2EGHRJuTT62zQ9kuyVCg4z0S1vg/1VR3cQdilIgnsAYouHksGcDl";
            String key = "rocketbird!@sjs!";
            String iv = "kiPqmEVXtZrgaVkf";
            byte[] encrypted1 = new BASE64Decoder().decodeBuffer(datacode);
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original);
            return originalString;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public void syncUserSuccess(DownLoadData resultResponse) {
        personDao = BaseApplication.getInstances().getDaoSession().getPersonDao();
        Person person = new Person();
        if (resultResponse.getDown_userInfo().length >= 0) {
            personDao.deleteAll();
            for (int i = 0; i < resultResponse.getDown_userInfo().length; i++) {
                person.setId((long) i);
                person.setUid(resultResponse.getDown_userInfo()[i].getUid());
                person.setNumber(resultResponse.getDown_userInfo()[i].getUserName());
                person.setPos(resultResponse.getDown_userInfo()[i].getFingerId());
                person.setFingermodel(resultResponse.getDown_userInfo()[i].getFeature());
                personDao.insert(person);
            }
            executeSql();
        }
    }
    @Override
    protected void onResume() {
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
        SharedPreferences sharedPreferences=getSharedPreferences("user_info",0);
        gpiostr=sharedPreferences.getString("gpiotext","");
        Logger.e("LockAcitvity"+"==========="+gpiostr);
        try {
            Gpio.gpioInt(gpiostr);
            Gpio.set(gpiostr,48);
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Gpio.set(gpiostr,49);
    }
    @Override
    public void onError(ApiException e) {
        String reg = "[^\u4e00-\u9fa5]";
        String syt=e.getMessage().replaceAll(reg, "");
        Logger.e("BindActivity"+syt);
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                text_error.setText(syt);
            }
        });
    }
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
}
