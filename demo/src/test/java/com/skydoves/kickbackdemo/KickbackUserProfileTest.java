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

    private Kickback_Profile box_profile = Kickback_Profile.getInstance();
    private static final String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
    private static final String uuid = "00001234-0000-0000-0000-000123456789";

    @Test
    public void initializeKickbackElementTest() {
        assertThat(box_profile.getVisits(), is(1));
        assertThat(box_profile.getName(), is("skydoves"));
        assertThat(box_profile.getPassword(), is(1234));
        Assert.assertNull(box_profile.getDog());
    }

    @Test
    public void setKickbackElementTest() {
        box_profile.setVisits(10);
        box_profile.setName("Kickback");
        box_profile.setPassword(4321);
        box_profile.setDog(new Dog("Akita", 4, "White"));

        assertThat(box_profile.getVisits(), is(10));
        assertThat(box_profile.getName(), is("Kickback"));
        assertThat(box_profile.getPassword(), is(4321));
        assertThat(box_profile.getDog().getBreed(), is("Akita"));
        assertThat(box_profile.getDog().getAge(), is(4));
        assertThat(box_profile.getDog().getColor(), is("White"));
    }

    @Test
    public void freeKickbackElementTest() {
        box_profile.freeVisits();
        box_profile.freeName();
        box_profile.freePassword();
        box_profile.freeDog();

        Assert.assertNull(box_profile.getVisits());
        Assert.assertNull(box_profile.getName());
        Assert.assertNull(box_profile.getPassword());
        Assert.assertNull(box_profile.getDog());
    }

    @Test
    public void weakKickbackElementTest() {
        box_profile.setPassword(4321);
        box_profile.setSecondPassword(11111);

        Assert.assertNotNull(box_profile.getPassword());
        Assert.assertNotNull(box_profile.getSecondPassword());

        System.gc();

        Assert.assertNull(box_profile.getPassword());
        Assert.assertNull(box_profile.getSecondPassword());
    }

    @Test
    public void softKickbackElementTest() {
        box_profile.setDog(new Dog("Akita", 4, "White"));
        box_profile.setUserPrivates(new UserPrivates(token, uuid));

        Assert.assertNotNull(box_profile.getDog());
        Assert.assertNotNull(box_profile.getUserPrivates());

        System.gc();

        Assert.assertNotNull(box_profile.getDog());
        Assert.assertNotNull(box_profile.getUserPrivates());
    }
}
