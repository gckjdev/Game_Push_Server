package com.orange.groupbuy.pushserver;

import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.orange.common.mongodb.MongoDBClient;
import com.orange.common.processor.BasicProcessorRequest;
import com.orange.common.processor.CommonProcessor;
import com.orange.common.urbanairship.BasicService;
import com.orange.common.urbanairship.ErrorCode;
import com.orange.common.urbanairship.PushMessageService;
import com.orange.groupbuy.constant.DBConstants;
import com.orange.groupbuy.constant.PushNotificationConstants;
import com.orange.groupbuy.constant.ServiceConstant;
import com.orange.groupbuy.dao.PushMessage;
import com.orange.groupbuy.manager.PushMessageManager;

/**
 * The Class PushMessageRequest.
 */
public class PushMessageRequest extends BasicProcessorRequest {

    public static final int MAX_PUSH_PER_SECOND = 3;
    public static final int SLEEP_INTERVAL = 1000;
    private static int pushCounter;
    private static long startCouterTime;

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

        MongoDBClient mongoClient = mainProcessor.getMongoDBClient();

    	startTime = new Date();

    	try {
    	    result = sendiPhonePushMessage(pushMessage);

            if (result != ErrorCode.ERROR_SUCCESS) {
                log.warn("Fail to push message, productId=" + pushMessage.getProductId() +
                        ", userId=" + pushMessage.getUserId() + ", deviceToken=" + pushMessage.getDeviceToken());
                setPushMessageStatisticData(pushMessage);
                PushMessageManager.pushMessageFailure(mongoClient, pushMessage);
                return;
            }
            else if (result == ErrorCode.ERROR_SUCCESS) {
                log.debug("Push message OK!, productId=" + pushMessage.getProductId() +
                        ", userId=" + pushMessage.getUserId() + ", deviceToken=" + pushMessage.getDeviceToken());
            }

            setPushMessageStatisticData(pushMessage);
            PushMessageManager.pushMessageClose(mongoClient, pushMessage);

//            flowControl();
    	}
    	catch (Exception e) {
            mainProcessor.severe(this, "push Message = " + pushMessage.toString() + ", but catch exception = " + e.toString());
            PushMessageManager.pushMessageFailure(mongoClient, pushMessage);
        }
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
                    log.info("PushServer sleep " + sleepTime + " ms for flow control");
                    Thread.sleep(sleepTime);
                }
                pushCounter = 0;
            }
        }
        catch (InterruptedException e) {
            log.fatal("<ScheduleServer> catch Exception while running. exception=" + e.toString());
        }
    }

    private int sendiPhonePushMessage(PushMessage message) {

        int badge = 1;
        String sound = "default";
        String deviceToken = message.getDeviceToken();
        String alertMessage = message.getPushIphone();
        HashMap<String, Object> userInfo = new HashMap<String, Object>();

        userInfo.put(ServiceConstant.PARA_PRODUCT, message.getProductId());
        BasicService pushService = PushMessageService.createService(PushNotificationConstants.APPLICATION_KEY,
                                                                    PushNotificationConstants.APPLICATION_SECRET,
                                                                    PushNotificationConstants.APPLICATION_MASTER_SECRET,
                                                                    deviceToken, badge, alertMessage, sound, userInfo);
        return pushService.handleServiceRequest();
    }

    private void setPushMessageStatisticData(final PushMessage message) {
        message.put(DBConstants.F_PUSH_MESSAGE_START_DATE, startTime);
        message.put(DBConstants.F_PUSH_MESSAGE_FINISH_DATE, new Date());
        message.put(DBConstants.F_PUSH_MESSAGE_ERROR_CODE, result);
    }

}
