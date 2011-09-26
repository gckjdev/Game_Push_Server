package com.orange.groupbuy.push.apnsmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import com.orange.common.mongodb.MongoDBClient;
import com.orange.common.utils.StringUtil;
import com.orange.groupbuy.constant.DBConstants;
import com.orange.groupbuy.dao.App;
import com.orange.groupbuy.pushserver.PushConstants;

public class AppApnsManager {
    public MongoDBClient mongoClient;
    public static Map<String, ApnsService> devServiceMap;
    public static Map<String, ApnsService> productServiceMap;
    public static List<App> apps;
    public static AppApnsManager instance;
    private static String apnsMode;

    public AppApnsManager(MongoDBClient mongoClient) {
        super();
        this.mongoClient = mongoClient;
        init();
    }

    public void init(){
        loadParams();
        apps = findAllApp();
    }
    
    public static AppApnsManager getInstance(MongoDBClient mongoClient){
        if(instance == null){
            instance = new AppApnsManager(mongoClient);
        }
        return instance;
    }

    private static void loadParams() {
        if (!StringUtil.isEmpty(System.getProperty("apns")))
        apnsMode = System.getProperty("apns");
    }

    public void createApnsServiecs() {
        ApnsService devService;
        ApnsService productService;
        for (App app : apps) {
            devService = APNS.newService().withCert(app.getDevCertificateFileName(), app.getDevCertPassword())
                    .withSandboxDestination().build();

            productService = APNS.newService().withCert(app.getProductCertificateFileName(), app.getProductCertPassword())
                    .withProductionDestination().build();
            
            if(devServiceMap == null){
                devServiceMap = new HashMap<String, ApnsService>();
            }
            if(productServiceMap == null){
                productServiceMap = new HashMap<String, ApnsService>();
            }
            devServiceMap.put(app.getAppId(), devService);
            productServiceMap.put(app.getAppId(), productService);
        }
    }

    public List<App> findAllApp() {
        DBCursor cursor = mongoClient.findAll(DBConstants.T_APP);
        if (cursor == null)
            return null;

        List<App> appList = new ArrayList<App>();
        Iterator<?> iter = cursor.iterator();
        if (iter == null) {
            return null;
        }
        while (iter.hasNext()) {
            BasicDBObject obj = (BasicDBObject) iter.next();
            App app = new App(obj);
            appList.add(app);
        }

        cursor.close();
        return appList;

    }

    public static ApnsService findApnsServiceByAppId(String appId) {
        if (!StringUtil.isEmpty(apnsMode) && apnsMode.equalsIgnoreCase(PushConstants.PRODUCTION)) {
            return productServiceMap.get(appId);
        }
        return devServiceMap.get(appId);
    }

}
