package com.max.memo3.TestSubject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class Test_ViewModel extends ViewModel {
    //var
    public static String DEFAULT_MSG = "Testing in Progress";
    private MutableLiveData<String> name = new MutableLiveData<>();
    private MutableLiveData<String> msgToShow = new MutableLiveData<>();

    //func
    public void setName (String name){
        this.name.setValue(name);
    }

    public LiveData<String> getName(){
        return name;
    }

    public void setMsgToShow(String msg){this.msgToShow.setValue(msg);}

    public LiveData<String> getMsgToShow(){return msgToShow;}

    public void resetMsgToShow(){setMsgToShow(DEFAULT_MSG);}
}
