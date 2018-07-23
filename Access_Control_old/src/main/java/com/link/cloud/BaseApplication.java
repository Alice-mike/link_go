
package com.link.cloud;
import android.app.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.alibaba.fastjson.JSON;
import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.anupcowkur.reservoir.Reservoir;
import com.example.TTSUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.link.cloud.activity.LockActivity;
import com.link.cloud.base.ApiException;
import com.link.cloud.base.LogcatHelper;
import com.link.cloud.base.UpDateBean;
import com.link.cloud.bean.CabinetNumberData;
import com.link.cloud.bean.CabinetNumberMessage;
import com.link.cloud.bean.DeviceData;
import com.link.cloud.bean.DownLoadData;
import com.link.cloud.bean.Member;
import com.link.cloud.bean.MessagetoJson;
import com.link.cloud.bean.PushMessage;
import com.link.cloud.bean.UpdateMessage;
import com.link.cloud.component.MyMessageReceiver;
import com.link.cloud.contract.CabinetNumberContract;
import com.link.cloud.contract.DownloadFeature;
import com.link.cloud.contract.GetDeviceIDContract;
import com.link.cloud.contract.SyncUserFeature;
import com.link.cloud.contract.VersoinUpdateContract;
//import com.link.cloud.greendao.gen.DaoMaster;
//import com.link.cloud.greendao.gen.DaoSession;
//import com.link.cloud.greendaodemo.HMROpenHelper;

import com.link.cloud.greendao.gen.DaoMaster;
import com.link.cloud.greendao.gen.DaoSession;
import com.link.cloud.greendao.gen.PersonDao;

import com.link.cloud.greendao.gen.SignUserDao;
import com.link.cloud.greendaodemo.HMROpenHelper;
import com.link.cloud.greendaodemo.Person;
import com.link.cloud.greendaodemo.SignUser;
import com.link.cloud.message.CrashHandler;
import com.link.cloud.utils.DownloadUtils;
import com.link.cloud.utils.FileUtils;
import com.link.cloud.view.ProgressHUD;
import com.orhanobut.logger.Logger;

import com.link.cloud.constant.Constant;
import com.link.cloud.utils.Utils;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.greendao.query.QueryBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Array;
import java.util.List;

import javax.xml.transform.Result;

import md.com.sdk.MicroFingerVein;

import static com.link.cloud.utils.Utils.byte2hex;
import static com.link.cloud.utils.Utils.isEmpty;


/**
 * Description：BaseApplication
 * Created by Shaozy on 2016/8/10.
 */
public class BaseApplication extends MultiDexApplication  implements GetDeviceIDContract.VersoinUpdate,DownloadFeature.download,SyncUserFeature.syncUser{
    public static boolean DEBUG = false;
    private static BaseApplication ourInstance = new BaseApplication();
    public boolean log = true;
    public boolean flag;
    public  static String messgetext;
    public Gson gson;
    private static Context context;

    public static final long ONE_KB = 1024L;
    public static final long ONE_MB = ONE_KB * 1024L;
    public static final long CACHE_DATA_MAX_SIZE = ONE_MB * 3L;
    private static final String TAG = "BaseApplication";
//    static String string;static int type;
   static LockActivity mainActivity=null;
    public static BaseApplication getInstance() {
        return ourInstance;
    }
    String deviceTargetValue;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    GetDeviceIDContract presenter;
    PersonDao personDao;
    public static BaseApplication instances;
    private static LockActivity mainAcivity;
   static DownloadFeature feature;
   static SyncUserFeature syncUserFeature;
    CabinetNumberContract cabinetNumberContract;
    MicroFingerVein microFingerVein;
    MyMessageReceiver receiver;
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    public static BaseApplication getInstances() {
        return instances;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        //        CrashHandler.getInstance().init(this);
        //        LogcatHelper.getInstance(this).start();
        feature=new DownloadFeature();
        feature.attachView(this);
        TTSUtils.getInstance().init(this);
        syncUserFeature=new SyncUserFeature();
        syncUserFeature.attachView(this);
        TTSUtils.getInstance().init(this);
        instances = this;
        ourInstance = this;
//        Logger.e("BaseApplication"+"oncreat=============");
        setDatabase();
         context=getApplicationContext();
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
            }
            @Override
            public void onActivityStarted(Activity activity) {
            }
            @Override
            public void onActivityResumed(Activity activity) {
                TestActivityManager.getInstance().setCurrentActivity(activity);
            }
            @Override
            public void onActivityPaused(Activity activity) {
            }
            @Override
            public void onActivityStopped(Activity activity) {

            }
            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
            }
            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        });
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            DEBUG = appInfo.metaData.getBoolean("DEBUG");
        } catch (Exception e) {
            DEBUG = false;
        }
        handler.sendEmptyMessageDelayed(0,1000);
        this.initGson();
        this.initReservoir();
        this.initCCPRestSms();
        presenter=new GetDeviceIDContract();
        presenter.attachView(this);
        initCloudChannel(this);
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
    }
    boolean ret=false;
//    private void checkMD(){
//        if (mainAcivity!=null&&ret!=true) {
//            microFingerVein = MicroFingerVein.getInstance(mainAcivity);
//            int devicecount=microFingerVein.fvdev_get_count();
//            if (devicecount!=0) {
//                ret = microFingerVein.fvdev_open();
////               Logger.e("WorkService1"+"devicecount"+devicecount + "=====================" + ret);
//            }
//        }
//    }
    public static String getMac(){
        String result = "";
        String Mac = "";
        result = callCmd("busybox ifconfig","HWaddr");
        //如果返回的result == null，则说明网络不可取
        if(result==null){
            return "网络出错，请检查网络";
        }
        //对该行数据进行解析
        //例如：eth0      Link encap:Ethernet  HWaddr 00:16:E8:3E:DF:67
        if(result.length()>0 && result.contains("HWaddr")==true){
            Mac = result.substring(result.indexOf("HWaddr")+6, result.length()-1);
            Log.i("test","Mac:"+Mac+" Mac.length: "+Mac.length());
            result = Mac;
            Log.i("test",result+" result.length: "+result.length());
        }
        return result;
    }
    private static String callCmd(String cmd,String filter) {
        String result = "";
        String line = "";
        try {
            Process proc = Runtime.getRuntime().exec(cmd);
            InputStreamReader is = new InputStreamReader(proc.getInputStream());
            BufferedReader br = new BufferedReader(is);
            //执行命令cmd，只取结果中含有filter的这一行
            while ((line = br.readLine ()) != null && line.contains(filter)== false) {
                //result += line;
                Log.i("test","line: "+line);
            }
            result = line;
            Log.i("test","result: "+result);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    public static Context getContext() {
        return context;
    }
    /**
     * 设置greenDao
     */
    private void setDatabase() {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        DaoMaster.DevOpenHelper mHelpter = new DaoMaster.DevOpenHelper(this,"notes-db");
//        HMROpenHelper mHelpter = new HMROpenHelper(this, "notes-db", null);//为数据库升级封装过的使用方式
        db = mHelpter.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }
    @Override
    public void downloadSuccess(DownLoadData resultResponse) {
        PersonDao personDao=BaseApplication.getInstances().getDaoSession().getPersonDao();
       if (resultResponse.getDown_userInfo().length>1) {
         for(int i=0;i<resultResponse.getDown_userInfo().length;i++) {
             Person person1 = BaseApplication.getInstances().getDaoSession().getPersonDao().queryBuilder().orderDesc(PersonDao.Properties.Id).limit(1).build().unique();
             Long id = person1.getId() + 1;
             Person person=new Person();
             person.setId(id);
             person.setUserType(1);
             person.setName(resultResponse.getDown_userInfo()[i].getUserName());
             person.setFingermodel(resultResponse.getDown_userInfo()[i].getFeature());
             person.setUid(resultResponse.getDown_userInfo()[i].getUid());
             personDao.insert(person);
         }
       }else {
           Person person2 = BaseApplication.getInstances().getDaoSession().getPersonDao().queryBuilder().orderDesc(PersonDao.Properties.Id).limit(1).build().unique();
           Long id = person2.getId() + 1;
           Person person=new Person();
           person.setId(id);
           person.setUserType(1);
           person.setName(resultResponse.getDown_userInfo()[0].getUserName());
           person.setFingermodel(resultResponse.getDown_userInfo()[0].getFeature());
           person.setUid(resultResponse.getDown_userInfo()[0].getUid());
           personDao.insert(person);
       }
    }
    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public SQLiteDatabase getDb() {
        return db;
    }


    private void initGson() {
        this.gson = new GsonBuilder().setDateFormat(Constant.BASE_DATA_FORMAT).create();
    }

    private void initReservoir() {
        try {
            Reservoir.init(this, CACHE_DATA_MAX_SIZE, this.gson);
        } catch (Exception e) {
            //failure
            e.printStackTrace();
            Logger.e("initReservoir failure :" + e);
        }
    }
    private void initCCPRestSms() {
        /*restAPI = new CCPRestSmsSDK();
        /*//******************************注释*********************************************
         /*//*初始化服务器地址和端口                                                       *
        /*//*沙盒环境（用于应用开发调试）：restAPI.init("sandboxapp.cloopen.com", "8883");*
        /*//*生产环境（用户应用上线使用）：restAPI.init("app.cloopen.com", "8883");       *
        /*//*******************************************************************************
         restAPI.init("app.cloopen.com", "8883");
         /*//******************************注释*********************************************
         /*//*初始化主帐号和主帐号令牌,对应官网开发者主账号下的ACCOUNT SID和AUTH TOKEN     *
        /*//*ACOUNT SID和AUTH TOKEN在登陆官网后，在“应用-管理控制台”中查看开发者主账号获取*
        /*//*参数顺序：第一个参数是ACOUNT SID，第二个参数是AUTH TOKEN。                   *
        /*//*******************************************************************************
         restAPI.setAccount(getString(R.string.ACCOUNT_SID), getString(R.string.AUTH_TOKEN));
         /*//******************************注释*********************************************
         /*//*初始化应用ID                                                                 *
        /*//*测试开发可使用“测试Demo”的APP ID，正式上线需要使用自己创建的应用的App ID     *
        /*//*应用ID的获取：登陆官网，在“应用-应用列表”，点击应用名称，看应用详情获取APP ID*
        /*//*******************************************************************************
         restAPI.setAppId(getString(R.string.APPID));
         */
    }

    /**
     * 获取log日志根目录
     *
     * @return
     */
    public static String getLogDir() {
        return getDiskCacheDir(Utils.getMetaData(Constant.CHANNEL_NAME));
    }
    /**
     * 获取相关功能业务目录
     *
     * @return 文件缓存路径
     */
    public static String getDiskCacheDir(String dirName) {
        String dir = String.format("%s/%s/", getDiskCacheRootDir(), dirName);
        File file = new File(dir);
        if (!file.exists()) {
            boolean isSuccess = file.mkdirs();
            if (isSuccess) {
                Logger.e(dir + " mkdirs success");
            }
        }
        return file.getPath();
    }
    /**
     * 获取app的根目录
     *
     * @return 文件缓存根路径
//   */
    public static String getDiskCacheRootDir() {
        File diskRootFile;
        if (existsSdcard()) {
            diskRootFile = BaseApplication.getInstance().getExternalCacheDir();
        } else {
            diskRootFile = BaseApplication.getInstance().getCacheDir();
        }
        String cachePath;
        if (diskRootFile != null) {
            cachePath = diskRootFile.getPath();
        } else {
            throw new IllegalArgumentException("disk is invalid");
        }
        return cachePath;
    }
    /**
     * 判断外置sdcard是否可以正常使用
     * @return
     */
    public static Boolean existsSdcard() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable();
    }
    /**
     * 初始化云推送通道
     * @param applicationContext
     */
    private void initCloudChannel(final Context applicationContext) {
        PushServiceFactory.init(applicationContext);
        final CloudPushService pushService = PushServiceFactory.getCloudPushService();
        pushService.register(applicationContext, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        deviceTargetValue = Utils.getMD5(getMac());
                        presenter.getDeviceID(deviceTargetValue,2);
                        Logger.e("BaseApplication"+getMac());
                    }
                }).start();
            }
            @Override
            public void onFailed(String errorCode, String errorMessage) {
                Log.e(TAG, "init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
            }
        });
    }
    @Override
    public void onResultError(ApiException e) {
    }
    @Override
    public void onError(ApiException e) {
    }
    @Override
    public void onPermissionError(ApiException e) {
    }

    @Override
    public void syncUserSuccess(DownLoadData resultResponse) {
        personDao=BaseApplication.getInstances().getDaoSession().getPersonDao();
        List<Person> list=BaseApplication.getInstances().getDaoSession().getPersonDao().loadAll();
        if (resultResponse.getDown_userInfo().length!=list.size()) {
            if (resultResponse.getDown_userInfo().length > 0) {
                personDao = BaseApplication.getInstances().getDaoSession().getPersonDao();
                personDao.deleteAll();
                Person person = new Person();
                for (int i = 0; i < resultResponse.getDown_userInfo().length; i++) {
                    person.setId((long) (i));
                    person.setPos(i + "");
                    person.setUid(resultResponse.getDown_userInfo()[i].getUid());
                    person.setUserType(1);
                    person.setName(resultResponse.getDown_userInfo()[i].getUserName());
                    person.setNumber(resultResponse.getDown_userInfo()[i].getUserName());
                    person.setFingerId(resultResponse.getDown_userInfo()[i].getFingerId());
                    person.setFingermodel(resultResponse.getDown_userInfo()[i].getFeature());
                    personDao.insert(person);
                }
                setDatabase();
            }
        }
        if(downLoadListner!=null){
            downLoadListner.finish();
        }
        List<Person>personList=personDao.loadAll();
        Logger.e("===========================数据同步"+personList.size());
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Logger.e("downloadNotReceiver>>>>>>>>>>>>>>>>>>>>>>>>");
            handler.removeCallbacksAndMessages(null);
            String s = FileUtils.loadDataFromFile(getContext(), "deviceId.text");
            if (!isEmpty(s)) {
                feature.downloadNotReceiver(s);
                handler.sendEmptyMessageDelayed(0, 30 * 1000);
            }
        }
    };
    @Override
    public void downloadNotReceiver(DownLoadData resultResponse) {
        Logger.e("downloadNotReceiver" + resultResponse.getDown_userInfo()[0].getUid() + ">>>>>>>>>>>>>>>>>>" + resultResponse.getDown_userInfo().length);
        PersonDao personDao = BaseApplication.getInstances().getDaoSession().getPersonDao();
        if (resultResponse.getDown_userInfo().length > 0) {
            Person person1 = BaseApplication.getInstances().getDaoSession().getPersonDao().queryBuilder().orderDesc(PersonDao.Properties.Id).limit(1).build().unique();
            Long id = person1.getId() + 1;
            Logger.e("downloadNotReceiver" + id);
            for (int i = 0; i < resultResponse.getDown_userInfo().length; i++) {
                Person person = new Person();
                person.setId(Long.valueOf(id));
                person.setUserType(1);
                Logger.e("downloadNotReceiver" + id);
                person.setName(resultResponse.getDown_userInfo()[i].getUserName());
                person.setFingermodel(resultResponse.getDown_userInfo()[i].getFeature());
                person.setUid(resultResponse.getDown_userInfo()[i].getUid());
                personDao.insert(person);
                id++;
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
            setDatabase();
        }
    }
    ConnectivityManager connectivityManager;
    @Override
    public void getDeviceSuccess(DeviceData deviceData) {
       Logger.e("BaseApplication+devicedate"+deviceData.getDeviceData().getDeviceId()+"numberType"+deviceData.getDeviceData().getNumberType());
            SharedPreferences userInfo = getSharedPreferences("user_info",0);
            userInfo.edit().putString("deviceId", deviceData.getDeviceData().getDeviceId()).commit();
            userInfo.edit().putInt("numberType",deviceData.getDeviceData().getNumberType()).commit();
            if (!isEmpty(deviceData.getDeviceData().getDeviceId())) {
                FileUtils.saveDataToFile(getContext(), deviceData.getDeviceData().getDeviceId(), "deviceId.text");
            }
            final CloudPushService pushService = PushServiceFactory.getCloudPushService();
            pushService.bindAccount(deviceData.getDeviceData().getDeviceId(), new CommonCallback() {
                @Override
                public void onSuccess(String s) {
//                    connectivityManager =(ConnectivityManager)mainAcivity.getSystemService(Context.CONNECTIVITY_SERVICE);//获取当前网络的连接服务
//                    NetworkInfo info =connectivityManager.getActiveNetworkInfo(); //获取活动的网络连接信息
//                    if (info != null) {   //当前没有已激活的网络连接（表示用户关闭了数据流量服务，也没有开启WiFi等别的数据服务）
                        String deviceID = FileUtils.loadDataFromFile(getContext(), "deviceId.text");
                        syncUserFeature.syncUser(deviceData.getDeviceData().getDeviceId());
                        syncUserFeature.syncSign(deviceData.getDeviceData().getDeviceId());
                        feature.appUpdateInfo(deviceData.getDeviceData().getDeviceId());
                        if (downLoadListner != null) {
                            downLoadListner.start();
                        }
//                    }
                    Logger.e(TAG + "init cloudchannel bindAccount" +"deviceTargetValue:" + deviceData.getDeviceData().getDeviceId());
                }
                @Override
                public void onFailed(String s, String s1) {
                }
            });
        }

    @Override
    public void downloadApK(UpDateBean resultResponse) {
        int version = getVersion(getApplicationContext());
        if(version<resultResponse.getData().getPackage_version()){
            downLoadApk(resultResponse.getData().getPackage_path());
        }
        Logger.e(resultResponse.getMsg()+resultResponse.getData().getPackage_path());
    }
    private static int getVersion(Context context)// 获取版本号
    {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 0;
        }
    }
    private void downLoadApk(String downloadurl) {
        // 判断当前用户是否有sd卡
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "lingxi.apk");
            if (file.exists()) {
                file.delete();
            }
            Toast.makeText(this, "通知栏下载中", Toast.LENGTH_SHORT).show();
            DownloadUtils utils = new DownloadUtils(this);
            utils.downloadAPK(downloadurl, "lingxi.apk");
            Logger.e(file.getAbsolutePath());

        }
    }
    public downloafinish downLoadListner;
    public void setDownLoadListner(downloafinish downLoadListner){
        this.downLoadListner=downLoadListner;
    }
    public static void setMainActivity(LockActivity activity) {
        mainActivity = activity;
    }
    static PushMessage pushMessage;
    public static void setConsoleText(String text) {
        Logger.e("BaseApplication setConsoleText===================="+text);
        pushMessage=toJsonArray(text);
        String deviceId=FileUtils.loadDataFromFile(getContext(),"deviceId.text");
        if ("1".equals(pushMessage.getType())) {
            feature.download(pushMessage.getMessageId(), pushMessage.getAppid(), pushMessage.getShopId(), FileUtils.loadDataFromFile(getContext(), "deviceId.text"), pushMessage.getUid());
        } else if ("9".equals(pushMessage.getType())) {
            String sql="INSERT INTO SIGN_USER (USER_ID) VALUES (\""+pushMessage.getUid()+"\"\n"+")";
            BaseApplication.getInstances().getDaoSession().getDatabase().execSQL(sql);
        }
    }
    public static PushMessage toJsonArray(String json) {
        try {
            pushMessage = new PushMessage();
            JSONObject object=new JSONObject(json);
            pushMessage.setType(object.getString("type"));
            pushMessage.setAppid(object.getString("appid"));
            pushMessage.setShopId(object.getString("shopId"));
            pushMessage.setUid(object.getString("uid"));
            pushMessage.setSendTime(object.getString("sendTime"));
            pushMessage.setMessageId(object.getString("messageId"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return pushMessage;
    }
    public interface downloafinish{
        void finish();
        void start();
    }
}
