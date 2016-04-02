package com.i10n.fleet.util;

import java.util.Properties;

import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import org.apache.log4j.Logger;

public class PushletHandler {
	private static Logger LOG = Logger.getLogger(PushletHandler.class);
	private InitialContext liveDatactx =null;
	private Queue liveDataQueue=null;
	private QueueConnectionFactory connLiveDataFactory=null;
	private QueueConnection queueLiveDataConn =null;
	private QueueSession queueLiveDataSessionForReceiver =null;
	private QueueReceiver queueReceiver=null;

	private static PushletHandler _instance = null;

	public int pushletConsumerSize = 2;

	int pushletThreadId = 0;

	public PushletHandler() {

	}

	public static PushletHandler getInstance(){
		if(_instance == null){
			_instance = new PushletHandler();
			_instance.initializePushletHandler();
		}
		return _instance;
	}

	private void initializePushletHandler(){
		try {
			this.initialize();
			// set an asynchronous message listener
			for(int i=0;i<pushletConsumerSize;i++){
				PushletHelper helper = new PushletHelper(pushletThreadId++);
				queueReceiver.setMessageListener(helper);
				queueLiveDataConn.setExceptionListener(helper);	
			}

		} catch (Exception e) {
			LOG.error("Error while creating livedatapacket Queue ",e);
		}

	}

	private void initialize() throws Exception {
		Properties env = new Properties();
		env.put(Context.INITIAL_CONTEXT_FACTORY,"org.apache.activemq.jndi.ActiveMQInitialContextFactory");
		env.put(Context.PROVIDER_URL, "tcp://localhost:61616");
		env.put("queue.queueSampleLiveDataQueue", "liveDataPacketQueue");
		// get the initial context
		liveDatactx = new InitialContext(env);
		// lookup the queue object
		liveDataQueue = (Queue) liveDatactx.lookup("queueSampleLiveDataQueue");
		// lookup the queue connection factory
		connLiveDataFactory = (QueueConnectionFactory) liveDatactx.lookup("QueueConnectionFactory");
		// create a queue connection
		queueLiveDataConn = connLiveDataFactory.createQueueConnection();
		queueLiveDataSessionForReceiver =queueLiveDataConn.createQueueSession(false,Session.AUTO_ACKNOWLEDGE);
		// create a queue receiver
		queueReceiver = queueLiveDataSessionForReceiver.createReceiver(liveDataQueue);
		// start the connection     
		queueLiveDataConn.start();
	}
}
