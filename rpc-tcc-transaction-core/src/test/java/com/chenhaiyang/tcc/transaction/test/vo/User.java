package com.chenhaiyang.tcc.transaction.test.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable{

    private int id;

    private int age;

    private String name;

    public User(int id, int age, String name) {
        this.id = id;
        this.age = age;
        this.name = name;
    }
}
