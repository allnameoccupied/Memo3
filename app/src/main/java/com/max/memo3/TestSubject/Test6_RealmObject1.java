package com.max.memo3.TestSubject;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Test6_RealmObject1 extends RealmObject {
    //var
//    @PrimaryKey
    private int id;
    private int int1;
    private String s1;

    //func
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getInt1() {
        return int1;
    }

    public void setInt1(int int1) {
        this.int1 = int1;
    }

    public String getS1() {
        return s1;
    }

    public void setS1(String s1) {
        this.s1 = s1;
    }
}
