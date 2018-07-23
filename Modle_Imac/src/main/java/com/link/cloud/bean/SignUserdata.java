package com.link.cloud.bean;

import com.google.gson.annotations.SerializedName;
import com.link.cloud.base.Signdata;

/**
 * Created by 30541 on 2018/3/10.
 */

public class SignUserdata extends ResultResponse {
    @SerializedName("data")
    Signdata signdata;

    public Signdata getSigndata() {
        return signdata;
    }

    public void setSigndata(Signdata signdata) {
        this.signdata = signdata;
    }


}
