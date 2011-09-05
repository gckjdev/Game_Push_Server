package com.orange.groupbuy.push.action;

import com.orange.common.mail.CommonMailSender;
import com.orange.groupbuy.dao.PushMessage;

public class PushEmailMessage extends CommonAction {



    public PushEmailMessage(PushMessage pushMessage) {
        this.pushMessage =pushMessage;
        // TODO Auto-generated constructor stub
    }

    @Override
    public int sendMessage() {
        String emailTitle = pushMessage.getPushSubject();
        String emailBody = pushMessage.getPushBody();
        CommonMailSender testmail = new CommonMailSender("ouyongyong@163.com",emailTitle,emailBody);
        testmail.send();
        return 0;
    }

    @Override
    public int validateMessage() {
        // TODO Auto-generated method stub
        return 0;
    }

}
