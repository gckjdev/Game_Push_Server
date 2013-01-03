package com.orange.game.pushserver;

import java.util.Timer;

import org.apache.commons.lang.NullArgumentException;

import com.orange.common.log.ServerLog;
import com.orange.common.mongodb.MongoDBClient;
import com.orange.common.processor.ScheduleServer;
import com.orange.game.constants.DBConstants;
import com.orange.game.push.service.PushMessageGenerator;


public class PushServer {

    private static final int MAX_THREAD_NUM = 5;
    private static final int MAX_PUSH_PER_SECOND = 20;
    private static final int PUSH_INTERVAL = 1000;
    private static final int GRANULARITY = 100;

    public static MongoDBClient mongoClient = new MongoDBClient(DBConstants.D_GAME);


    public static void main(final String[] args)  {
        
        String createMessage = System.getProperty("create_message");
        
        if ( createMessage != null && !createMessage.isEmpty()) {      
            createPushMessage();
        } else {
            runPushServer();    
           }
    }


    private static void runPushServer() {
        
        ServerLog.info(0, "Game Push Server starting..."); 
        ScheduleServer scheduleServer = new ScheduleServer(new PushRunnableProcessor(mongoClient));
        scheduleServer.setFrequency(MAX_PUSH_PER_SECOND);
        scheduleServer.setThreadNum(MAX_THREAD_NUM);
        scheduleServer.setInterval(PUSH_INTERVAL);

        Thread server = new Thread(scheduleServer);
        server.start();
        
        // set timer to reset user push counter
        Timer resetUserPushCounterTimer = new Timer();
        resetUserPushCounterTimer.schedule(new ResetUserPushCounterTimer(mongoClient), 
                ResetUserPushCounterTimer.getTaskDate());
    }


    private static void createPushMessage() {
        
        String message = System.getProperty("message");
        
        if ( message == null || message.isEmpty() ) {
            ServerLog.error(0, new NullArgumentException("<PushServer> message should not be empty"));
            return ;
           }
        PushMessageGenerator psg = new PushMessageGenerator(message);
        psg.creageMessage(GRANULARITY);
    }

}
