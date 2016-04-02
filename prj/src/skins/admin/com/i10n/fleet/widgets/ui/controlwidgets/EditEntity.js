(function() {
	var $B = YAHOO.Bubbling;
	var $L = YAHOO.lang;
	var $YU = YAHOO.util;
	var $E = YAHOO.util.Event;
	var $D = YAHOO.util.Dom;
	var $YW = YAHOO.widget;
	var $U = getPackageForName("com.i10n.fleet.Utils");
	var $W = getPackageForName("com.i10n.fleet.widget.ui");
	var $V = getPackageForName("com.i10n.fleet.widget.view");
	/**
	 * Generic javascript for the editing an entity pattern
	 *
	 *
	 * The writing mechanism (eg: the URL to hit) is very specific to individual
	 * widgets. Thus they have to be implemented in their respective scripts
	 *
	 * @param {Object} el
	 * @param {Object} oArgs
	 */
	$W.EditEntity = function(el, oArgs) {
		if (!oArgs) {
			oArgs = {};
		}
		if (!$L.isString(oArgs.editSheetClass)) {
			oArgs.editSheetClass = "edit-sheet";
		}
		if (!$L.isString(oArgs.cancelButtonClass)) {
			oArgs.cancelButtonClass = "cancel-button";
		}
		this.initAddEntity(el, oArgs);
		this.initEditEntity(el, oArgs);
	};
	$L.augmentObject($W.EditEntity, {
		ATTR_SELECTED_ENTITY: "oSelectedEntity",
		EVT_SELECTED_ENTITY_CHANGE: "oSelectedEntityChange"
	});
	$L.augmentObject($W.EditEntity, {
		EDIT_STATES: {
		STATE_ONE: 1,
		STATE_TWO: 2,
		STATE_THREE: 3
	}
	});
	$L.augmentProto($W.EditEntity, $W.AddEntity);
	$L.augmentObject($W.EditEntity.prototype, {
		/**
		 * The initialization function
		 * @param {Object} el
		 * @param {Object} oArgs
		 */
		initEditEntity: function(el, oArgs) {
		this.elBase = el;
		this.setAttributeConfig($W.EditEntity.ATTR_SELECTED_ENTITY, {
			value: null
		});
		this.addEditEntityListeners();
	},
	/*Declaring Member Properties*/
	oSelectedEntity: null,
	/*Defining methods*/
	/**
	 * This method sets the values of the elements supported
	 * by $U.Forms.setFormValue
	 *
	 * Un-supported elements should be handled by the sub-classes
	 * themselves
	 *
	 * @param {Object} oData The data received from the server
	 */
	processData: function(oData) {
		var dataarray=$U.Arrays.mapToArray(oData);
		var iconidi=dataarray[5];

		if(dataarray.length==4){
			var img=dataarray[3];

			var imgsrc=$D.get("image");
			if(imgsrc!=null){
				imgsrc.src="@STATIC_DIR@/driverimage/"+img;

			}
		}

		var grp=$D.getElementsByClassName("input-element");
		if(!oData.isoffroadid && document.getElementById("isoffroaddiv")){
			document.getElementById("isoffroaddiv").style.display = "none"
		}else if(document.getElementById("isoffroaddiv")){
			document.getElementById("isoffroaddiv").style.display = "block"
		}
		for(var i=1;i<grp.length;i++){

			if(grp[i].value==iconidi){
				grp[i].setAttribute( "checked","checked");
			}
			else{
				grp[i].removeAttribute( "checked");
			}
		}
		$U.Forms.setFormValue($D.getElementsByClassName(this.oConfiguration.formClass, null, this.elBase)[0], oData);
	},
	/**
	 * Abstract method that should be used by subclasses
	 * to set values to elements that are not supported
	 * by $U.Forms.setFormValue
	 *
	 * @param {Object} oData The data received from the server
	 */
	processSpecificData: function(oData) {
	},
	/**
	 * Abstract method that has to be implemented by sub classes
	 *
	 * The responsibility of this method is implementing the mechanism to
	 * get the data from the server
	 */
	getEntity: function(oKey) {
	},


	validateForm: function(oInput) {
		return true;
	},
	/**
	 * The responsibility of this method is to show the
	 * edit entity form
	 */
	showEditSheet: function() {
		var elSheet = $D.getElementsByClassName(this.oConfiguration.editSheetClass, null, this.elBase)[0];

		if ($D.hasClass(elSheet, "disabled")) {
			$D.removeClass(elSheet, "disabled");
		}
		this.showInputForm();
		this.setState($W.EditEntity.EDIT_STATES.STATE_TWO);
		/*
		 * Preventing possible memory leaks
		 */
		elSheet = null;
	},
	setState: function(oState) {
		var elStateView = $D.getElementsByClassName("state-view", null, this.elBase)[0];
		var i = 0;
		$D.getElementsByClassName("state-item", null, elStateView, function(elTarget) {
			if (i < oState) {
				if ($D.hasClass(elTarget, "disabled")) {
					$D.removeClass(elTarget, "disabled");
				}
			}
			else {
				if (!$D.hasClass(elTarget, "disabled")) {
					$D.addClass(elTarget, "disabled");
				}
			}
			i++;
		});
		/*
		 * Preventing possible memory leaks
		 */
		elStateView = i = null;
	},


	/**
	 * The responsibility of this method is to hide the
	 * edit entity form
	 */
	hideEditSheet: function() {
		var elSheet = $D.getElementsByClassName(this.oConfiguration.editSheetClass, null, this.elBase)[0];
		if (!$D.hasClass(elSheet, "disabled")) {
			$D.addClass(elSheet, "disabled");
		}
		this.hideInputForm();
		this.setState($W.EditEntity.EDIT_STATES.STATE_ONE);
		/*
		 * Preventing possible memory leaks
		 */
		elSheet = null;
	},
	hideInputForm: function() {
		var elForm = $D.getElementsByClassName(this.oConfiguration.formClass, null, this.elBase)[0];
		var elButtons = $D.getElementsByClassName("frmbutton", null, this.elBase);
		if (!$D.hasClass(elForm, "disabled")) {
			$D.addClass(elForm, "disabled");
		}
		for (var i = 0; i < elButtons.length; i++) {
			if (!$D.hasClass(elButtons[i], "disabled")) {
				$D.addClass(elButtons[i], "disabled");
			}
		}
		/*
		 * Preventing possible memory leaks
		 */
		elForm = elButtons = null;
	},
	showInputForm: function() {
		var elForm = $D.getElementsByClassName(this.oConfiguration.formClass, null, this.elBase)[0];

		var elButtons = $D.getElementsByClassName("frmbutton", null, this.elBase);
		if ($D.hasClass(elForm, "disabled")) {
			$D.removeClass(elForm, "disabled");
		}
		for (var i = 0; i < elButtons.length; i++) {
			if ($D.hasClass(elButtons[i], "disabled")) {
				$D.removeClass(elButtons[i], "disabled");
			}
		}
		/*
		 * Preventing possible memory leaks
		 */
		elForm = elButtons = null;
	},
	showMessage: function() {
		this.showEditSheet();
		this.hideInputForm();
	},
	/*Overriding necessary methods*/
	/**
	 * Refreshes the widget
	 */
	refresh: function() {
		$D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Please Note:</span>  All fields are mandatory.";
		$D.getElementsByClassName(this.oConfiguration.formClass, null, this.elBase)[0].reset();
		this.hideEditSheet();
		this.setState($W.EditEntity.EDIT_STATES.STATE_ONE);
	},
	/*Defining call backs*/
	onEntitySubmited: function(oEvent, oArgs, oMe) {
		this.getEntity(this.get($W.EditEntity.ATTR_SELECTED_ENTITY));

	},
	onSave: function() {
		var oInput = $U.Forms.getFormValue($D.getElementsByClassName("input-form", null, this.elBase)[0]);
		oInput.key = this.get($W.EditEntity.ATTR_SELECTED_ENTITY);
		/*Convert the input elements to a map*/
		if(this.validateForm(oInput)){
			this.saveEntity(oInput);
		}
		this.setState($W.EditEntity.EDIT_STATES.STATE_THREE);


		/*Preventing possible memory leaks*/
		oInput = null;
	},
	/*Installing listeners*/
	addEditEntityListeners: function() {
		this.subscribe($W.EditEntity.EVT_SELECTED_ENTITY_CHANGE, this.onEntitySubmited, null, this);
		/*Adding the listener for the cancel button*/
		$W.Buttons.addDefaultHandler($D.getElementsByClassName(this.oConfiguration.cancelButtonClass, null, this.elBase)[0], function(oEvent, oArgs) {
			this.refresh();
		}, null, this);
	}
	}, true);
})();
