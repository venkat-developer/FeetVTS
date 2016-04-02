( function() {
	var $W = getPackageForName("com.i10n.fleet.widget.ui");
	var $U = getPackageForName("com.i10n.fleet.Utils");
	var $D = YAHOO.util.Dom;
	var $B = YAHOO.Bubbling;
	var $L = YAHOO.lang;
	var $YU = YAHOO.util;
	var $E = YAHOO.util.Event;
	var $YW = YAHOO.widget;

	/**
	 * Support Desk Widget for Fleetcheck project
	 * 
	 * @author Madhu Shree M
	 */
	$W.SupportDesk = function(el, params) {

		this.init(el,params);
		/*this.init = function(el, params) {
            this.elBase=el;
            this.eventManager = new $U.DOMEventHandler(this.elBase, {
                type: "click"
            });
            this.addListener();
        };*/
		//this.baseElement = el;
		//this.initParams = params;
		//this.init(this.baseElement, params);
	};
	$L.augmentObject($W.SupportDesk.prototype, {
		init :function(el, params) {
		this.elBase=el;
		this.eventManager = new $U.DOMEventHandler(el, {
			type: "click"
		});
		this.addListener();
	},
	successSend: {
		success: function(o){
		document.getElementById("msg").style.color="#006400";
		document.getElementById("msg").innerHTML="Sent Successfully!";
		document.forms[0].reset(); 
	},
	failure: function(o){
		document.getElementById("msg").style.color="#FF0000";
		document.getElementById("msg").innerHTML="Thre is an error.Please try again later! ";
		document.forms[0].reset(); 
	}

	},
	cancelSend:{
		success:function(o){
		$D.getElementsByClassName("issue-description")[0].value=null;
	},
	failure:function(o){
	}
	},

	onSend : function(){
		this.successSend.scope=this;
		var contents=$D.getElementsByClassName("issue-description")[0].value;
		var issue=$D.getElementsByClassName("issue-type")[0].value;
		$U.Connect.asyncRequest('GET', "/fleet/form/supportdesk/?command_type=support_desk&contents="+contents+"&issue="+issue, this.successSend, null);
	},

	onCancel : function(){
		$U.Connect.asyncRequest('GET', "/fleet/view/dashboard/", this.cancelSend, null);
	},

	clearMessageDiv : function(){
		document.getElementById("msg").innerHTML="";
	},
	changeplaceholderContent : function(){
		document.getElementById("msg").innerHTML="";
		var issue=$D.getElementsByClassName("issue-type")[0].value;
		var message='';
		if(issue=='Support'){
			message='Do you want to get any reports generated? (or) ';
			message+='Do you want any other assistance or clarification?';
		}
		if(issue=='Bug'){
			message='Did you find any feature not working?';
		}
		if(issue=='Performance'){
			message='Do you feel some feature is taking time for loading?';
		}
		if(issue=='Suggestion'){
			message='Do you feel any other features can be provided?';
		}
		if(issue=='Anonymous'){
			message='Select issue type.';
		}
		document.getElementById("issue-description").placeholder=message;
	},
	addListener: function(){
		this.eventManager.addListener($D.getElementsByClassName("send-button"), function(params){
			this.onSend();
		}, null, this);
		this.eventManager.addListener($D.getElementsByClassName("issue-description"), function(params){
			this.clearMessageDiv();
		}, null, this);
		this.eventManager.addListener($D.getElementsByClassName("issue-type"), function(params){
			this.changeplaceholderContent();
			this.clearMessageDiv();
		}, null, this);
		this.eventManager.addListener($D.getElementsByClassName("cancel-button"), function(params){
			this.onCancel();
		}, null, this);
	}
	});
})();