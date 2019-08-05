package com.tosan.bpms.controller;

import com.tosan.bpms.bean.property.PropertiesBean;
import com.tosan.bpms.framework.injector.Injector;

/**
 * Created by kasra.haghpanah on 30/06/2018.
 */
public class PropertiesController {

    public static void main(String[] args) {
        PropertiesBean propertiesBean = Injector.getAnyBean(PropertiesBean.class);
        System.out.println(propertiesBean);

    }
}
