package com.orange.groupbuy.pushserver;

import com.orange.common.processor.ScheduleServer;

/**
 * The Class PushServer.
 */
public class PushServer {

    /** The Constant MAX_THREAD_NUM. */
    static final int MAX_THREAD_NUM = 5;

    /** The Constant MAX_PUSH_PER_SECOND. */
    static final int MAX_PUSH_PER_SECOND = 20;

    /** The Constant PUSH_INTERVAL. */
    static final int PUSH_INTERVAL = 1000;

    /**
     * The main method.
     *
     * @param args the arguments
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static void main(final String[] args) {

//        ScheduleServer scheduleServer = new ScheduleServer(PushRunnableProcessor.class);
        
        // TODO , print version string

        ScheduleServer scheduleServer = new ScheduleServer(new PushRunnableProcessor());

        scheduleServer.setMax_request_per_second(MAX_PUSH_PER_SECOND);
        scheduleServer.setMax_thread_num(MAX_THREAD_NUM);
        scheduleServer.setSleep_interval_for_no_request(PUSH_INTERVAL); // TODO change back

        Thread server = new Thread(scheduleServer);
        server.start();
    }

}
