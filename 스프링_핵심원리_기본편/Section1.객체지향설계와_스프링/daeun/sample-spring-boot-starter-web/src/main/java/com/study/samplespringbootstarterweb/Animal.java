package com.study.samplespringbootstarterweb;

public class Animal {
    String name = "";
    public Animal(String name) {
        setName(name);
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getName(){
        return name;
    }
}
