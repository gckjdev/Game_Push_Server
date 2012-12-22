package com.orange.game.push.action;

import com.orange.common.mongodb.MongoDBClient;
import com.orange.game.constants.DBConstants;
import com.orange.game.model.dao.PushMessage;

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
            //return new PushSinaWeiboMessage(pushMessage);
            break;
        }
        
        return null;
    }
}
