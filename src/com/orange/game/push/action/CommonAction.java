package com.orange.game.push.action;

import com.orange.game.model.dao.PushMessage;


public abstract class CommonAction {

    PushMessage pushMessage;
    
    abstract public int validateMessage();
    abstract public int sendMessage(); 
}
