package com.orange.game.push.action;

import com.orange.common.mail.CommonMailSender;
import com.orange.game.model.dao.PushMessage;

public class PushEmailMessage extends CommonAction {



    public PushEmailMessage(PushMessage pushMessage) {
        this.pushMessage =pushMessage;
        // TODO Auto-generated constructor stub
    }

    @Override
    public int sendMessage() {
        String emailTitle = pushMessage.getPushSubject();
        String emailBody = pushMessage.getPushBody();
        String imageUrl = pushMessage.getImage();
        CommonMailSender testmail = new CommonMailSender("ouyongyong@163.com",emailTitle,emailBody,imageUrl);
        testmail.send();
        return 0;
    }

    @Override
    public int validateMessage() {
        // TODO Auto-generated method stub
        return 0;
    }

}
