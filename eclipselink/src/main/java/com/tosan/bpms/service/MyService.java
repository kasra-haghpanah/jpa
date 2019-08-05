package com.tosan.bpms.service;

import com.tosan.bpms.framework.interceptor.Logged;

/**
 * Created by kasra.haghpanah on 14/12/2018.
 */
public class MyService {

    @Logged
    public String myMethod(){
        return "My Name is Kasra!";
    }
}
