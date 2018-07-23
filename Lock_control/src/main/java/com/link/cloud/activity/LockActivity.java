package com.link.cloud.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SyncStatusObserver;
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

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;

import com.google.gson.annotations.SerializedName;
import com.hotelmanager.xzy.util.HotelUtil;
import com.hotelmanager.xzy.util.OpenDoorUtil;
import com.link.cloud.BaseApplication;
import com.link.cloud.R;
import com.link.cloud.base.ApiException;
import com.link.cloud.bean.DownLoadData;
import com.link.cloud.bean.Isopenmessage;
import com.link.cloud.bean.Lockdata;
import com.link.cloud.bean.PushMessage;
import com.link.cloud.bean.ResultResponse;
import com.link.cloud.contract.AdminopenCabinet;
import com.link.cloud.contract.ClearCabinetContract;
import com.link.cloud.contract.DownloadFeature;
import com.link.cloud.contract.IsopenCabinet;
import com.link.cloud.contract.SyncUserFeature;
import com.link.cloud.core.BaseAppCompatActivity;
import com.link.cloud.fragment.BindVeinMainFragment;
import com.link.cloud.fragment.FirstFragment;
import com.link.cloud.fragment.MainFragment;
import com.link.cloud.fragment.SecondFragment;
import com.link.cloud.fragment.ThirdFragment;
//import com.link.cloud.greendao.gen.PersonDao;
import com.link.cloud.greendao.gen.PersonDao;
import com.link.cloud.greendaodemo.Person;

import com.link.cloud.view.NoScrollViewPager;
import com.orhanobut.logger.Logger;

import org.greenrobot.greendao.query.QueryBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android_serialport_api.SerialPort;
import butterknife.Bind;
import butterknife.OnClick;
import md.com.sdk.MicroFingerVein;

import static com.alibaba.sdk.android.ams.common.util.HexUtil.hexStringToByte;
import static com.link.cloud.utils.Utils.byte2hex;

/**
 * Created by 30541 on 2018/3/12.
 */

public class LockActivity extends BaseAppCompatActivity {
    @Bind(R.id.bing_main_page)
    NoScrollViewPager viewPager;
    @Bind(R.id.main_bt_01)
    Button button_01;
    @Bind(R.id.main_bt_02)
    Button button_02;
    @Bind(R.id.main_bt_03)
    Button button_03;
    @Bind(R.id.layout_two)
    LinearLayout layout_two;
    @Bind(R.id.text_error)
    TextView text_error;
    @Bind(R.id.layout_three)
    LinearLayout layout_three;
    @Bind(R.id.layout_one)
    LinearLayout layout_one;
    @Bind(R.id.text_number)
    TextView text_number;
//    @Bind(R.id.head_text_001)
//    TextView head_text_001;
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
    @Bind(R.id.head_text_03)
    TextView head_text_03;
//    @Bind(R.id.edit_code)
//    EditText edit_code;
//    @Bind(R.id.edit_Text)
//      EditText edit_Text;
//    @Bind(R.id.button_code)
//      Button button_cod;
    PushMessage pushMessage;
    DownloadFeature feature;
    IsopenCabinet isopenCabinet;
    AdminopenCabinet adminopenCabinet;
    ClearCabinetContract clearCabinetContract;
    private ArrayList<Fragment> mFragmentList = new ArrayList<Fragment>();
    byte[] featuer = null;
    int[] state = new int[1];
    byte[] img1 = null;

    int[] pos = new int[1];
    float[] score = new float[1];
    private PersonDao personDao;
    SyncUserFeature syncUserFeature;
    private HotelUtil hotelUtil;
    boolean flog=true;
    String deviceId,uid;
    MesReceiver mesReceiver;
    OpenDoorUtil openDoorUtil;
    public static boolean isStart=false;
    BaseApplication baseApplication;
    private BindVeinMainFragment bindVeinMainFragment;
    MainFragment mainFragment;
    FirstFragment firstFragment;
    SecondFragment secondFragment;
    ThirdFragment thirdFragment;
    String timedata=null;
    public MicroFingerVein microFingerVein;
    public static final String ACTION_UPDATEUI = "action.updateTiem";
    private static String ACTION_USB_PERMISSION = "com.android.USB_PERMISSION";
    Intent intent;
    public SerialPort serialpprt_wk1=null;
    public SerialPort serialpprt_wk2=null;
    public SerialPort serialpprt_wk3=null;
    private UsbDeviceConnection usbDevConn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        BaseApplication.setMainActivity(this);
        openDoorUtil=new OpenDoorUtil();
        baseApplication=(BaseApplication) getApplication();
        intent=new Intent(this,WorkService.class);
        startService(intent);
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
        microFingerVein=MicroFingerVein.getInstance(this);
//        setupParam();
    }
    boolean bopen=false;
    boolean bRun=false;
    private Thread mdWorkThread=null;
    @Override
    protected void initViews(Bundle savedInstanceState) {
        bindVeinMainFragment=new BindVeinMainFragment();
        mFragmentList.add(bindVeinMainFragment);
        FragmentManager fm=getSupportFragmentManager();
        mainFragment =new MainFragment();
        mFragmentList.add(mainFragment);
        firstFragment =new FirstFragment();
        mFragmentList.add(firstFragment);
        secondFragment=new SecondFragment();
        mFragmentList.add(secondFragment);
        thirdFragment=new ThirdFragment();
        mFragmentList.add(thirdFragment);
       SectionsPagerAdapter mfpa=new SectionsPagerAdapter(fm,mFragmentList); //new myFragmentPagerAdater记得带上两个参数
        viewPager.setAdapter(mfpa);
        viewPager.setCurrentItem(0);
    }
     String sql;
     Cursor cursor;
//    public byte[] executeSql() {
//        byte[] featuer =null;
//        personDao= BaseApplication.getInstances().getDaoSession().getPersonDao();
//        personDao.loadAll();
//        int i=0;
//        sql = "select FINGERMODEL from PERSON" ;
//        cursor = BaseApplication.getInstances().getDaoSession().getDatabase().rawQuery(sql,null);
//        byte[][] feature=new byte[cursor.getCount()][];
//        while (cursor.moveToNext()){
//            int nameColumnIndex = cursor.getColumnIndex("FINGERMODEL");
//            String strValue=cursor.getString(nameColumnIndex);
//            feature[i]=hexStringToByte(strValue);
//            i++;
//        }
//        int len = 0;
//        // 计算一维数组长度
//        if(feature.length>0) {
//            for (byte[] element : feature) {
//                len += element.length;
//            }
//            // 复制元素
//            featuer = new byte[len];
//            int index = 0;
//            for (byte[] element : feature) {
//                for (byte element2 : element) {
//                    featuer[index++] = element2;
//                }
//            }
//        }
//        else {
//
//        }
//        return featuer;
//    }

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
//    @Override
//    public void mcuRetrunResult(byte[] data) {
//        hotelUtil.openDoorSuccess(data);
//    }
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
                case 0:
                    text_error.setText("请正确放置手指...");
                    break;
                case 1:
                    text_error.setText("验证成功");
                    SharedPreferences userinfo=getSharedPreferences("user_info",0);
                    deviceId=userinfo.getString("deviceId","");
                    QueryBuilder qb = personDao.queryBuilder();
                    List<Person> users = qb.where(PersonDao.Properties.Id.eq((long)pos[0]+1)).list();
                    uid=users.get(0).getUid();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            isopenCabinet.isopen(openType,deviceId,uid,"vein");
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
                        Logger.e(usbDevice.getManufacturerName()+"  节点："+usbDevice.getDeviceName());
                    }
                }
                break;
                case MicroFingerVein.USB_DISCONNECT:{
                    microFingerVein.close(1);
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
        isopenCabinet=new IsopenCabinet();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        intent=new Intent(this,WorkService.class);
        stopService(intent);
        microFingerVein.close(1);
        if(usbDevConn==null){
        }else{
            usbDevConn.close();
        }
    }
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
