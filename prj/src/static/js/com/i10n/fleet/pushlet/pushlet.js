var PL = {
		NV_P_FORMAT: 'p_format=xml-strict',
		NV_P_MODE: 'p_mode=pull',
		pushletURL: null,
		webRoot: null,
		sessionId: null,
		STATE_ERROR: -2,
		STATE_ABORT: -1,
		STATE_NULL: 1,
		STATE_READY: 2,
		STATE_JOINED: 3,
		STATE_LISTENING: 4,
		state: 1,
		//applnName: 'fleet',

		/************** START PUBLIC FUNCTIONS  **************/

		/** Send heartbeat. */
		heartbeat: function() {
	//console.debug('20');
	PL._doRequest('hb');
},

/** Join. */
join: function() {
	//console.debug('at 26');
	PL.sessionId = null;

	// Streaming is only supported in Mozilla. E.g. IE does not allow access to responseText on readyState == 3
	PL._doRequest('join', PL.NV_P_FORMAT + '&' + PL.NV_P_MODE);
},

/** Join, listen and subscribe. */
joinListen: function(aSubject) {
	PL._setStatus('join-listen ' + aSubject);
	// PL.join();
	// PL.listen(aSubject);

	PL.sessionId = null;
	// Create event URI for listen
	var query = PL.NV_P_FORMAT + '&' + PL.NV_P_MODE;

	// Optional subject to subscribe to
	if (aSubject) {
		query = query + '&p_subject=' + aSubject;
	}

	PL._doRequest('join-listen', query);
	//console.debug('at 49 join-listen');

},

/** Close pushlet session. */
leave: function() {
	//console.debug('at 55 leave');
	PL._doRequest('leave');
},

/** Listen on event channel. */
listen: function(aSubject) {

	// Create event URI for listen
	var query = PL.NV_P_MODE;

	// Optional subject to subscribe to
	if (aSubject) {
		query = query + '&p_subject=' + aSubject;
	}
	//console.debug('at 69 listen');
	PL._doRequest('listen', query);
},

/** Publish to subject. */
publish: function(aSubject, theQueryArgs) {

	var query = 'p_subject=' + aSubject;
	if (theQueryArgs) {
		query = query + '&' + theQueryArgs;
	}
	//console.debug('at 80 listen');
	PL._doRequest('publish', query);
},

/** Subscribe to (comma separated) subject(s). */
subscribe: function(aSubject, aLabel) {

	var query = 'p_subject=' + aSubject;
	if (aLabel) {
		query = query + '&p_label=' + aLabel;
	}
	//console.debug('at 91 subscribe');
	PL._doRequest('subscribe', query);

},

/** Unsubscribe from (all) subject(s). */
unsubscribe: function(aSubscriptionId) {
	var query;

	// If no sid we unsubscribe from all subscriptions
	if (aSubscriptionId) {
		query = 'p_sid=' + aSubscriptionId;
	}
	//console.debug('at 104 unsubscribe');
	PL._doRequest('unsubscribe', query);
},

setDebug: function(bool) {
	PL.debugOn = bool;
},


/************** END PUBLIC FUNCTIONS  **************/

// Cross-browser add event listener to element
_addEvent: function (elm, evType, callback, useCapture) {
	var obj = PL._getObject(elm);
	if (obj.addEventListener) {
		obj.addEventListener(evType, callback, useCapture);
		return true;
	} else if (obj.attachEvent) {
		var r = obj.attachEvent('on' + evType, callback);
		return r;
	} else {
		obj['on' + evType] = callback;
	}
},

_doCallback: function(event, cbFunction) {
	// Do specific callback function if provided by client
	if (cbFunction) {
		// Do specific callback like onData(), onJoinAck() etc.
		cbFunction(event);
	} else if (window.onEvent) {
		// general callback onEvent() provided to catch all events
		onEvent(event);
	}
},

// Do XML HTTP request
_doRequest: function(anEvent, aQuery) {
	//console.debug('an event is '+anEvent);
	//console.debug('PL.state is '+PL.state);
	//console.debug(' listining is '+PL.STATE_LISTENING);
	// Check if we are not in any error state
	//console.log('state : '+PL.state);
	if (PL.state < 0) {
		PL._setStatus('died (' + PL.state + ')');
		return;
	}

	// We may have (async) requests outstanding and thus
	// may have to wait for them to complete and change state.
	var waitForState = false;
	if (anEvent == 'join' || anEvent == 'join-listen') {
		// We can only join after initialization
		//console.debug((PL.state < PL.STATE_READY));
		waitForState = (PL.state < PL.STATE_READY);
	} else if (anEvent == 'leave') {
		PL.state = PL.STATE_READY;
	} else if (anEvent == 'refresh') {
		// We must be in the listening state
		if (PL.state != PL.STATE_LISTENING) {
			console.log('state not in listen : '+PL.STATE_LISTENING);
			return;
		}
	} else if (anEvent == 'listen') {
		// We must have joined before we can listen
		waitForState = (PL.state < PL.STATE_JOINED);
	} else if (anEvent == 'subscribe' || anEvent == 'unsubscribe') {
		// We must be listeing for subscription mgmnt
		waitForState = (PL.state < PL.STATE_LISTENING);
	} else {
		// All other requests require that we have at least joined
		waitForState = (PL.state < PL.STATE_JOINED);
	}

	// May have to wait for right state to issue request
	if (waitForState == true) {
		PL._setStatus(anEvent + ' , waiting... state=' + PL.state);
		//console.debug('at 181 anEvent');
		setTimeout(function() {
			PL._doRequest(anEvent, aQuery);
		}, 100);
		return;
	}

	// ASSERTION: PL.state is OK for this request

	// Construct base URL for GET
	var url = PL.pushletURL + '?p_event=' + anEvent;

	// Optionally attach query string
	if (aQuery) {
		//console.debug(' aQuery is '+aQuery);
		url = url + '&' + aQuery;
		//console.debug('URL is '+url);
	}

	// Optionally attach session id
	if (PL.sessionId != null) {
		//console.debug('PL.sessionId is '+PL.sessionId);
		url = url + '&p_id=' + PL.sessionId;
		if (anEvent == 'p_leave') {
			PL.sessionId = null;
		}
	}
	url = url +'&time='+new Date().getTime();
	//console.debug('at 207 _doRequest');
	PL.debug('_doRequest', url);
	PL._getXML(url, PL._onResponse);

	// uncomment to use synchronous XmlHttpRequest
	//var rsp = PL._getXML(url);
	//PL._onResponse(rsp);  */
},

// Get object reference
_getObject: function(obj) {
	if (typeof obj == "string") {
		return document.getElementById(obj);
	} else {
		// pass through object reference
		return obj;
	}
},


_getWebRoot: function() {
	/** Return directory of this relative to document URL.*/ 
	if (PL.webRoot != null) {
		return PL.webRoot;
	}
	//derive the baseDir value by looking for the script tag that loaded this file
	var head = document.getElementsByTagName('head')[0];
	var nodes = head.childNodes;
	//http://192.168.1.109/fleet/struts/dojo/dojo/dojo.js
	for (var i = 0; i < nodes.length; ++i) {
		var src = nodes.item(i).src;
		//alert("src : "+src);
		if (src) {
			var index = src.indexOf("fleet");
			if (index >= 0) {
				//alert("ind => "+index);
				PL.webRoot = src.substring(0, index);
				break;
			}
		}
	}
	//alert('webroot '+PL.webRoot);
	return PL.webRoot;
},

// Get XML doc from server
// On response  optional callback fun is called with optional user data.
_getXML: function(url, callback) {

	// Obtain XMLHttpRequest object
	var xmlhttp = new XMLHttpRequest();
	if (!xmlhttp || xmlhttp == null) {
		alert('No browser XMLHttpRequest (AJAX) support');
		return;
	}

	// Setup optional async response handling via callback
	var cb = callback;
	var async = false;

	if (cb) {
		// Async mode
		async = true;
		xmlhttp.onreadystatechange = function() {
			if (xmlhttp.readyState == 4) {
				if (xmlhttp.status == 200) {
					// Processing statements go here...
					cb(xmlhttp.responseXML);

					// Avoid memory leaks in IE
					// 12.may.2007 thanks to Julio Santa Cruz
					xmlhttp = null;
				} else {
					var event = new PushletEvent();
					event.put('p_event', 'error')
					event.put('p_reason', '[pushlet] problem retrieving XML data:\n' + xmlhttp.statusText);
					PL._onEvent(event);
				}
			}
		};
	}
	// Open URL
	xmlhttp.open('GET', url, async);

	// Send XML to KW server
	xmlhttp.send(null);

	if (!cb) {
		if (xmlhttp.status != 200) {
			var event = new PushletEvent();
			event.put('p_event', 'error')
			event.put('p_reason', '[pushlet] problem retrieving XML data:\n' + xmlhttp.statusText);
			PL._onEvent(event)
			return null;
		}
		// Sync mode (no callback)
		// alert(xmlhttp.responseText);

		return xmlhttp.responseXML;
	}
},


_init: function () {
	PL._showStatus();
	PL._setStatus('initializing...');
	/*
			Setup Cross-Browser XMLHttpRequest v1.2
		   Emulate Gecko 'XMLHttpRequest()' functionality in IE and Opera. Opera requires
		   the Sun Java Runtime Environment <http://www.java.com/>.

		   by Andrew Gregory
		   http://www.scss.com.au/family/andrew/webdesign/xmlhttprequest/

		   This work is licensed under the Creative Commons Attribution License. To view a
		   copy of this license, visit http://creativecommons.org/licenses/by-sa/2.5/ or
		   send a letter to Creative Commons, 559 Nathan Abbott Way, Stanford, California
		   94305, USA.

	 */
	// IE support
	////console.debug('at 1 window.ActiveXObject is '+window.ActiveXObject+' !window.XMLHttpRequest is '+!window.XMLHttpRequest);
	if (window.ActiveXObject && !window.XMLHttpRequest) {
		////console.debug('success');
		window.XMLHttpRequest = function() {
			var msxmls = new Array(
					'Msxml2.XMLHTTP.5.0',
					'Msxml2.XMLHTTP.4.0',
					'Msxml2.XMLHTTP.3.0',
					'Msxml2.XMLHTTP',
					'Microsoft.XMLHTTP');
			for (var i = 0; i < msxmls.length; i++) {
				try {
					////console.debug('327');
					return new ActiveXObject(msxmls[i]);
				} catch (e) {
				}
			}
			return null;
		};
	}else{
		////console.debug('Some thing window.ActiveXObject && !window.XMLHttpRequest');
	}
	////console.debug('at 2 window.ActiveXObject is '+window.ActiveXObject+' !window.XMLHttpRequest is '+!window.XMLHttpRequest);
	// ActiveXObject emulation
	if (!window.ActiveXObject && window.XMLHttpRequest) {
		////console.debug('at 338 type is ');
		window.ActiveXObject = function(type) {
			////console.debug('at 340 ');	
			switch (type.toLowerCase()) {
			case 'microsoft.xmlhttp':
			case 'msxml2.xmlhttp':
			case 'msxml2.xmlhttp.3.0':
			case 'msxml2.xmlhttp.4.0':
			case 'msxml2.xmlhttp.5.0':
				return new XMLHttpRequest();
			}
			return null;
		};
	}
	//		PL.pushletURL = PL._getWebRoot() +'fleet/pushlet.srv';
	//		PL.pushletURL =  'http://localhost:8080/fleet//pushlet.srv';
	//		PL.pushletURL =  'http://http://ec2-35-161-33-229.us-west-2.compute.amazonaws.com:8080/fleet//pushlet.srv';
			PL._setStatus('initialized');
			PL.state = PL.STATE_READY;
},
	
/** Handle incoming events from server. */
_onEvent: function (event) {
	//console.debug('On event '+event);
	// Create a PushletEvent object from the arguments passed in
	// push.arguments is event data coming from the Server
	//console.log('_onEvent()', event.toString());
	PL.debug('_onEvent()', event.toString());

	// Do action based on event type
	var eventType = event.getEvent();

	if (eventType == 'data') {
		PL._setStatus('data');
		PL._doCallback(event, window.onData);
		return;
	} else if (eventType == 'refresh') {
		if (PL.state < PL.STATE_LISTENING) {
			//console.log('not refreshing state=' + PL.STATE_LISTENING);
			PL._setStatus('not refreshing state=' + PL.STATE_LISTENING);
		}
		var timeout = event.get('p_wait');
		//console.debug('at 406 refresh ');
		setTimeout(function () {
			PL._doRequest('refresh');
		}, timeout);
		//console.log('refreshing again....  state=' + PL.STATE_LISTENING);
		return;
	} else if (eventType == 'error') {
		PL.state = PL.STATE_ERROR;
		PL._setStatus('server error: ' + event.get('p_reason'));
		PL._doCallback(event, window.onError);
	} else if (eventType == 'join-ack') {
		PL.state = PL.STATE_JOINED;
		PL.sessionId = event.get('p_id');
		PL._setStatus('connected');
		PL._doCallback(event, window.onJoinAck);
	} else if (eventType == 'join-listen-ack') {
		PL.state = PL.STATE_LISTENING;
		PL.sessionId = event.get('p_id');
		PL._setStatus('join-listen-ack');
		PL._doCallback(event, window.onJoinListenAck);
	} else if (eventType == 'listen-ack') {
		PL.state = PL.STATE_LISTENING;
		PL._setStatus('listening');
		PL._doCallback(event, window.onListenAck);
	} else if (eventType == 'hb') {
		PL._setStatus('heartbeat');
		PL._doCallback(event, window.onHeartbeat);
	} else if (eventType == 'hb-ack') {
		PL._doCallback(event, window.onHeartbeatAck);
	} else if (eventType == 'leave-ack') {
		PL._setStatus('disconnected');
		PL._doCallback(event, window.onLeaveAck);
	} else if (eventType == 'refresh-ack') {
		PL._doCallback(event, window.onRefreshAck);
	} else if (eventType == 'subscribe-ack') {
		PL._setStatus('subscribed to ' + event.get('p_subject'));
		PL._doCallback(event, window.onSubscribeAck);
	} else if (eventType == 'unsubscribe-ack') {
		PL._setStatus('unsubscribed');
		PL._doCallback(event, window.onUnsubscribeAck);
	} else if (eventType == 'abort') {
		PL.state = PL.STATE_ERROR;
		PL._setStatus('abort');
		PL._doCallback(event, window.onAbort);
	} else if (eventType.match(/nack$/)) {
		PL._setStatus('error response: ' + event.get('p_reason'));
		PL._doCallback(event, window.onNack);
	}
},

/**  Handle XMLHttpRequest response XML. */
_onResponse: function(xml) {
	//console.debug('_onResponse');
	PL.debug('_onResponse', xml);
	var events = PL._rsp2Events(xml);
	if (events == null) {
		PL._setStatus('null events')
		return;
	}

	delete xml;

	PL.debug('_onResponse eventCnt=', events.length);
	// Go through all <event/> elements
	//console.debug('events.length is '+events.length);
	for (i = 0; i < events.length; i++) {
		PL._onEvent(events[i]);
	}
},

/** Convert XML response to PushletEvent objects. */
_rsp2Events: function(xml) {
	// check empty response or xml document
	if (!xml || !xml.documentElement) {
		return null;
	}

	// Convert xml doc to array of PushletEvent objects
	var eventElements = xml.documentElement.getElementsByTagName('event');
	var events = new Array(eventElements.length);
	for (i = 0; i < eventElements.length; i++) {
		events[i] = new PushletEvent(eventElements[i]);
	}

	return events;

},

statusMsg: 'null',
statusChanged: false,
statusChar: '|',


_showStatus: function() {
	// To show progress
	if (PL.statusChanged == true) {
		if (PL.statusChar == '|') PL.statusChar = '/';
		else if (PL.statusChar == '/') PL.statusChar = '--';
		else if (PL.statusChar == '--') PL.statusChar = '\\';
		else PL.statusChar = '|';
		PL.statusChanged = false;
	}
	//console.debug('PL.statusMsg in _showStatus is '+PL.statusMsg)
	window.defaultStatus = PL.statusMsg;
	window.status = PL.statusMsg + '  ' + PL.statusChar;
	timeout = window.setTimeout('PL._showStatus()', 400);
},

_setStatus: function(status) {
	//console.debug('_setStatus');
	PL.statusMsg = "pushlet - " + status;
	PL.statusChanged = true;
},



/*************** Debug utility *******************************/
timestamp: 0,
debugWindow: null,
messages: new Array(),
messagesIndex: 0,
debugOn: false,

/** Send debug messages to a (D)HTML window. */
debug: function(label, value) {
	if (PL.debugOn == false) {
		return;
	}
	var funcName = "none";

	// Fetch JS function name if any
	if (PL.debug.caller) {
		funcName = PL.debug.caller.toString()
				funcName = funcName.substring(9, funcName.indexOf(")") + 1)
	}

	// Create message
	var msg = "-" + funcName + ": " + label + "=" + value

			// Add optional timestamp
			var now = new Date()
	var elapsed = now - PL.timestamp
	if (elapsed < 10000) {
		msg += " (" + elapsed + " msec)"
	}

	PL.timestamp = now;

	// Show.

	if ((PL.debugWindow == null) || PL.debugWindow.closed) {
		PL.debugWindow = window.open("", "p_debugWin", "toolbar=no,scrollbars=yes,resizable=yes,width=600,height=400");
	}

	// Add message to current list
	PL.messages[PL.messagesIndex++] = msg

			// Write doc header
			PL.debugWindow.document.writeln('<html><head><title>Pushlet Debug Window</title></head><body bgcolor=#DDDDDD>');

	// Write the messages
	for (var i = 0; i < PL.messagesIndex; i++) {
		PL.debugWindow.document.writeln('<pre>' + i + ': ' + PL.messages[i] + '</pre>');
	}

	// Write doc footer and close
	PL.debugWindow.document.writeln('</body></html>');
	PL.debugWindow.document.close();
	PL.debugWindow.focus();

}


}


/* Represents nl.justobjects.pushlet.Event in JS. */
function PushletEvent(xml) {
	//console.debug('at 569');
	// Member variable setup; the assoc array stores the N/V pairs
	this.arr = new Array();

	this.getSubject = function() {
		return this.get('p_subject');
	}

	this.getEvent = function() {
		return this.get('p_event');
	}

	this.put = function(name, value) {
		return this.arr[name] = value;
	}

	this.get = function(name) {
		return this.arr[name];
	}

	this.toString = function() {
		var res = '';
		for (var i in this.arr) {
			res = res + i + '=' + this.arr[i] + '\n';
		}
		return res;
	}

	this.toTable = function() {
		var res = '<table border="1" cellpadding="3">';
		var styleDiv = '<div style="color:black; font-family:monospace; font-size:10pt; white-space:pre;">'

				for (var i in this.arr) {
					res = res + '<tr><td bgColor=white>' + styleDiv + i + '</div></td><td bgColor=white>' + styleDiv + this.arr[i] + '</div></td></tr>';
				}
		res += '</table>'
				return res;
	}

	// Optional XML element <event name="value" ... />
	if (xml) {
		// Put the attributes in Map
		for (var i = 0; i < xml.attributes.length; i++) {
			this.put(xml.attributes[i].name, xml.attributes[i].value);
		}
	}
}

/**********************************************************************
 START - OLD application functions (LEFT HERE FOR FORWARD COMPAT)
 ***********************************************************************/
// Debug util
function p_debug(aBool, aLabel, aMsg) {
	if (aBool == false) {
		return;
	}

	PL.setDebug(true);
	PL.debug(aLabel, aMsg);
	PL.setDebug(false);
}

// Embed pushlet frame in page (OBSOLETE)
function p_embed(thePushletWebRoot) {
	alert('Pushlet: p_embed() is no longer required for AJAX client')
}

// Join the pushlet server
function p_join() {
	PL.join();
}

function p_setCallback() {
	PL._doCallback();
}


// Create data event channel with the server
function p_listen(aSubject, aMode) {
	// Note: mode is fixed to 'pull'
	PL.listen(aSubject);
}

// Shorthand: Join the pushlet server and start listening immediately
function p_join_listen(aSubject) {
	PL.joinListen(aSubject);
}

// Leave the pushlet server
function p_leave() {
	PL.leave();
}

// Send heartbeat event; callback is onHeartbeatAck()
function p_heartbeat() {
	PL.heartbeat();
}

// Publish to a subject
function p_publish(aSubject, nvPairs) {
	var args = p_publish.arguments;

	// Put the arguments' name/value pairs in the URI
	var query = '';
	var amp = '';
	for (var i = 1; i < args.length; i++) {
		if (i > 1) {
			amp = '&';
		}
		query = query + amp + args[i] + '=' + args[++i];
	}
	PL.publish(aSubject, query);
}

// Subscribe to a subject with optional label
function p_subscribe(aSubject, aLabel) {
	PL.subscribe(aSubject, aLabel);
}

// Unsubscribe from a subject
function p_unsubscribe(aSid) {
	PL.unsubscribe(aSid);
}

/**********************************************************************
 END - Public application functions (LEFT HERE FOR FORWARD COMPAT)
 ***********************************************************************/



