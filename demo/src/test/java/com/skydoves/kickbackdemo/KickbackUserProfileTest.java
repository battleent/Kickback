package com.skydoves.kickbackdemo;

import com.skydoves.kickbackdemo.kickback.Kickback_Profile;
import com.skydoves.kickbackdemo.sample.Dog;
import com.skydoves.kickbackdemo.sample.UserPrivates;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Developed by skydoves on 2017-12-05.
 * Copyright (c) 2017 skydoves rights reserved.
 */

public class KickbackUserProfileTest {

    private static final String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
    private static final String uuid = "00001234-0000-0000-0000-000123456789";

    @Test
    public void initializeKickbackElementTest() {
        assertThat(Kickback_Profile.getVisits(), is(1));
        assertThat(Kickback_Profile.getName(), is("skydoves"));
        assertThat(Kickback_Profile.getPassword(), is(1234));
        Assert.assertNull(Kickback_Profile.getDog());
    }

    @Test
    public void setKickbackElementTest() {
        Kickback_Profile.setVisits(10);
        Kickback_Profile.setName("Kickback");
        Kickback_Profile.setPassword(4321);
        Kickback_Profile.setDog(new Dog("Akita", 4, "White"));

        assertThat(Kickback_Profile.getVisits(), is(10));
        assertThat(Kickback_Profile.getName(), is("Kickback"));
        assertThat(Kickback_Profile.getPassword(), is(4321));
        assertThat(Kickback_Profile.getDog().getBreed(), is("Akita"));
        assertThat(Kickback_Profile.getDog().getAge(), is(4));
        assertThat(Kickback_Profile.getDog().getColor(), is("White"));
    }

    @Test
    public void freeKickbackElementTest() {
        Kickback_Profile.freeVisits();
        Kickback_Profile.freeName();
        Kickback_Profile.freePassword();
        Kickback_Profile.freeDog();

        Assert.assertNull(Kickback_Profile.getVisits());
        Assert.assertNull(Kickback_Profile.getName());
        Assert.assertNull(Kickback_Profile.getPassword());
        Assert.assertNull(Kickback_Profile.getDog());
    }

    @Test
    public void weakKickbackElementTest() {
        Kickback_Profile.setPassword(4321);
        Kickback_Profile.setSecondPassword(11111);

        Assert.assertNotNull(Kickback_Profile.getPassword());
        Assert.assertNotNull(Kickback_Profile.getSecondPassword());

        System.gc();

        Assert.assertNull(Kickback_Profile.getPassword());
        Assert.assertNull(Kickback_Profile.getSecondPassword());
    }

    @Test
    public void softKickbackElementTest() {
        Kickback_Profile.setDog(new Dog("Akita", 4, "White"));
        Kickback_Profile.setUserPrivates(new UserPrivates(token, uuid));

        Assert.assertNotNull(Kickback_Profile.getDog());
        Assert.assertNotNull(Kickback_Profile.getUserPrivates());

        System.gc();

        Assert.assertNotNull(Kickback_Profile.getDog());
        Assert.assertNotNull(Kickback_Profile.getUserPrivates());
    }
}
