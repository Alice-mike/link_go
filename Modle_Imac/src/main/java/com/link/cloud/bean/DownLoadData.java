package com.link.cloud.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 30541 on 2018/3/10.
 */
public class DownLoadData extends ResultResponse {
    @SerializedName("data")
    Down_UserInfo[] down_userInfo;

    public void setDown_userInfo(Down_UserInfo[] down_userInfo) {
        this.down_userInfo = down_userInfo;
    }

    public Down_UserInfo[] getDown_userInfo() {
        return down_userInfo;
    }
}
