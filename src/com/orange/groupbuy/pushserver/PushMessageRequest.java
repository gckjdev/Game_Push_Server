package com.orange.groupbuy.pushserver;

import java.util.Date;
import java.util.HashMap;

import com.orange.common.mongodb.MongoDBClient;
import com.orange.common.processor.BasicProcessorRequest;
import com.orange.common.processor.CommonProcessor;
import com.orange.common.urbanairship.BasicService;
import com.orange.common.urbanairship.ErrorCode;
import com.orange.common.urbanairship.PushMessageService;
import com.orange.groupbuy.constant.DBConstants;
import com.orange.groupbuy.dao.PushMessage;
import com.orange.groupbuy.manager.PushMessageManager;

/**
 * The Class PushMessageRequest.
 */
public class PushMessageRequest extends BasicProcessorRequest {
    /** The push message. */
    private PushMessage pushMessage;

    /** The start time. */
    private Date startTime;

    /** The result. */
    private int result;

    /**
     * Instantiates a new push message request.
     *
     * @param pushMessage the push message
     */
    public PushMessageRequest(final PushMessage pushMessage) {
        super();
        this.pushMessage = pushMessage;
    }

    /* (non-Javadoc)
     * @see com.orange.common.processor.BasicProcessorRequest@execute
     */
    @Override
	public void execute(CommonProcessor mainProcessor) {
        
        MongoDBClient mongoClient = mainProcessor.getMongoDBClient();

		startTime = new Date();

		try {
		    /*
		    // send push request to iPhone
	        result = sendiPhonePushMessage(pushMessage);

	        if (result != ErrorCode.ERROR_SUCCESS) {
	            // update pushMessage status to failure
	            mainProcessor.warning(this, "Fail to push message.");
	            setPushMessageStatisticData(pushMessage);
	            PushMessageManager.pushMessageFailure(mongoClient, pushMessage);
	            return;
	        }*/

	        // update status and result code
	        setPushMessageStatisticData(pushMessage);
	        PushMessageManager.pushMessageClose(mongoClient, pushMessage);
		}
		catch (Exception e) {
            mainProcessor.severe(this, "push Message = "+ pushMessage.toString() +", but catch exception = "+e.toString());
            e.printStackTrace();
        }
	}

    /**
     * Send iphone push message.
     *
     * @param pushMessage the push message
     * @return the int
     */
    private int sendiPhonePushMessage(PushMessage pushMessage) {

        int badge = 0;
        String sound = "default";
        String deviceToken = pushMessage.getDeviceToken();
        String alertMessage = pushMessage.getPushBody();
        HashMap<String, Object> userInfo = new HashMap<String, Object>();
        
        userInfo.put(DBConstants.F_PUSH_MESSAGE_USER_ID, pushMessage.getUserId());
        
        BasicService pushService = PushMessageService.createService(PushConstants.APPLICATION_KEY, 
                                                                    PushConstants.APPLICATION_SECRET, 
                                                                    PushConstants.APPLICATION_MASTER_SECRET,
                                                                    deviceToken, badge, alertMessage, sound, userInfo);
        int result = pushService.handleServiceRequest();

        return result;
    }

    /**
     * Sets the push message statistic data.
     *
     * @param pushMessage the new push message statistic data
     */
    private void setPushMessageStatisticData(final PushMessage pushMessage) {
        pushMessage.put(DBConstants.F_PUSH_MESSAGE_START_DATE, startTime);
        pushMessage.put(DBConstants.F_PUSH_MESSAGE_FINISH_DATE, new Date());
        pushMessage.put(DBConstants.F_PUSH_MESSAGE_ERROR_CODE, result);
    }

}
