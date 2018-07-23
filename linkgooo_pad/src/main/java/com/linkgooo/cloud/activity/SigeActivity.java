package com.soonvein.cloud.activity;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.soonvein.cloud.R;
import com.soonvein.cloud.base.CountDownTimerUtil;
import com.soonvein.cloud.bean.Member;
import com.soonvein.cloud.core.BaseAppCompatActivity;
import com.soonvein.cloud.fragment.BindVeinMainFragment;
import com.soonvein.cloud.fragment.RegisterFragment_Three;
import com.soonvein.cloud.fragment.RegisterFragment_Two;
import com.soonvein.cloud.fragment.SignFragment_Three;
import com.soonvein.cloud.fragment.SignFragment_Two;
import com.soonvein.cloud.fragment.SignInMainFragment;
import com.soonvein.cloud.utils.CleanMessageUtil;
import com.soonvein.cloud.view.NoScrollViewPager;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;


/**
 * Created by Administrator on 2017/8/17.
 */

public class SigeActivity extends BaseAppCompatActivity implements CallBackValue{
    //    @Bind(R.id.bind_page)
//    ViewPager viewPager;
    @Bind(R.id.tv_time)
    TextView timeStr;
    @Bind(R.id.home_back_bt)
    Button home_back;
    @Bind(R.id.sign_one_Cimg)
    ImageView sign_one_Cimg;
    @Bind(R.id.sign_one_Pimg)
    ImageView sign_one_Pimg;
    @Bind(R.id.sign_one_tv)
    TextView sign_one_tv;
    @Bind(R.id.sign_two_Cimg)
    ImageView sign_two_Cimg;
    @Bind(R.id.sign_two_Pimg)
    ImageView sign_two_Pimg;
    @Bind(R.id.sign_two_tv)
    TextView sign_two_tv;
    @Bind(R.id.sign_three_Cimg)
    ImageView sign_three_Cimg;
    @Bind(R.id.sign_three_Pimg)
    ImageView sign_three_Pimg;
    @Bind(R.id.sign_three_tv)
    TextView sign_three_tv;
    @Bind(R.id.sign_four_Cimg)
    ImageView sign_four_Cimg;
    @Bind(R.id.sign_four_Pimg)
    ImageView sign_four_Pimg;
    @Bind(R.id.sign_four_tv)
    TextView sign_four_tv;
    private ArrayList<Fragment> mFragmentList = new ArrayList<Fragment>();
    public static final String ACTION_UPDATEUI = "action.updateTiem";
    //记录当前用户信息
    private Member memberInfo;
    private SignInMainFragment signInMainFragment;
    private SignFragment_Two signFragment_two;
    private SignFragment_Three signFragment_three;
    public NoScrollViewPager viewPager;
    private MesReceiver mesReceiver;
    private MediaPlayer mediaPlayer,mediaPlayer1,mediaPlayer2;
    TextView tvTitle;
    private int recLen = 40;
    Handler handler;
    Runnable runnable;
    private boolean hasFinish = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,WindowManager.LayoutParams. FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        mediaPlayer=MediaPlayer.create(this,R.raw.phone_4);
//        init();
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN && hasFinish == false) {
            hasFinish = true;
            if (runnable != null) {
                home_back.setText(" 返回首页 ");
                handler.removeCallbacks(runnable);
            }
        }else {
                hasFinish=false;
            if (runnable != null) {
                handler.removeCallbacks(runnable);
                recLen = 40;
            }
            startAD();
        }
        return super.dispatchTouchEvent(ev);
    }
    public void startAD() {
        recLen=40;
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (recLen<=30) {
                    home_back.setText(" 返回首页 " + recLen + " s");
                }else {
                    home_back.setText(" 返回首页 ");
                }
                recLen--;
                handler.postDelayed(this,1000);
                if (recLen < 0) {
                    Intent intent = new Intent();
                    intent.setClass(SigeActivity.this, NewMainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };
        handler.postDelayed(runnable, 1000);
        Logger.e("SignActivity=======+startAD()");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.sign_layout;
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onStart() {
        super.onStart();
        mediaPlayer.start();
        startAD();
    }
    @Override
    protected void initViews(Bundle savedInstanceState) {
        mesReceiver=new MesReceiver();
        timeStr = (TextView) findViewById(R.id.tv_time);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setText("会员签到");
        viewPager=(NoScrollViewPager)findViewById(R.id.sige_page);
        signInMainFragment=new SignInMainFragment();
        mFragmentList.add(signInMainFragment);
        signFragment_two=new SignFragment_Two();
        mFragmentList.add(signFragment_two);
        signFragment_three=new SignFragment_Three();
        mFragmentList.add(signFragment_three);
        FragmentManager fm=getSupportFragmentManager();
        SectionsPagerAdapter mfpa=new SectionsPagerAdapter(fm,mFragmentList); //new myFragmentPagerAdater记得带上两个参数
        viewPager.setAdapter(mfpa);
        viewPager.setCurrentItem(0);
    }
    @Override
    public void setActivtyChange(String string) {
        switch (string) {
            case "1":
                mediaPlayer.start();
                sign_one_Pimg.setImageResource(R.drawable.flow_circle_pressed);
                sign_one_Pimg.setImageResource(R.drawable.new_phone_pressed);
                sign_one_tv.setTextColor(getResources().getColor(R.color.colorText));
                sign_two_Cimg.setImageResource(R.drawable.flow_circle);
                sign_two_Pimg.setImageResource(R.drawable.new_puting);
                sign_two_tv.setTextColor(getResources().getColor(R.color.edittv));
                sign_three_Cimg.setImageResource(R.drawable.flow_circle);
                sign_three_Pimg.setImageResource(R.drawable.new_message);
                sign_three_tv.setTextColor(getResources().getColor(R.color.edittv));
                sign_four_Cimg.setImageResource(R.drawable.flow_circle);
                sign_four_Pimg.setImageResource(R.drawable.new_finish);
                sign_four_tv.setTextColor(getResources().getColor(R.color.edittv));
                break;
            case "2":
                mediaPlayer1=MediaPlayer.create(this,R.raw.putfinger_right);
                mediaPlayer1.start();
                sign_one_Cimg.setImageResource(R.drawable.flow_circle);
                sign_one_Pimg.setImageResource(R.drawable.new_phone);
                sign_one_tv.setTextColor(getResources().getColor(R.color.edittv));
                sign_two_Cimg.setImageResource(R.drawable.flow_circle_pressed);
                sign_two_Pimg.setImageResource(R.drawable.new_puting_pressed);
                sign_two_tv.setTextColor(getResources().getColor(R.color.colorText));
                sign_three_Cimg.setImageResource(R.drawable.flow_circle);
                sign_three_Pimg.setImageResource(R.drawable.new_message);
                sign_three_tv.setTextColor(getResources().getColor(R.color.edittv));
                sign_four_Cimg.setImageResource(R.drawable.flow_circle);
                sign_four_Pimg.setImageResource(R.drawable.new_finish);
                sign_four_tv.setTextColor(getResources().getColor(R.color.edittv));
                break;
            case "3":
                sign_one_Cimg.setImageResource(R.drawable.flow_circle);
                sign_one_Pimg.setImageResource(R.drawable.new_phone);
                sign_one_tv.setTextColor(getResources().getColor(R.color.edittv));
                sign_two_Cimg.setImageResource(R.drawable.flow_circle);
                sign_two_Pimg.setImageResource(R.drawable.new_puting);
                sign_two_tv.setTextColor(getResources().getColor(R.color.edittv));
                sign_three_Cimg.setImageResource(R.drawable.flow_circle_pressed);
                sign_three_Pimg.setImageResource(R.drawable.new_message_pressed);
                sign_three_tv.setTextColor(getResources().getColor(R.color.colorText));
                sign_four_Cimg.setImageResource(R.drawable.flow_circle);
                sign_four_Pimg.setImageResource(R.drawable.new_finish);
                sign_four_tv.setTextColor(getResources().getColor(R.color.edittv));
                break;
            case "4":
                mediaPlayer2=MediaPlayer.create(this,R.raw.sign_success);
                mediaPlayer2.start();
                sign_one_Cimg.setImageResource(R.drawable.flow_circle);
                sign_one_Pimg.setImageResource(R.drawable.new_phone);
                sign_one_tv.setTextColor(getResources().getColor(R.color.edittv));
                sign_two_Cimg.setImageResource(R.drawable.flow_circle);
                sign_two_Pimg.setImageResource(R.drawable.new_puting);
                sign_two_tv.setTextColor(getResources().getColor(R.color.edittv));
                sign_three_Cimg.setImageResource(R.drawable.flow_circle);
                sign_three_Pimg.setImageResource(R.drawable.new_message);
                sign_three_tv.setTextColor(getResources().getColor(R.color.edittv));
                sign_four_Cimg.setImageResource(R.drawable.flow_circle_pressed);
                sign_four_Pimg.setImageResource(R.drawable.new_finish_pressed);
                sign_four_tv.setTextColor(getResources().getColor(R.color.colorText));
                break;
        }
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void initData() {
        mesReceiver=new MesReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_UPDATEUI);
        registerReceiver(mesReceiver, intentFilter);
    }

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
    protected void onDestroy() {
        super.onDestroy();
        CleanMessageUtil.clearAllCache(getApplicationContext());
        if(runnable!=null) {
            handler.removeCallbacks(runnable);
        }
        unregisterReceiver(mesReceiver);
        if (mediaPlayer!=null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }else if (mediaPlayer1!=null) {
            mediaPlayer1.stop();
            mediaPlayer1.release();
        }
        else if (mediaPlayer2!=null) {
            mediaPlayer2.stop();
            mediaPlayer2.release();
            mediaPlayer2 = null;
        }
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
                Intent intent=new Intent();
                intent.setClass(this,NewMainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
        }
    }
    @Override
    public void onBackPressed() {
        // super.onBackPressed();
    }

    /**
     * 广播接收器
     *
     * @author kevin
     */
    public class MesReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            timeStr.setText(intent.getStringExtra("timeStr"));
//            Logger.e("NewMainActivity" + intent.getStringExtra("timeStr"));
            if (context == null) {
                context.unregisterReceiver(this);
            }
        }
    }
}
