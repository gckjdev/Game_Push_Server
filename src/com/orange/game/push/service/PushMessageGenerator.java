package com.orange.game.push.service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.orange.common.db.MongoDBExecutor;
import com.orange.common.log.ServerLog;
import com.orange.common.mongodb.MongoDBClient;
import com.orange.game.constants.DBConstants;
import com.orange.game.model.manager.PushMessageManager;
import com.orange.game.model.manager.UserManager;
import com.orange.game.traffic.service.GameDBService;

public class PushMessageGenerator {

     private final ExecutorService executor;
     private final String message;
    
     private final static GameDBService dbService = GameDBService.getInstance();
     private final static MongoDBClient dbClient = dbService.getMongoDBClient(0);
    
     private final static DBCursor userTableCursor = UserManager.iterator(dbClient);
     private static int success = 0;
     
     public PushMessageGenerator(String message) {
         this.executor = Executors.newSingleThreadExecutor();
         this.message = message;
       }
     

     private Runnable insertPushMessage(final int offset, final int size) {
         
         return new Runnable() {
            
            @Override
            public void run() {
                List<DBObject> userList = UserManager.iterator(dbClient).skip(offset).limit(size).toArray();
                if ( userList != null ) {
                   ServerLog.info(0, "Start to insert pushing message for DBobject from "+offset +" to "+(offset+size-1));
                   for ( int i = 0; i < userList.size(); i++) {
                       DBObject dbObject = userList.get(i);
                       String userId = dbObject.get(DBConstants.F_USERID).toString();
                       String deviceToken = dbObject.get(DBConstants.F_DEVICETOKEN).toString();
                       String appId = dbObject.get(DBConstants.F_APPID).toString();
                       PushMessageManager.insertIphonePushMessage(dbService.getMongoDBClient(MongoDBExecutor.EXECUTOR_POOL_NUM),
                                   userId, deviceToken, message, appId, new Date());
                      success++;
                         }
                    }
                   
              }  
         };
       }
     
     public void doCreateMessage(int granularity) throws Throwable {
        
         final int count = userTableCursor.count();
         if ( granularity < 0 ) {
             ServerLog.info(0, "<PushMessageGenerator> An invalid granularity supplied, bailing out !");
             return;
            }
         
         if ( granularity > count ) {
             granularity = 100;
            }
         
         int offset = 0;
         while( offset <= count) {
              Future<?> future = executor.submit(insertPushMessage(offset, granularity));
            try {
                 future.get(); 
             } catch(InterruptedException e) {
                 Thread.currentThread().interrupt();
                 future.cancel(true);
                 break;
             } catch(ExecutionException e) {
                 throw e.getCause();
                 } 
             offset += granularity;
             }
         }
     
     public boolean creageMessage(int granularity) {
         
           Boolean isDone = true;
           long start = 0;
           long end = 0;
           try {
               start = System.currentTimeMillis();
               doCreateMessage(granularity);
               end = System.currentTimeMillis();
           } catch (Throwable e) {
               e.printStackTrace();
           } finally {
               ServerLog.info(0, "<PushMessageGenerator> Total time: " + (end - start) / 1000 / 60  +" minutes "+
                   (end - start) / 1000 % 60 +" seconds . Success : " + success);
               if ( success != userTableCursor.count()) {
                   ServerLog.info(0, "<PushMessageGenerator> Fail during creating messages.");
                   isDone = false;
                    }
               }
           
           return isDone;
     }
}
