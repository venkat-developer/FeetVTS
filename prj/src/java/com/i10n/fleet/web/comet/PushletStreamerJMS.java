package com.i10n.fleet.web.comet;

import org.apache.log4j.Logger;

import nl.justobjects.pushlet.core.Dispatcher;
import nl.justobjects.pushlet.core.Event;
import nl.justobjects.pushlet.core.EventSource;
/**
 * 
 * @author Thiru
 */
public class PushletStreamerJMS implements EventSource {

	private static Logger LOG = Logger.getLogger(PushletStreamerJMS.class);

	private static PushletStreamerJMS singletonInstance = null;

	private PushletStreamerJMS(){
		LOG.debug("new PushletStreamerJMS created...");
	}
	public static PushletStreamerJMS getInstance(){
		if(null == singletonInstance){
			singletonInstance = new PushletStreamerJMS();
		}
		return singletonInstance;
	}

	//Dispatch for every user login(here subject...)
	public void publishEvent(String[] subjects,String msgContent,String msgType){
		for(final String sub : subjects) {

			publishEvent(sub,msgContent,msgType);
		}
	}

	public void publishEvent(String subject,String msgContent,String msgType){
		Event event = Event.createDataEvent(subject);
		event.setField("msg", msgContent);
		event.setField("type", msgType);
		//Push into Pushlet Framework
		Dispatcher dispatcher = Dispatcher.getInstance();
		if (dispatcher == null) {
			LOG.debug("Fleetcheck : Unlikely event of Dispatcher being null");	    	
			return;
		}
		/*	Used for debugging pushlet update bug
		 * if (event == null) {
			LOG.debug("Fleetcheck : Unlikely event of event being null");	    	
			return;	    	
		}*/	    
		dispatcher.multicast(event);
	}

	//	@Override
	public void activate() {
	}

	//	@Override
	public void passivate() {
	}

	//	@Override
	public void stop() {
	}

}
