package com.orange.groupbuy.push.action;

import java.util.HashMap;

import com.orange.common.urbanairship.BasicService;
import com.orange.common.urbanairship.PushMessageService;
import com.orange.groupbuy.constant.PushNotificationConstants;
import com.orange.groupbuy.constant.ServiceConstant;
import com.orange.groupbuy.dao.PushMessage;

public class PushiPhoneMessage extends CommonAction {

    public PushiPhoneMessage(PushMessage message) {
        this.pushMessage = message;
    }

    @Override
    public int sendMessage() {

        int badge = 1;
        String sound = "default";
        String deviceToken = pushMessage.getDeviceToken();
        String alertMessage = pushMessage.getPushIphone();
        HashMap<String, Object> userInfo = new HashMap<String, Object>();

        userInfo.put(ServiceConstant.PARA_PRODUCT, pushMessage.getProductId());
        BasicService pushService = PushMessageService.createService(PushNotificationConstants.APPLICATION_KEY,
                                                                    PushNotificationConstants.APPLICATION_SECRET,
                                                                    PushNotificationConstants.APPLICATION_MASTER_SECRET,
                                                                    deviceToken, badge, alertMessage, sound, userInfo);
        return pushService.handleServiceRequest();
    }

}
