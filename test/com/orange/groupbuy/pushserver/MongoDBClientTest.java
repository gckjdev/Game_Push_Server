package com.orange.groupbuy.pushserver;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.orange.common.mongodb.MongoDBClient;
import com.orange.groupbuy.constant.DBConstants;
import com.orange.groupbuy.dao.Product;
import com.orange.groupbuy.manager.ProductManager;
import com.orange.groupbuy.manager.PushMessageManager;
import com.orange.groupbuy.manager.UserManager;

public class MongoDBClientTest {

    MongoDBClient mongoClient;
	static String userId;
	static String deviceId;
	private Random seed;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() {
		mongoClient = new MongoDBClient("localhost", "groupbuy", "", "");
		seed = new Random();
	}

	
	@Test
	public void insertPushMessage() {
	    mongoClient = new MongoDBClient("localhost", "groupbuy", "", "");
	    for (int i = 0; i < 100; i++) {
	        BasicDBObject obj = new BasicDBObject();
	        obj.put(DBConstants.F_PUSH_MESSAGE_STATUS, DBConstants.C_PUSH_MESSAGE_STATUS_NOT_RUNNING);
	        obj.put(DBConstants.F_PUSH_MESSAGE_USER_ID, Integer.toString(i));
	        mongoClient.insert(DBConstants.T_PUSH_MESSAGE, obj);
	    }
	}
	
	@Test
    public void updatetPushMessage() {
        mongoClient = new MongoDBClient("localhost", "groupbuy", "", "");
            
            BasicDBObject query = new BasicDBObject();
            query.put(DBConstants.F_PUSH_MESSAGE_STATUS, DBConstants.C_PUSH_MESSAGE_STATUS_CLOSE);

            BasicDBObject updateValue = new BasicDBObject();
            updateValue.put(DBConstants.F_PUSH_MESSAGE_STATUS, DBConstants.C_PUSH_MESSAGE_STATUS_NOT_RUNNING);
            BasicDBObject update = new BasicDBObject();
            update.put("$set", updateValue);

            mongoClient.updateAll(DBConstants.T_PUSH_MESSAGE, query, update);
    }
}
