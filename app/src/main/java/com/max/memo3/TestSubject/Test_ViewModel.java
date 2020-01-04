package com.max.memo3.TestSubject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class Test_ViewModel extends ViewModel {
    //var
    private MutableLiveData<String> name = new MutableLiveData<>();

    //func
    public void setName (String name){
        this.name.setValue(name);
    }

    public LiveData<String> getName(){
        return name;
    }
}
