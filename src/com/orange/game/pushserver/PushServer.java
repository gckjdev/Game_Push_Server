package com.orange.game.pushserver;

import java.util.Timer;

import org.apache.log4j.Logger;

import com.orange.common.mongodb.MongoDBClient;
import com.orange.common.processor.ScheduleServer;
import com.orange.game.constants.DBConstants;

/**
 * The Class PushServer.
 */
public class PushServer {

    static final int MAX_THREAD_NUM = 5;

    static final int MAX_PUSH_PER_SECOND = 20;

    static final int PUSH_INTERVAL = 1000;

    public static final String VERSION_STRING = "0.9 Beta Build 20110829-01";

    public static final Logger log = Logger.getLogger(PushServer.class.getName());

    public static MongoDBClient mongoClient = new MongoDBClient(DBConstants.D_GAME);


    public static void main(final String[] args) throws ClassNotFoundException {
        
        log.info("Game Push Server start... version " + VERSION_STRING);

        ScheduleServer scheduleServer = new ScheduleServer(new PushRunnableProcessor(mongoClient));
        scheduleServer.setFrequency(MAX_PUSH_PER_SECOND);
        scheduleServer.setThreadNum(MAX_THREAD_NUM);
        scheduleServer.setInterval(PUSH_INTERVAL);

        Thread server = new Thread(scheduleServer);
        server.start();
        
        // schedule reset user push counter timer
        Timer resetUserPushCounterTimer = new Timer();
        resetUserPushCounterTimer.schedule(new ResetUserPushCounterTimer(mongoClient), 
                ResetUserPushCounterTimer.getTaskDate());    
    }

}
