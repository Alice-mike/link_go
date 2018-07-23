package com.link.cloud.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anupcowkur.reservoir.Reservoir;
import com.hotelmanager.xzy.util.OpenDoorUtil;
import com.link.cloud.BaseApplication;
import com.link.cloud.R;
import com.link.cloud.activity.LockActivity;
import com.link.cloud.activity.WorkService;
import com.link.cloud.base.ApiException;
import com.link.cloud.bean.CabinetNumberData;
import com.link.cloud.bean.DownLoadData;
import com.link.cloud.bean.ResultResponse;
import com.link.cloud.constant.Constant;
import com.link.cloud.contract.AdminopenCabinet;
import com.link.cloud.contract.CabinetNumberContract;
import com.link.cloud.contract.ClearCabinetContract;
import com.link.cloud.contract.SyncUserFeature;
import com.link.cloud.core.BaseFragment;
import com.link.cloud.greendao.gen.CabinetNumberDao;
import com.link.cloud.greendao.gen.CabinetRecordDao;
import com.link.cloud.greendao.gen.PersonDao;
//import com.link.cloud.greendao.gen.SigePersonDao;
import com.link.cloud.greendao.gen.SignUserDao;
import com.link.cloud.greendaodemo.CabinetNumber;
import com.link.cloud.greendaodemo.CabinetRecord;
import com.link.cloud.greendaodemo.Person;
//import com.link.cloud.greendaodemo.SignPerson;
import com.link.cloud.greendaodemo.SignUser;
//import com.link.cloud.utils.CountDownTimer;
import com.link.cloud.utils.APKVersionCodeUtils;
import com.link.cloud.utils.FileUtils;
import com.link.cloud.utils.ToastUtils;
import com.link.cloud.utils.Utils;
import com.link.cloud.view.CheckUsedRecored;
import com.link.cloud.view.LockMessage;
import com.orhanobut.logger.Logger;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android_serialport_api.SerialPort;
import butterknife.Bind;
import butterknife.OnClick;


/**
 * Created by 30541 on 2018/3/28.
 */
public class MainFragment1 extends BaseFragment implements AdminopenCabinet.adminopen,ClearCabinetContract.clearCabinet,SyncUserFeature.syncUser,CabinetNumberContract.cabinetNumber{
    private EditText etPwd;
    private Button btCancel;
    private Button btConfirm;
    @Bind(R.id.head_text_01)
    TextView head_text_01;
    @Bind(R.id.head_text_03_main)
    TextView head_text_03;
    @Bind(R.id.text_num1)
    TextView text_num1;
    @Bind(R.id.text_num2)
    TextView text_num2;
    @Bind(R.id.text_num3)
    TextView text_num3;
    @Bind(R.id.main_bt_01)
    Button main_bt_01;
    @Bind(R.id.main_bt_02)
    Button main_bt_02;
    @Bind(R.id.main_bt_03)
    Button main_bt_03;
    @Bind(R.id.layout_one)
    LinearLayout layout_one;
    @Bind(R.id.adminmessage)
    LinearLayout adminmessage;
    @Bind(R.id.edit_01)
    EditText clearlock;
    @Bind(R.id.edit_02)
    EditText openlockplate;
    @Bind(R.id.edit_03)
    EditText openlock;
    @Bind(R.id.textView2)
     TextView textView2;
    @Bind(R.id.cabinet_used)
    EditText cabinet_used;
    @Bind(R.id.openlock_one)
    EditText opelock_one;
    @Bind(R.id.head_text_02)
    TextView head_text_02;
    AdminopenCabinet adminopenCabinet;
    ClearCabinetContract clearCabinetContract;
    SyncUserFeature syncUserFeature;
    OpenDoorUtil openDoorUtil;
    MesReceiver mesReceiver;
    LockActivity activity;
    ExitAlertDialog exitAlertDialog;
    public static boolean isStart=false;
    CabinetNumberContract cabinetNumberContract;
    WorkService workService;
    String opentime=null;
    String pwdmodel="0";
    SerialPort serialPort;
    boolean isClearnAll=false,isClearnOther=false;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity=(LockActivity) activity;
    }
    public static MainFragment1 newInstance() {
        MainFragment1 fragment = new MainFragment1();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adminopenCabinet=new AdminopenCabinet();
        clearCabinetContract=new ClearCabinetContract();
        adminopenCabinet.attachView(this);
        clearCabinetContract.attachView(this);
        syncUserFeature=new SyncUserFeature();
        syncUserFeature.attachView(this);
        cabinetNumberContract=new CabinetNumberContract();
        cabinetNumberContract.attachView(this);
        openDoorUtil=new OpenDoorUtil();
        SharedPreferences sharedPreferences=activity.getSharedPreferences("user_info",0);
       String pwd= sharedPreferences.getString("devicepwd",null);
        if (pwd==null){
            sharedPreferences.edit().putString("devicepwd","888888").commit();
            Logger.e("MainFragment1========="+"devicepwd");
        }
    }
    @Override
    protected void initListeners() {
    }
    @Override
    protected void initViews(View self, Bundle savedInstanceState) {
        TextView textView=findView(R.id.versionName);
        textView.setText( APKVersionCodeUtils.getVerName(activity));
        CabinetNumberDao cabinetCountDao=BaseApplication.getInstances().getDaoSession().getCabinetNumberDao();
        QueryBuilder qb=cabinetCountDao.queryBuilder();
        List<CabinetNumber>listcount=cabinetCountDao.loadAll();
        List<CabinetNumber>listuser=qb.where(CabinetNumberDao.Properties.IsUser.eq("占用")).list();
        text_num1.setText("全部"+listcount.size());
        text_num2.setText("已用"+listuser.size());
        text_num3.setText("剩余"+(listcount.size()-listuser.size()));
//        time_out();
//        timer.start();
        activity.bRun=true;
//        Logger.e("MainFragment1===="+"count=="+listcount.size()+"used=="+listuser.size()+"surplus=="+(listcount.size()-listuser.size()));
    }
    @Override
    protected int getLayoutId() {
        return R.layout.first_fragment;
    }
    @Override
    protected void initData() {
        mesReceiver = new MesReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LockActivity.ACTION_UPDATEUI);
        intentFilter.addAction(LockActivity.ACTION_UPDATE);
        activity.registerReceiver(mesReceiver, intentFilter);
//        timer.cancel();
        head_text_02.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                pwdmodel="1";
                exitAlertDialog=new ExitAlertDialog(getContext());
                exitAlertDialog.show();
                return false;
            }
        });
    }
    @Override
    protected void onVisible() {
    }
    @Override
    protected void onInvisible() {
    }
    @OnClick({R.id.main_bt_01,R.id.main_bt_02,R.id.chang_pdw,R.id.main_bt_03,R.id.head_text_02,R.id.clean_other,R.id.clean_all,R.id.openlock_all,
            R.id.openlock_other,R.id.openlock_button,R.id.back,R.id.head_text_03_main,R.id.button4,R.id.record_button,R.id.lock_message})
    public void Onclick(View view){
        String device;

        CheckUsedRecored checkUsedRecored;
        LockMessage lockMessage;
        CabinetNumberDao cabinetNumberDao=BaseApplication.getInstances().getDaoSession().getCabinetNumberDao();
        CabinetRecordDao cabinetRecordDao=BaseApplication.getInstances().getDaoSession().getCabinetRecordDao();
        SharedPreferences user=activity.getSharedPreferences("user_info",0);
        device= FileUtils.loadDataFromFile(activity,"deviceId.text");
        ConnectivityManager connectivityManager;//用于判断是否有网络 connectivityManager =(ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);//获取当前网络的连接服务
        connectivityManager =(ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);//获取当前网络的连接服务
        NetworkInfo info =connectivityManager.getActiveNetworkInfo(); //获取活动的网络连接信息
        switch (view.getId()){
            case R.id.main_bt_01:
                if (Utils.isFastClick()&&info!=null) {
                    ((BindVeinMainFragment)getParentFragment()).setFragment(1);
                }else {
                    Toast.makeText(getActivity(),"网络已断开，请查看网络",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.main_bt_02:
                if (Utils.isFastClick()&&info!=null) {
                    ((BindVeinMainFragment)getParentFragment()).setFragment(2);
                }else {
                    Toast.makeText(getActivity(),"网络已断开，请查看网络",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.main_bt_03:
                if (Utils.isFastClick()&&info!=null) {
                    ((BindVeinMainFragment)getParentFragment()).setFragment(3);
                }else {
                    Toast.makeText(getActivity(),"网络已断开，请查看网络",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.clean_other:
                if (!"".equals(clearlock.getText().toString().trim())&&!"0".equals(clearlock.getText().toString().trim())){
                List<CabinetRecord> cabinetRecord=cabinetRecordDao.queryBuilder().where(CabinetRecordDao.Properties.CabinetNumber.eq(clearlock.getText().toString())).list();
                if (cabinetRecord.size()>0){
                    Toast.makeText(activity,"清除柜号"+clearlock.getText().toString()+"成功",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(activity,"清除柜号"+clearlock.getText().toString()+"失败",Toast.LENGTH_SHORT).show();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        clearCabinetContract.clearCabinet(device,""+clearlock.getText().toString().trim());
                    }
                }).start();
                QueryBuilder qb1 = cabinetRecordDao.queryBuilder();
                QueryBuilder qb=cabinetNumberDao.queryBuilder();
                List<CabinetRecord> users = qb1.where(CabinetRecordDao.Properties.CabinetNumber.eq(clearlock.getText().toString())).build().list();
                if (users.size()>0) {
                    List<CabinetNumber>list=qb.where(CabinetNumberDao.Properties.CabinetNumber.eq(users.get(users.size()-1).getCabinetNumber())).list();
                    if (list.size()!=0) {
                        Logger.e("ThirdFragment"+"=============list.size"+list.size());
                        CabinetNumber cabinetNumber = new CabinetNumber();
                        cabinetNumber.setId(list.get(0).getId());
                        cabinetNumber.setCabinetLockPlate(list.get(0).getCabinetLockPlate());
                        cabinetNumber.setCircuitNumber(list.get(0).getCircuitNumber());
                        cabinetNumber.setCabinetNumber(list.get(0).getCabinetNumber());
                        cabinetNumber.setIsUser("空闲");
                        cabinetNumberDao.update(cabinetNumber);
                    }
                }
                }else{
                    Toast.makeText(activity,"清除柜号失败"+"不存在"+clearlock.getText().toString(),Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.clean_all:
                cabinetNumberDao=BaseApplication.getInstances().getDaoSession().getCabinetNumberDao();
                List<CabinetNumber>listnum=cabinetNumberDao.loadAll();
                QueryBuilder qb=cabinetNumberDao.queryBuilder();
                listnum=qb.where(CabinetNumberDao.Properties.IsUser.eq("占用")).list();
                CabinetNumber cabinetNumber=new CabinetNumber();
                Logger.e("MainActivity"+"listnum"+listnum.size());
//                if(listnum.size()>0) {
//                    for (int i = 0; i < listnum.size(); i++) {
//                        Logger.e("MainFragment1=" + listnum.get(i).getCabinetLockPlate() + "=" + listnum.get(i).getCabinetNumber() + "=" + listnum.get(i).getCircuitNumber() + "=" + listnum.get(i).getIsUser());
//                    }
                  clearCabinetContract.clearCabinet(device, "");
                    Toast.makeText(activity, "清除所有柜号成功", Toast.LENGTH_SHORT).show();
//                }else {
                    Toast.makeText(getActivity(),"网络已断开，请查看网络",Toast.LENGTH_LONG).show();
//                }
                break;
            case R.id.openlock_button:
                String number;
                number=opelock_one.getText().toString();
                cabinetNumberDao.loadAll();
                QueryBuilder queryBuilder=cabinetNumberDao.queryBuilder();
                List<CabinetNumber>list=queryBuilder.where(CabinetNumberDao.Properties.CabinetNumber.eq(number)).list();
                if (list.size()>0){
                   int cabinetLockPlate=Integer.parseInt(list.get(0).getCabinetLockPlate());
                   int circuitNumber= Integer.parseInt(list.get(0).getCircuitNumber());
                    CabinetRecord cabinetRecord1=new CabinetRecord();
                    cabinetRecord1.setMemberName("管理员");
                    cabinetRecord1.setPhoneNum("***********");
                    cabinetRecord1.setCabinetNumber(circuitNumber+"");
                    cabinetRecord1.setCabinetStating("管理员开柜");
                    cabinetRecord1.setOpentime(opentime);
                    cabinetRecordDao.insert(cabinetRecord1);
                    if (cabinetLockPlate<11) {
                        try {
                            circuitNumber=circuitNumber%10;
                            if (circuitNumber==0){
                                circuitNumber=10;
                            }
                            activity.serialpprt_wk1.getOutputStream().write(openDoorUtil.openOneDoor(cabinetLockPlate, circuitNumber));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Logger.e("openlockplate:"+cabinetLockPlate+"    openlock:"+circuitNumber);
                    }else if (cabinetLockPlate<21&&cabinetLockPlate>10){
                        try {
                            circuitNumber=circuitNumber%10;
                            if (circuitNumber==0){
                                circuitNumber=10;
                            }
                            int openlocknum=0;
                            if (cabinetLockPlate!=20){
                                openlocknum=cabinetLockPlate%10;
                            }
                            else {
                                openlocknum=10;
                            }
                            activity.serialpprt_wk2.getOutputStream().write(openDoorUtil.openOneDoor(openlocknum, circuitNumber));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Logger.e("openlockplate:"+cabinetLockPlate+"openlock:"+circuitNumber);
                    }else if (cabinetLockPlate<31&&cabinetLockPlate>20){
                        try {
                            circuitNumber=circuitNumber%10;
                            if (circuitNumber==0){
                                circuitNumber=10;
                            }
                            int openlocknum=0;
                            if (cabinetLockPlate!=30){
                                openlocknum=cabinetLockPlate%10;
                            }
                            else {
                                openlocknum=10;
                            }
                            activity.serialpprt_wk3.getOutputStream().write(openDoorUtil.openOneDoor(openlocknum, circuitNumber));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Logger.e("openlockplate:"+cabinetLockPlate+"openlock:"+circuitNumber);
                    }
                }
                break;
            case R.id.openlock_all:
//                CabinetNumberDao cabinetNumberDao1=BaseApplication.getInstances().getDaoSession().getCabinetNumberDao();
//                List<CabinetNumber>alluser=cabinetNumberDao1.loadAll();
//                for (int j=0;j<alluser.size();j++){
//                    CabinetRecord cabinetRecord=new CabinetRecord();
//                    cabinetRecord.setMemberName("管理员");
//                    cabinetRecord.setPhoneNum("**********");
//                    cabinetRecord.setCabinetStating("打开所有");
//                    cabinetRecord.setOpentime(opentime);
//                    cabinetRecord.setCabinetNumber(alluser.get(j).getCircuitNumber());
//                    cabinetRecordDao.insert(cabinetRecord);
//                }
                try {
                    serialPort =new SerialPort(new File("/dev/ttysWK1"),9600,0);
                    serialPort.getOutputStream().write(openDoorUtil.openAllDoor());
                }catch (IOException e){
                    e.printStackTrace();
                }
                try {
                    serialPort =new SerialPort(new File("/dev/ttysWK2"),9600,0);
                    serialPort.getOutputStream().write(openDoorUtil.openAllDoor());
                }catch (IOException e){
                    e.printStackTrace();
                }
                try {
                    serialPort =new SerialPort(new File("/dev/ttysWK3"),9600,0);
                    serialPort.getOutputStream().write(openDoorUtil.openAllDoor());
                }catch (IOException e){
                    e.printStackTrace();
                }
                 adminopenCabinet.adminopen(device,"");
                break;
            case R.id.openlock_other:
                if ( !TextUtils.isEmpty(openlockplate.getText().toString())&&!TextUtils.isEmpty(openlock.getText().toString())) {
                    if (Integer.parseInt(openlockplate.getText().toString().trim())<11) {
                        try {
                            activity.serialpprt_wk1.getOutputStream().write(openDoorUtil.openOneDoor(Integer.parseInt(openlockplate.getText().toString().trim()), Integer.parseInt(openlock.getText().toString().trim())));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Logger.e("openlockplate:"+openlockplate.getText().toString()+"    openlock:"+openlock.getText().toString().trim());
                    }else if (Integer.parseInt(openlockplate.getText().toString().trim())<21&Integer.parseInt(openlockplate.getText().toString().trim())>10){
                        try {
                            int openlocknum=0;
                            if (Integer.parseInt(openlockplate.getText().toString().trim())!=20){
                                openlocknum=Integer.parseInt(openlockplate.getText().toString().trim())%10;
                            }
                            else {
                                openlocknum=10;
                            }
                            activity.serialpprt_wk2.getOutputStream().write(openDoorUtil.openOneDoor(openlocknum, Integer.parseInt(openlock.getText().toString().trim())));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Logger.e("openlockplate:"+openlockplate.getText().toString()+"openlock:"+openlock.getText().toString().trim());
                    }else if (Integer.parseInt(openlockplate.getText().toString().trim())<31&Integer.parseInt(openlockplate.getText().toString().trim())>20){
                        try {
                            int openlocknum=0;
                            if (Integer.parseInt(openlockplate.getText().toString().trim())!=30){
                                openlocknum=Integer.parseInt(openlockplate.getText().toString().trim())%10;
                            }
                            else {
                                openlocknum=10;
                            }
                            activity.serialpprt_wk3.getOutputStream().write(openDoorUtil.openOneDoor(openlocknum, Integer.parseInt(openlock.getText().toString().trim())));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Logger.e("openlockplate:"+openlockplate.getText().toString()+"openlock:"+openlock.getText().toString().trim());
                    }
                }
                else if (TextUtils.isEmpty(openlock.getText().toString())&&!TextUtils.isEmpty(openlockplate.getText().toString())){
                    if (Integer.parseInt(openlockplate.getText().toString().trim())<11) {
                        for (int i = 0; i <= 10; i++) {
                            try {
                                Thread.sleep(500);
                                activity.serialpprt_wk1.getOutputStream().write(openDoorUtil.openOneDoor(Integer.parseInt(openlockplate.getText().toString().trim()), i));
                            } catch (Exception e) {
                            }
                        }
                    }else if (Integer.parseInt(openlockplate.getText().toString().trim())<21&Integer.parseInt(openlockplate.getText().toString().trim())>10){
                        for (int i = 0; i <= 10; i++) {
                            try {
                                Thread.sleep(500);
                                activity.serialpprt_wk2.getOutputStream().write(openDoorUtil.openOneDoor(Integer.parseInt(openlockplate.getText().toString().trim())%10, i));
                            } catch (Exception e) {
                            }
                        }
                    }else {
                        for (int i = 0; i <= 10; i++) {
                            try {
                                Thread.sleep(500);
                                activity.serialpprt_wk3.getOutputStream().write(openDoorUtil.openOneDoor(Integer.parseInt(openlockplate.getText().toString().trim())%10, i));
                            } catch (Exception e) {
                            }
                        }
                    }
                }
                  adminopenCabinet.adminopen(device,""+openlock.getText().toString().trim());
                break;
            case R.id.back:
                adminmessage.setVisibility(View.GONE);
                layout_one.setVisibility(View.VISIBLE);
                CabinetNumberDao cabinetCountDao=BaseApplication.getInstances().getDaoSession().getCabinetNumberDao();
                QueryBuilder qb2=cabinetCountDao.queryBuilder();
                List<CabinetNumber>listcount=cabinetCountDao.loadAll();
                List<CabinetNumber>listuser=qb2.where(CabinetNumberDao.Properties.IsUser.eq("占用")).list();
                text_num1.setText("全部"+listcount.size());
                text_num2.setText("已用"+listuser.size());
                text_num3.setText("剩余"+(listcount.size()-listuser.size()));
                break;
            case R.id.button4:
                SharedPreferences sharedPreferences2=activity.getSharedPreferences("user_info",0);
                String deviceID=sharedPreferences2.getString("deviceId", "");
                textView2.setText(deviceID);
                break;
            case R.id.head_text_03_main:
                        syncUserFeature.syncUser(device);
                        syncUserFeature.syncSign(device);
                break;
            case R.id.record_button:
               String number1=cabinet_used.getText().toString();
                cabinetRecordDao= BaseApplication.getInstances().getDaoSession().getCabinetRecordDao();
                cabinetRecordDao.loadAll();
                QueryBuilder qb3=cabinetRecordDao.queryBuilder();
                List<CabinetRecord> recordList=qb3.where(CabinetRecordDao.Properties.CabinetNumber.eq(number1)).list();
                checkUsedRecored=new CheckUsedRecored(getContext(),recordList,number1);
                checkUsedRecored.show();
                break;
            case R.id.lock_message:
                cabinetNumberDao= BaseApplication.getInstances().getDaoSession().getCabinetNumberDao();
                List<CabinetNumber>  cabinetNumberList=cabinetNumberDao.loadAll();
                for (int i=0;i<cabinetNumberList.size();i++){
                    Logger.e("MainFragment1"+cabinetNumberList.get(i).getCabinetLockPlate()+"="+cabinetNumberList.get(i).getCircuitNumber()+"="+cabinetNumberList.get(i).getCabinetNumber()+"="+cabinetNumberList.get(i).getIsUser());
                }
                lockMessage=new LockMessage(getContext(),cabinetNumberList);
                lockMessage.show();
                break;
            case R.id.chang_pdw:
                pwdmodel="2";
                exitAlertDialog=new ExitAlertDialog(getContext());
                exitAlertDialog.show();
        }
    }
    CabinetNumber cabinetNumber=new CabinetNumber();
    CabinetNumberDao cabinetNumberDao=BaseApplication.getInstances().getDaoSession().getCabinetNumberDao();
    private void cleanLock(CabinetNumber listnum){
        cabinetNumber.setId(listnum.getId());
        cabinetNumber.setCabinetLockPlate(listnum.getCabinetLockPlate());
        cabinetNumber.setCabinetNumber(listnum.getCabinetNumber());
        cabinetNumber.setCircuitNumber(listnum.getCircuitNumber());
        cabinetNumber.setIsUser("空闲");
    }
    private class ExitAlertDialog extends Dialog implements View.OnClickListener {
        private Context mContext;
        private EditText etPwd;
        private Button btCancel;
        private Button btConfirm;
        private TextView texttilt;
        public ExitAlertDialog(Context context, int theme) {
            super(context, theme);
            mContext = context;
            initDialog();
        }
        public ExitAlertDialog(Context context) {
            super(context, R.style.customer_dialog);
            mContext = context;
            initDialog();
        }
        private void initDialog() {
            View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_exit_confirm, null);
            setContentView(view);
            btCancel = (Button) view.findViewById(R.id.btCancel);
            btConfirm = (Button) view.findViewById(R.id.btConfirm);
            etPwd = (EditText) view.findViewById(R.id.deviceCode);
            texttilt=(TextView)view.findViewById(R.id.text_title);
            btCancel.setOnClickListener(this);
            btConfirm.setOnClickListener(this);
        }
        @Override
        public void show() {
            etPwd.setText("");
            if (pwdmodel=="1"){
            }else if (pwdmodel=="2"){
                texttilt.setText("修改密码");
                etPwd.setHint("请输入新密码");
            }
            super.show();
        }
        String devicepwd;
        SharedPreferences userInfo;
        Intent intent;
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btCancel:
                    this.dismiss();
                    break;
                case R.id.btConfirm:
                    if(pwdmodel.equals("1")){
                    String pwd = etPwd.getText().toString().trim();
                    if (Utils.isEmpty(pwd)) {
                        ToastUtils.show(mContext, "请输入密码", ToastUtils.LENGTH_SHORT);
                        return;
                    }
                    String repwd;
                    try {
                        repwd = Reservoir.get(Constant.KEY_PASSWORD, String.class);
                    } catch (Exception e) {
                        userInfo=activity.getSharedPreferences("user_info",0);
                        repwd = userInfo.getString("devicepwd","0");
                    }
                    if (!pwd.equals(repwd)) {
                        ToastUtils.show(mContext, "密码不正确", ToastUtils.LENGTH_SHORT);
                        return;
                    }else {
                        userInfo = activity.getSharedPreferences("user_info", 0);
                        userInfo.edit().putString("devicepwd", pwd).commit();
                        layout_one.setVisibility(View.GONE);
                        adminmessage.setVisibility(View.VISIBLE);
                        this.dismiss();
                    }
                  }else if (pwdmodel.equals("2")){
                        userInfo=activity.getSharedPreferences("user_info",0);
                        String pwd = etPwd.getText().toString().trim();
                        if (userInfo.getString("devicepwd","").toString().trim()==pwd) {
                            ToastUtils.show(mContext, "密码不能跟上一次相同", ToastUtils.LENGTH_SHORT);
                        }else {
                            userInfo.edit().putString("devicepwd",pwd).commit();
                            ToastUtils.show(mContext, "密码修改成功", ToastUtils.LENGTH_SHORT);
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public void onError(ApiException error) {
        super.onError(error);
    }
    @Override
    public void onResultError(ApiException e) {
    }
    @Override
    public void onPermissionError(ApiException e) {
    }
    @Override
    public void adminopenSuccess(ResultResponse resultResponse) {

    }
    @Override
    public void ClearCabinetSuccess(ResultResponse resultResponse) {
//        CabinetRecordDao cabinetRecordDao = BaseApplication.getInstances().getDaoSession().getCabinetRecordDao();
//        CabinetNumberDao cabinetNumberDao=BaseApplication.getInstances().getDaoSession().getCabinetNumberDao();
//        QueryBuilder qb1 = cabinetRecordDao.queryBuilder();
//        QueryBuilder qb = cabinetNumberDao.queryBuilder();
//    if (isClearnOther) {
//        CabinetRecord cabinetRecord1=new CabinetRecord();
//        cabinetRecord1.setMemberName("管理员");
//        cabinetRecord1.setPhoneNum("*************");
//        cabinetRecord1.setCabinetNumber(clearlock.getText().toString()+"");
//        cabinetRecord1.setCabinetStating("管理员清柜");
//        cabinetRecord1.setOpentime(opentime);
//        cabinetRecordDao.insert(cabinetRecord1);
//    List<CabinetRecord> users = qb1.where(CabinetRecordDao.Properties.CabinetNumber.eq(clearlock.getText().toString())).build().list();
//    if (users.size() > 0) {
//        List<CabinetNumber> list = qb.where(CabinetNumberDao.Properties.CabinetNumber.eq(users.get(users.size() - 1).getCabinetNumber())).list();
//        if (list.size() != 0) {
//            Logger.e("ThirdFragment" + "=============list.size" + list.size());
//            CabinetNumber cabinetNumber = new CabinetNumber();
//            cabinetNumber.setId(list.get(0).getId());
//            cabinetNumber.setCabinetLockPlate(list.get(0).getCabinetLockPlate());
//            cabinetNumber.setCircuitNumber(list.get(0).getCircuitNumber());
//            cabinetNumber.setCabinetNumber(list.get(0).getCabinetNumber());
//            cabinetNumber.setIsUser("空闲");
//            cabinetNumberDao.update(cabinetNumber);
//        }
//    }
//    isClearnOther=false;
//    }
//    else
//        if (isClearnAll){
//        List<CabinetNumber> alluser=qb.where(CabinetNumberDao.Properties.IsUser.eq("占用")).list();

//        for (int j=0;j<alluser.size();j++){
//            CabinetRecord cabinetRecord=new CabinetRecord();
//            cabinetRecord.setMemberName("管理员");
//            cabinetRecord.setPhoneNum("************");
//            cabinetRecord.setCabinetStating("清除所有");
//            cabinetRecord.setOpentime(opentime);
//            cabinetRecord.setCabinetNumber(alluser.get(j).getCircuitNumber());
//            cabinetRecordDao.insert(cabinetRecord);
//        }
//        Cursor cursor;
//        String sql;
//        sql = "update CABINET_NUMBER set IS_USER = '空闲1' where IS_USER='占用'" ;
//        cursor=BaseApplication.getInstances().getDaoSession().getDatabase().rawQuery(sql,null);
//        Logger.e("MainFragment1"+"==========isClearnAll==========="+cursor.getCount());
//        byte[][] feature=new byte[cursor.getCount()][];
//        String [] Uids=new String[cursor.getCount()];
//        while (cursor.moveToNext()){
//            int nameColumnIndex = cursor.getColumnIndex("FINGERMODEL");
//            String strValue=cursor.getString(nameColumnIndex);
//            feature[i]=hexStringToByte(strValue);
//            Uids[i]=cursor.getString(cursor.getColumnIndex("UID"));
//            i++;
//        }
//        alluser=cabinetNumberDao.loadAll();
//        for (int i=0;i<alluser.size();i++){
//            CabinetNumber cabinetNumber = new CabinetNumber();
//            cabinetNumber.setId(alluser.get(i).getId());
//            cabinetNumber.setCabinetLockPlate(alluser.get(i).getCabinetLockPlate());
//            cabinetNumber.setCircuitNumber(alluser.get(i).getCircuitNumber());
//            cabinetNumber.setCabinetNumber(alluser.get(i).getCabinetNumber());
//            cabinetNumber.setIsUser("空闲");
//            cabinetNumberDao.update(cabinetNumber);
//        }
//        isClearnAll=false;
//    }
    }
    @Override
    public void cabinetNumberSuccess(CabinetNumberData cabinetNumberData) {
        CabinetNumberDao cabinetNumberDao=BaseApplication.getInstances().getDaoSession().getCabinetNumberDao();
        cabinetNumberDao.deleteAll();
        if(cabinetNumberData.getCabinetNumberMessage().length>0) {
            for (int i = 0; i < cabinetNumberData.getCabinetNumberMessage().length; i++) {
                CabinetNumber cabinetNumber = new CabinetNumber();
                cabinetNumber.setCircuitNumber(cabinetNumberData.getCabinetNumberMessage()[i].getCircuitNumber());
                cabinetNumber.setCabinetLockPlate(cabinetNumberData.getCabinetNumberMessage()[i].getCabinetLockPlate());
                cabinetNumber.setCabinetNumber(cabinetNumberData.getCabinetNumberMessage()[i].getCabinetNumber());
                cabinetNumber.setIsUser("空闲");
                BaseApplication.getInstances().getDaoSession().getCabinetNumberDao().insert(cabinetNumber);
            }
        }
    }

    @Override
    public void syncSignUserSuccess(DownLoadData downLoadData) {
        String sql;
        sql = "select USER_ID from SIGN_USER";
        int i=0;
        Cursor cursor;
        cursor = BaseApplication.getInstances().getDaoSession().getDatabase().rawQuery(sql,null);
        Logger.e("BaseApplication"+"downLoadData.getDown_userInfo().length="+cursor.getCount());
        String [] Uids=new String[cursor.getCount()];
        while (cursor.moveToNext()){
            Uids[i]=cursor.getString(cursor.getColumnIndex("USER_ID"));
            i++;
        }
        SignUserDao signUserDao=BaseApplication.getInstances().getDaoSession().getSignUserDao();
        if (downLoadData.getDown_userInfo().length>Uids.length){
            for (int j=0;j<downLoadData.getDown_userInfo().length;j++){
                SignUser signPerson =new SignUser();
                signPerson.setPos(j+"");
                signPerson.setUserId(downLoadData.getDown_userInfo()[j].getUid());
                signUserDao.insert(signPerson);
            }
        }
    }

    @Override
    public void syncUserSuccess(DownLoadData resultResponse) {
        if (resultResponse!=null) {
           PersonDao personDao = BaseApplication.getInstances().getDaoSession().getPersonDao();
            List<Person> user=personDao.loadAll();
            List<Person> persons = personDao.loadAll();
            Person person = new Person();
            QueryBuilder qb = personDao.queryBuilder();
            if (resultResponse.getDown_userInfo().length > 0) {
                personDao.deleteAll();
                for (int i = 0; i < resultResponse.getDown_userInfo().length; i++) {
                    person.setId((long) i+1);
                    person.setUid(resultResponse.getDown_userInfo()[i].getUid());
                    person.setNumber(resultResponse.getDown_userInfo()[i].getUserName());
                    person.setPos(i+1+"");
                    person.setFingermodel(resultResponse.getDown_userInfo()[i].getFeature());
                    personDao.insert(person);
                }
            }
        }
        Logger.e( "BaseApplication"+"resultResponse.getDown_userInfo() " + resultResponse.getDown_userInfo().length);
    }
    /**
     * 广播接收器
     *
     * @author kevin
     */
    public class MesReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String time=intent.getStringExtra("timeStr");
            head_text_03.setText(time);
            head_text_01.setText(intent.getStringExtra("timeData"));
            if (context == null) {
                context.unregisterReceiver(this);
            }else {
                opentime=time;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        activity.unregisterReceiver(mesReceiver);//释放广播接收者
    }
}
