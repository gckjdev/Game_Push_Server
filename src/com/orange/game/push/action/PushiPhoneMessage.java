package com.orange.game.push.action;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.notnoop.apns.ApnsService;
import com.orange.common.mongodb.MongoDBClient;
import com.orange.game.constants.ErrorCode;
import com.orange.game.model.dao.PushMessage;
import com.orange.game.model.manager.NotificationService;

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
                        
        return NotificationService.getInstance().directSendMessage(appId, deviceToken, badge, alertMessage, sound);
    }

    @Override
    public int validateMessage() {
        
        String deviceToken = pushMessage.getDeviceToken();
        if (deviceToken == null || deviceToken.length() == 0)
            return ErrorCode.ERROR_DEVICE_TOKEN_NULL;
        
        return 0;
    }

}
