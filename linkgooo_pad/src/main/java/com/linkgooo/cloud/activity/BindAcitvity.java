package com.soonvein.cloud.activity;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AsyncPlayer;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
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
import com.soonvein.cloud.bean.Member;
import com.soonvein.cloud.core.BaseAppCompatActivity;
import com.soonvein.cloud.fragment.BindVeinMainFragment;

import com.soonvein.cloud.fragment.RegisterFragment_Three;
import com.soonvein.cloud.fragment.RegisterFragment_Two;
import com.soonvein.cloud.utils.CleanMessageUtil;
import com.soonvein.cloud.view.NoScrollViewPager;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/8/24.
 */

public class BindAcitvity extends BaseAppCompatActivity implements CallBackValue{

    @Bind(R.id.bind_page)
    NoScrollViewPager viewPager;
    @Bind(R.id.tv_time)
    TextView timeStr;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.home_back_bt)
    Button home_back;
    @Bind(R.id.bind_one_Cimg)
    ImageView bind_one_Cimg;
    @Bind(R.id.bind_one_Pimg)
    ImageView bind_one_Pimg;
    @Bind(R.id.bind_one_tv)
    TextView bind_one_tv;
    @Bind(R.id.bind_two_Cimg)
    ImageView bind_two_Cimg;
    @Bind(R.id.bind_two_Pimg)
    ImageView bind_two_Pimg;
    @Bind(R.id.bind_two_tv)
    TextView bind_two_tv;
    @Bind(R.id.bind_three_Cimg)
    ImageView bind_three_Cimg;
    @Bind(R.id.bind_three_Pimg)
    ImageView bind_three_Pimg;
    @Bind(R.id.bind_three_tv)
    TextView bind_three_tv;
    @Bind(R.id.bind_four_Cimg)
    ImageView bind_four_Cimg;
    @Bind(R.id.bind_four_Pimg)
    ImageView bind_four_Pimg;
    @Bind(R.id.bind_four_tv)
    TextView bind_four_tv;
    private ArrayList<Fragment> mFragmentList = new ArrayList<Fragment>();
    public static final String ACTION_UPDATEUI = "action.updateTiem";
    //记录当前用户信息
    private Member memberInfo;
    private BindVeinMainFragment bindVeinMainFragment;
    private RegisterFragment_Two registerFragment_two;
    private RegisterFragment_Three registerFragment_three;
    private MesReceiver mesReceiver;
    private MediaPlayer mediaPlayer0,mediaPlayer1,mediaPlayer2,mediaPlayer;
    private int recLen=40;
    private Runnable runnable;
    private Handler handler;
    private boolean hasFinish = false;
    private AsyncPlayer asyncPlayer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,WindowManager.LayoutParams. FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        mediaPlayer= MediaPlayer.create(this,R.raw.phone_11);
        mediaPlayer0=MediaPlayer.create(this,R.raw.sure_massage);
        mediaPlayer1=MediaPlayer.create(this,R.raw.putfinger_right_3);
        mediaPlayer2=MediaPlayer.create(this,R.raw.bind_success);
    }
    @Override
    protected int getLayoutId() {
        return R.layout.bind_layout;
    }
    @Override
    protected void onStart() {
        super.onStart();
        mediaPlayer.start();
    }
    @Override
    protected void initViews(Bundle savedInstanceState) {
        mesReceiver=new MesReceiver();
        tvTitle.setText("绑定手指");
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
                mediaPlayer.start();
                bind_one_Cimg.setImageResource(R.drawable.flow_circle_pressed);
                bind_one_Pimg.setImageResource(R.drawable.new_phone_pressed);
                bind_one_tv.setTextColor(getResources().getColor(R.color.colorText));
                bind_two_Cimg.setImageResource(R.drawable.flow_circle);
                bind_two_Pimg.setImageResource(R.drawable.new_message);
                bind_two_tv.setTextColor(getResources().getColor(R.color.edittv));
                bind_three_Cimg.setImageResource(R.drawable.flow_circle);
                bind_three_Pimg.setImageResource(R.drawable.new_puting);
                bind_three_tv.setTextColor(getResources().getColor(R.color.edittv));
                bind_four_Cimg.setImageResource(R.drawable.flow_circle);
                bind_four_Pimg.setImageResource(R.drawable.new_finish);
                bind_four_tv.setTextColor(getResources().getColor(R.color.edittv));
                break;
            case "2":
                mediaPlayer0.start();
                bind_one_Cimg.setImageResource(R.drawable.flow_circle);
                bind_one_Pimg.setImageResource(R.drawable.new_phone);
                bind_one_tv.setTextColor(getResources().getColor(R.color.edittv));
                bind_two_Cimg.setImageResource(R.drawable.flow_circle_pressed);
                bind_two_Pimg.setImageResource(R.drawable.new_message_pressed);
                bind_two_tv.setTextColor(getResources().getColor(R.color.colorText));
                bind_three_Cimg.setImageResource(R.drawable.flow_circle);
                bind_three_Pimg.setImageResource(R.drawable.new_puting);
                bind_three_tv.setTextColor(getResources().getColor(R.color.edittv));
                bind_four_Cimg.setImageResource(R.drawable.flow_circle);
                bind_four_Pimg.setImageResource(R.drawable.new_finish);
                bind_four_tv.setTextColor(getResources().getColor(R.color.edittv));
                break;
            case "3":
                mediaPlayer1.start();
                bind_one_Cimg.setImageResource(R.drawable.flow_circle);
                bind_one_Pimg.setImageResource(R.drawable.new_phone);
                bind_one_tv.setTextColor(getResources().getColor(R.color.edittv));
                bind_two_Cimg.setImageResource(R.drawable.flow_circle);
                bind_two_Pimg.setImageResource(R.drawable.new_message);
                bind_two_tv.setTextColor(getResources().getColor(R.color.edittv));
                bind_three_Cimg.setImageResource(R.drawable.flow_circle_pressed);
                bind_three_Pimg.setImageResource(R.drawable.new_puting_pressed);
                bind_three_tv.setTextColor(getResources().getColor(R.color.colorText));
                bind_four_Cimg.setImageResource(R.drawable.flow_circle);
                bind_four_Pimg.setImageResource(R.drawable.new_finish);
                bind_four_tv.setTextColor(getResources().getColor(R.color.edittv));
                break;
            case "4":
                mediaPlayer2.start();
                bind_one_Cimg.setImageResource(R.drawable.flow_circle);
                bind_one_Pimg.setImageResource(R.drawable.new_phone);
                bind_one_tv.setTextColor(getResources().getColor(R.color.edittv));
                bind_two_Cimg.setImageResource(R.drawable.flow_circle);
                bind_two_Pimg.setImageResource(R.drawable.new_message);
                bind_two_tv.setTextColor(getResources().getColor(R.color.edittv));
                bind_three_Cimg.setImageResource(R.drawable.flow_circle);
                bind_three_Pimg.setImageResource(R.drawable.new_puting);
                bind_three_tv.setTextColor(getResources().getColor(R.color.edittv));
                bind_four_Cimg.setImageResource(R.drawable.flow_circle_pressed);
                bind_four_Pimg.setImageResource(R.drawable.new_finish_pressed);
                bind_four_tv.setTextColor(getResources().getColor(R.color.colorText));
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
//        etPhoneNum.setShowSoftInputOnFocus(false);
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN && hasFinish == false ) {
            hasFinish = true;
            if (runnable != null) {
                home_back.setText(" 返回首页 ");
                handler.removeCallbacks(runnable);
            }
        }else {
            hasFinish = false;
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
        super.onDestroy();
        CleanMessageUtil.clearAllCache(getApplicationContext());
        if (runnable!=null){
            handler.removeCallbacks(runnable);
        }
        unregisterReceiver(mesReceiver);
        if (mediaPlayer!=null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }else if (mediaPlayer0!=null) {
            mediaPlayer0.stop();
            mediaPlayer0.release();
            mediaPlayer0 = null;
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
                 intent.setClass(BindAcitvity.this,NewMainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
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
            timeStr.setText(intent.getStringExtra("timeStr"));
//            Logger.e("NewMainActivity" + intent.getStringExtra("timeStr"));
            if (context == null) {
                context.unregisterReceiver(this);
            }
        }
    }
}
