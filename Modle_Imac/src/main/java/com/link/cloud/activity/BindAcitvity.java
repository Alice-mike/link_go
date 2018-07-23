package com.link.cloud.activity;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.media.AsyncPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.link.cloud.BaseApplication;
import com.link.cloud.R;
import com.link.cloud.base.ApiException;
import com.link.cloud.contract.BindTaskContract;
import com.link.cloud.fragment.RegisterFragment_Two;
import com.link.cloud.greendao.gen.PersonDao;
import com.link.cloud.greendaodemo.Person;
import com.orhanobut.logger.Logger;
import com.link.cloud.bean.Member;
import com.link.cloud.core.BaseAppCompatActivity;
import com.link.cloud.fragment.BindVeinMainFragment;

import com.link.cloud.view.NoScrollViewPager;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;
import md.com.sdk.MicroFingerVein;


import static com.link.cloud.utils.Utils.byte2hex;

/**
 * Created by Administrator on 2017/8/24.
 */

public class BindAcitvity extends BaseAppCompatActivity implements CallBackValue,BindTaskContract.BindView {

    @Bind(R.id.bing_main_page)
    NoScrollViewPager viewPager;
    @Bind(R.id.layout_page_time)
    TextView timeStr;
    @Bind(R.id.layout_page_title)
    TextView tvTitle;
    @Bind(R.id.bind_one_Cimg)
    ImageView bind_one_Cimg;
    @Bind(R.id.bind_one_line)
    View bind_one_line;
    @Bind(R.id.layout_main_error)
    LinearLayout layout_error_text;
    @Bind(R.id.bind_two_Cimg)
    ImageView bind_two_Cimg;
    @Bind(R.id.bind_two_line)
    View bind_two_line;
    @Bind(R.id.bind_three_Cimg)
    ImageView bind_three_Cimg;
    @Bind(R.id.bind_three_line)
    View bind_three_line;
    @Bind(R.id.bind_four_Cimg)
    ImageView bind_four_Cimg;
    @Bind(R.id.bind_one_tv)
    TextView bind_one_tv;
    @Bind(R.id.bind_two_tv)
    TextView bind_two_tv;
    @Bind(R.id.bind_three_tv)
    TextView bind_three_tv;
    @Bind(R.id.mian_text_error)
    TextView text_error;
    @Bind(R.id.text_tile)
    TextView text_tile;
//    @Bind(R.id.bind_four_tv)
//    TextView bind_four_tv;
//    @Bind(R.id.bind_four_line)
//    ImageView bind_four_Pimg;
    @Bind(R.id.bind_four_tv)
    TextView bind_four_tv;
    private ArrayList<Fragment> mFragmentList = new ArrayList<Fragment>();
    public static final String ACTION_UPDATEUI = "action.updateTiem";
    BindTaskContract bindTaskContract;
    //记录当前用户信息
    private Member memberInfo;
    private BindVeinMainFragment bindVeinMainFragment;
    private RegisterFragment_Two registerFragment_two;
//    private RegisterFragment_Three registerFragment_three;
    private MesReceiver mesReceiver;
//    private MediaPlayer mediaPlayer0,mediaPlayer1,mediaPlayer2,mediaPlayer;
    private int recLen=0;
    private Runnable runnable;
//    private Handler handler;
    private boolean hasFinish = false;
    private AsyncPlayer asyncPlayer;
    byte[] feauter = null;
    byte[] feauter1 = null;
    byte[] feauter2 = null;
    byte[] feauter3 = null;
    int[] state = new int[1];
    byte[]img=null;
    byte[] img1 = null;
    byte[] img2 = null;
    byte[] img3 = null;
    boolean ret = false;
    int[] pos = new int[1];
    float[] score = new float[1];
    int run_type = 2;
    private PersonDao personDao;
    private static String ACTION_USB_PERMISSION = "com.android.USB_PERMISSION";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,WindowManager.LayoutParams. FLAG_FULLSCREEN);
        WorkService.setActactivity(this);
        super.onCreate(savedInstanceState);
    }
    @Override
    protected int getLayoutId() {
        return R.layout.layout_main_bind;
    }
    @Override
    protected void onStart() {
        super.onStart();
//        microFingerVein=MicroFingerVein.getInstance(this);
        bindTaskContract=new BindTaskContract();
        bindTaskContract.attachView(this);
    }
    @Override
    protected void initViews(Bundle savedInstanceState) {
        mesReceiver=new MesReceiver();
        tvTitle.setText("绑定手指");
        bind_one_tv.setText("请输入手机号码");
        bind_two_tv.setText("确认个人信息");
        bind_three_tv.setText("请放置手指");
        bind_four_tv.setText("绑定完成");
        bindVeinMainFragment=new BindVeinMainFragment();
        mFragmentList.add(bindVeinMainFragment);
        FragmentManager fm=getSupportFragmentManager();
        SectionsPagerAdapter mfpa=new SectionsPagerAdapter(fm,mFragmentList); //new myFragmentPagerAdater记得带上两个参数
        viewPager.setAdapter(mfpa);
        viewPager.setCurrentItem(0);
    }
    @Override
    public void setActivtyChange(String string) {
        switch (string) {
            case "1":
//                mediaPlayer.start();
                bind_one_Cimg.setImageResource(R.drawable.flow_circle_pressed);
                bind_one_line.setBackgroundResource(R.color.colorText);
                bind_one_tv.setTextColor(getResources().getColor(R.color.colorText));
                bind_two_Cimg.setImageResource(R.drawable.flow_circle);
                bind_two_line.setBackgroundResource(R.color.edittv);
                bind_two_tv.setTextColor(getResources().getColor(R.color.edittv));
                bind_three_Cimg.setImageResource(R.drawable.flow_circle);
                bind_three_line.setBackgroundResource(R.color.edittv);
                bind_three_tv.setTextColor(getResources().getColor(R.color.edittv));
                bind_four_Cimg.setImageResource(R.drawable.flow_circle);
                bind_four_tv.setTextColor(getResources().getColor(R.color.edittv));
                break;
            case "2":
//                mediaPlayer0.start();
                bind_one_Cimg.setImageResource(R.drawable.flow_circle);
                bind_one_line.setBackgroundResource(R.color.edittv);
                bind_one_tv.setTextColor(getResources().getColor(R.color.edittv));
                bind_two_Cimg.setImageResource(R.drawable.flow_circle_pressed);
                bind_two_line.setBackgroundResource(R.color.colorText);
                bind_two_tv.setTextColor(getResources().getColor(R.color.colorText));
                bind_three_Cimg.setImageResource(R.drawable.flow_circle);
                bind_three_line.setBackgroundResource(R.color.edittv);
                bind_three_tv.setTextColor(getResources().getColor(R.color.edittv));
                bind_four_Cimg.setImageResource(R.drawable.flow_circle);
                bind_four_tv.setTextColor(getResources().getColor(R.color.edittv));
                break;
            case "3":
                layout_error_text.setVisibility(View.VISIBLE);
                setupParam();
                bind_one_Cimg.setImageResource(R.drawable.flow_circle);
                bind_one_line.setBackgroundResource(R.color.edittv);
                bind_one_tv.setTextColor(getResources().getColor(R.color.edittv));
                bind_two_Cimg.setImageResource(R.drawable.flow_circle);
                bind_two_line.setBackgroundResource(R.color.edittv);
                bind_two_tv.setTextColor(getResources().getColor(R.color.edittv));
                bind_three_Cimg.setImageResource(R.drawable.flow_circle_pressed);
                bind_three_line.setBackgroundResource(R.color.colorText);
                bind_three_tv.setTextColor(getResources().getColor(R.color.colorText));
                bind_four_Cimg.setImageResource(R.drawable.flow_circle);
                bind_four_tv.setTextColor(getResources().getColor(R.color.edittv));
                break;
            case "4":
                bind_one_Cimg.setImageResource(R.drawable.flow_circle);
                bind_one_line.setBackgroundResource(R.color.edittv);
                bind_one_tv.setTextColor(getResources().getColor(R.color.edittv));
                bind_two_Cimg.setImageResource(R.drawable.flow_circle);
                bind_two_line.setBackgroundResource(R.color.edittv);
                bind_two_tv.setTextColor(getResources().getColor(R.color.edittv));
                bind_three_Cimg.setImageResource(R.drawable.flow_circle);
                bind_three_line.setBackgroundResource(R.color.edittv);
                bind_three_tv.setTextColor(getResources().getColor(R.color.edittv));
                bind_four_Cimg.setImageResource(R.drawable.flow_circle_pressed);
                bind_four_tv.setTextColor(getResources().getColor(R.color.colorText));
                break;
        }
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void initData() {
        text_tile.setText("会员绑定");
        mesReceiver=new MesReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_UPDATEUI);
        registerReceiver(mesReceiver, intentFilter);
//        etPhoneNum.setShowSoftInputOnFocus(false);
    }
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    String feature=new String(byte2hex(feauter3));
                    SharedPreferences userinfo=getSharedPreferences("user_info",0);
                    SharedPreferences userinfo2=getSharedPreferences("user_info_bind",0);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            bindTaskContract.bindVeinMemeber(userinfo.getString("deviceId",""),
                                    userinfo2.getInt("userType",0),userinfo.getInt("numberType",0),
                                    userinfo2.getString("numberValue",""),feature);
                        }
                    }).start();
                    break;
                case 1:
                    text_error.setText("请移开手指...");
                    break;
                case 2:
                    text_error.setText("请再次放置手指...");
                    break;
                case 3:
                    text_error.setText("验证错误，请放置同一根手指");
                    break;
                case 4:
                    text_error.setText("请按图示放置手指");
                    break;
                case 5:
                    text_error.setText("请稍等...");
                    break;
                case 6:
                    text_error.setText("恭喜您绑定成功");
                    break;
//                case MicroFingerVein.USB_HAS_REQUST_PERMISSION:
//                {
//                    UsbDevice  usbDevice=(UsbDevice) msg.obj;
//                    UsbManager mManager=(UsbManager)getSystemService(Context.USB_SERVICE);
//                    PendingIntent mPermissionIntent = PendingIntent.getBroadcast(BindAcitvity.this, 0, new Intent(ACTION_USB_PERMISSION), 0);
//                    if(mManager == null)
//                    {
//                        mManager=(UsbManager)getSystemService(Context.USB_SERVICE);
//                        IntentFilter filter = new IntentFilter();
//                    }
//                    mManager.requestPermission(usbDevice,mPermissionIntent);
//                }
//                break;
//                case MicroFingerVein.USB_CONNECT_SUCESS: {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        UsbDevice  usbDevice=(UsbDevice) msg.obj;
//                            Logger.e(usbDevice.getManufacturerName()+"  节点："+usbDevice.getDeviceName());
//                    }
//                }
//                break;
//                case MicroFingerVein.USB_DISCONNECT:{
//                    //--------------------------
////                    WorkService.ret=false;
//                    deviceTouchState=2;
//                    //--------------------------
////                   WorkService.microFingerVein.close();
////                    bopen=false;
////                    bt_model.setText("开始建模");
////                    bt_identify.setText("开始认证");
//                }
//                break;
            }
        }
    };
    private void initial(Member userinfo){
        try {
            String string=new String(byte2hex(feauter3));
//            SharedPreferences userInfo= getSharedPreferences("user_info_bind",0);
            Person mUser1 = new Person();
            mUser1.setUserType(userinfo.getMemberdata().getUserInfo().getUserType());
            mUser1.setUid(userinfo.getMemberdata().getUserInfo().getUid());
            mUser1.setName(userinfo.getMemberdata().getUserInfo().getName());
            mUser1.setNumber(userinfo.getMemberdata().getUserInfo().getPhone());
            mUser1.setSex(userinfo.getMemberdata().getUserInfo().getSex());
            mUser1.setImg(userinfo.getMemberdata().getUserInfo().getImg());
            if (string!=null) {
                mUser1.setFingermodel(string);
            }
            personDao = BaseApplication.getInstances().getDaoSession().getPersonDao();
            personDao.insert(mUser1);
            personDao.loadAll();
//            Logger.d("Errormessage"+string+"添加完成");
//            Toast.makeText(BindAcitvity.this, "添加完成", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Logger.d("Errormessage"+e.getMessage());
            Toast.makeText(BindAcitvity.this, "添加数据出错，请重新绑定"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }
    public void startAD() {
        recLen=3;
        runnable = new Runnable() {
            @Override
            public void run() {
                recLen--;
                handler.postDelayed(this,1000);
                if (recLen<=0) {
                    Intent intent = new Intent();
                    intent.setClass(BindAcitvity.this, NewMainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };
        Logger.e("BindActivity=======+startAD()");
        handler.postDelayed(runnable, 1000);
    }
    private volatile boolean bRun=false;
    boolean  bopen=false;
    private boolean bWorkModel=true;//建模是否进行
    private Thread mdWorkThread=null;//进行建模或认证的全局工作线程
    private int deviceTouchState=1;//触摸：0，移开1，设备断开或其他状态2
//    MicroFingerVein microFingerVein;
    private void setupParam() {
        bRun=true;
        mdWorkThread=new Thread(runnablemol);
        mdWorkThread.start();
    }
    Runnable  runnablemol=new Runnable() {
        @Override
        public void run() {
            int state=0;
            int[] pos = new int[1];
            float[] score = new float[1];
            boolean ret=false;
            int[] tipTimes={0,0};//后两次次建模时用了不同手指，重复提醒限制3次
            int modOkProgress=0;
            while(bRun) {
                state=WorkService.microFingerVein.fvdev_get_state();
                //设备连接正常则进入正常建模或认证流程
                if(state != 0) {
                    Logger.e("BindActivty===========state"+state);
                    if(state==1||state==2) {
                        continue;
                    }else if(state==3){
                        img=WorkService.microFingerVein.fvdev_grab();
                        Logger.e("BindActivty===========img"+img);
                        handler.sendEmptyMessage(1);
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    deviceTouchState=0;
                    if(img==null) {
                        continue;
                    }
                    feauter=WorkService.microFingerVein.fv_extract_model(img,null,null);
                    Logger.e("BindActivty===========feauter1"+feauter);
                    if(feauter == null) {
                        continue;
                    }
                    else
                    { //建模
                        modOkProgress++;
                        Logger.e("BindActivity" +"Progress="+modOkProgress);
                        if (modOkProgress == 1) {//first model

                                handler.sendEmptyMessage(2);
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                            tipTimes[0] = 0;
                            tipTimes[1] = 0;
                            img1 = img;
                            feauter1=WorkService.microFingerVein.fv_extract_model(img1, null, null);
                            Logger.e("BindActivity" + "model 1 ok"+"modOkProgress="+modOkProgress);
                        } else if (modOkProgress == 2) {//second model

                                handler.sendEmptyMessage(2);
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                            ret = WorkService.microFingerVein.fv_index(feauter1, 1, img, pos, score);
                            if (ret && score[0] > 0.4) {
                                Logger.e("BindActivity" + "model 2 ok"+"modOkProgress"+modOkProgress);
                                feauter2 = WorkService.microFingerVein.fv_extract_model(img1, img, null);
                                if (feauter2 != null) {
                                        handler.sendEmptyMessage(2);
                                        try {
                                            Thread.sleep(500);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }

                                    tipTimes[0] = 0;
                                    tipTimes[1] = 0;
                                    img2=img;
                                } else {//第二次建模从图片中取特征值无效
                                    modOkProgress = 1;
                                    if (++tipTimes[0] <= 5) {
                                    } else {//连续超过3次放了不同手指则忽略此次建模重来
                                        modOkProgress = 0;
                                    }
                                }
                            } else {
                                handler.sendEmptyMessage(3);
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                modOkProgress = 1;
                                if (++tipTimes[0] <= 5) {
//                                        Log.e(TAG,"get feature from img failed when try second modeling");
//                                        handler.obtainMessage(MSG_SHOW_LOG,"please move away your finger and put the same one for second modeling").sendToTarget();
                                } else {//连续超过3次放了不同手指则忽略此次建模重来
//                                        Log.e(TAG,"put different finger more than 3 times,this modeling is ignored,a new modeling start.");
//                                        handler.obtainMessage(MSG_SHOW_LOG,"put different finger more than 3 times,this modeling is IGNORED,a new modeling start.\n").sendToTarget();
                                    modOkProgress = 0;
//                                        modelImgMng.reset();
                                }
                            }
                        } else if (modOkProgress == 3) {//third model
                            if (WorkService.microFingerVein.fvdev_get_state()==3){
                                handler.sendEmptyMessage(5);
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            ret = WorkService.microFingerVein.fv_index(feauter2, 1, img, pos, score);
                            if (ret && score[0] > 0.4) {
                                Logger.e("BindActivity" + "model 3 ok"+"modOkProgress"+modOkProgress);
                                feauter3 = WorkService.microFingerVein.fv_extract_model(img1, img2, img);
                                if (feauter3 != null) {//成功生成一个3次建模并融合的融合特征数组
                                    handler.sendEmptyMessage(0);
                                    tipTimes[0] = 0;
                                    tipTimes[1] = 0;
                                } else {//第三次建模从图片中取特征值无效
                                    modOkProgress = 2;
                                    if (++tipTimes[1] <= 3) {
                                    }
                                }
                                bRun=false;
                                bopen = false;
                            } else {
                                modOkProgress = 2;
                                continue;
                            }
                        } else if (modOkProgress > 3 || modOkProgress <= 0) {
                            modOkProgress = 0;
                        }
                    }
                    while (state == 3  && bRun) {
                        deviceTouchState=0;
                        state=WorkService.microFingerVein.fvdev_get_state();
                        if(!bopen){//等待手指拿开的中途设备断开了
//                            Log.e(TAG,"device disconnected when identifying is waiting for finger moving away");
//                            handler.obtainMessage(MSG_SHOW_LOG,"device disconnected when identifying is waiting for finger moving away");
                        }
                    }
                    //--------------------------------------------------------------
                    continue;
                }else {//触摸state==0时
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(bopen) {
                        deviceTouchState = 1;
                    }
                }
            }
            if (bopen){
                WorkService.microFingerVein.close();
                bopen=false;
            }
        }
    };
    @Override
    protected void initToolbar(Bundle savedInstanceState) {
    }
    @Override
    protected void initListeners() {
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
    }
    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    public void onBackPressed() {
        // super.onBackPressed();
    }
    @Override
    protected void onDestroy() {
        WorkService.microFingerVein.close();
        if (bopen){
            bopen=false;
        }
        bRun=false;
        WorkService.microFingerVein.close();
        if (handler!=null) {
            handler.removeCallbacksAndMessages(null);
            handler.removeCallbacks(runnable);
            handler=null;
        }
        super.onDestroy();
        unregisterReceiver(mesReceiver);
        finish();
    }
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
    @OnClick(R.id.home_back_bt)
    public void onClick(View view){
        switch (view.getId()){
            case R.id.home_back_bt:
                if (bopen){
                    bopen=false;
                }
                bRun=false;
             Intent intent=new Intent();
             intent.setClass(BindAcitvity.this,NewMainActivity.class);
             intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
             startActivity(intent);
             finish();
             break;
        }
    }
    @Override
    public void onPermissionError(ApiException e) {
        onError(e);
    }
    @Override
    public void onError(ApiException e) {
        String reg = "[^\u4e00-\u9fa5]";
        String syt=e.getMessage().replaceAll(reg, "");
        Logger.e("BindActivity"+syt);
        text_error.setText(syt);
    }
    @Override
    public void onResultError(ApiException e) {
        onError(e);
    }
    @Override
    public void bindSuccess(Member returnBean) throws InterruptedException {
        startAD();
        if (handler!=null) {
            Message message = new Message();
            message.what = 6;
            handler.sendMessage(message);
        }
        setActivtyChange("4");
        initial(returnBean);
    }
    /**
     * 广播接收器
     *
     * @author kevin
     */
    public class MesReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            timeStr.setText(intent.getStringExtra("timethisStr"));
//            Logger.e("NewMainActivity" + intent.getStringExtra("timeStr"));
            if (context == null) {
                context.unregisterReceiver(this);
            }
        }
    }
}
