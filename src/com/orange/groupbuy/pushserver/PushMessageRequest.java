package com.orange.groupbuy.pushserver;

import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.orange.common.mongodb.MongoDBClient;
import com.orange.common.processor.BasicProcessorRequest;
import com.orange.common.processor.CommonProcessor;
import com.orange.common.urbanairship.BasicService;
import com.orange.common.urbanairship.PushMessageService;
import com.orange.groupbuy.constant.DBConstants;
import com.orange.groupbuy.constant.ErrorCode;
import com.orange.groupbuy.constant.PushNotificationConstants;
import com.orange.groupbuy.constant.ServiceConstant;
import com.orange.groupbuy.dao.PushMessage;
import com.orange.groupbuy.manager.PushMessageManager;
import com.orange.groupbuy.push.action.ActionCreator;
import com.orange.groupbuy.push.action.CommonAction;

/**
 * The Class PushMessageRequest.
 */
public class PushMessageRequest extends BasicProcessorRequest {

    public static final int MAX_PUSH_PER_SECOND = 3;
    public static final int SLEEP_INTERVAL = 1000;
    private static int pushCounter;
    private static long startCouterTime;

    private MongoDBClient mongoClient;
    private PushMessage pushMessage;
    private Date startTime;
    private int result;


    public static final Logger log = Logger.getLogger(PushMessageRequest.class.getName());

    public PushMessageRequest(final PushMessage message) {
        super();
        this.pushMessage = message;
    }

    @Override
    public String toString() {
        return "PushMessageRequest [pushMessage=" + pushMessage.toString() +
                "]";
    }



    /* (non-Javadoc)
     * @see com.orange.common.processor.BasicProcessorRequest@execute
     */
    @Override
    public void execute(CommonProcessor mainProcessor) {

        mongoClient = mainProcessor.getMongoDBClient();

    	startTime = new Date();

    	try {
    	    result = sendMessage();
            if (result != ErrorCode.ERROR_SUCCESS) {
                log.warn("Fail to push message, productId=" + pushMessage.getProductId() +
                        ", userId=" + pushMessage.getUserId() + ", deviceToken=" + pushMessage.getDeviceToken() 
                        + ", result=" + result);
                setPushMessageStatisticData(pushMessage);
                
                if (canRetry(result)){
                    PushMessageManager.pushMessageRetry(mongoClient, pushMessage, result);
                }
                else {
                    PushMessageManager.pushMessageClose(mongoClient, pushMessage, result);                    
                }
                return;
            }
            else if (result == ErrorCode.ERROR_SUCCESS) {
                log.info("Push message OK!, productId=" + pushMessage.getProductId() +
                        ", userId=" + pushMessage.getUserId() + ", deviceToken=" + pushMessage.getDeviceToken());

                setPushMessageStatisticData(pushMessage);
                PushMessageManager.pushMessageClose(mongoClient, pushMessage, result);
            }

            flowControl();
    	}
    	catch (Exception e) {
            log.error("process message = " + pushMessage.toString() + ", but catch exception = " + e.toString(), e);
            PushMessageManager.pushMessageRetry(mongoClient, pushMessage, ErrorCode.ERROR_GENERAL_EXCEPTION);
        }    	
    }

    private boolean canRetry(int result) {
        if (result == ErrorCode.ERROR_DEVICE_TOKEN_NULL)
            return false;
        else
            return true;
    }

    private int sendMessage() {
        CommonAction action = ActionCreator.getAction(pushMessage, mongoClient);
        int result = action.validateMessage();
        if (result != ErrorCode.ERROR_SUCCESS)
            return result;
        
        return action.sendMessage();
    }

    private void flowControl() {
        try {
            pushCounter++;

            if (pushCounter == 1) {
                startCouterTime = System.currentTimeMillis();
            }

            if (pushCounter == MAX_PUSH_PER_SECOND) {
                long duration = System.currentTimeMillis() - startCouterTime;
                if (duration < SLEEP_INTERVAL) {
                    long sleepTime = SLEEP_INTERVAL - duration;
                    log.info("PushServer sleep " + sleepTime + " ms for flow control, push count = " + pushCounter);
                    Thread.sleep(sleepTime);
                }
                pushCounter = 0;
            }
        }
        catch (InterruptedException e) {
            log.fatal("<ScheduleServer> catch Exception while running. exception=" + e.toString());
            e.printStackTrace();
        }
    }

    private void setPushMessageStatisticData(final PushMessage message) {
        message.put(DBConstants.F_PUSH_MESSAGE_START_DATE, startTime);
        message.put(DBConstants.F_PUSH_MESSAGE_FINISH_DATE, new Date());
    }

}
