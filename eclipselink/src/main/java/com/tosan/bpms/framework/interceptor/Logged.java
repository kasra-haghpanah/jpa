package com.tosan.bpms.framework.interceptor;

import java.lang.annotation.*;

/**
 * Created by kasra.haghpanah on 01/07/2017.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Logged {
}
