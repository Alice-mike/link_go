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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.anupcowkur.reservoir.Reservoir;
import com.link.cloud.BaseApplication;
import com.link.cloud.R;

import com.link.cloud.base.ApiException;
import com.link.cloud.bean.DownLoadData;
import com.link.cloud.contract.SyncUserFeature;
import com.link.cloud.greendao.gen.PersonDao;
import com.link.cloud.greendaodemo.Person;

import com.orhanobut.logger.Logger;

import com.link.cloud.constant.Constant;
import com.link.cloud.utils.CleanMessageUtil;
import com.link.cloud.utils.ToastUtils;
import com.link.cloud.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import md.com.sdk.MicroFingerVein;


/**
 * Created by Administrator on 2017/8/18.
 */

public class NewMainActivity extends AppCompatActivity implements SyncUserFeature.syncUser{
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
    MicroFingerVein microFingerVein;
    private String deviceID;
    public static final String ACTION_UPDATEUI = "action.updateTiem";
    public static final String ACTION_DATABASES = "action.databases";
    ObjectAnimator rotationAnimator,translateAnimatorIn, translateAnimatorOut;
    OvershootInterpolator interpolator;
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
    SyncUserFeature syncUserFeature;
//    WorkService.MyBinder myBinder=null;
//    NewMainActivity activity = null;
    WorkService service;
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main_activity);
        ButterKnife.bind(this);

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
            {
                try{
                    if (os!=null){
                        os.close();
                    }
                    process.destroy();
                }catch (Exception e){

                }
            }
            return true;
        }
    }
    private void inview() {
        syncUserFeature=new SyncUserFeature();
        syncUserFeature.attachView(this);
        exitAlertDialog = new ExitAlertDialog(NewMainActivity.this);
        interpolator = new OvershootInterpolator();
        rotationAnimator = ObjectAnimator.ofFloat(fabExit, "rotation", 0, 360 * 2);
        rotationAnimator.setDuration(1000);
        rotationAnimator.setInterpolator(interpolator);
        tvTitle.setText("欢迎使用");
        tv_time.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                SharedPreferences sharedPreferences=getSharedPreferences("user_info",0);
                String deviceId=sharedPreferences.getString("deviceId","");
                syncUserFeature.syncUser(deviceId);
                return false;
            }
        });
    }
    NewMainActivity activity;
    private void init() {
        utils=new Utils();
        microFingerVein=MicroFingerVein.getInstance(this);
        mesReceiver = new MesReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_UPDATEUI);
        registerReceiver(mesReceiver, intentFilter);
        intent = new Intent(this, WorkService.class);
        startService(intent);
        WorkService.setActactivity(this);
    }
    @OnClick({R.id.bt_main_bind,R.id.bt_main_sign,R.id.bt_main_pay,R.id.bt_main_up,R.id.bt_main_down,R.id.fabExit})
    public void OnClick(View view){
        switch (view.getId()){
            case R.id.bt_main_bind:
                intent = new Intent();
                intent.setClass(NewMainActivity.this,BindAcitvity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
                finish();
            break;
            case R.id.bt_main_sign:
                intent = new Intent();
                intent.setClass(NewMainActivity.this,SigeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
                finish();
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
    public void syncUserSuccess(DownLoadData resultResponse) {
        if (resultResponse.getDown_userInfo().length>0){
            personDao= BaseApplication.getInstances().getDaoSession().getPersonDao();
            personDao.deleteAll();
            Person person=new Person();
            for (int i=0;i<resultResponse.getDown_userInfo().length;i++){
                person.setId((long) (i+1));
                person.setPos(i+"");
                person.setUid(resultResponse.getDown_userInfo()[i].getUid());
                person.setNumber(resultResponse.getDown_userInfo()[i].getUserName());
                person.setFingermodel(resultResponse.getDown_userInfo()[i].getFeature());
                personDao.insert(person);

            }
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
//            btCancel = (Button) view.findViewById(R.id.btCancel);
//            btConfirm = (Button) view.findViewById(R.id.btConfirm);
//            etPwd = (EditText) view.findViewById(R.id.deviceCode);
//            btCancel.setOnClickListener(this);
//            btConfirm.setOnClickListener(this);
//            this.setOnDismissListener(dialog -> {
//                InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//                mInputMethodManager.hideSoftInputFromWindow(etPwd.getWindowToken(), 0);
//                exitButtonOut();
//            });
        }

        @Override
        public void show() {
//            etPwd.setText("");
            super.show();
        }
//        @Override
//        public void onClick(View v) {
//            switch (v.getId()) {
//                case R.id.btCancel:
//                    this.dismiss();
//                    break;
//                case R.id.btConfirm:
//                    String pwd = etPwd.getText().toString().trim();
//                    if (Utils.isEmpty(pwd)) {
//                        ToastUtils.show(mContext, "请输入密码", ToastUtils.LENGTH_SHORT);
//                        return;
//                    }
//                    String repwd;
//                    try {
//                        repwd = Reservoir.get(Constant.KEY_PASSWORD, String.class);
//                    } catch (Exception e) {
//                        repwd = "888888";
//                    }
//                    if (!pwd.equals(repwd)) {
//                        ToastUtils.show(mContext, "密码不正确", ToastUtils.LENGTH_SHORT);
////                        mediaPlayer1.start();
//                        return;
//                    }
//                    try {
////                        Reservoir.delete(Constant.KEY_DEVICE_ID);
//                    } catch (Exception e) {
//                    }
//                    userInfo = NewMainActivity.this.getSharedPreferences("user_info", 0);
//                    deviceID = userInfo.getString("DeviceID", "");
//                    intent =new Intent();
//                    Logger.e("NewMainActivity===="+utils.getCurrentDeviceID());
////                    intent.setClass(NewMainActivity.this,LoginActivity.class);
//                    intent.putExtra("mdeviceID",deviceID);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                    this.dismiss();
//                    finish();
//                    break;
//            }
//        }
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
        WorkService.microFingerVein.close();
        super.onDestroy();
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
                        finish();
                        break;

                    case 1:
                         intent=new Intent();
                        intent.setClass(NewMainActivity.this,EliminateActivity.class);
                        intent.putExtra("downLesson",toJson(text));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        dialog.dismiss();
                        finish();
                        break;

                    case 2:
                        intent=new Intent();
                        intent.setClass(NewMainActivity.this,PayActivity.class);
                        intent.putExtra("menbertopay",toJson(text));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        dialog.dismiss();
                        finish();
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
            context.unregisterReceiver(this);
            }
        }
    }
}
