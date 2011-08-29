package com.orange.groupbuy.pushserver;

import java.text.ParseException;
import java.util.Random;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.BasicDBObject;
import com.orange.common.mongodb.MongoDBClient;
import com.orange.common.utils.DateUtil;
import com.orange.groupbuy.constant.DBConstants;

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
	public void testLength(){
	    String str = "abc";
	    System.out.println("str=" + str + ", len=" + str.length());
        str = "你好";
        System.out.println("str=" + str + ", len=" + str.length());
	}
	
	
	@Test
	public void insertPushMessage() {
	    mongoClient = new MongoDBClient("localhost", "groupbuy", "", "");
	    for (int i = 0; i < 100; i++) {
	        BasicDBObject obj = new BasicDBObject();
	        obj.put(DBConstants.F_PUSH_MESSAGE_STATUS, DBConstants.C_PUSH_MESSAGE_STATUS_NOT_RUNNING);
	        obj.put(DBConstants.F_FOREIGN_USER_ID, Integer.toString(i));
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
	
	@Test
	public void testDate() throws ParseException{
	    boolean isMiddleAM = false;
        boolean isMiddlePM = false;

        if (DateUtil.isMiddleDate(PushConstants.START_DATE_HOUR_AM, PushConstants.START_DATE_MINUTE_AM, PushConstants.END_DATE_HOUR_AM, PushConstants.END_DATE_MINUTE_AM)){
            isMiddleAM =  true;
        }

        if (DateUtil.isMiddleDate(PushConstants.START_DATE_HOUR_PM, PushConstants.START_DATE_MINUTE_PM, PushConstants.END_DATE_HOUR_PM, PushConstants.END_DATE_MINUTE_PM)){
            isMiddlePM = true;
        }
        
        if(isMiddleAM || isMiddlePM) {
            System.out.println("true");
        } 
	}
}
