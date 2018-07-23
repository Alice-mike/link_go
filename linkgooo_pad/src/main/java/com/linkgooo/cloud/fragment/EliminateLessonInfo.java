package com.soonvein.cloud.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.soonvein.cloud.R;
import com.soonvein.cloud.bean.LessonResponse;
import com.soonvein.cloud.bean.Voucher;
import com.soonvein.cloud.constant.Constant;
import com.soonvein.cloud.core.BaseFragment;

import org.w3c.dom.Text;

import butterknife.Bind;

/**
 * Created by Administrator on 2017/8/3.
 */

public class EliminateLessonInfo extends BaseFragment{
    @Bind(R.id.tvMemberName)
    TextView tvMemnerName;
    @Bind(R.id.tvMemberPhone)
    TextView tvMemberPhone;
    @Bind(R.id.tvMemberCoach)
    TextView tvMemberCoach;
    @Bind(R.id.tvLessonName)
    TextView tvLessonName;
    @Bind(R.id.tvLessonDate)
    TextView tvLessonDate;
    private LessonResponse lesson;
    public static EliminateLessonInfo newInstance(LessonResponse lesson) {
        EliminateLessonInfo fragment = new EliminateLessonInfo();
        Bundle args = new Bundle();
        args.putSerializable(Constant.EXTRAS_LESSON_INFO, lesson);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    protected void onInvisible() {

    }

    @Override
    protected void onVisible() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_eliminatesuccess;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void initViews(View self, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            lesson = (LessonResponse) bundle.getSerializable(Constant.EXTRAS_LESSON_INFO);
        }
//        tvLessonName.setText(lesson.getLessonName());
        tvMemberCoach.setText(lesson.getCoach());
        tvMemberPhone.setText(lesson.getMemberphone());
//        tvLessonDate.setText(lesson.getLessonDate());
        tvMemnerName.setText(lesson.getMembername());
        Logger.e("EliminateLEssonInfo:initViews"+lesson.toString());
    }
}
