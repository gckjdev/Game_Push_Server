package com.orange.groupbuy.push.action;

import com.orange.groupbuy.dao.PushMessage;

public abstract class CommonAction {

    PushMessage pushMessage;
    
    abstract public int sendMessage(); 
}
