var baselive;
function onData(event)
{
	var type = event.get('type');
	var msgContent = event.get('msg');
	baselive.processPushletData(msgContent,type);
};

(function(){
	var $B = YAHOO.Bubbling;
	var $L = YAHOO.lang;
	var $YU = YAHOO.util;
	var $E = YAHOO.util.Event;
	var $D = YAHOO.util.Dom;
	var $YW = YAHOO.widget;
	var $W = getPackageForName("com.i10n.fleet.widget.ui");
	var $V = getPackageForName("com.i10n.fleet.widget.view");

	/**
	 * 4 Subscribers 
	 * 1) Live track
	 * 2) Vehicle Status
	 * 3) SidepaneList
	 * 4) Live Alerts
	 * 5) Vehicle map report
	 */
	var SUBSCRIBE_TYPE_SIZE = 5;

	var subscribedTypes = new Array(SUBSCRIBE_TYPE_SIZE);
	var username = null;
	var callBack= null;
	var listen = false;

	/**
	 * Base Class for all the Views.Should contain functionalities common across
	 * views.
	 *
	 * @author sabarish
	 */
	$V.BaseView = function(){
		this._widgets = {};
		baselive=this;
		PL._addEvent(window, 'load', PL._init, false);
		for(var i=0 ; i< SUBSCRIBE_TYPE_SIZE ; i++){
			subscribedTypes[i] = new Array(2);
		}

//		YAHOO.lang.later(30*1000, this, 'timerStart',[{data:'bar', data2:'zeta'}],true);
	};

	/**changes for over all live streaming*/ 

	$V.BaseView.subscribeFn = function(type,callback) {
		baselive.username=YAHOO.util.Dom.getElementsByClassName("user-link")[0].innerHTML;

		if(!listen){
			p_join_listen(baselive.username,'stream');
			listen = true;
		}
		var subscribed = false;

		for(var i=0;i<SUBSCRIBE_TYPE_SIZE ;i++){
			if(subscribedTypes[i][0] == null){
				subscribedTypes [i][0] = type;
				subscribedTypes [i][1] = callback;
				break;
			}
		}

//		for(var i =0 ; i< SUBSCRIBE_TYPE_SIZE; i++){
//		console.debug('subscribedTypes :'+subscribedTypes[i][0]);
//		console.debug('subscribed callback :'+subscribedTypes[i][1]);
//		}
	};

	$L.augmentObject($V.BaseView.prototype,{
		processPushletData : function(msgContent,type){
			baselive.sendPushletData(msgContent,type);
		},

		sendPushletData : function(pushletData,type) {
			var sendingData = pushletData;
			for(var j=0 ; j<subscribedTypes.length ; j++){
				if(type == subscribedTypes[j][0]){
					callBack= subscribedTypes[j][1];
					callBack.success(sendingData);
				}else{
					//Type not subscribed
				}
			}
		},
		timerStart:function(data) {
			this.tCallback.scope = this;
			YAHOO.util.Connect.asyncRequest('GET',  "/fleet/view/controlpanel/?markup=DemoManage&debug=true&module=/blocks/json"+
					"&data=view&subpage=liveData&dataView=assignment", this.tCallback,null);
		},

		/**
		 *  "t" for timer, Callback method for timer base auto update
		 */
		tCallback:{
			success: function(o) {
				var type = 'statusUpdate';
				var msgContent = o;
				baselive.processPushletData(msgContent,type);
				type='livetrack';
				baselive.processPushletData(msgContent,type);
				type='vehiclemapreport';
				baselive.processPushletData(msgContent,type);

			},
			failure: function(o) {},
		}

	});

	var proto = $V.BaseView.prototype;
	proto.init = function(params){
	};

})();
