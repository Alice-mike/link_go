package com.link.cloud.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hotelmanager.xzy.util.OpenDoorUtil;
import com.link.cloud.BaseApplication;
import com.link.cloud.R;
import com.link.cloud.activity.LockActivity;
import com.link.cloud.activity.WorkService;
import com.link.cloud.base.ApiException;
import com.link.cloud.bean.Code_Message;
import com.link.cloud.bean.Lockdata;
import com.link.cloud.contract.IsopenCabinet;
import com.link.cloud.core.BaseFragment;
import com.link.cloud.greendao.gen.CabinetNumberDao;
import com.link.cloud.greendao.gen.CabinetRecordDao;
import com.link.cloud.greendaodemo.CabinetNumber;
import com.link.cloud.greendaodemo.CabinetRecord;
import com.link.cloud.model.MdFvHelper;
import com.link.cloud.utils.CountDownTimer;
import com.link.cloud.utils.Finger_identify;
import com.link.cloud.utils.Utils;
import com.orhanobut.logger.Logger;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by 30541 on 2018/3/28.
 */
public class FourFragment extends BaseFragment implements IsopenCabinet.isopen {
    @Bind(R.id.head_layout_01)
    RelativeLayout head_layout_01;
    @Bind(R.id.head_text_02)
    TextView head_text_02;
    @Bind(R.id.layout_three)
    LinearLayout layout_three;
    @Bind(R.id.open_lock_layout)
    LinearLayout open_lock_layout;
    @Bind(R.id.text_start)
    TextView text_start;
    @Bind(R.id.text_number)
    TextView text_number;
    @Bind(R.id.text_end)
    TextView text_end;
    @Bind(R.id.text_error)
    TextView text_error;
    @Bind(R.id.head_text_03)
    TextView head_text_03;
    @Bind(R.id.time_forfinger)
    TextView time_forfinger;
    OpenDoorUtil openDoorUtil;
    private CabinetNumberDao cabinetNumberDao;
    int state = 0;
    boolean timestart;
    int[] pos = new int[1];
    float[] score = new float[1];
    LockActivity activity;
    IsopenCabinet isopenCabinet;
    MesReceiver mesReceiver;
    boolean flog = true;
    String userUid;
    WorkService workService;
    private final static int MSG_SHOW_LOG=3;
    private final static int MSG_SHOW_START=0;
    private final static int MSG_SHOW_SUCCESS=1;
    Context context;
    String opentype=null;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (LockActivity) activity;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Logger.e("FirstFragment"+"======onCreate======="+bRun);
        super.onCreate(savedInstanceState);
        isopenCabinet = new IsopenCabinet();
        isopenCabinet.attachView(this);
        openDoorUtil = new OpenDoorUtil();
        context=getContext();
    }
    public static FourFragment newInstance(String opentype) {
        FourFragment fragment = new FourFragment();
        Bundle args = new Bundle();
        args.putString("opentype",opentype);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    protected void initListeners() {
        setupParam();
        Logger.e("FirstFragment"+"======initListeners=======");
    }
    @Override
    protected void initViews(View self, Bundle savedInstanceState) {
        Logger.e("FirstFragment" + "======initViews=======");
        time_out();
        Bundle bundle = getArguments();
        if (bundle != null) {
            opentype = bundle.getString("opentype");
            mesReceiver = new MesReceiver();
            IntentFilter intentFilter = new IntentFilter();

            activity.registerReceiver(mesReceiver, intentFilter);
            if ("0".equals(opentype)) {
                head_text_02.setText("寄存物品");
            }else if ("1".equals(opentype)){
                head_text_02.setText("临时存件");
            }else if("2".equals(opentype)){
                head_text_02.setText("离场退柜");
            }
            text_error.setText("请正确放置手指...");
            workService = new WorkService();
            time_forfinger.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    protected int getLayoutId() {
        return R.layout.layout_01;
    }
    @Override
    protected void initData() {
        Logger.e("FirstFragment"+"======initData=======");
        time_out();
    }
    @Override
    protected void onVisible() {
        Logger.e("FirstFragment"+"======onVisible=======");
    }
    @Override
    protected void onInvisible() {
        if (timer!=null) {
            timer.cancel();
        }
        Logger.e("FirstFragment"+"======onInvisible=======");
    }
    @OnClick(R.id.head_layout_01)
    public void Onclick(View view) {
        switch (view.getId()) {
            case R.id.head_layout_01:
                if (Utils.isFastClick()) {
                    isview = true;
                    MainFragment mainFragment = MainFragment.newInstance();
                    ((BindVeinMainFragment) getParentFragment()).setFragment(0);
                }
                break;
        }
    }
    CountDownTimer timer;
    boolean time_start=false;
    boolean isview=false;
    private void time_out() {
    /**
     * 倒计时60秒，一次1秒
     */
    timer = new CountDownTimer(40 * 1000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            time_start=true;
//            if (isview==false&&context!=null) {
//                time_forfinger.setVisibility(View.INVISIBLE);
//            }
            // TODO Auto-generated method stub
//            Logger.e("FirstFragment"+millisUntilFinished / 1000);
            if (isview==false) {
                time_forfinger.setVisibility(View.VISIBLE);
                time_forfinger.setText(millisUntilFinished / 1000 + "");
            }
        }
        @Override
        public void onFinish() {
            MainFragment mainFragment = MainFragment.newInstance();
            ((BindVeinMainFragment) getParentFragment()).setFragment(0);
        }
    };
}
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SHOW_START:
                    if(msg.obj!=null&&text_error!=null) {
                        text_error.setText("请正确放置手指...");
                    }
                    break;
                case MSG_SHOW_SUCCESS:
                    handler.removeMessages(MSG_SHOW_SUCCESS);
                    if(msg.obj!=null&&text_error!=null) {
                        text_error.setText("验证成功");
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    SharedPreferences userinfo = activity.getSharedPreferences("user_info", 0);
                    String deviceId = userinfo.getString("deviceId", "");
                    Logger.e("FirstFragment"+"opentype"+opentype);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    int type=0;
                                    if ("0".equals(opentype)){
                                        type=0;
                                    }else if ("1".equals(opentype)){
                                        type=1;
                                    }else if ("2".equals(opentype)){
                                        type=2;
                                    }
                                    isopenCabinet.isopen(type, deviceId, userUid, "vein");
                                }
                            }).start();
                    break;
//                case 2:
//                    text_error.setText("暂无签到数据");
//                    break;
                case MSG_SHOW_LOG:
                    if (text_error!=null) {
                        if (msg.obj != null && handler != null) {
                            text_error.setText((String) (msg.obj));
                        }
                    }
                    break;
                case 7:
                    if (context!=null) {
                        text_error.setText("验证失败...");
                    }  try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                  break;
                case 8:
                    text_error.setText("请移开手指");
                    break;
            }
        }
    };
    private Thread mdWorkThread=null;//进行建模或认证的全局工作线程
    boolean bRun;
    private void setupParam() {
        bRun=true;
        mdWorkThread=new Thread(runnablemol);
        mdWorkThread.start();
    }
    Runnable  runnablemol=new Runnable() {
        @Override
        public void run() {
            boolean ret = false;
            int[] tipTimes = {0, 0};//后两次次建模时用了不同手指，重复提醒限制3次
            int modOkProgress = 0;
            while (bRun) {
                if(!activity.bopen) {
                    modOkProgress++;
                    activity.bopen = activity.microFingerVein.fvdev_open();//开启指定索引的设备
                    int cnt = activity.microFingerVein.fvdev_get_count();
                    if(cnt == 0){
                        continue;
                    }
                   if (modOkProgress>10){
                       bRun=false;
                   }
                    continue;
                }
                state = activity.microFingerVein.fvdev_get_state();
                if (state != 0) {
                    time_start=false;
                    Logger.e("FirstFragment===========state" + state);
                    byte[] img= MdFvHelper.tryGetFirstBestImg(activity.microFingerVein,0,5);
                    Logger.e("FirstFragment===========img" + img);
                    if (img == null) {
                        continue;
                    }
                    userUid=Finger_identify.Finger_identify(activity,img);
                    if (userUid!=null){
                        if (handler != null) {
                            handler.obtainMessage(MSG_SHOW_SUCCESS,"验证成功").sendToTarget();
                        }
                    }else {
                        if (handler != null) {
                            Log.e("Identify failed,", "ret=" + ret + ",pos=" + pos[0] + ", score=" + score[0]);
                            handler.obtainMessage(MSG_SHOW_LOG,"验证失败").sendToTarget();
                        }
                    }
                }
                else {
                    if (time_start==false&&timer!=null) {
                        try {
                            timer.start();
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (handler != null&&getContext()!=null) {
                        handler.obtainMessage(MSG_SHOW_LOG,"请正确放置手指...").sendToTarget();
                    }
                }
            }
        }
    };
    @Override
    public void onError(ApiException e) {
        super.onError(e);
        bRun=false;
        String reg = "[^\u4e00-\u9fa5]";
        String syt=e.getMessage().replaceAll(reg, "");
        handler.obtainMessage(MSG_SHOW_LOG,syt).sendToTarget();
        try {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MainFragment mainFragment = MainFragment.newInstance();
                    ((BindVeinMainFragment) getParentFragment()).setFragment(0);
                }
            }, 5000);
        }catch (Exception e1){
            e.printStackTrace();
        }
    }
    @Override
    public void onPermissionError(ApiException e) {
        onError(e);
    }
    @Override
    public void onResultError(ApiException e) {
        onError(e);
    }
    int  nuberlock=0;
    List<CabinetNumber> cabinet;
    String lockplate;
    String opentime=null;
    CabinetRecordDao cabinetRecordDao;
    @Override
    public void isopenSuccess(Lockdata resultResponse) {
        if(resultResponse.getStatus()==0) {
            isview = true;
            layout_three.setVisibility(View.GONE);
            open_lock_layout.setVisibility(View.VISIBLE);
            cabinetNumberDao = BaseApplication.getInstances().getDaoSession().getCabinetNumberDao();
            cabinetRecordDao = BaseApplication.getInstance().getDaoSession().getCabinetRecordDao();
            String numstr = resultResponse.getLockdata().getCabinetnumber();
            QueryBuilder qb1 = cabinetRecordDao.queryBuilder();
            QueryBuilder qb = cabinetNumberDao.queryBuilder();
            cabinetNumberDao.loadAll();
            List<CabinetRecord> users = qb1.where(CabinetRecordDao.Properties.CabinetNumber.eq(numstr)).build().list();
            CabinetRecord cabinetRecord = new CabinetRecord();
            cabinetRecord.setMemberName(resultResponse.getLockdata().getName());
            String number = resultResponse.getLockdata().getNumberValue();
            if (number.length() == 11) {
                number = number.substring(0, 3) + "****" + number.substring(7, number.length());
            }
            cabinetRecord.setPhoneNum(number);
            cabinetRecord.setOpentime(opentime);
            if ("0".equals(opentype)) {
                cabinetRecord.setCabinetStating("寄存物品");
                cabinetRecord.setExist("1");
            } else if ("1".equals(opentype)) {
                cabinetRecord.setCabinetStating("临时存件");
                cabinetRecord.setExist("1");
            } else if ("2".equals(opentype)) {
                cabinetRecord.setCabinetStating("离场退柜");
                cabinetRecord.setExist("0");
            }
            cabinetRecord.setCabinetNumber(numstr);
            cabinetRecordDao.insert(cabinetRecord);
            cabinetRecordDao.loadAll();
            List<CabinetRecord> users1 = qb1.where(CabinetRecordDao.Properties.CabinetNumber.eq(numstr)).build().list();
            List<CabinetNumber> list;
            list = qb.where(CabinetNumberDao.Properties.CabinetNumber.eq(users1.get(users1.size() - 1).getCabinetNumber())).list();
            if (list.size() != 0) {
                Logger.e("FirstFragment" + "=============list.size" + list.size());
                CabinetNumber cabinetNumber = new CabinetNumber();
                cabinetNumber.setId(list.get(0).getId());
                cabinetNumber.setCabinetLockPlate(list.get(0).getCabinetLockPlate());
                cabinetNumber.setCircuitNumber(list.get(0).getCircuitNumber());
                cabinetNumber.setCabinetNumber(list.get(0).getCabinetNumber());
                if ("0".equals(opentype)) {
                    cabinetNumber.setIsUser("占用");
                } else if ("2".equals(opentype)) {
                    cabinetNumber.setIsUser("空闲");
                }
                cabinetNumberDao.update(cabinetNumber);
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    cabinet = qb.where(CabinetNumberDao.Properties.CabinetNumber.eq(numstr)).list();
                    Logger.e("FirstFragment" + numstr + "==========================" + cabinet.size());
                    try {
                        lockplate = cabinet.get(0).getCabinetLockPlate();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    nuberlock = Integer.parseInt(cabinet.get(0).getCabinetNumber());
                    if (nuberlock > 10) {
                        nuberlock = nuberlock % 10;
                        Logger.e("FirstFragment===" + nuberlock);
                        if (nuberlock == 0) {
                            nuberlock = 10;
                        }
                    }
                    try {
                        if (Integer.parseInt(lockplate) <= 10) {
                            activity.serialpprt_wk1.getOutputStream().write(openDoorUtil.openOneDoor(Integer.parseInt(lockplate), nuberlock));
                        } else if (Integer.parseInt(lockplate) > 10 && Integer.parseInt(numstr) <= 20) {
                            activity.serialpprt_wk2.getOutputStream().write(openDoorUtil.openOneDoor(Integer.parseInt(lockplate) % 10, nuberlock));
                        } else if (Integer.parseInt(lockplate) > 20 && Integer.parseInt(numstr) <= 30) {
                            activity.serialpprt_wk3.getOutputStream().write(openDoorUtil.openOneDoor(Integer.parseInt(lockplate) % 10, nuberlock));
                        }
                        Logger.e("FirstFragment===" + Integer.parseInt(lockplate) + "====" + nuberlock);
                    } catch (Exception e) {
                    } finally {
                        if (timer != null) {
                            timer.cancel();
                        }
                    }
                }
            }).start();
            text_number.setText(resultResponse.getLockdata().getCabinetnumber());
            bRun = false;
            try {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MainFragment mainFragment = MainFragment.newInstance();
                        ((BindVeinMainFragment) getParentFragment()).setFragment(0);
                    }
                }, 3000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void codeSuccess(Code_Message resultResponse) {

    }

    /**
     * 广播接收器
     *
     * @author kevin
     */
    public class MesReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            head_text_03.setText(intent.getStringExtra("timeStr"));
            opentime=intent.getStringExtra("timeStr");
            if (context == null) {
                context.unregisterReceiver(this);
            }else {

            }
        }
    }
    @Override
    public void onDestroy() {
        activity.unregisterReceiver(mesReceiver);//释放广播接收者
        Logger.e("FirstFragment"+"OnDestroy");
        isview=true;
        if (handler!=null){
            handler.removeCallbacksAndMessages(null);
        }

        super.onDestroy();
    }

}
