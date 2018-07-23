package com.soonvein.cloud.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
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

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.anupcowkur.reservoir.Reservoir;
import com.orhanobut.logger.Logger;
import com.soonvein.cloud.BaseApplication;
import com.soonvein.cloud.R;
import com.soonvein.cloud.base.DataCleanMassage;
import com.soonvein.cloud.constant.Constant;
import com.soonvein.cloud.utils.CleanMessageUtil;
import com.soonvein.cloud.utils.ToastUtils;
import com.soonvein.cloud.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/8/18.
 */

public class NewMainActivity extends AppCompatActivity {
    @Bind(R.id.fabExit)
    FloatingActionButton fabExit;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_time)
    TextView tv_time;
    @Bind(R.id.btn_bind_main)
    Button btn_bind;
    @Bind(R.id.btn_sign_main)
    Button btn_sign;
    @Bind(R.id.up_lesson_main)
    Button btn_lesson;
    @Bind(R.id.down_lesson_main)
    Button down_lesson;
    @Bind(R.id.btn_pay_main)
    Button btn_pay;
    @Bind(R.id.test_push)
    TextView textView;
    private Utils utils;
//    ImageButton btn_bind;
    private SharedPreferences userInfo;
    private MediaPlayer mediaPlayer,mediaPlayer1,mediaPlayer2,mediaPlayer3;
    private MesReceiver mesReceiver;
    private Intent intent;
    private String deviceID;
    public static final String ACTION_UPDATEUI = "action.updateTiem";
    ObjectAnimator rotationAnimator,translateAnimatorIn, translateAnimatorOut;
    OvershootInterpolator interpolator;
    ExitAlertDialog exitAlertDialog;

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_actmain);
        ButterKnife.bind(this);
        DataCleanMassage.cleanApplicationData(this);
        inview();
        init();
//        try{
//            Logger.e("NewMainActivity"+CleanMessageUtil.getTotalCacheSize(getApplicationContext()));
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        BaseApplication.setMainActivity(this);
    }
    private void inview() {
        exitAlertDialog = new ExitAlertDialog(NewMainActivity.this);
        interpolator = new OvershootInterpolator();
        rotationAnimator = ObjectAnimator.ofFloat(fabExit, "rotation", 0, 360 * 2);
        rotationAnimator.setDuration(1000);
        rotationAnimator.setInterpolator(interpolator);
        tvTitle.setText("欢迎使用");
    }
    private void init() {
        utils=new Utils();
        mesReceiver = new MesReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_UPDATEUI);
        registerReceiver(mesReceiver, intentFilter);
        intent = new Intent(this, WorkService.class);
        startService(intent);
    }
    @OnClick({R.id.btn_bind_main,R.id.btn_sign_main,R.id.btn_pay_main,R.id.up_lesson_main,R.id.down_lesson_main,R.id.fabExit})
    public void OnClick(View view){
        switch (view.getId()){
            case R.id.btn_bind_main:
                intent = new Intent();
                intent.setClass(NewMainActivity.this,BindAcitvity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
                finish();
            break;
            case R.id.btn_sign_main:
                intent = new Intent();
                intent.setClass(NewMainActivity.this,SigeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
                finish();
                break;
            case R.id.up_lesson_main:
                intent = new Intent();
                intent.setClass(NewMainActivity.this,EliminateActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
                finish();
                break;
            case R.id.down_lesson_main:
                intent = new Intent();
                intent.setClass(NewMainActivity.this,LessonDownActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
                finish();
                break;
            case R.id.btn_pay_main:
                intent = new Intent();
                intent.setClass(NewMainActivity.this,PayActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
                finish();
                break;
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
    private class ExitAlertDialog extends Dialog implements View.OnClickListener {
        private Context mContext;
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
            btCancel = (Button) view.findViewById(R.id.btCancel);
            btConfirm = (Button) view.findViewById(R.id.btConfirm);
            etPwd = (EditText) view.findViewById(R.id.deviceCode);
            btCancel.setOnClickListener(this);
            btConfirm.setOnClickListener(this);
            this.setOnDismissListener(dialog -> {
                InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                mInputMethodManager.hideSoftInputFromWindow(etPwd.getWindowToken(), 0);
                exitButtonOut();
            });
        }
        @Override
        public void show() {
            etPwd.setText("");
            super.show();
        }
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btCancel:
                    this.dismiss();
                    break;
                case R.id.btConfirm:
                    String pwd = etPwd.getText().toString().trim();
                    if (Utils.isEmpty(pwd)) {
                        ToastUtils.show(mContext, "请输入密码", ToastUtils.LENGTH_SHORT);
                        return;
                    }
                    String repwd;
                    try {
                        repwd = Reservoir.get(Constant.KEY_PASSWORD, String.class);
                    } catch (Exception e) {
                        repwd = "888888";
                    }
                    if (!pwd.equals(repwd)) {
                        ToastUtils.show(mContext, "密码不正确", ToastUtils.LENGTH_SHORT);
//                        mediaPlayer1.start();
                        return;
                    }
                    try {
//                        Reservoir.delete(Constant.KEY_DEVICE_ID);
                    } catch (Exception e) {
                    }
                    userInfo = NewMainActivity.this.getSharedPreferences("user_info", 0);
                    deviceID = userInfo.getString("DeviceID", "");
                    intent =new Intent();
                    Logger.e("NewMainActivity===="+utils.getCurrentDeviceID());
                    intent.setClass(NewMainActivity.this,LoginActivity.class);
                    intent.putExtra("mdeviceID",deviceID);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    this.dismiss();
                    finish();
                    break;
            }
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
        CleanMessageUtil.clearAllCache(getApplicationContext());
        unregisterReceiver(mesReceiver);//释放广播接收者
    }
    public void appendConsoleText(String text) {
//        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
//        dialog.setTitle("消课").setIcon(R.drawable.app_icon_small).setMessage("请确认是否消课")
//                .setPositiveButton("是", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intent=new Intent();
//                        intent.setClass(NewMainActivity.this,EliminateActivity.class);
//                        startActivity(intent);
//                        dialog.dismiss();
//                    }})
//                .setNegativeButton("否", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }}
//                ).create();
//        dialog.show();
//        this.textView.append(text + "\n");
    }
    /**
     * 广播接收器
     *
     * @author kevin
     */
    public class MesReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            tv_time.setText(intent.getStringExtra("timeStr"));
//            Logger.e("NewMainActivity" + intent.getStringExtra("timeStr"));
            if (context == null) {
            context.unregisterReceiver(this);
            }
        }
    }
}
