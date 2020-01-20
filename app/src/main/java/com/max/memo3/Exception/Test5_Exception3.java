package com.max.memo3.Exception;

public class Test5_Exception3 extends RuntimeException {
    //var
    private static String DEFAULT_MESSAGE = "Test5 Exception 3";

    //func
    public Test5_Exception3(){super(DEFAULT_MESSAGE);}
    public Test5_Exception3(String message){super(message!=null?message:DEFAULT_MESSAGE);}
}
