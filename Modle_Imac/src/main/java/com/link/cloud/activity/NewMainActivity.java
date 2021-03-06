package com.link.cloud.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.webkit.DownloadListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.link.cloud.BaseApplication;
import com.link.cloud.R;

import com.link.cloud.base.ApiException;
import com.link.cloud.bean.DownLoadData;
import com.link.cloud.bean.PagesInfoBean;
import com.link.cloud.bean.SyncFeaturesPage;
import com.link.cloud.bean.SyncUserFace;
import com.link.cloud.bean.UpDateBean;
import com.link.cloud.contract.DownloadFeature;
import com.link.cloud.contract.SyncUserFeature;
import com.link.cloud.greendao.gen.PersonDao;
import com.link.cloud.greendaodemo.Person;

import com.link.cloud.utils.APKVersionCodeUtils;
import com.link.cloud.utils.FileUtils;
import com.link.cloud.view.ExitAlertDialogshow;
import com.orhanobut.logger.Logger;

import com.link.cloud.utils.CleanMessageUtil;
import com.link.cloud.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import md.com.sdk.MicroFingerVein;


/**
 * Created by Administrator on 2017/8/18.
 */

public class NewMainActivity extends AppCompatActivity implements DownloadFeature.download,SyncUserFeature.syncUser{
    @Bind(R.id.fabExit)
    FloatingActionButton fabExit;
    @Bind(R.id.layout_title)
    TextView tvTitle;
    @Bind(R.id.layout_time)
    TextView tv_time;
    @Bind(R.id.bt_main_bind)
    TextView btn_bind;
    @Bind(R.id.bt_main_sign)
    Button btn_sign;
    @Bind(R.id.bt_main_up)
    Button btn_lesson;
    @Bind(R.id.bt_main_down)
    Button down_lesson;
    @Bind(R.id.bt_main_pay)
    Button btn_pay;
    @Bind(R.id.textView2)
    TextView timeText;
    @Bind(R.id.data_time)
    TextView data_time;
    //    @Bind(R.id.test_push)
//    TextView textView;
    private Utils utils;
//    ImageButton btn_bind;
    private SharedPreferences userInfo;
    private static String ACTION_USB_PERMISSION = "com.android.USB_PERMISSION";
    private MediaPlayer mediaPlayer,mediaPlayer1,mediaPlayer2,mediaPlayer3;
    private MesReceiver mesReceiver;
    private String deviceID;
    public static final String ACTION_UPDATEUI = "com.link.cloud.updateTiem";
    public static final String ACTION_DATABASES = "com.link.cloud.databases";
    ObjectAnimator rotationAnimator,translateAnimatorIn, translateAnimatorOut;
    OvershootInterpolator interpolator;
    ExitAlertDialogshow exitAlertDialogshow;;
    ExitAlertDialog exitAlertDialog;
    private PersonDao personDao;
    private List<Person> userList2 = new ArrayList<Person>();
    byte[] feauter = null;
    byte[] feauter1 = null;
    byte[] feauter2 = null;
    int[] state = new int[1];
    byte[] img1 = null;
    byte[] img2 = null;
    byte[] img3 = null;
    boolean ret = false;
    int[] pos = new int[1];
    float[] score = new float[1];
    int run_type = 2;
    WorkService workService;

    DownloadFeature downloadFeature;
    public MicroFingerVein microFingerVein;
//    WorkService.MyBinder myBinder=null;
//    NewMainActivity activity = null;
    BaseApplication baseApplication;
    WorkService service;
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        CleanMessageUtil.clearAllCache(getApplicationContext());
        setContentView(R.layout.layout_main_activity);
        ButterKnife.bind(this);
//            intent = new Intent(this, WorkService.class);
//            startService(intent);
        baseApplication=(BaseApplication)getApplication();
        exitAlertDialogshow=new ExitAlertDialogshow(this);
        exitAlertDialogshow.setCanceledOnTouchOutside(false);
        exitAlertDialogshow.setCancelable(false);
        WorkService.setActactivity(this);
        Logger.e("NewMainActivity"+"=======================");
//        Permition.verifyStoragePermissions(this);//检验外部存储器访问权限
        inview();
        init();
    }
    public boolean RootCmd(String cmd){
        Process process=null;
        DataOutputStream os=null;
        try{
            process=Runtime.getRuntime().exec("su");
            os=new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd+"\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        }catch (Exception e){
            return false;
        }finally {
                try{
                    if (os!=null){
                        os.close();
                    }
                    process.destroy();
                }catch (Exception e){
            }
            return true;
        }
    }
    ConnectivityManager connectivityManager;
    private void inview() {
        TextView textView=(TextView) findViewById(R.id.versionName);
        textView.setText( APKVersionCodeUtils.getVerName(this));
        downloadFeature=new DownloadFeature();
        downloadFeature.attachView(this);
        exitAlertDialog = new ExitAlertDialog(NewMainActivity.this);
        interpolator = new OvershootInterpolator();
        rotationAnimator = ObjectAnimator.ofFloat(fabExit, "rotation", 0, 360 * 2);
        rotationAnimator.setDuration(1000);
        rotationAnimator.setInterpolator(interpolator);
        tvTitle.setText("欢迎使用");
        tv_time.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                connectivityManager =(ConnectivityManager)NewMainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);//获取当前网络的连接服务
                NetworkInfo info =connectivityManager.getActiveNetworkInfo(); //获取活动的网络连接信息
                if (info != null) {   //当前没有已激活的网络连接（表示用户关闭了数据流量服务，也没有开启WiFi等别的数据服务）
                    exitAlertDialogshow.show();
                    SharedPreferences sharedPreferences = getSharedPreferences("user_info", 0);
                    String deviceId = sharedPreferences.getString("deviceId", "");
                    downloadFeature.getPagesInfo(deviceId);
                }else {
                    Toast.makeText(NewMainActivity.this,"网络已断开，请检查网络",Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });
        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new AlertDialog.Builder(NewMainActivity.this)
                        .setTitle("确定退出软件么？")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                // 为Intent设置Action、Category属性
                                intent.setAction(Intent.ACTION_MAIN);// "android.intent.action.MAIN"
                                intent.addCategory(Intent.CATEGORY_HOME); //"android.intent.category.HOME"
                                startActivity(intent);
                            }
                        })
                        .create().show();
                return false;
            }
        });
    }
    NewMainActivity activity;
    private void init() {
        utils=new Utils();
        mesReceiver = new MesReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_UPDATEUI);
        registerReceiver(mesReceiver, intentFilter);
    }
    @OnClick({R.id.bt_main_bind,R.id.bt_main_sign,R.id.bt_main_pay,R.id.bt_main_up,R.id.bt_main_down,R.id.fabExit,R.id.bt_main_bind_face})
    public void OnClick(View view){
        connectivityManager =(ConnectivityManager)NewMainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);//获取当前网络的连接服务
        NetworkInfo info =connectivityManager.getActiveNetworkInfo(); //获取活动的网络连接信息
        switch (view.getId()){
            case R.id.bt_main_bind:
                if (info != null) {   //当前没有已激活的网络连接（表示用户关闭了数据流量服务，也没有开启WiFi等别的数据服务）
                    if (Utils.isFastClick()) {
                        intent = new Intent();
                        intent.setClass(NewMainActivity.this, BindAcitvity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                    }
                }else {
                    Toast.makeText(NewMainActivity.this,"网络已断开，请检查网络",Toast.LENGTH_LONG).show();
                }
            break;
            case R.id.bt_main_sign:
                if (info!=null) {
                    if (Utils.isFastClick()) {
                        intent = new Intent();
                        intent.setClass(NewMainActivity.this, SignChooseActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                    }
                }else {
                    Toast.makeText(NewMainActivity.this,"网络已断开，请检查网络",Toast.LENGTH_LONG).show();
                }
                break;
                case R.id.bt_main_bind_face:
                if (info!=null) {
                    if (Utils.isFastClick()) {
                        intent = new Intent();
                        intent.setClass(NewMainActivity.this, BindFaceActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                    }
                }else {
                    Toast.makeText(NewMainActivity.this,"网络已断开，请检查网络",Toast.LENGTH_LONG).show();
                }
                break;
//            case R.id.bt_main_up:
//                intent = new Intent();
//                intent.setClass(NewMainActivity.this,EliminateActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
//                finish();
//                break;
//            case R.id.bt_main_down:
//                intent = new Intent();
//                intent.setClass(NewMainActivity.this,LessonDownActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
//                finish();
//                break;
//            case R.id.bt_main_pay:
//                intent = new Intent();
//                intent.setClass(NewMainActivity.this,PayActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
//                finish();
//                break;
            case R.id.fabExit:
                exitAlertDialog.show();
                break;
        }
    }
    private void exitButtonIn() {
        if (translateAnimatorIn == null) {
            translateAnimatorIn = ObjectAnimator.ofFloat(fabExit, "TranslationY", -200, 1);
            translateAnimatorIn.setDuration(1500);
            translateAnimatorIn.setInterpolator(interpolator);
            translateAnimatorIn.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (translateAnimatorOut != null && translateAnimatorOut.isRunning())
                        translateAnimatorOut.cancel();

                    fabExit.setVisibility(View.VISIBLE);
                    rotationAnimator.start();
                }
                @Override
                public void onAnimationEnd(Animator animation) {
                }
                @Override
                public void onAnimationCancel(Animator animation) {
                }
                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
        }
        translateAnimatorIn.start();
    }
    private void exitButtonOut() {
        if (fabExit.getVisibility() == View.GONE) {
            return;
        }
        if (translateAnimatorOut == null) {
            translateAnimatorOut = ObjectAnimator.ofFloat(fabExit, "TranslationY", 1, -200);
            translateAnimatorOut.setDuration(1200);
            translateAnimatorOut.setInterpolator(interpolator);
            translateAnimatorOut.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (translateAnimatorIn != null && translateAnimatorIn.isRunning())
                        translateAnimatorIn.cancel();
                    rotationAnimator.start();
                }
                @Override
                public void onAnimationEnd(Animator animation) {
                    fabExit.setVisibility(View.GONE);
                }
                @Override
                public void onAnimationCancel(Animator animation) {
                }
                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        translateAnimatorOut.start();
    }
    private float mLastMotionX;
    private float mLastMotionY;
    private int touchSlop = 0;
    Handler handler=new Handler(){
    @Override
    public void handleMessage(Message msg) {
        switch (msg.what){
                case MicroFingerVein.USB_HAS_REQUST_PERMISSION:
                {
                    UsbDevice  usbDevice=(UsbDevice) msg.obj;
                    UsbManager mManager=(UsbManager)getSystemService(Context.USB_SERVICE);
                    PendingIntent mPermissionIntent = PendingIntent.getBroadcast(NewMainActivity.this, 0, new Intent(ACTION_USB_PERMISSION), 0);
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
//                            Logger.e(usbDevice.getManufacturerName()+"  节点："+usbDevice.getDeviceName());
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
//                        handler.obtainMessage(MSG_SHOW_LOG,"UsbDeviceConnection.");
                //----------------------------------------------
                if(msg.obj!=null) {
                    UsbDeviceConnection usbDevConn=(UsbDeviceConnection)msg.obj;
                }
                //----------------------------------------------
                //if(msg.obj!=null) {
                //   microFingerVein.close();
                //}
                //----------------------------------------------
            }
            break;
        }
    }
};
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                final float x = ev.getX();
                final int xDiff = (int) Math.abs(x - mLastMotionX);
                final float y = ev.getY();
                final int yDiff = (int) Math.abs(y - mLastMotionY);
                boolean yMoved = yDiff > touchSlop;
                if (yMoved) {//上下滑动
                    if (yDiff > xDiff) {
                        if ((mLastMotionY - y) > 0 && yDiff > 20) {
                            //上滑隐藏退出按钮
                            exitButtonOut();
                        } else if ((y - mLastMotionY) > 0 && yDiff > 100) {
                            //下滑显示退出按钮
                            exitButtonIn();
                        }
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                // Release the drag.
                break;
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = ev.getY();
                mLastMotionX = ev.getX();
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
    @Override
    public void onError(ApiException e) {
    }
    @Override
    public void onPermissionError(ApiException e) {
    }
    @Override
    public void onResultError(ApiException e) {
    }

    @Override
    public void downloadNotReceiver(DownLoadData resultResponse) {
    }

    @Override
    public void downloadSuccess(DownLoadData resultResponse) {
    }
    @Override
    public void downloadApK(UpDateBean resultResponse) {
    }

    @Override
    public void syncUserFacePagesSuccess(SyncUserFace resultResponse) {

    }

    ArrayList<Person> SyncFeaturesPages = new ArrayList<>();
    int totalPage=0,currentPage=0,downloadPage=0;
    @Override
    public void getPagesInfo(PagesInfoBean resultResponse) {
        totalPage=resultResponse.getData().getPageCount();
        for(int x=0;x<8;x++){
            if(x>totalPage-1){
                return;
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    currentPage++;
                    Logger.e(currentPage+"currentPage");
                    downloadFeature.syncUserFeaturePages(FileUtils.loadDataFromFile(NewMainActivity.this, "deviceId.text"),currentPage);
                }
            }).start();
        }
    }

    @Override
    public void syncUserSuccess(DownLoadData resultResponse) {
        personDao = BaseApplication.getInstances().getDaoSession().getPersonDao();
        Person person = new Person();
        if (resultResponse.getData().size() > 0) {
            personDao.deleteAll();
            personDao.insertInTx(resultResponse.getData());
            Toast.makeText(NewMainActivity.this, getResources().getString(R.string.syn_data), Toast.LENGTH_SHORT).show();
        }

        exitAlertDialog.dismiss();
    }

    @Override
    public void syncUserFeaturePagesSuccess(SyncFeaturesPage resultResponse) {
        downloadPage++;
        Logger.e(downloadPage+"downloadPage");
        if(totalPage>8&&currentPage<totalPage){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    currentPage++;
                    Logger.e(currentPage+"currentPage");
                    downloadFeature.syncUserFeaturePages(FileUtils.loadDataFromFile(NewMainActivity.this, "deviceId.text"),currentPage);
                }
            }).start();
        }
        SyncFeaturesPages.addAll(resultResponse.getData());
        if(downloadPage==totalPage){
            PersonDao personDao=BaseApplication.getInstances().getDaoSession().getPersonDao();
            personDao.insertInTx(resultResponse.getData());
            Logger.e(SyncFeaturesPages.size()+"数据同步完成");
            NetworkInfo info =connectivityManager.getActiveNetworkInfo(); //获取活动的网络连接信息
            if (info != null) {   //当前没有已激活的网络连接（表示用户关闭了数据流量服务，也没有开启WiFi等别的数据服务）
                downloadFeature.appUpdateInfo(FileUtils.loadDataFromFile(NewMainActivity.this, "deviceId.text"));
            }else {
                Toast.makeText(NewMainActivity.this, "网络已断开，请查看网络", Toast.LENGTH_LONG).show();
            }
            exitAlertDialogshow.dismiss();
        }
    }
    private class ExitAlertDialog extends Dialog  {
        private Context mContext;
        TextView texttitle;
        private EditText etPwd;
        private Button btCancel;
        private Button btConfirm;
        public ExitAlertDialog(Context context, int theme) {
            super(context, theme);
            mContext = context;
            initDialog();
        }
        public ExitAlertDialog(Context context) {
            super(context, R.style.customer_dialog);
            mContext = context;
            initDialog();
        }
        private void initDialog() {
            View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_exit_confirm, null);
            setContentView(view);
            texttitle=(TextView)view.findViewById(R.id.text_title);
            SharedPreferences sharedPreference=getSharedPreferences("user_info",0);
            texttitle.setText("设备ID:"+sharedPreference.getString("deviceId",""));
        }
        @Override
        public void show() {
            super.show();
        }

    }
    @Override
    public void onBackPressed() {
        // super.onBackPressed();
    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WorkService.microFingerVein.close();
        CleanMessageUtil.clearAllCache(getApplicationContext());
//        Intent intent=new Intent(NewMainActivity.this,WorkService.class);
//        stopService(intent);
        unregisterReceiver(mesReceiver);//释放广播接收者
    }
    Intent intent;
    public void todialog(String text) {
        toJson(text);
       android.app.AlertDialog.Builder dialog=new android.app.AlertDialog.Builder(this);
        dialog.setTitle(titile);
        dialog.setIcon(R.drawable.app_icon_small);
        dialog.setMessage("请确认是否"+titile);
        dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Logger.e("NewMainActivity============type"+type);
                switch (type){
                    case 0:
                         intent=new Intent();
                        intent.setClass(NewMainActivity.this,EliminateActivity.class);
                        intent.putExtra("elminitaLesson",toJson(text));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        dialog.dismiss();
                        break;
                    case 1:
                         intent=new Intent();
                        intent.setClass(NewMainActivity.this,EliminateActivity.class);
                        intent.putExtra("downLesson",toJson(text));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        dialog.dismiss();
                        break;

                    case 2:
                        intent=new Intent();
                        intent.setClass(NewMainActivity.this,PayActivity.class);
                        intent.putExtra("menbertopay",toJson(text));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        dialog.dismiss();

                        break;

                }
            }})
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }}
                ).create();
        dialog.show();
//        this.textView.append(text + "\n");
    }
    int type;String string,titile;
    private String toJson(String text){
        try {
            JSONObject object=new JSONObject(text);
            type=object.getInt("type");
            string=object.getString("data");
        }catch (JSONException e){
            e.printStackTrace();
        }
        return string;
    }
    /**
     * 广播接收器
     *
     * @author kevin
     */
    public class MesReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            tv_time.setText(intent.getStringExtra("timethisStr"));
            timeText.setText(intent.getStringExtra("timeStr"));
            data_time.setText(intent.getStringExtra("timeData"));
            if (context == null) {
            }
        }
    }
}
