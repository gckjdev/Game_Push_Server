package com.orange.groupbuy.push.action;

import com.orange.groupbuy.dao.PushMessage;

public class PushiPhoneMessage extends CommonAction {

    public PushiPhoneMessage(PushMessage message) {
        this.pushMessage = message;
    }

    @Override
    public int sendMessage() {
        return 0;
    }

}
