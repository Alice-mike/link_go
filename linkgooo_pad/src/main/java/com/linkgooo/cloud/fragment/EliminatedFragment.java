package com.soonvein.cloud.fragment;

import android.app.Activity;
import android.app.backup.SharedPreferencesBackupHelper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jakewharton.disklrucache.Util;
import com.soonvein.cloud.R;
import com.soonvein.cloud.bean.UserResponse;
import com.soonvein.cloud.constant.Constant;
import com.soonvein.cloud.contract.UserLessonContract;
import com.soonvein.cloud.core.BaseFragment;
import com.soonvein.cloud.utils.Utils;

import java.lang.reflect.Array;

import butterknife.Bind;
import butterknife.OnClick;

import static android.content.Context.MODE_WORLD_WRITEABLE;

/**
 * Created by Administrator on 2017/8/2.
 */

public class EliminatedFragment extends BaseFragment {
//    @Bind(R.id.tvMemberName)
//    TextView tvMemberName;
//    @Bind(R.id.tvMemberPhone)
//    TextView tvMemberPhone;
//    @Bind(R.id.tvuserType)
//    TextView tvuserTyper;
//    @Bind(R.id.tvmemberSex)
//    TextView tvmemberSex;
//    @Bind(R.id.tvnext)
    Button btnext;
    public UserLessonContract userLessonContract;
    private String deviceID, memberUID,coachUID,checkUID;
    private UserLessonContract presenter;
    private EliminateLessonFragment eliminateLessonFragment;
    private NextAction action=NextAction.MENBER_NEXT;
    public UserResponse eliminatedInfo;
    public String[] Uid=new String[3];
    Boolean ischeck;
    public static EliminatedFragment newInstance(UserResponse userResponse) {
        EliminatedFragment fragment = new EliminatedFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constant.EXTRAS_ELIMINATE_INFO, userResponse);
        fragment.setArguments(args);
        return fragment;
    }
    public static EliminatedFragment newInstance(int dowhat) {
        EliminatedFragment fragment = new EliminatedFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constant.EXTRAS_ELIMINATE_INFO,dowhat);
        fragment.setArguments(args);
        return fragment;
    }
    enum NextAction {
        MENBER_NEXT(0),
        COACH_NEXT(1),
        CHECK_NEXT(2);
        int state = 0;
        NextAction(int state) {
            this.state = state;
        }
        public int getState() {
            return state;
        }
    }
    @Override
    protected void onInvisible() {

    }

    @Override
    protected void initData() {
        userLessonContract=new UserLessonContract();
        eliminateLessonFragment=new EliminateLessonFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter=new UserLessonContract();
    }

    @Override
    protected void onVisible() {

    }

    @Override
    protected void initViews(View self, Bundle savedInstanceState) {
//        Bundle bundle = getArguments();
//        if (bundle != null) {
//            eliminatedInfo = (UserResponse) bundle.getSerializable(Constant.EXTRAS_ELIMINATE_INFO);
//            if (eliminatedInfo.getUserTyper()==1){
//                tvuserTyper.setText("教练");
////                Uid[0]=null;
//                Uid[0]=eliminatedInfo.getUid();
//            }else if (eliminatedInfo.getUserTyper()==2){
//                tvuserTyper.setText("会员");
////                Uid[1]=null;
//                Uid[1]=eliminatedInfo.getUid();
//            }else if (eliminatedInfo.getUserTyper()==3){
//                tvuserTyper.setText("员工");
////                Uid[2]=null;
//                Uid[2]=eliminatedInfo.getUid();
//            }
//            tvMemberName.setText(eliminatedInfo.getName());
//            tvMemberPhone.setText(eliminatedInfo.getPhone());
//            tvmemberSex.setText(eliminatedInfo.getSex());
//        }
    }

    @Override
    protected void initListeners() {

    }
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_usermessage;
    }
//@OnClick(R.id.tvnext)
//    public void OnClick(View view){
//    String code;
//    deviceID= Utils.getCurrentDeviceID();
//    switch (view.getId()){
////                case R.id.tvnext:
////                    ((EliminateLessonMainFragment) getParentFragment()).viewPager.setCurrentItem(0);
////
////                    eliminateLessonFragment.setUserID(2);
////                    eliminateLessonFragment.setUsernum(1);
////                break;
//    }
//}
//    public void init(){
////        userLessonContract.eliminateLesson(Utils.getCurrentDeviceID(), Uid[0],Uid[1],Uid[2]);
//    }
}
