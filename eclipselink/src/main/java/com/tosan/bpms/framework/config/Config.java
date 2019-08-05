package com.tosan.bpms.framework.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by kasra.haghpanah on 25/06/2018.
 */
public class Config {

    static final Map<String, Map<String, String>> resources = new HashMap<String, Map<String, String>>();
    static final Map<String, Properties> properties = new HashMap<String, Properties>();

    public static void addResource(String resource) {
        synchronized (Config.class) {
            Properties properties = load(resource);
            resources.put(resource, getMapProperty(properties));
            properties.put(resource, properties);
        }
    }

    private static Properties load(String resource) {

        Properties propertiesObj = null;

        synchronized (Config.class) {
            InputStream inputStream = Config.class.getClass().getResourceAsStream(resource);

            try {
                propertiesObj = new Properties();
                propertiesObj.load(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return propertiesObj;
    }

    public static Properties getProperty(String resource) {
        return load(resource);
    }

    public static Object getPropertyByResource(String resource, String key) {
        Map<String, String> map = resources.get(resource);
        if (map == null) {
            return null;
        }
        return map.get(key);
    }

    private static Map<String, String> getMapProperty(Properties property) {

        Map<String, String> map = new HashMap<String, String>();
        Enumeration<String> enums = (Enumeration<String>) property.propertyNames();

        while (enums.hasMoreElements()) {
            String key = enums.nextElement();
            String value = property.getProperty(key);
            map.put(key, value);
        }
        return map;

    }

    public static Map<String, Map<String, String>> getResources() {
        return resources;
    }
}
