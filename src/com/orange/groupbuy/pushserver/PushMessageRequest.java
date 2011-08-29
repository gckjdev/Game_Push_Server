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

    private PushMessage pushMessage;
    private Date startTime;
    private int result;

    public static final Logger log = Logger.getLogger(PushMessageRequest.class.getName());

    public PushMessageRequest(final PushMessage pushMessage) {
        super();
        this.pushMessage = pushMessage;
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
		    
		    // send push request to iPhone
		    result = sendiPhonePushMessage(pushMessage);

	        if (result != ErrorCode.ERROR_SUCCESS) {
	            log.warn("Fail to push message, productId=" + pushMessage.getProductId() +
	                    ", userId=" + pushMessage.getUserId() + ", deviceToken=" + pushMessage.getDeviceToken());
	            setPushMessageStatisticData(pushMessage);
	            PushMessageManager.pushMessageFailure(mongoClient, pushMessage);
	            return;
	        }
	        else{
                log.debug("Push message OK!, productId=" + pushMessage.getProductId() +
                        ", userId=" + pushMessage.getUserId() + ", deviceToken=" + pushMessage.getDeviceToken());
	        }

	        // update status and result code
	        setPushMessageStatisticData(pushMessage);
	        PushMessageManager.pushMessageClose(mongoClient, pushMessage);
		}
		catch (Exception e) {
            mainProcessor.severe(this, "push Message = "+ pushMessage.toString() +", but catch exception = "+e.toString());
            PushMessageManager.pushMessageFailure(mongoClient, pushMessage);
        }
	}

    /**
     * Send iphone push message.
     *
     * @param pushMessage the push message
     * @return the int
     */
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

    /**
     * Sets the push message statistic data.
     *
     * @param pushMessage the new push message statistic data
     */
    private void setPushMessageStatisticData(final PushMessage message) {
        message.put(DBConstants.F_PUSH_MESSAGE_START_DATE, startTime);
        message.put(DBConstants.F_PUSH_MESSAGE_FINISH_DATE, new Date());
        message.put(DBConstants.F_PUSH_MESSAGE_ERROR_CODE, result);
    }

}
