package com.link.cloud.activity;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.link.cloud.R;
import com.link.cloud.contract.MatchVeinTaskContract;
import com.link.cloud.fragment.SignFragment_One;
import com.link.cloud.greendao.gen.PersonDao;

import com.link.cloud.bean.Member;
import com.link.cloud.core.BaseAppCompatActivity;
import com.link.cloud.fragment.SignFragment_Three;
import com.link.cloud.fragment.SignFragment_Two;
import com.link.cloud.fragment.SignInMainFragment;
import com.link.cloud.utils.CleanMessageUtil;
import com.link.cloud.view.NoScrollViewPager;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;
import md.com.sdk.MicroFingerVein;


/**
 * Created by Administrator on 2017/8/17.
 */

public class SigeActivity extends BaseAppCompatActivity implements CallBackValue{
//    @Bind(R.id.bing_main_page)
//    NoScrollViewPager viewPager;
    @Bind(R.id.layout_page_time)
    TextView timeStr;
    @Bind(R.id.layout_page_title)
    TextView tvTitle;
//    @Bind(R.id.home_back_bt)
//    TextView home_back;
    @Bind(R.id.bind_one_Cimg)
    ImageView bind_one_Cimg;
    @Bind(R.id.bind_one_line)
    View bind_one_line;
    @Bind(R.id.bind_one_tv)
    TextView bind_one_tv;
    @Bind(R.id.bind_two_Cimg)
    ImageView bind_two_Cimg;
    @Bind(R.id.bind_two_line)
    View bind_two_line;
    @Bind(R.id.layout_main_error)
    LinearLayout layout_main_error;
    @Bind(R.id.bind_two_tv)
    TextView bind_two_tv;
    @Bind(R.id.bind_three_Cimg)
    ImageView bind_three_Cimg;
    @Bind(R.id.bind_three_line)
    View bind_three_line;
    @Bind(R.id.bind_three_tv)
    TextView bind_three_tv;
    @Bind(R.id.bind_four_Cimg)
    ImageView bind_four_Cimg;
    @Bind(R.id.bind_four_tv)
    TextView bind_four_tv;
    @Bind(R.id.mian_text_error)
    TextView text_error;
    @Bind(R.id.text_tile)
    TextView text_tile;
    private ArrayList<Fragment> mFragmentList = new ArrayList<Fragment>();
    public static final String ACTION_UPDATEUI = "action.updateTiem";
    Handler mHandler=new Handler();
    //记录当前用户信息
    private Member memberInfo;
    private SignInMainFragment signInMainFragment;
    public NoScrollViewPager viewPager;
    private MesReceiver mesReceiver;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,WindowManager.LayoutParams. FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }
    @Override
    protected int getLayoutId() {
        return R.layout.layout_main_bind;
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void initViews(Bundle savedInstanceState) {
        WorkService.setActactivity(this);
        text_tile.setText("会员签到");
        viewPager=(NoScrollViewPager)findViewById(R.id.bing_main_page) ;
        mesReceiver=new MesReceiver();
        signInMainFragment=new SignInMainFragment();
        mFragmentList.add(signInMainFragment);
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
//                mediaPlayer1.start();
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
//                mediaPlayer1.start();
                fingersign();
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
    private void fingersign(){
        if (mHandler!=null) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent();
                    intent.setClass(SigeActivity.this, NewMainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }, 3000);
        }
    }
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
        bind_one_tv.setVisibility(View.INVISIBLE);
        bind_one_Cimg.setVisibility(View.INVISIBLE);
        bind_one_line.setVisibility(View.INVISIBLE);
        bind_one_tv.setText("输入手机号四");
        bind_two_tv.setText("放置手指");
        bind_three_tv.setText("签到成功");
        tvTitle.setText("会员签到");
        bind_three_line.setVisibility(View.INVISIBLE);
        bind_four_Cimg.setVisibility(View.INVISIBLE);
        bind_four_tv.setVisibility(View.INVISIBLE);
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
        WorkService.microFingerVein.close();
        super.onDestroy();
        if (mHandler!=null){
            mHandler.removeCallbacksAndMessages(null);
        }
        WorkService.microFingerVein.close();
        mHandler=null;
        CleanMessageUtil.clearAllCache(getApplicationContext());
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
            timeStr.setText(intent.getStringExtra("timethisStr"));
            if (context == null) {
                context.unregisterReceiver(this);
            }
        }
    }

}
