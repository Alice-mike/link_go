package com.soonvein.cloud.contract;

import com.soonvein.cloud.bean.LessonResponse;
import com.soonvein.cloud.constant.Constant;
import com.soonvein.cloud.contract.MatchVeinTaskContract;
import com.soonvein.cloud.core.BasePresenter;
import com.soonvein.cloud.core.MvpView;

/**
 * Created by Administrator on 2017/8/2.
 */

public class Userlesson extends BasePresenter<MatchVeinTaskContract.MatchVeinView> {

    public interface UserLessonContract extends MvpView{
        void verifyUserEliminateLesson(Constant.UsersMessage usersMessage);
        void eliminateLess(LessonResponse lessonResponse);
    }
}
