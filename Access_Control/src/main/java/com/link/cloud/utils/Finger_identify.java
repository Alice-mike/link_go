package com.link.cloud.utils;

import android.app.Activity;
import android.database.Cursor;

import com.link.cloud.BaseApplication;
import com.link.cloud.activity.LockActivity;
import com.link.cloud.greendao.gen.PersonDao;

import static com.alibaba.sdk.android.ams.common.util.HexUtil.hexStringToByte;

/**
 * Created by 30541 on 2018/6/20.
 */

public class Finger_identify {
    final static float IDENTIFY_SCORE_THRESHOLD=0.63f;
   public static String Finger_identify (LockActivity activty, byte[] img){
       int[]pos=new int[1];
       float[]score=new float[1];
       int i=0;
       Cursor cursor;
       String sql;
       sql = "select FINGERMODEL,UID from PERSON" ;
       cursor = BaseApplication.getInstances().getDaoSession().getDatabase().rawQuery(sql,null);
       byte[][] feature=new byte[cursor.getCount()][];
       String [] Uids=new String[cursor.getCount()];
       while (cursor.moveToNext()){
           int nameColumnIndex = cursor.getColumnIndex("FINGERMODEL");
           String strValue=cursor.getString(nameColumnIndex);
           feature[i]=hexStringToByte(strValue);
           Uids[i]=cursor.getString(cursor.getColumnIndex("UID"));
           i++;
       }
       int len = 0;
       // 计算一维数组长度
       if(feature.length>0) {
           for (byte[] element : feature) {
               len += element.length;
           }
           // 复制元素
           byte[]  nFeatuer = new byte[len];
           int index = 0;
           for (byte[] element : feature) {
               for (byte element2 : element) {
                   nFeatuer[index++] = element2;
               }
           }
           boolean  identifyResult = activty.microFingerVein.fv_index(nFeatuer, nFeatuer.length / 3352, img, pos, score);//比对是否通过
           identifyResult = identifyResult && score[0] > IDENTIFY_SCORE_THRESHOLD;//得分是否达标
           if (identifyResult) {
               String Uid = Uids[pos[0]];
               return Uid;
           }else {
               return null;
           }
       }else {
           return null;
       }
   }
}
