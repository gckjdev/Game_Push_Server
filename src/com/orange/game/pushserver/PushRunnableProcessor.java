package com.orange.game.pushserver;

import com.orange.common.mongodb.MongoDBClient;
import com.orange.common.processor.BasicProcessorRequest;
import com.orange.common.processor.ScheduleServerProcessor;
import com.orange.common.utils.DateUtil;
import com.orange.game.model.dao.PushMessage;
import com.orange.game.model.manager.PushMessageManager;

public class PushRunnableProcessor extends ScheduleServerProcessor {

    private MongoDBClient mongoClient;

    public PushRunnableProcessor(MongoDBClient mongoDBClient) {
        super();
        this.mongoClient = mongoDBClient;
    }

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
            log.debug("No message to push.");
            return null;
        }
        PushMessageRequest request = new PushMessageRequest(pushMessage);
        return request;
    }

    @Override
    public final boolean canProcessRequest() {

        boolean isMiddleAM = false;
        boolean isMiddlePM = false;

        if (DateUtil.isMiddleDate(PushConstants.START_DATE_HOUR_AM, PushConstants.START_DATE_MINUTE_AM, 
                PushConstants.END_DATE_HOUR_AM, PushConstants.END_DATE_MINUTE_AM, DateUtil.CHINA_TIMEZONE)) {
            isMiddleAM =  true;
        }

        if (DateUtil.isMiddleDate(PushConstants.START_DATE_HOUR_PM, PushConstants.START_DATE_MINUTE_PM, 
                PushConstants.END_DATE_HOUR_PM, PushConstants.END_DATE_MINUTE_PM, DateUtil.CHINA_TIMEZONE)) {
            isMiddlePM = true;
        }

        if (isMiddleAM || isMiddlePM) {
            return true;
        }
        else {
            log.debug("Current datetime is out of process time, can not process request.");
            return false;
        }
    }

}
