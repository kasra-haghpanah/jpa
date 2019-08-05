package com.tosan.bpms.controller;

import com.tosan.bpms.bean.scope.Person;
import com.tosan.bpms.framework.injector.Injector;

/**
 * Created by kasra.haghpanah on 30/06/2018.
 */
public class CustomScopesController {

    public static void main(String[] args) {

        Person person1 = Injector.getAnyBean(Person.class);
        Person person2 = Injector.getAnyBean(Person.class);

    }

}
