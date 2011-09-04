package com.orange.groupbuy.push.action;

import com.orange.groupbuy.constant.DBConstants;
import com.orange.groupbuy.dao.PushMessage;

public class ActionCreator {

    public static CommonAction getAction(PushMessage pushMessage) {
        
        int messageType = pushMessage.getPushType();
        switch (messageType){
        case DBConstants.C_PUSH_TYPE_IPHONE:
            return new PushiPhoneMessage(pushMessage);
        case DBConstants.C_PUSH_TYPE_ANDROID:
            break;
        case DBConstants.C_PUSH_TYPE_EMAIL:
            break;
        case DBConstants.C_PUSH_TYPE_WEIBO:
            break;
        }
        
        return null;
    }
}
