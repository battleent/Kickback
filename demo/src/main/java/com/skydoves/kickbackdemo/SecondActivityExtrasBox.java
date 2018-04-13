package com.skydoves.kickbackdemo;

import com.skydoves.kickback.KickbackBox;
import com.skydoves.kickback.KickbackElement;
import com.skydoves.kickback.Weak;

/**
 * Created by skydoves on 2018. 4. 13.
 * Copyright (c) 2018 battleent All rights reserved.
 */

@KickbackBox(name = "SecondActivityExtra")
abstract public class SecondActivityExtrasBox {
    @KickbackElement(name = "name")
    String userName;

    @Weak
    @KickbackElement(name = "password")
    final int userPassword = 0;
}
