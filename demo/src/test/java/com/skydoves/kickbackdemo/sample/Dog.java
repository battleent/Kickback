package com.skydoves.kickbackdemo.sample;

/**
 * Developed by skydoves on 2017-12-05.
 * Copyright (c) 2017 skydoves rights reserved.
 */

public class Dog {
    private String breed;
    private int age;
    private String color;

    public Dog(String breed, int age, String color) {
        this.breed = breed;
        this.age = age;
        this.color = color;
    }

    void barking() {
    }

    void hungry() {
    }

    void sleeping() {
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}