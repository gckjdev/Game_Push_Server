package com.orange.game.pushserver;


import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

import org.bson.types.ObjectId;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.BasicDBObject;
import com.orange.common.mongodb.MongoDBClient;
import com.orange.common.utils.DateUtil;
import com.orange.game.constants.DBConstants;
import com.orange.game.model.dao.User;
import com.orange.game.model.manager.UserManager;

public class MongoDBData {

    MongoDBClient mongoClient;
	static String userId;
	static String deviceId;
//	static ObjectId id;
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
    public void addUserForRecommend() {
        for (int i = 0; i < 1; i++) {
            BasicDBObject obj = new BasicDBObject();
            ObjectId id = new ObjectId("4e5355560364950fcb95449a");
            obj.put(DBConstants.F_USERID, id);
            obj.put(DBConstants.F_PUSH_MESSAGE_TYPE, DBConstants.C_PUSH_TYPE_EMAIL);
            obj.put(DBConstants.F_APPID, "GROUPBUY");
            obj.put(DBConstants.F_DEVICEMODEL, "deviceModel");
            obj.put(DBConstants.F_DEVICEID, "deviceId");
            obj.put(DBConstants.F_DEVICEOS, "deviceOs");
            obj.put(DBConstants.F_DEVICETOKEN, "deviceToken");
            obj.put(DBConstants.F_LANGUAGE, "language");
            obj.put(DBConstants.F_COUNTRYCODE, "countryCode");
            obj.put(DBConstants.F_CREATE_DATE, new Date()); // DateUtil.currentDate());
            obj.put(DBConstants.F_CREATE_SOURCE_ID, "sourceId");
            obj.put(DBConstants.F_STATUS, DBConstants.STATUS_NORMAL);     
            mongoClient.insert(DBConstants.T_USER, obj);


            User user = UserManager.findUserByUserId(mongoClient, id.toString());
            user.setDeviceToken("a5bdd473afd091e9537aeef306a3a2992be9c11f4a198532c991be55d59eafe2");
            UserManager.save(mongoClient, user);

            TimeZone timeZone = TimeZone.getTimeZone("GMT+0800");
            Calendar day = Calendar.getInstance(timeZone);
            day.setTime(new Date());
            day.set(Calendar.DAY_OF_MONTH, 11);

            UserManager.addUserShoppingItem(mongoClient, "4e5355560364950fcb95449a", "item" + 0, "GROUPBUY", "美食", "湘菜", "",
                    "Peking", 2000000f, 200000f, day.getTime(), "7", "8", "9");
            UserManager.addUserShoppingItem(mongoClient, "4e5355560364950fcb95449a", "item" + 1, "GROUPBUY", "西餐", "法国菜", "",
                    "Guangzhou", 2000000f, 1000000f, day.getTime(), "7", "8", "9");

        }
    }

    @Test
    public void setdd () {

      System.out.println(new Date());
    }
	
	
	@Test
    public void resetRecommendStatus() {
            
	    BasicDBObject query = new BasicDBObject();
        BasicDBObject update = new BasicDBObject();
        
        BasicDBObject value = new BasicDBObject();
        value.put("$ne", DBConstants.C_RECOMMEND_STATUS_NOT_RUNNING);
        query.put(DBConstants.F_RECOMMEND_STATUS, value);

        BasicDBObject updateValue = new BasicDBObject();
        updateValue.put(DBConstants.F_RECOMMEND_STATUS, DBConstants.C_RECOMMEND_STATUS_NOT_RUNNING);
        update.put("$set", updateValue);
        
        mongoClient.updateAll(DBConstants.T_USER, query, update);
    }
	
	@Test
	public void getDateOfToday() {
	    Date date = DateUtil.getDateOfToday();
	    System.out.println(date);
	}
}
