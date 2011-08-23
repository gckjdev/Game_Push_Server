package com.orange.groupbuy.pushserver;

import java.util.concurrent.BlockingQueue;
import com.orange.common.mongodb.MongoDBClient;
import com.orange.common.processor.BasicProcessorRequest;
import com.orange.groupbuy.dao.PushMessage;
import com.orange.groupbuy.manager.PushMessageManager;

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

    /** The pushMessage counter. */
    private static int pushCounter = 0;

    /** The start time. */
    private static long startTime = 0;

    /**
     * Reset all running message.
     *
     * @param mongoClient the mongo client
     */
    public static void resetAllRunningMessage(final MongoDBClient mongoClient) {
        PushMessageManager.resetAllRunningMessage(mongoClient);
    }

    /**
     * The main method.
     *
     * @param args the arguments
     */
public static void main(final String[] args) {

		BlockingQueue<BasicProcessorRequest> queue = null;
		MongoDBClient mongoClient = null;

		for (int i = 0; i < MAX_THREAD_NUM; i++) {
			PushRunnableProcessor runnable = new PushRunnableProcessor();
			Thread thread = new Thread(runnable);
			thread.start();
			if (i == 0) {
			    queue = PushRunnableProcessor.getQueue();
				mongoClient = runnable.getMongoDBClient();
			}
		}

		if (queue == null) {
			// print log here
		    System.out.println("no queue available to use, application quit");
			return;
		}

		resetAllRunningMessage(mongoClient);

		while (true) {
			try {
				// get 1 record and put into queue
			    PushMessage pushMessage = PushMessageManager.findMessageForPush(mongoClient);

				// if there is no record, sleep one second
			    if (pushMessage == null) {
			        Thread.sleep(PUSH_INTERVAL);
			    } else {
			        PushMessageRequest request = new PushMessageRequest(pushMessage);
			        queue.put(request);
			    }

			    flowControl();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Flow control.
	 */
	private static void flowControl() {
	    try {
	            pushCounter++;

	            if (pushCounter == 1) {
	                startTime = System.currentTimeMillis();
	            }

	            if (pushCounter == MAX_PUSH_PER_SECOND) {
	                long duration = System.currentTimeMillis() - startTime;
	                if (duration < PUSH_INTERVAL) {
	                    Thread.sleep(PUSH_INTERVAL - duration);
	                }
	                pushCounter = 0;
	            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	}

}
