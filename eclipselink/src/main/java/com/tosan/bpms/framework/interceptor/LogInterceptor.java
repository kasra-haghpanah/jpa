package com.tosan.bpms.framework.interceptor;


import com.google.inject.persist.Transactional;
import com.tosan.bpms.framework.orm.repository.jpql.Repository;
import com.tosan.bpms.model.sql.Log;
import com.tosan.bpms.service.LogService;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * Created by kasra.haghpanah on 01/07/2017.
 */
//@Singleton
public class LogInterceptor implements MethodInterceptor {


    //@Inject
    //@Named("kafka_log")
    final Repository repository;
    //private final ThreadLocal<Boolean> didWeStartWork = new ThreadLocal();

    //@Inject
    public LogInterceptor(Repository repository) {
        this.repository = repository;
    }



    public Object invoke(MethodInvocation invocation) throws Throwable {


        //repository.getEntityManager().getTransaction().begin();
        //this.didWeStartWork.set(true);
        Method method = invocation.getMethod();
        System.out.println("\u001B[43mBefore Logged!");

        Log log = new Log();
        log.setMethod(method.getName());
        log.setClassName(method.getDeclaringClass().getName());
        log.setDate(new Date());

        Object o = null;
        try {
            o = invocation.proceed();
        } catch (Throwable throwable) {

            StringWriter errors = new StringWriter();
            throwable.printStackTrace(new PrintWriter(errors));
            log.setException(errors.toString());
            throwable.printStackTrace();
        }

        repository.save(log);
        //repository.getEntityManager().getTransaction().commit();
        //this.didWeStartWork.remove();
        System.out.println("\u001B[45mAfter Logged!");
        return o;
    }
}
