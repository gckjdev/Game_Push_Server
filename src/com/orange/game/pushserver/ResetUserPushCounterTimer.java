package com.orange.game.pushserver;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import com.orange.common.log.ServerLog;
import com.orange.common.mongodb.MongoDBClient;
import com.orange.common.utils.DateUtil;
import com.orange.game.model.manager.UserManager;

public class ResetUserPushCounterTimer extends TimerTask {

    private final MongoDBClient mongoClient;
    
    public ResetUserPushCounterTimer(MongoDBClient mongoClient) {
        super();
        this.mongoClient = mongoClient;
    }

    @Override
    public void run() {
        
        // reset all push counter of user to 0 and push date to null
        ServerLog.info(0, "<ResetUserPushCounterTimer> Timer fired, reset push counter to 0, and reset push date to null");
        UserManager.resetPushCounter(mongoClient);
        
        // set next timer
        Timer newResetTaskTimer = new Timer();
        newResetTaskTimer.schedule(new ResetUserPushCounterTimer(mongoClient), 
                ResetUserPushCounterTimer.getTaskDate());
    }
    
   public static Date getTaskDate(){
        
        int scheduleHour = 0;       // 0 AM of the day
        
        TimeZone timeZone = TimeZone.getTimeZone(DateUtil.CHINA_TIMEZONE);
        Calendar now = Calendar.getInstance(timeZone);
        now.setTime(new Date());
        
        if (now.get(Calendar.HOUR_OF_DAY) >= scheduleHour){
            now.add(Calendar.DAY_OF_MONTH, 1);
          }
        
        now.set(Calendar.HOUR_OF_DAY, scheduleHour);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);               
        
        ServerLog.info(0, "<ResetUserPushCounterTimer> next timer set to "+now.getTime().toString());        
        return now.getTime();
    }
    
}

