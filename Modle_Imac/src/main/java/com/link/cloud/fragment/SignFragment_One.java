package com.link.cloud.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.link.cloud.BaseApplication;
import com.link.cloud.R;
import com.link.cloud.activity.NewMainActivity;
import com.link.cloud.activity.WorkService;
import com.link.cloud.base.ApiException;
import com.link.cloud.bean.Member;
import com.link.cloud.bean.SignUserdata;
import com.link.cloud.greendao.gen.PersonDao;
import com.link.cloud.greendaodemo.Person;
import com.orhanobut.logger.Logger;

import com.link.cloud.activity.CallBackValue;
import com.link.cloud.activity.SigeActivity;
import com.link.cloud.contract.MatchVeinTaskContract;
import com.link.cloud.core.BaseFragment;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

import butterknife.Bind;
import md.com.sdk.MicroFingerVein;


import static com.alibaba.sdk.android.ams.common.util.HexUtil.hexStringToByte;
import static com.link.cloud.utils.Utils.byte2hex;

public class SignFragment_One extends BaseFragment implements MatchVeinTaskContract.MatchVeinView{
    @Bind(R.id.layout_two)
    LinearLayout layout_two;
    @Bind(R.id.layout_three)
    LinearLayout layout_three;
    @Bind(R.id.bind_member_next)
    Button next;
    @Bind(R.id.bind_member_name)
    TextView menber_name;
    @Bind(R.id.bind_member_cardtype)
    TextView cardtype;
    @Bind(R.id.userType)
    TextView userType;
    @Bind(R.id.bind_member_cardnumber)
    TextView cardnumber;
    @Bind(R.id.bind_member_begintime)
    TextView startTime;
    @Bind(R.id.bind_member_endtime)
    TextView endTime;
    @Bind(R.id.bind_member_sex)
    TextView menber_sex;
    @Bind(R.id.bind_member_phone)
    TextView menber_phone;
    @Bind(R.id.layout_error_text)
    LinearLayout layout_error_text;
    @Bind(R.id.text_error)
    TextView text_error;
    @Bind(R.id.button_layout)
    LinearLayout button_layout;
    private Member mMemberInfo;
    private Context context;
    private String phoneNum, deviceID, veinFingerID, price, mark;
    private int doWhat = 0;
    private boolean flag=false;
    byte[] aByte={0};
    int action;
    public SigeActivity activity;
    private SharedPreferences userInfo;
    public static CallBackValue callBackValue;
    MatchVeinTaskContract matchVeinTaskContract;
    byte[] featuer = null;
    int state = 0;
    int[] pos = new int[1];
    float[] score = new float[1];
    boolean ret = false;
    int[] tipTimes = {0, 0};//后两次次建模时用了不同手指，重复提醒限制3次
    int modOkProgress = 0;
    private PersonDao personDao;
    String deviceId,uid;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity=(SigeActivity) activity;
        callBackValue=(CallBackValue) activity;
    }
    public static SignFragment_One newInstance() {
        SignFragment_One fragment = new SignFragment_One();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=activity.getApplicationContext();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
    @Override
    protected int getLayoutId() {
        return R.layout.layout_bind_member;
    }
    @Override
    protected void initViews(View self, Bundle savedInstanceState) {
        callBackValue.setActivtyChange("2");
        layout_two.setVisibility(View.GONE);
        layout_three.setVisibility(View.VISIBLE);
    }
    @Override
    protected void initListeners() {
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void initData() {
        matchVeinTaskContract=new MatchVeinTaskContract();
        matchVeinTaskContract.attachView(this);
        userInfo = activity.getSharedPreferences("user_info", 0);
        deviceID = userInfo.getString("DeviceID", "");
        new Thread(new Runnable() {
            @Override
            public void run() {
                executeSql();
            }
        }).start();
        setupParam();
    }
    @Override
    protected void onVisible() {
    }
    @Override
    protected void onInvisible() {
    }
 Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    text_error.setText("请正确放置手指...");
                    break;
                case 1:
                    text_error.setText("验证成功");
                    SharedPreferences userinfo=activity.getSharedPreferences("user_info",0);
                    deviceId=userinfo.getString("deviceId","");
                    personDao.loadAll();
                    QueryBuilder qb = personDao.queryBuilder();
                    int value=pos[0]+1;
                    List<Person> users = qb.where(PersonDao.Properties.Id.eq(value)).build().list();
                    Logger.e("SignFragment_One"+"============="+(value+1) +"======");
                    uid=users.get(0).getUid();
                    showProgress(true);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            matchVeinTaskContract.signedMember(deviceId,uid,"vein");
                        }
                    }).start();
                    break;
                case 2:
                    text_error.setText("验证失败...");
                    runnable=new Runnable() {
                        @Override
                        public void run() {
                            text_error.setText("请按图示放置手指");
                        }
                    };
                    mHandler.postDelayed(runnable,1000);
                    break;
                case 3:
                    text_error.setText("请移开手指");
                    break;
            }
        }
    };

    boolean flog=true;
//    public void run()
//    {
//        layout_error_text.setVisibility(View.VISIBLE);
//        if (ret != true) {
//            Log.i("fingetopen","failed");
//        } else {
//            Log.i("fingetopen","success");
//        }
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (flog) {
////                    identify_process();
//                }
//            }
//        }).start();
//    }
    @Override
    public void onPermissionError(ApiException e) {
    }
    private Runnable runnable;
    @Override
    public void onError(ApiException e) {
        this.showProgress(false);
        String reg = "[^\u4e00-\u9fa5]";
        String syt=e.getMessage().replaceAll(reg, "");
        Logger.e("SignActivity"+syt);
        runnable=new Runnable() {
            @Override
            public void run() {
                text_error.setText("请按图示放置手指");
            }
        };
        text_error.setText(syt);
        mHandler.postDelayed(runnable,2000);
    }
    @Override
    public void onResultError(ApiException e) {
    }
    Handler mHandler=new Handler();
    @Override
    public void signSuccess(SignUserdata signedResponse) {
        this.showProgress(false);
        SignFragment_Two fragment = SignFragment_Two.newInstance(signedResponse);
        ((SignInMainFragment)this.getParentFragment()).addFragment(fragment, 1);
    }
    private volatile boolean bRun=false;
    private Thread mdWorkThread=null;//进行建模或认证的全局工作线程
    private void setupParam() {
        layout_error_text.setVisibility(View.VISIBLE);
        bRun=true;
        mdWorkThread=new Thread(runnablemol);
        mdWorkThread.start();
    }
    Runnable  runnablemol=new Runnable() {
        @Override
        public void run() {
            while (bRun) {
                state = WorkService.microFingerVein.fvdev_get_state();
                //设备连接正常则进入正常建模或认证流程
//                Logger.e("BindActivty===========state"+state);
                if (state != 0) {
                    Logger.e("BindActivty===========state" + state);
                    if (state == 1 || state == 2) {
                        continue;
                    } else if (state == 3) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    byte[] img = WorkService.microFingerVein.fvdev_grab();
                    Logger.e("BindActivty===========img" + img);
                    if (img == null) {
                        continue;
                    }
                    ret=WorkService.microFingerVein.fv_index(featuer, featuer.length / 3352, img, pos, score);
                    Logger.e("BindActivty===========count" +featuer.length / 3352 +"pos==="+pos[0]+"score= ="+score[0]);
                    if (ret == true && score[0] > 0.63) {
                        Log.e("Identify success,", "pos=" + pos[0] + ", score=" + score[0]);
                        if (handler != null) {
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                        }
                        bRun=false;
                    } else {
                        if (handler != null) {
                            Log.e("Identify failed,", "ret=" + ret + ",pos=" + pos[0] + ", score=" + score[0]);
                            Message message = new Message();
                            message.what = 2;
                            handler.sendMessage(message);
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    };
//    private void identify_process()
//    {
//        try {
//            Thread.sleep(30);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        ret = MicroFingerVein.fvdevGetState(state);
//        if (ret != true) {
//            MicroFingerVein.fvdevOpen();
//            return;
//        }
//        if (state[0] != 3) {
//            return;
//        }
//        img1 = MicroFingerVein.fvdevGrabImage();
//        if (img1 == null) {
//            return;
//        }
////        Logger.e("SignActivity"+byte2hex(featuer));
//        ret = MicroFingerVein.fvSearchFeature(featuer,featuer.length/3352, img1, pos, score);
//        if (ret == true && score[0] > 0.63) {
//            Log.e("Identify success,", "pos=" + pos[0] + ", score=" + score[0]);
//            if (handler!=null) {
//                Message message=new Message();
//                message.what=1;
//                handler.sendMessage(message);
//            }
//            flog=false;
//        } else {
//            Log.e("Identify failed,", "ret=" + ret + ",pos=" + pos[0] + ", score=" + score[0]);
////            handler.sendEmptyMessage(4);
//            if (handler!=null) {
//                Message message=new Message();
//                message.what=2;
//                handler.sendMessage(message);
//            }
//        }
//        while (state[0] == 3) {
////            Logger.e("SigeActivity======"+"请移开手指");
//            MicroFingerVein.fvdevGetState(state);
//        }
//    }
    void  executeSql() {
        long startime=System.currentTimeMillis();
        Log.e("SignFragment_one","startime:"+startime);
//        Logger.e("SigeActivity-------"+"executeSql");
        personDao= BaseApplication.getInstances().getDaoSession().getPersonDao();
        personDao.loadAll();
        String sql = "select FINGERMODEL from PERSON" ;
        int i =0;
        Cursor cursor = BaseApplication.getInstances().getDaoSession().getDatabase().rawQuery(sql,null);
        byte[][] feature=new byte[cursor.getCount()][];
        while (cursor.moveToNext()){
//            Logger.e("SigeActivity----no---");
            int nameColumnIndex = cursor.getColumnIndex("FINGERMODEL");
            String strValue=cursor.getString(nameColumnIndex);
//            Logger.e("SigeActivity-------"+strValue);
            feature[i]=hexStringToByte(strValue);
            i++;
        }
        int len = 0;
        // 计算一维数组长度
        for (byte[] element : feature) {
            len += element.length;
        }
        // 复制元素
        featuer = new byte[len];
        int index = 0;
        for (byte[] element : feature) {
            for (byte element2 : element) {
                featuer[index++] = element2;
            }
        }
        long endtime=System.currentTimeMillis();
        Log.e("SignFragment_one","endtime:"+endtime);
//        Logger.e("SignActivity======feature"+byte2hex(featuer));
    }
    @Override
    public void onStart() {
        super.onStart();
    }
    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onDestroy() {
        bRun=false;

        if (handler!=null) {
            handler.removeCallbacksAndMessages(null);
            Logger.e("SignFragment_one"+"onDestroy");
        }
        handler=null;
        super.onDestroy();
    }
}
