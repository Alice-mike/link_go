package com.link.cloud.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.link.cloud.bean.Lockdata;
import com.link.cloud.contract.IsopenCabinet;
import com.link.cloud.core.BaseFragment;
import com.link.cloud.greendao.gen.CabinetNumberDao;
import com.link.cloud.greendao.gen.CabinetRecordDao;
import com.link.cloud.greendao.gen.PersonDao;
import com.link.cloud.greendaodemo.CabinetNumber;
import com.link.cloud.greendaodemo.CabinetRecord;
import com.link.cloud.greendaodemo.Person;

import com.link.cloud.utils.CountDownTimer;
import com.orhanobut.logger.Logger;

import org.greenrobot.greendao.query.QueryBuilder;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android_serialport_api.SerialPort;
import butterknife.Bind;
import butterknife.OnClick;
import md.com.sdk.MicroFingerVein;

import static com.alibaba.sdk.android.ams.common.util.HexUtil.hexStringToByte;
/**
 * Created by 30541 on 2018/3/28.
 */
public class FirstFragment extends BaseFragment implements IsopenCabinet.isopen {
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
    private PersonDao personDao;
    private CabinetNumberDao cabinetNumberDao;
    byte[] featuer = null;
    byte[] img1 = null;
    boolean ret = false;
    int state = 0;
    boolean timestart;
    int[] pos = new int[1];
    float[] score = new float[1];
    LockActivity activity;
    IsopenCabinet isopenCabinet;
    MesReceiver mesReceiver;
    boolean flog = true;
    WorkService workService;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (LockActivity) activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Logger.e("FirstFragment"+"======onCreate=======");
        super.onCreate(savedInstanceState);
        isopenCabinet = new IsopenCabinet();
        isopenCabinet.attachView(this);
        openDoorUtil = new OpenDoorUtil();
    }

    public static FirstFragment newInstance() {
        FirstFragment fragment = new FirstFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initListeners() {
        Logger.e("FirstFragment"+"======initListeners=======");
    }

    @Override
    protected void initViews(View self, Bundle savedInstanceState) {
        Logger.e("FirstFragment"+"======initViews=======");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_01;
    }

    @Override
    protected void initData() {
        Logger.e("FirstFragment"+"======initData=======");
        mesReceiver = new MesReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LockActivity.ACTION_UPDATEUI);
        activity.registerReceiver(mesReceiver, intentFilter);
        head_text_02.setText("寄存物品");
        text_error.setText("请正确放置手指...");
        workService = new WorkService();
        time_forfinger.setVisibility(View.INVISIBLE);
        setupParam();
        time_out();
    }
    @Override
    protected void onVisible() {
        Logger.e("FirstFragment"+"======onVisible=======");
    }
    @Override
    protected void onInvisible() {
        timer.cancel();
        Logger.e("FirstFragment"+"======onInvisible=======");
    }

    @OnClick(R.id.head_layout_01)
    public void Onclick(View view) {
        switch (view.getId()) {
            case R.id.head_layout_01:
                bRun = false;
                isview=true;
                if (timer!=null) {
                    timer.cancel();
                    timer = null;
                }
                MainFragment mainFragment = MainFragment.newInstance();
                ((BindVeinMainFragment) getParentFragment()).addFragment(mainFragment, 0);
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
            if (isview==false) {
                time_forfinger.setVisibility(View.INVISIBLE);
            }
            // TODO Auto-generated method stub
            Logger.e("FirstFragment"+millisUntilFinished / 1000);
            if (millisUntilFinished / 1000 <= 30&&isview==false) {
                time_forfinger.setVisibility(View.VISIBLE);
                time_forfinger.setText(millisUntilFinished / 1000 + "");
            }
        }
        @Override
        public void onFinish() {
            if (bRun=true) {
                bRun = false;
            }
            MainFragment mainFragment = MainFragment.newInstance();
            ((BindVeinMainFragment) getParentFragment()).addFragment(mainFragment, 0);
        }
    };
}
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    text_error.setText("请正确放置手指...");
                    break;
                case 1:
                    text_error.setText("验证成功");
                    SharedPreferences userinfo = activity.getSharedPreferences("user_info", 0);
                    String deviceId = userinfo.getString("deviceId", "");
                    personDao = BaseApplication.getInstances().getDaoSession().getPersonDao();
                    QueryBuilder qb = personDao.queryBuilder();
                    String value = pos[0] + "";
                    List<Person> users = qb.where(PersonDao.Properties.Pos.eq(value)).build().list();
                    if (users.size() > 0) {
                        String uid = users.get(0).getUid();
                        Logger.e("ThirdFragment==============" + pos[0] + "==uid=" + uid + "users.size" + users.size());
                        try {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    isopenCabinet.isopen(0, deviceId, uid, "vein");
                                }
                            }).start();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    } else {
                        text_error.setText("没找到该会员数据");
                    }
                    break;
                case 2:
                    text_error.setText("暂无签到数据");
                    break;
                case 7:
                    text_error.setText("验证失败...");
                    break;
                case 8:
                    text_error.setText("请移开手指");
                    break;
            }
        }
    };

    byte[]  executeSql() {
        byte[] nFeatuer=null;
        personDao= BaseApplication.getInstances().getDaoSession().getPersonDao();
        personDao.loadAll();
        int i=0;
        Cursor cursor;
        String sql;
        sql = "select FINGERMODEL from PERSON" ;
        cursor = BaseApplication.getInstances().getDaoSession().getDatabase().rawQuery(sql,null);
        byte[][] feature=new byte[cursor.getCount()][];
        while (cursor.moveToNext()){
            int nameColumnIndex = cursor.getColumnIndex("FINGERMODEL");
            String strValue=cursor.getString(nameColumnIndex);
            feature[i]=hexStringToByte(strValue);
            i++;
        }
        int len = 0;
        // 计算一维数组长度
        if(feature.length>0) {
            for (byte[] element : feature) {
                len += element.length;
            }
            // 复制元素
            nFeatuer = new byte[len];
            int index = 0;
            for (byte[] element : feature) {
                for (byte element2 : element) {
                    nFeatuer[index++] = element2;
                }
            }
        }
        return nFeatuer;
    }
    boolean bopen=false;
    private volatile boolean bRun=false;
    private Thread mdWorkThread=null;//进行建模或认证的全局工作线程
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
                state = activity.microFingerVein.fvdev_get_state();
                //设备连接正常则进入正常建模或认证流程
//                Logger.e("BindActivty===========state"+state);
                if (state != 0) {
                    time_start=false;
                    timer.cancel();
                    featuer=executeSql();
                    Logger.e("FirstFragment===========state" + state);
                    if (state == 1 || state == 2) {
                        continue;
                    } else if (state == 3) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    byte[] img = activity.microFingerVein.fvdev_grab();
                    Logger.e("FirstFragment===========img" + img);
                    if (img == null) {
                        continue;
                    }
                    if (featuer!=null) {
                        ret = activity.microFingerVein.fv_index(featuer, featuer.length/ 3352, img, pos, score);
                        Logger.e("FirstFragment_count"+"===========featuer.length"+featuer.length/3352+"pos"+pos[0]+"ret="+ret);
                    }else {
                        if (handler != null) {
                            Message message = new Message();
                            message.what = 2;
                            handler.sendMessage(message);
                        }
                    }
                    if (ret == true && score[0] > 0.63) {
                        Logger.e("Identify success,"+"pos=" + pos[0] + ", score=" + score[0]);
                        if (handler != null) {
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                            bRun=false;
                        }

                    } else {
                        if (handler != null) {
                            Log.e("Identify failed,", "ret=" + ret + ",pos=" + pos[0] + ", score=" + score[0]);
                            Message message = new Message();
                            message.what = 7;
                            handler.sendMessage(message);
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else {
                    if (time_start==false) {
                        try {
                            timer.start();
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (handler != null) {
                        Message message = new Message();
                        message.what = 0;
                        handler.sendMessage(message);
                    }
                }
            }
        }
    };
    @Override
    public void onError(ApiException e) {
        super.onError(e);
        String reg = "[^\u4e00-\u9fa5]";
        String syt=e.getMessage().replaceAll(reg, "");
        Logger.e("FirstFragment"+syt);
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                text_error.setText(syt);
            }
        });
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
        isview=true;
        layout_three.setVisibility(View.GONE);
        open_lock_layout.setVisibility(View.VISIBLE);
        cabinetNumberDao=BaseApplication.getInstances().getDaoSession().getCabinetNumberDao();
        cabinetRecordDao=BaseApplication.getInstance().getDaoSession().getCabinetRecordDao();
        String numstr=resultResponse.getLockdata().getCabinetnumber();
        QueryBuilder qb1 = cabinetRecordDao.queryBuilder();
        QueryBuilder qb = cabinetNumberDao.queryBuilder();
        cabinetNumberDao.loadAll();
        List<CabinetRecord> users = qb1.where(CabinetRecordDao.Properties.CabinetNumber.eq(numstr)).build().list();
        CabinetRecord cabinetRecord=new CabinetRecord();
        cabinetRecord.setMemberName(resultResponse.getLockdata().getName());
        String number=resultResponse.getLockdata().getNumberValue();
        if (number.length() == 11) {
            number = number.substring(0, 3) + "****" + number.substring(7, number.length());
        }
        cabinetRecord.setPhoneNum(number);
        cabinetRecord.setOpentime(opentime);
        cabinetRecord.setExist("1");
        cabinetRecord.setCabinetStating("寄存物品");
        cabinetRecord.setCabinetNumber(numstr);
        cabinetRecordDao.insert(cabinetRecord);
        cabinetRecordDao.loadAll();
        List<CabinetRecord> users1 = qb1.where(CabinetRecordDao.Properties.CabinetNumber.eq(numstr)).build().list();
        List<CabinetNumber> list;
        list = qb.where(CabinetNumberDao.Properties.CabinetNumber.eq(users1.get(users1.size()-1).getCabinetNumber())).list();
        if (list.size()!=0) {
            Logger.e("FirstFragment"+"=============list.size"+list.size());
            CabinetNumber cabinetNumber = new CabinetNumber();
            cabinetNumber.setId(list.get(0).getId());
            cabinetNumber.setCabinetLockPlate(list.get(0).getCabinetLockPlate());
            cabinetNumber.setCircuitNumber(list.get(0).getCircuitNumber());
            cabinetNumber.setCabinetNumber(list.get(0).getCabinetNumber());
            cabinetNumber.setIsUser("占用");
            cabinetNumberDao.update(cabinetNumber);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                cabinet = qb.where(CabinetNumberDao.Properties.CabinetNumber.eq(numstr)).list();
                Logger.e("FirstFragment"+numstr+"=========================="+cabinet.size());
                try {
                    lockplate=cabinet.get(0).getCabinetLockPlate();
                }catch (Exception e){
                    e.printStackTrace();
                }
                nuberlock=Integer.parseInt(cabinet.get(0).getCabinetNumber());
              if (nuberlock>10){
               nuberlock=nuberlock%10;
              Logger.e("FirstFragment==="+nuberlock);
              if (nuberlock==0){
                nuberlock=10;
              }
                }
                try {
                    if (Integer.parseInt(lockplate)<=10) {
                            activity.serialpprt_wk1.getOutputStream().write(openDoorUtil.openOneDoor(Integer.parseInt(lockplate), nuberlock));
                    }else if (Integer.parseInt(lockplate)>10&&Integer.parseInt(numstr)<=20){
                        activity.serialpprt_wk2.getOutputStream().write(openDoorUtil.openOneDoor(Integer.parseInt(lockplate)%10, nuberlock));
                    }else if (Integer.parseInt(lockplate)>20&&Integer.parseInt(numstr)<=30){
                        activity.serialpprt_wk3.getOutputStream().write(openDoorUtil.openOneDoor(Integer.parseInt(lockplate)%10, nuberlock));
                    }
                    Logger.e("FirstFragment===" + Integer.parseInt(lockplate) + "====" + nuberlock);
                }catch (Exception e){
                }finally {
                    if (timer!=null) {
                        timer.cancel();

                    }
                }
            }
        }).start();
//        Logger.e("opencabind==="+"CabinetLockPlate: "+users.get(0).getCabinetLockPlate()+"Cabinetnumber: "+resultResponse.getLockdata().getCabinetnumber()+"nuberlock: "+nuberlock);
        text_number.setText(resultResponse.getLockdata().getCabinetnumber());
        try {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MainFragment mainFragment = MainFragment.newInstance();
                    ((BindVeinMainFragment) getParentFragment()).addFragment(mainFragment, 1);
                }
            }, 3000);
        }catch (Exception e){
            e.printStackTrace();
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
            head_text_03.setText(intent.getStringExtra("timeStr"));
            opentime=intent.getStringExtra("timeStr");
            if (context == null) {
                context.unregisterReceiver(this);
            }
        }
    }
    @Override
    public void onDestroy() {
        Logger.e("FirstFragment"+"OnDestroy");
        activity.microFingerVein.close(1);
        bRun=false;
        isview=true;
        if (timer!=null) {
            timer.cancel();
        }
        if (handler!=null){
            handler.removeCallbacksAndMessages(null);
            handler=null;
        }
        activity.unregisterReceiver(mesReceiver);//释放广播接收者
        super.onDestroy();
    }
}
