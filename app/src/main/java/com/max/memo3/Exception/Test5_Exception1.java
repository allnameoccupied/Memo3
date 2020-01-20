package com.max.memo3.Exception;

public class Test5_Exception1 extends Error {
    //var
    private static String DEFAULT_MESSAGE = "Test5 Exception 1";

    //func
    public Test5_Exception1(){super(DEFAULT_MESSAGE);}
    public Test5_Exception1(String message){super(message!=null?message:DEFAULT_MESSAGE);}
}
