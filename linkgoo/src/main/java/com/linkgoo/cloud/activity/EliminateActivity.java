package com.soonvein.cloud.activity;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.soonvein.cloud.base.DataCleanMassage;
import com.soonvein.cloud.bean.Member;
import com.soonvein.cloud.core.BaseAppCompatActivity;
import com.soonvein.cloud.fragment.EliminateLessonMainFragment;

import com.soonvein.cloud.fragment.LessonFragment_Three;
import com.soonvein.cloud.fragment.LessonFragment_test;
import com.soonvein.cloud.utils.CleanMessageUtil;
import com.soonvein.cloud.view.NoScrollViewPager;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;


/**
 * Created by Administrator on 2017/8/17.
 */

public class EliminateActivity extends BaseAppCompatActivity implements CallBackValue {
    //    @Bind(R.id.bind_page)
//    ViewPager viewPager;
    @Bind(R.id.tv_time)
    TextView timeStr;
    @Bind(R.id.home_back_bt)
    Button home_back;
    @Bind(R.id.lesson_one_Cimg)
    ImageView lesson_one_Cimg;
    @Bind(R.id.lesson_one_Pimg)
    ImageView lesson_one_Pimg;
    @Bind(R.id.lesson_one_tv)
    TextView lesson_one_tv;
    @Bind(R.id.lesson_two_Cimg)
    ImageView lesson_two_Cimg;
    @Bind(R.id.lesson_two_Pimg)
    ImageView lesson_two_Pimg;
    @Bind(R.id.lesson_two_tv)
    TextView lesson_two_tv;
    @Bind(R.id.lesson_three_Cimg)
    ImageView lesson_three_Cimg;
    @Bind(R.id.lesson_three_Pimg)
    ImageView lesson_three_Pimg;
    @Bind(R.id.lesson_three_tv)
    TextView lesson_three_tv;
    @Bind(R.id.lesson_four_Cimg)
    ImageView lesson_four_Cimg;
    @Bind(R.id.lesson_four_Pimg)
    ImageView lesson_four_Pimg;
    @Bind(R.id.lesson_four_tv)
    TextView lesson_four_tv;

    private ArrayList<Fragment> mFragmentList = new ArrayList<Fragment>();
    public static final String ACTION_UPDATEUI = "action.updateTiem";
    //记录当前用户信息
    private Member memberInfo;
    private EliminateLessonMainFragment eliminateLessonMainFragment;
    private LessonFragment_test lessonFragment_test;
    private LessonFragment_Three lessonFragment_three;
    public NoScrollViewPager viewPager;
    private MesReceiver mesReceiver;
    private MediaPlayer mediaPlayer,mediaPlayer0,mediaPlayer1,mediaPlayer2;
    TextView tvTitle;

    private boolean hasFinish = false;
    private int recLen = 40;
    Handler handler;
    Runnable runnable;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        DataCleanMassage.cleanApplicationData(this);
        mediaPlayer0=MediaPlayer.create(this,R.raw.putfinger_member);
        mediaPlayer1=MediaPlayer.create(this,R.raw.select_lesson);
        mediaPlayer2=MediaPlayer.create(this,R.raw.sign_success);
        mediaPlayer=MediaPlayer.create(this,R.raw.putfinger_coach);
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
            hasFinish = false;
            if (runnable != null) {
                handler.removeCallbacks(runnable);
                recLen = 40;
            }
            startAD();
        }
        return super.dispatchTouchEvent(ev);
    }
    @Override
    public void setActivtyChange(String string) {
        switch (string) {
            case "1":
                mediaPlayer.start();
                lesson_one_Cimg.setImageResource(R.drawable.flow_circle_pressed);
                lesson_one_Pimg.setImageResource(R.drawable.new_puting_pressed);
                lesson_one_tv.setTextColor(getResources().getColor(R.color.colorText));
                lesson_two_Cimg.setImageResource(R.drawable.flow_circle);
                lesson_two_Pimg.setImageResource(R.drawable.new_puting);
                lesson_two_tv.setTextColor(getResources().getColor(R.color.edittv));
                lesson_three_Cimg.setImageResource(R.drawable.flow_circle);
                lesson_three_Pimg.setImageResource(R.drawable.new_puting);
                lesson_three_tv.setTextColor(getResources().getColor(R.color.edittv));
                lesson_four_Cimg.setImageResource(R.drawable.flow_circle);
                lesson_four_Pimg.setImageResource(R.drawable.new_finish);
                lesson_four_tv.setTextColor(getResources().getColor(R.color.edittv));
                break;
            case "2":
                mediaPlayer0.start();
                lesson_one_Cimg.setImageResource(R.drawable.flow_circle);
                lesson_one_Pimg.setImageResource(R.drawable.new_puting);
                lesson_one_tv.setTextColor(getResources().getColor(R.color.edittv));
                lesson_two_Cimg.setImageResource(R.drawable.flow_circle_pressed);
                lesson_two_Pimg.setImageResource(R.drawable.new_puting_pressed);
                lesson_two_tv.setTextColor(getResources().getColor(R.color.colorText));
                lesson_three_Cimg.setImageResource(R.drawable.flow_circle);
                lesson_three_Pimg.setImageResource(R.drawable.new_puting);
                lesson_three_tv.setTextColor(getResources().getColor(R.color.edittv));
                lesson_four_Cimg.setImageResource(R.drawable.flow_circle);
                lesson_four_Pimg.setImageResource(R.drawable.new_finish);
                lesson_four_tv.setTextColor(getResources().getColor(R.color.edittv));
                break;
            case "3":
                mediaPlayer1.start();
                lesson_one_Cimg.setImageResource(R.drawable.flow_circle);
                lesson_one_Pimg.setImageResource(R.drawable.new_puting);
                lesson_one_tv.setTextColor(getResources().getColor(R.color.edittv));
                lesson_two_Cimg.setImageResource(R.drawable.flow_circle);
                lesson_two_Pimg.setImageResource(R.drawable.new_puting);
                lesson_two_tv.setTextColor(getResources().getColor(R.color.edittv));
                lesson_three_Cimg.setImageResource(R.drawable.flow_circle_pressed);
                lesson_three_Pimg.setImageResource(R.drawable.new_puting_pressed);
                lesson_three_tv.setTextColor(getResources().getColor(R.color.colorText));
                lesson_four_Cimg.setImageResource(R.drawable.flow_circle);
                lesson_four_Pimg.setImageResource(R.drawable.new_finish);
                lesson_four_tv.setTextColor(getResources().getColor(R.color.edittv));
                break;
            case "4":
                mediaPlayer2.start();
                lesson_one_Cimg.setImageResource(R.drawable.flow_circle);
                lesson_one_Pimg.setImageResource(R.drawable.new_puting);
                lesson_one_tv.setTextColor(getResources().getColor(R.color.edittv));
                lesson_two_Cimg.setImageResource(R.drawable.flow_circle);
                lesson_two_Pimg.setImageResource(R.drawable.new_puting);
                lesson_two_tv.setTextColor(getResources().getColor(R.color.edittv));
                lesson_three_Cimg.setImageResource(R.drawable.flow_circle);
                lesson_three_Pimg.setImageResource(R.drawable.new_puting);
                lesson_three_tv.setTextColor(getResources().getColor(R.color.edittv));
                lesson_four_Cimg.setImageResource(R.drawable.flow_circle_pressed);
                lesson_four_Pimg.setImageResource(R.drawable.new_finish_pressed);
                lesson_four_tv.setTextColor(getResources().getColor(R.color.colorText));
                break;
        }
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
                            intent.setClass(EliminateActivity.this, NewMainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }
                };
                handler.postDelayed(runnable, 1000);
                Logger.e("SigeActivity=======startAD()");
    }
    @Override
    protected int getLayoutId() {
        return R.layout.lesson_layout;
    }
    @Override
    protected void onStart() {
        super.onStart();
        mediaPlayer.start();
//        startAD();
    }
    @Override
    protected void initViews(Bundle savedInstanceState) {
        mesReceiver=new MesReceiver();
        timeStr = (TextView) findViewById(R.id.tv_time);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setText("上课");
        viewPager=(NoScrollViewPager)findViewById(R.id.bind_page);
        eliminateLessonMainFragment=new EliminateLessonMainFragment();
        mFragmentList.add(eliminateLessonMainFragment);
        FragmentManager fm=getSupportFragmentManager();
        SectionsPagerAdapter mfpa=new SectionsPagerAdapter(fm,mFragmentList); //new myFragmentPagerAdater记得带上两个参数
        viewPager.setAdapter(mfpa);
        viewPager.setCurrentItem(0);
    }
    @Override
    public void onBackPressed() {
        // super.onBackPressed();
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
//        unregisterReceiver(mesReceiver);//释放广播接收者
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CleanMessageUtil.clearAllCache(getApplicationContext());
        if (runnable!=null) {
            handler.removeCallbacks(runnable);
        }
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
                Intent intent=new Intent();
                intent.setClass(this,NewMainActivity.class);
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
