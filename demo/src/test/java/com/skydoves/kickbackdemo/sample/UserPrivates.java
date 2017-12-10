package com.skydoves.kickbackdemo.sample;

/**
 * Developed by skydoves on 2017-12-05.
 * Copyright (c) 2017 skydoves rights reserved.
 */

public class UserPrivates {
    private String userToken;
    private String userUuid;

    public UserPrivates(String userToken, String userUuid) {
        this.userToken = userToken;
        this.userUuid = userUuid;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }
}
