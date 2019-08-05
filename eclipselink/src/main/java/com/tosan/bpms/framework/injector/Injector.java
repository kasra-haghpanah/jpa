package com.tosan.bpms.framework.injector;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.Transactional;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.tosan.bpms.bean.scope.CustomScopes;
import com.tosan.bpms.bean.scope.Person;
import com.tosan.bpms.bean.statics.StaticInjection;
import com.tosan.bpms.framework.config.Config;
import com.tosan.bpms.framework.interceptor.JpaLocalTxnInterceptor2;
import com.tosan.bpms.framework.interceptor.LogInterceptor;
import com.tosan.bpms.framework.interceptor.Logged;
import com.tosan.bpms.framework.orm.repository.jpql.Repository;
import com.tosan.bpms.framework.orm.service.Service;
import com.tosan.bpms.bean.provider.Gum;
import com.tosan.bpms.bean.provider.GumProvider;
import com.tosan.bpms.framework.validator.custom.ComplexBeanValid;
import com.tosan.bpms.framework.validator.custom.ComplexBeanValidator;
import com.tosan.bpms.framework.validator.custom.CustomService;
//import com.tosan.bpms.service.GroupService;
import com.tosan.bpms.service.LogService;
import com.tosan.bpms.service.MyService;
import com.tosan.bpms.service.UserService;
import ru.vyarus.guice.validator.ImplicitValidationModule;

import java.util.*;

/**
 * Created by kasra.haghpanah on 02/07/2017.
 */
public class Injector {

    static final Map<String, com.google.inject.Injector> injectorPersistMap = new HashMap<String, com.google.inject.Injector>();

    public static <M extends Repository> M getReository(String unitname, Class<M> implementation) {

        unitname = unitname.toLowerCase();

        if (!unitname.equals("kafka_log")) {
            unitname = "kafka";
        }

        com.google.inject.Injector injectorPersist = injectorPersistMap.get(unitname);


        if (unitname.equals("kafka_log")) {

            if (injectorPersist == null) {
                synchronized (Injector.class) {

                    injectorPersist = Guice.createInjector(
                            new AbstractModule() {
                                @Override
                                protected void configure() {

                                    //bind(new TypeLiteral<Repository2<Log>>() {}).annotatedWith(Names.named("persist")).toInstance(getReository("kafka_log" ,LogRepository.class));

                                    //bind(new TypeLiteral<Repository2<Log>>() {}).to(LogRepository.class);
                                    //bind(LogRepository.class);

                                    // bind(Repository1.class).annotatedWith(Names.named("log")).toInstance(getReository("kafka_log", Repository1.class));

                                    install(new JpaPersistModule("kafka_log"));
                                    //for validation
                                    install(new ImplicitValidationModule());

                                }
                            });

                    PersistService persistService = injectorPersist.getInstance(PersistService.class);
                    persistService.start();

                    injectorPersistMap.put(unitname, injectorPersist);

                }
            }
        } else if (unitname.equals("kafka")) {

            if (injectorPersist == null) {
                synchronized (Injector.class) {

                    injectorPersist = Guice.createInjector(
                            new AbstractModule() {
                                @Override
                                protected void configure() {


                                    install(new JpaPersistModule("kafka"));

                                    //for validation
                                    install(new ImplicitValidationModule());

                                }


                            });

                    PersistService persistService = injectorPersist.getInstance(PersistService.class);
                    persistService.start();

                    injectorPersistMap.put(unitname, injectorPersist);

                }
            }
        }


        M instance = (M) injectorPersist.getInstance(implementation);

        return instance;
    }


    public static <M extends Service> M getBean(Class<M> implementation) {

        return getAnyBean(implementation);
    }


    public static <M> M getAnyBean(Class<M> implementation) {

        com.google.inject.Injector injectorPersist = injectorPersistMap.get("simple-bean");

        if (injectorPersist == null) {

            synchronized (Injector.class) {
                injectorPersist = Guice.createInjector(
                        new AbstractModule() {
                            @Override
                            protected void configure() {
                                //bind(UserService.class).annotatedWith(Names.named("persist")).toInstance(getReository("kafka", UserService.class));
                                //bind(new TypeLiteral<Service<User>>() {}).annotatedWith(Names.named("persist")).toInstance(getReository("kafka", UserService.class));

                                //LogService logService = getReository("kafka_log", LogService.class);
                                //UserService userService = getReository("kafka", UserService.class);
                                //GroupService groupService = getReository("kafka", GroupService.class);
                                ////////////////////////////////////////////////////////////////////////////////////////////
                                bind(Repository.class).annotatedWith(Names.named("kafka")).toInstance(getReository("kafka", Repository.class));
                                bind(Repository.class).annotatedWith(Names.named("kafka_log")).toInstance(getReository("kafka_log", Repository.class));
                                //bind(new TypeLiteral<Service<User>>() {}).toInstance(getReository("kafka", UserService.class));
                                ////////////////////////////////////////////////////////////////////////////////////////////
                                //bind(GroupService.class).toInstance(getReository("kafka", GroupService.class));
                                //bind(new TypeLiteral<Service<Group>>() {}).toInstance(getReository("kafka", GroupService.class));
                                ////////////////////////////////////////////////////////////////////////////////////////////
                                //bind(LogService.class).toInstance(getReository("kafka_log", LogService.class));
                                //bind(new TypeLiteral<Service<Log>>() {}).toInstance(getReository("kafka_log", LogService.class));
                                ////////////////////////////////////////////////////////////////////////////////////////////
                                bind(MyService.class);
                                bind(UserService.class);
                                //bind(GroupService.class);
                                bind(LogService.class);
                                //bind(LogInterceptor.class);

                                //inteceptor
                                bindInterceptor(Matchers.any(), Matchers.annotatedWith(Logged.class), new LogInterceptor(getReository("kafka_log", Repository.class)));
                                bindInterceptor(Matchers.any(), Matchers.annotatedWith(Transactional.class), new JpaLocalTxnInterceptor2());


                                ////////////////////////////////////////////////////////////////////////////////////////////
                                bind(ComplexBeanValidator.class);
                                bind(CustomService.class);

                                //for validation
                                //install(new ImplicitValidationModule());

                                // custom provider
                                bind(Gum.class).toProvider(GumProvider.class);


                                // for property
                                Config.addResource("/config.properties");
                                Config.addResource("/META-INF/config2.properties");
                                Map<String, String> properties = Config.getResources().get("/config.properties"); //Config.getProperty("/config.properties");
                                Names.bindProperties(binder(), properties);
                                // for property


                                //for static injection
                                bindConstant().annotatedWith(Names.named("my.static")).to("My static method injection!");
                                requestStaticInjection(StaticInjection.class);
                                //for static injection


                                // custom scope
                                bind(Person.class).in(CustomScopes.DEFAULT);
                                // custom scope


                                //for validation
                                install(new ImplicitValidationModule()
                                        .withMatcher(
                                                Matchers
                                                        .not(Matchers.annotatedWith(ComplexBeanValid.class))
                                                        .and(Matchers.any())
                                        )
                                );



                            }
                        }
                );

            }

            injectorPersistMap.put("simple-bean", injectorPersist);

        }


        M instance = (M) injectorPersist.getInstance(implementation);

        return instance;
    }

}

