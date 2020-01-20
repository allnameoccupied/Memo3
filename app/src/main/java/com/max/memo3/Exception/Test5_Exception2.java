package com.max.memo3.Exception;

public class Test5_Exception2 extends Exception {
    //var
    private static String DEFAULT_MESSAGE = "Test5 Exception 2";

    //func
    public Test5_Exception2(){super(DEFAULT_MESSAGE);}
    public Test5_Exception2(String message){super(message!=null?message:DEFAULT_MESSAGE);}
}
