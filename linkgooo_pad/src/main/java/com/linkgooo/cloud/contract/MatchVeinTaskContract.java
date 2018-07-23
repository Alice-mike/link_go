package com.soonvein.cloud.contract;

import android.media.MediaPlayer;
import android.support.v4.util.Pair;

//import com.baidu.tts.client.SpeechSynthesizer;
import com.orhanobut.logger.Logger;
import com.soonvein.cloud.BaseApplication;
import com.soonvein.cloud.R;
import com.soonvein.cloud.TestActivityManager;
import com.soonvein.cloud.base.AbsAPICallback;
import com.soonvein.cloud.base.ApiException;
import com.soonvein.cloud.bean.CardInfo;
import com.soonvein.cloud.bean.LessonResponse;
import com.soonvein.cloud.bean.Member;
import com.soonvein.cloud.bean.ReturnBean;
import com.soonvein.cloud.bean.SignedResponse;
import com.soonvein.cloud.bean.Voucher;
import com.soonvein.cloud.constant.Constant;
import com.soonvein.cloud.core.BasePresenter;
import com.soonvein.cloud.core.MvpView;
import com.soonvein.cloud.utils.ReservoirUtils;
import com.soonvein.cloud.utils.RxUtils;
import com.soonvein.cloud.utils.Utils;

import java.util.ArrayList;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by Shaozy on 2016/8/11.
 */
public class MatchVeinTaskContract extends BasePresenter<MatchVeinTaskContract.MatchVeinView> {
//    SpeechSynthesizer mSpeechSynthesizer=SpeechSynthesizer.getInstance();
//    ActMainActivity mainActivity;
    public interface MatchVeinView extends MvpView {
        void verifyTemplateSuccess(String veinFingerID);
        void verifyTemplateFailure(ApiException e);

//        void getMemberInfoSuccess(Member memberInfo);
//
//        void signSuccess(Member memberInfo);

        void signSuccess(SignedResponse signedResponse);

        void costSuccess(Voucher voucherInfo);

    }

    public ReservoirUtils reservoirUtils;

    public MatchVeinTaskContract() {
        this.reservoirUtils = new ReservoirUtils();
    }

    public void getVerifyTemplate() {
        this.mCompositeSubscription.add(this.mDataManager.initSDK()
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<Long, Observable<Pair<ApiException, String>>>() {
                    @Override
                    public Observable<Pair<ApiException, String>> call(Long result) {
                        return Observable.create(new Observable.OnSubscribe<Pair<ApiException, String>>() {
                            @Override
                            public void call(Subscriber<? super Pair<ApiException, String>> subscriber) {
                                subscriber.onNext(MatchVeinTaskContract.this.mDataManager.getVerifyTemplate());
                                subscriber.onCompleted();
                            }
                        }).compose(RxUtils.applyIOToMainThreadSchedulers());
                    }
                })
                .subscribe(new Subscriber<Pair<ApiException, String>>() {
                    @Override
                    public void onCompleted() {
                        if (MatchVeinTaskContract.this.mCompositeSubscription != null)
                            MatchVeinTaskContract.this.mCompositeSubscription.remove(this);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e("MatchVeinTaskContract----onError");
                        ApiException apiException = new ApiException(e, ApiException.MATCH_TEMPLATE_ERROR);
                        apiException.setDisplayMessage("获取验证模板失败！");
                        MatchVeinTaskContract.this.getMvpView().onError(apiException);
                    }

                    @Override
                    public void onNext(Pair<ApiException, String> result) {
                        Logger.e("MatchVeinTaskContract----onNext");
                        if (result.first != null) {
                            MatchVeinTaskContract.this.getMvpView().verifyTemplateFailure(result.first);
                        } else {
                            MatchVeinTaskContract.this.getMvpView().verifyTemplateSuccess(result.second);
                        }
                    }
                }));
    }

//    //TODO 2017/4/3以前签到流程 开始
//    //签到第一步，获取会员信息
//    public void getMemberInfoByVein(String phone, String deviceID, String veinFingerID) {
//        this.mCompositeSubscription.add(this.mDataManager.getMemberInfoByVein(phone, deviceID, veinFingerID)
//                .subscribe(new AbsAPICallback<Member>() {
//                    @Override
//                    public void onCompleted() {
//                        if (MatchVeinTaskContract.this.mCompositeSubscription != null)
//                            MatchVeinTaskContract.this.mCompositeSubscription.remove(this);
//                    }
//
//                    @Override
//                    protected void onError(ApiException e) {
//                        MatchVeinTaskContract.this.getMvpView().onError(e);
//                    }
//
//                    @Override
//                    protected void onPermissionError(ApiException e) {
//                        MatchVeinTaskContract.this.getMvpView().onPermissionError(e);
//                    }
//
//                    @Override
//                    protected void onResultError(ApiException e) {
//                        MatchVeinTaskContract.this.getMvpView().onResultError(e);
//                    }
//
//                    @Override
//                    public void onNext(Member member) {
//                        if (member != null && !Utils.isEmpty(member.getMemID())) {
//                            getCardInfo(member);
//                        } else {
//                            ApiException e = new ApiException(new Throwable("会员不存在"), ApiException.PARSE_ERROR);
//                            e.setDisplayMessage("会员不存在");
////                            mainActivity.startHeCheng("会员不存在");
//                            onResultError(e);
//                        }
//                    }
//                }));
//    }
//
//    //签到第二步，获取签到会员卡信息
//    public void getCardInfo(Member memberInfo) {
//        this.mCompositeSubscription.add(this.mDataManager.getCardInfo(memberInfo.getMemID())
//                .subscribe(new AbsAPICallback<ArrayList<CardInfo>>() {
//                    @Override
//                    public void onCompleted() {
//                        if (MatchVeinTaskContract.this.mCompositeSubscription != null)
//                            MatchVeinTaskContract.this.mCompositeSubscription.remove(this);
//                    }
//
//                    @Override
//                    protected void onError(ApiException e) {
//                        MatchVeinTaskContract.this.getMvpView().onError(e);
//                    }
//
//                    @Override
//                    protected void onPermissionError(ApiException e) {
//                        MatchVeinTaskContract.this.getMvpView().onPermissionError(e);
//                    }
//
//                    @Override
//                    protected void onResultError(ApiException e) {
//                        MatchVeinTaskContract.this.getMvpView().onResultError(e);
//                    }
//
//                    @Override
//                    public void onNext(ArrayList<CardInfo> cardInfos) {
//                        if (BaseApplication.DEBUG) Logger.e("获取会员卡信息成功");
//                        if (cardInfos != null && !cardInfos.isEmpty()) {
//                            memberInfo.setCardInfos(cardInfos);
//                            MatchVeinTaskContract.this.getMvpView().getMemberInfoSuccess(memberInfo);
//                        } else {
//                            ApiException e = new ApiException(new Throwable("没有找到任何会员卡信息"), ApiException.PARSE_ERROR);
//                            e.setDisplayMessage("没有找到任何会员卡信息");
//                            onResultError(e);
//                        }
//                    }
//                }));
//    }
//
//    //签到第三步
//    public void signedMember(Member memberInfo) {
//        this.mCompositeSubscription.add(this.mDataManager.signedMember(memberInfo.getMemID(), memberInfo.getCardInfo().cardID)
//                .subscribe(new AbsAPICallback<ReturnBean>() {
//                    @Override
//                    public void onCompleted() {
//                        if (MatchVeinTaskContract.this.mCompositeSubscription != null)
//                            MatchVeinTaskContract.this.mCompositeSubscription.remove(this);
//                    }
//
//                    @Override
//                    protected void onError(ApiException e) {
//                        MatchVeinTaskContract.this.getMvpView().onError(e);
//                    }
//
//                    @Override
//                    protected void onPermissionError(ApiException e) {
//                        onError(e);
//                    }
//
//                    @Override
//                    protected void onResultError(ApiException e) {
//                        onError(e);
//                    }
//
//                    @Override
//                    public void onNext(ReturnBean returnBean) {
//                        Logger.e("签到成功 threadid : " + Thread.currentThread().getId());
////                        mSpeechSynthesizer.speak("签到成功");
//                        MatchVeinTaskContract.this.getMvpView().signSuccess(memberInfo);
//                    }
//                }));
//    }
    //TODO 2017/4/3以前签到流程 结束

    //2014/4/3新品台签到接口
    public void signedMember(String phone, String deviceID, String veinFingerID) {
        this.mCompositeSubscription.add(this.mDataManager.signedMember(phone, deviceID, veinFingerID)
                .subscribe(new AbsAPICallback<SignedResponse>() {
                    @Override
                    public void onCompleted() {
                        if (MatchVeinTaskContract.this.mCompositeSubscription != null)
                            MatchVeinTaskContract.this.mCompositeSubscription.remove(this);
                    }

                    @Override
                    protected void onError(ApiException e) {
                        MatchVeinTaskContract.this.getMvpView().onError(e);
                    }

                    @Override
                    protected void onPermissionError(ApiException e) {
                        onError(e);
                    }

                    @Override
                    protected void onResultError(ApiException e) {
                        onError(e);
                    }

                    @Override
                    public void onNext(SignedResponse signedResponse) {
                        MatchVeinTaskContract.this.getMvpView().signSuccess(signedResponse);
                    }
                }));
    }
    //消费
    public void consumeRecord(String phoneNum, String deviceID, String veinFingerID, String price, String method, String mark) {
        this.mCompositeSubscription.add(this.mDataManager.consumeRecord(phoneNum, deviceID, veinFingerID, price, method, mark)
                .subscribe(new AbsAPICallback<Voucher>() {
                    @Override
                    public void onCompleted() {
                        if (MatchVeinTaskContract.this.mCompositeSubscription != null)
                            MatchVeinTaskContract.this.mCompositeSubscription.remove(this);
                    }
                    @Override
                    protected void onError(ApiException e) {
                        MatchVeinTaskContract.this.getMvpView().onError(e);
                    }
                    @Override
                    protected void onPermissionError(ApiException e) {
                        MatchVeinTaskContract.this.getMvpView().onPermissionError(e);
                    }
                    @Override
                    protected void onResultError(ApiException e) {
                        MatchVeinTaskContract.this.getMvpView().onResultError(e);
                    }
                    @Override
                    public void onNext(Voucher result) {
                        //result.setCost(Utils.isEmpty(price) ? 0 : Double.valueOf(price));
                        MatchVeinTaskContract.this.getMvpView().costSuccess(result);
                    }
                }));
    }

}
