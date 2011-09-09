package com.orange.groupbuy.push.action;

import com.orange.common.mongodb.MongoDBClient;
import com.orange.groupbuy.constant.DBConstants;
import com.orange.groupbuy.dao.PushMessage;

public class ActionCreator {

    public static CommonAction getAction(PushMessage pushMessage, MongoDBClient mongoClient) {
        
        int messageType = pushMessage.getPushType();
        switch (messageType){
        case DBConstants.C_PUSH_TYPE_IPHONE:
            return new PushiPhoneMessage(pushMessage, mongoClient);
        case DBConstants.C_PUSH_TYPE_ANDROID:
            break;
        case DBConstants.C_PUSH_TYPE_EMAIL:
            return new PushEmailMessage(pushMessage);
        case DBConstants.C_PUSH_TYPE_WEIBO:
            break;
        }
        
        return null;
    }
}
