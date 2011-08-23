package com.orange.groupbuy.pushserver;

import com.orange.common.mongodb.MongoDBClient;
import com.orange.common.processor.CommonProcessor;
import com.orange.groupbuy.constant.DBConstants;

public class PushRunnableProcessor extends CommonProcessor {

	static final String MONGO_SERVER = "localhost";
	static final String MONGO_USER = "";
	static final String MONGO_PASSWORD = "";
	
	public static MongoDBClient mongoClient = new MongoDBClient(MONGO_SERVER, DBConstants.D_GROUPBUY, MONGO_USER, MONGO_PASSWORD);
	
	@Override
	public MongoDBClient getMongoDBClient() {
		return mongoClient;
	}

}
