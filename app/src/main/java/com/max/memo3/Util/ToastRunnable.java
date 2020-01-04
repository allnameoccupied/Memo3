package com.max.memo3.Util;

import static com.max.memo3.Util.util.makeToast_wait;

public class ToastRunnable implements Runnable{
    public CharSequence theThing;
    ToastRunnable(){}
    ToastRunnable(CharSequence theThing){
        this.theThing = theThing;
    }
    @Override
    public void run() {
        makeToast_wait(theThing);
    }
}