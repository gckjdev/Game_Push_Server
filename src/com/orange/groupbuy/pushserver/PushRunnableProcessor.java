package com.orange.groupbuy.pushserver;

import com.orange.common.mongodb.MongoDBClient;
import com.orange.common.processor.BasicProcessorRequest;
import com.orange.common.processor.ScheduleServerProcessor;
import com.orange.common.utils.DateUtil;
import com.orange.groupbuy.constant.DBConstants;
import com.orange.groupbuy.dao.PushMessage;
import com.orange.groupbuy.manager.PushMessageManager;

public class PushRunnableProcessor extends ScheduleServerProcessor {

	public static MongoDBClient mongoClient = new MongoDBClient(DBConstants.D_GROUPBUY);

	@Override
    public final MongoDBClient getMongoDBClient() {
		return mongoClient;
	}

    @Override
    public final void resetAllRunningMessage() {
        PushMessageManager.resetAllRunningMessage(mongoClient);
    }

    @Override
    public final BasicProcessorRequest getProcessorRequest() {
        PushMessage pushMessage = PushMessageManager.findMessageForPush(mongoClient);
        if (pushMessage == null) {
            log.info("no message to push.");
            return null;
        }
        PushMessageRequest request = new PushMessageRequest(pushMessage);
        return request;
    }

    @Override
    public final boolean canProcessRequest() {

        boolean isMiddleAM = false;
        boolean isMiddlePM = false;

        if (DateUtil.isMiddleDate(PushConstants.START_DATE_HOUR_AM, PushConstants.START_DATE_MINUTE_AM, PushConstants.END_DATE_HOUR_AM, PushConstants.END_DATE_MINUTE_AM)){
            isMiddleAM =  true;
        }

        if (DateUtil.isMiddleDate(PushConstants.START_DATE_HOUR_PM, PushConstants.START_DATE_MINUTE_PM, PushConstants.END_DATE_HOUR_PM, PushConstants.END_DATE_MINUTE_PM)){
            isMiddlePM = true;
        }
        
        if(isMiddleAM || isMiddlePM) {
            return true;
        } 
        else {
            log.warn("current datetime is out of process time, can not process request.");
            return false;
        }
    }

}
