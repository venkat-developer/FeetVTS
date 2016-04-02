(function(){
	var $W = getPackageForName("com.i10n.fleet.widget.ui");
	var $L = YAHOO.lang;
	var $E = YAHOO.util.Event;
	var $D = YAHOO.util.Dom;

	/**
	 * System Settings Sub Nav Page
	 *
	 * @author irk
	 */
	$W.Upload = function(el, params){
		this.baseElement = el;
		this._widgets = {};
		this.init(el, params);
	};

	$L.augmentObject($W.Upload.prototype, {
		/**
		 * Initializing fnuction
		 * @param {Object} el
		 * @param {Object} params
		 */
		init: function(el, params){
		this.addListeners();

	},

	/**
	 * Event Handler for update of fetch method (Live Streaming or Polling)
	 * @param {Object} oArgs
	 */
	handleUpdate: function(oArgs){
	
	},
	/*Defining Utilities*/
	addListeners: function(){
		
	}

	});
})();
