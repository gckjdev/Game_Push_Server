package com.orange.groupbuy.pushserver;

import java.util.concurrent.BlockingQueue;

import com.orange.common.processor.BasicProcessorRequest;

public class PushServer {

	static final int MAX_THREAD_NUM = 5;
	static final int MAX_PUSH_PER_SECOND = 30;

	
	public static void resetAllRunningMessage(){
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		

		BlockingQueue<BasicProcessorRequest> queue = null;
		for (int i=0; i<MAX_THREAD_NUM; i++){
			PushRunnableProcessor runnable = new PushRunnableProcessor();
			Thread thread = new Thread(runnable);
			thread.start();
			if (i == 0){
				queue = PushRunnableProcessor.getQueue(); 
			}
		}
		
		if (queue == null){
			// print log here
			System.out.println("no queue available to use, application quit");
			return;
		}
		
		resetAllRunningMessage();
		
		while (true){
			try{
				
				// get one record and put into queue
				
				// if there is no record, sleep one second
				
				// TODO flow control later
				
			}catch (Exception e){
				
			}
		}
	}

}
