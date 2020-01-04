package com.max.memo3.Util;

import io.realm.RealmObject;

public class MemoRecord extends RealmObject implements Interface_for_Firestore_class {
    public String title;
    public int qwer;

    public MemoRecord() {
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public MemoRecord(String title, int qwer) {
        this.title = title;
        this.qwer = qwer;
    }

    public int getQwer() {
        return qwer;
    }

    public void setQwer(int qwer) {
        this.qwer = qwer;
    }
}
