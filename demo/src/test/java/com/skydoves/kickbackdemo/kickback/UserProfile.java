package com.skydoves.kickbackdemo.kickback;

import com.skydoves.kickback.KickbackBox;
import com.skydoves.kickback.KickbackElement;
import com.skydoves.kickback.Soft;
import com.skydoves.kickback.Weak;
import com.skydoves.kickbackdemo.sample.Dog;
import com.skydoves.kickbackdemo.sample.UserPrivates;

/**
 * Developed by skydoves on 2017-12-03.
 * Copyright (c) 2017 skydoves rights reserved.
 */

@KickbackBox(name = "Profile")
public abstract class UserProfile {
    final int visits = 1;

    @KickbackElement(name = "name")
    final String userName = "skydoves";

    @Weak
    @KickbackElement(name = "password")
    final int userPassword = 1234;

    @Weak
    @KickbackElement(name = "secondPassword")
    final int userSecondPassword = 12345;

    @Soft
    @KickbackElement(name = "dog")
    Dog userDog;

    @Soft
    UserPrivates userPrivates;
}
