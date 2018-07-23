/*
 * {EasyGank}  Copyright (C) {2015}  {CaMnter}
 *
 * This program comes with ABSOLUTELY NO WARRANTY; for details type `show w'.
 * This is free software, and you are welcome to redistribute it
 * under certain conditions; type `show c' for details.
 *
 * The hypothetical commands `show w' and `show c' should show the appropriate
 * parts of the General Public License.  Of course, your program's commands
 * might be different; for a GUI interface, you would use an "about box".
 *
 * You should also get your employer (if you work as a programmer) or school,
 * if any, to sign a "copyright disclaimer" for the program, if necessary.
 * For more information on this, and how to apply and follow the GNU GPL, see
 * <http://www.gnu.org/licenses/>.
 *
 * The GNU General Public License does not permit incorporating your program
 * into proprietary programs.  If your program is a subroutine library, you
 * may consider it more useful to permit linking proprietary applications with
 * the library.  If this is what you want to do, use the GNU Lesser General
 * Public License instead of this License.  But first, please read
 * <http://www.gnu.org/philosophy/why-not-lgpl.html>.
 */

package com.link.cloud;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;


import com.alibaba.fastjson.JSON;
import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.anupcowkur.reservoir.Reservoir;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.link.cloud.base.ApiException;
import com.link.cloud.base.LogcatHelper;
import com.link.cloud.bean.DeviceData;
import com.link.cloud.bean.DownLoadData;
import com.link.cloud.bean.Member;
import com.link.cloud.bean.MessagetoJson;
import com.link.cloud.bean.PushMessage;
import com.link.cloud.bean.UpdateMessage;
import com.link.cloud.contract.DownloadFeature;
import com.link.cloud.contract.GetDeviceIDContract;
import com.link.cloud.contract.RegisterTaskContract;
import com.link.cloud.contract.VersoinUpdateContract;
import com.link.cloud.greendao.gen.DaoMaster;
import com.link.cloud.greendao.gen.DaoSession;
import com.link.cloud.greendao.gen.PersonDao;
import com.link.cloud.greendaodemo.HMROpenHelper;
import com.link.cloud.greendaodemo.Person;
import com.link.cloud.message.CrashHandler;
import com.orhanobut.logger.Logger;
import com.link.cloud.activity.NewMainActivity;
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
import java.util.List;

import javax.xml.transform.Result;

/**
 * Description：BaseApplication
 * Created by Shaozy on 2016/8/10.
 */
public class BaseApplication extends MultiDexApplication  implements GetDeviceIDContract.VersoinUpdate,DownloadFeature.download{
    public static boolean DEBUG = false;
    private static BaseApplication ourInstance = new BaseApplication();
    public boolean log = true;
    public boolean flag;
    public Gson gson;
    private static Context context;

    public static final long ONE_KB = 1024L;
    public static final long ONE_MB = ONE_KB * 1024L;
    public static final long CACHE_DATA_MAX_SIZE = ONE_MB * 3L;
    private static final String TAG = "BaseApplication";
    private static NewMainActivity mainActivity = null;
    public static BaseApplication getInstance() {
        return ourInstance;
    }
    PersonDao personDao;
    String deviceTargetValue;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    GetDeviceIDContract presenter;
    public static BaseApplication instances;
    boolean ret = false;
    //    private CCPRestSmsSDK restAPI;
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    public static BaseApplication getInstances() {
        return instances;
    }

    public void setRet(boolean ret) {
        this.ret = ret;
    }


    @Override
    public void onCreate() {
        super.onCreate();
//        CrashHandler.getInstance().init(this);
//        LogcatHelper.getInstance(this).start();
        setDatabase();
        instances = this;
        ourInstance = this;
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

        Logger.init("S1 Vip Manages")               // default tag : PRETTYLOGGER or use just init()
                .hideThreadInfo();             // default it is shown
        this.initGson();
        this.initReservoir();
        this.initCCPRestSms();
        presenter=new GetDeviceIDContract();
        presenter.attachView(this);
        initCloudChannel(this);
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
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
//        DaoMaster.DevOpenHelper mHelpter = new DaoMaster.DevOpenHelper(this,"notes-db");
        HMROpenHelper mHelpter = new HMROpenHelper(this, "notes-db", null);//为数据库升级封装过的使用方式
        db = mHelpter.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }
    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    public boolean isRet() {
        return ret;
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
//     */
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
     *
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
                        deviceTargetValue = Utils.getMD5(getMac());
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                presenter.getDeviceID(deviceTargetValue,1);
                            }
                        }).start();
                        Logger.e(TAG + "init cloudchannel success" +"   deviceTargetValue:" + deviceTargetValue);
//                        }
//                        Log.i(TAG, "init cloudchannel success" + deviceTargetValue);
                    }

                    @Override
                    public void onFailed(String errorCode, String errorMessage) {
                        Log.e(TAG, "init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
//                setConsoleText("init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
                    }
                });
//        MiPushRegister.register(applicationContext, "XIAOMI_ID", "XIAOMI_KEY"); // 初始化小米辅助推送
//        HuaWeiRegister.register(applicationContext); // 接入华为辅助推送
//        GcmRegister.register(applicationContext, "send_id", "application_id"); // 接入FCM/GCM初始化推送
    }
    /**
     * 添加推送账户
     */
    private void devicebindAccount (final Context applicationContext) {
        PushServiceFactory.init(applicationContext);
        final CloudPushService pushService = PushServiceFactory.getCloudPushService();
        pushService.bindAccount(Utils.getMD5(getMac()), new CommonCallback() {
            @Override
            public void onSuccess(String s) {
                deviceTargetValue = Utils.getMD5(getMac());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        presenter.getDeviceID(deviceTargetValue,1);
                    }
                }).start();
                Logger.e(TAG + "init cloudchannel success" + "   deviceTargetValue:" + deviceTargetValue);
            }
            @Override
            public void onFailed(String s, String s1) {
                Log.e(TAG, "init cloudchannel failed -- errorcode:" + s + " -- errorMessage:" + s1);
            }
        });
    }
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
    public static void setMainActivity(NewMainActivity activity) {
        mainActivity = activity;
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
    public void downloadSuccess(DownLoadData resultResponse) {
        if (resultResponse!=null) {
            personDao = getDaoSession().getPersonDao();
            List<Person> persons=personDao.loadAll();
            QueryBuilder qb = personDao.queryBuilder();
            List<Person> users = qb.where(PersonDao.Properties.Uid.eq(resultResponse.getDown_userInfo()[0].getUid())).list();
            if (users.size() > 0) {
            } else{
                Person person = new Person();
                person.setPos(persons.size()+"");
                person.setUid(resultResponse.getDown_userInfo()[0].getUid());
                person.setNumber(resultResponse.getDown_userInfo()[0].getUserName());
                person.setFingermodel(resultResponse.getDown_userInfo()[0].getFeature());
                getDaoSession().getPersonDao().insert(person);
            }
        }
    }

    class ResultData<T>{
        T data;
        String msg;
    }
        @Override
    public void getDeviceSuccess(DeviceData deviceData) {
       Logger.e("BaseApplication+devicedate"+deviceData.getDeviceData().getDeviceId()+"numberType"+deviceData.getDeviceData().getNumberType());
            SharedPreferences userInfo = getSharedPreferences("user_info",0);
                userInfo.edit().putString("deviceId", deviceData.getDeviceData().getDeviceId()).commit();
                userInfo.edit().putInt("numberType",deviceData.getDeviceData().getNumberType()).commit();
                CloudPushService pushService = PushServiceFactory.getCloudPushService();
            pushService.bindAccount(deviceData.getDeviceData().getDeviceId(), new CommonCallback() {
                @Override
                public void onSuccess(String s) {
                    Logger.e(TAG + "init cloudchannel bindAccount" +"deviceTargetValue:" + deviceData.getDeviceData().getDeviceId());
                }
                @Override
                public void onFailed(String s, String s1) {
                }
            });
    }

    public static void setConsoleText(String text) {
        if (mainActivity != null && text != null) {
            mainActivity.todialog(text);
        }
    }
    private static PushMessage pushMessage;
    public static PushMessage toJsonArray(String json) {
        try {
            pushMessage = new PushMessage();
            JSONObject object=new JSONObject(json);
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
}
