package com.link.cloud.greendaodemo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by 30541 on 2018/6/26.
 */
@Entity
public class SignUser {
    @Id
    Long id;
    String userId;
    String pos;

    @Generated(hash = 1423642848)
    public SignUser(Long id, String userId, String pos) {
        this.id = id;
        this.userId = userId;
        this.pos = pos;
    }

    @Generated(hash = 93985625)
    public SignUser() {
    }

    public String getPos() {
        return pos;
    }

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
