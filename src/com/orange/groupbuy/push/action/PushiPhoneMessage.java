package com.orange.groupbuy.push.action;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.notnoop.apns.ApnsService;
import com.orange.common.apnsservice.AppApnsService;
import com.orange.common.apnsservice.BasicService;
import com.orange.common.mongodb.MongoDBClient;
import com.orange.groupbuy.constant.ErrorCode;
import com.orange.groupbuy.constant.ServiceConstant;
import com.orange.groupbuy.dao.App;
import com.orange.groupbuy.dao.PushMessage;
import com.orange.groupbuy.manager.AppManager;
import com.orange.groupbuy.push.apnsmanager.AppApnsManager;

public class PushiPhoneMessage extends CommonAction {

    static Logger log = Logger.getLogger(PushiPhoneMessage.class.getName()); 
    MongoDBClient mongoClient;
    
    public PushiPhoneMessage(PushMessage message, MongoDBClient mongoClient) {
        this.pushMessage = message;
        this.mongoClient = mongoClient;
    }

    @Override
    public int sendMessage() {

        int badge = 1;
        String sound = "default";
        String deviceToken = pushMessage.getDeviceToken();
        String alertMessage = pushMessage.getPushIphone();
        String appId = pushMessage.getAppId();
        HashMap<String, Object> userInfo = new HashMap<String, Object>();
        
        userInfo.put(ServiceConstant.PARA_ITEMID, pushMessage.getItemId());
        
        App app = AppManager.getApp(mongoClient, appId);
        if (app == null){
            log.error("send push message but appId "+appId+" not found");
            return ErrorCode.ERROR_APP_NOT_FOUND;
        }
        
//        String appKey = app.getPushAppKey();
//        String appSecret = app.getPushAppSecret();
//        String appMasterSecret = app.getPushAppMasterSecret();
//        if (appKey == null || appSecret == null || appMasterSecret == null){
//            log.error("send push message but app key/secret/master secret is null");
//            return ErrorCode.ERROR_APP_EMPTY_PUSH_INFO;            
//        }
//        
//        BasicService pushService = PushMessageService.createService(appKey,
//                                                                    appSecret,
//                                                                    appMasterSecret,
//                                                                    deviceToken, badge, alertMessage, sound, userInfo);

        /*        
        String certificate = app.getDevCertificateFileName();
        String password = app.getDevCertPassword();
        if (certificate == null || password == null) {
//            code below is mainly for test            
//            String SEP = System.getProperty("file.separator");
//            certificate = System.getProperty("user.dir") + SEP + "certificate" + SEP + "groupbuy_push_development.p12";
//            password = "123456";
            
            log.error("send push message but cannot find push certificate for the app");
            return ErrorCode.ERROR_APP_CERT_NOT_FOUND;
        }
        
//        BasicService pushService = ApnsService.createService(certificate,password,            
//                                                            deviceToken, badge, alertMessage, sound, userInfo);

         */
        
        ApnsService apnsService = AppApnsManager.findApnsServiceByAppId(appId);
        
        if(apnsService == null) {
            log.error("apnsService for appId "+appId+" not found");
            return ErrorCode.ERROR_APNS_NOT_FOUND;
        }
        
        BasicService pushService = AppApnsService.createService(apnsService,            
                                    deviceToken, badge, alertMessage, sound, userInfo);

        return pushService.handleServiceRequest();
    }

    @Override
    public int validateMessage() {
        
        String deviceToken = pushMessage.getDeviceToken();
        if (deviceToken == null || deviceToken.length() == 0)
            return ErrorCode.ERROR_DEVICE_TOKEN_NULL;
        
        return 0;
    }

}
