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
	var $WU = getPackageForName("com.i10n.fleet.widget.util");
	/**
	 * Edit User UI Widget
	 * @author sabarish
	 */
	$W.EditHardware = function(el, oArgs) {
		$W.EditHardware.superclass.constructor.call(this, el, oArgs);
		var oDataSrcEditHardware=[];     
		this.elTEditHardwareContainer = $D.getElementsByClassName("hardware-sel",null,this.elBase)[0];
		this.initEditHardware(el, oArgs);
	};
	$L.extend($W.EditHardware, $W.EditEntity, {
		/*Defining necessary callbacks*/
		oSaveCallback: {
			success: function(o) {
				if(o.responseText.length>0){
					$D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Success:</span> Hardware successfully edited.";
					$D.getElementsByClassName(this.oConfiguration.formClass, null, this.elBase)[0].reset();
					this.hideInputForm();
				}else{
					$D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Failure:</span> Creation of Hardware failed. Because IMEI,SIM ID or Mobile Number is already existed";
				}

			},
			failure: function(o) {
				$D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Failure:</span> Editing Hardware information failed.";
			}
		},
		oCallBacks: {
			oGetEntityCallBack: {
				success: function(o) {
					var oData = JSON.parse(o.responseText);
					if (!oData.adminmanage.hardwaremanage.Error) {
						this.processData(oData.adminmanage.hardwaremanage);
						this.processSpecificData(oData.adminmanage.hardwaremanage);
						$D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Please Note:</span> All the fields are mandatory.";
						this.showEditSheet();

					}
					else if (oData.adminmanage.hardware.Error.name == "ResourceNotFoundError") {
						$D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Error:</span> The Hardware does not exist. The list has been reloaded with available hardwares";
						this.reloadHardwareList(oData.adminmanage.hardwaremanage.hardwares);
						this.showMessage();
					}
				},
				failure: function(o) {
					$D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Failure:</span> Failed to retrive Hardware information.";
				}
			}
		},
		/*Over-riding necessary methods*/
		processSpecificData: function(oData) {

		},
		getEntity: function(oKey) {
			/*Correcting the scope of the call back*/
			this.oCallBacks.oGetEntityCallBack.scope = this;
			$U.Connect.asyncRequest("GET", "@APP_CONTEXT@/view/controlpanel/?markup=AdminManage&debug=true&module=/blocks/json&data=view&subpage=hardware&hardwareID=" + oKey, this.oCallBacks.oGetEntityCallBack, null);
		},
		getSaveURL: function() {
			/*
			 * TODO: Change this to the correct URL once the data providers are implemented
			 */
			return "@APP_CONTEXT@/form/admin/";
		},

		/**
		 * Function to Validate the Hardware details being updated.
		 */ 
		validateForm: function(oInput) {
			var imei = oInput['imei'];
			var module = oInput['moduleversion'];
			var firm = oInput['firmwareversion'];
			var mobileNumber = oInput['mobilenumber'];
			var simId = oInput['simid'];
			// IMEI has to be number only.
			var isImei = /^-?\d+$/.test(imei);
			// Module version has to be a float value.
			var isModule = /^[-+]?\d+(\.\d+)?$/.test(module);
			// Firmware version has to be float value.
			var isFirm = /^[-+]?\d+(\.\d+)?$/.test(firm);

			// Check for null entries of the mandatory fields.
			if(imei == "" || module == "" ||  firm== ""){	
				this.editHardwarePopUp("empty");
				return false;
			}

			// Check for type and length validation.
			if(isImei == false || isModule == false || isFirm == false || imei.length > 20 || imei.length < 15 
					|| module.length > 5 || firm.length > 5){
				if(isImei == false){
					this.editHardwarePopUp("imei")
				}
				if(isModule == false ){
					this.editHardwarePopUp("module");
				}	
				if(isFirm == false  ){
					this.editHardwarePopUp("firm");
				}
				if(imei.length > 20 || imei.length < 15){
					this.editHardwarePopUp("imei");
				}
				if(module.length > 5){
					this.editHardwarePopUp("module");
				}
				if(firm.length > 5){
					this.editHardwarePopUp("firm");
				}
				return false ;
			}
			//checks for the lenth and type of Mobile Number 		
			if(mobileNumber.length!=0){
				if(parseInt(mobileNumber) != 0){	
					if(mobileNumber.length != 10 || isNaN(mobileNumber)){
						this.editHardwarePopUp("mobilenumber");
						return false;

					}
				}	
			}
			
			//checks for the length and type of SIM ID 
			if(simId.length != 0){
				if(parseInt(simId)!=0){
					if(simId.length < 18 || simId.length > 20 || isNaN(simId)){
						this.editHardwarePopUp("simid");
						return false;
					}	
				}
			}
			return true;
		},

		/**
		 * Function to provide a customized alert popup.
		 */
		editHardwarePopUp : function(type){
			var popUpType = "edit-hardware-"+type+"-popup";
			this.oPopUp = new $W.PopUp($D.getElementsByClassName(popUpType, null, this.elBase,function(elTarget){ 
				if($D.hasClass(elTarget,"disabled")){
					$D.removeClass(elTarget,"disabled");
				}
			})[0]);

			this.oPopUp.render();
			this.oPopUp.show();
		}
	}, true);
	$L.augmentObject($W.EditHardware.prototype, {
		/**
		 * The initialization function
		 * @param {Object} el The base element
		 * @param {Object} oArgs The optional configuration params
		 */
		initEditHardware: function(el, oArgs) {
			this.elBase = el;
			this.eventEditHardwareManager = new $U.DOMEventHandler(this.elBase, {
				type: "click"
			});
			this.sortHardwareList();
			this.addListeners();
			this.generateDataSourceForEditHardware();
			this.setAutoCompleteForEditHardware();
		},
		/*Declaring the data members*/
		elBase: null,
		/*Defining methods*/
		reloadHardwareList: function(oList) {
			var elList = $D.getElementsByClassName("hardware-sel", null, this.elBase)[0];
			/*Removing all the available contents*/
			$D.getElementsByClassName("hardware-sel-item", null, elList, function(elTarget) {
				elTarget.parentNode.removeChild(elTarget);
			});
			/*Writing new Data*/
			var sTemplate = $D.getElementsByClassName("template-hardware-list-item", null, this.elBase)[0].innerHTML;
			$U.Element.DivElement.appendTemplatizedChildren(elList, sTemplate, $U.Arrays.mapToArray(oList));
			this.sortHardwareList();
		},
		sortHardwareList: function() {
			var elList = new $WU.SortableList($D.getElementsByClassName("hardware-sel", null, this.elBase)[0]);
			elList.setComparator(this.hardwareComparator);
			elList.sort();
			/*Preventing possible memory leaks*/
			elList = null;
		},
		/**
		 * Setups for showing autocomplete
		 */
		setAutoCompleteForEditHardware: function(){
			var autoCompleteHardware = new $YW.AutoComplete(this.searchHardwareInput, $D.getElementsByClassName("search-autocomplete-hardware", null, this.elBase)[0], this.oDataSrcEditHardware);
			autoCompleteHardware.prehighlightClassName = "yui-ac-prehighlight";
			autoCompleteHardware.useShadow = true;
			autoCompleteHardware.maxResultsDisplayed = 11;
			this.editHardwareListener();
		},
		/**
		 * Generates Datasource for autocomplete
		 */
		generateDataSourceForEditHardware: function(){
			var dataSrcEditHardware = [];
			$D.getElementsByClassName("hardware-sel-item", null, this.elTEditHardwareContainer, function(target){
				var itemID = target.getAttribute("value");
				var itemName = target.innerHTML;
				var data = {
						"name": itemName,
						"id": itemID
				};

				dataSrcEditHardware.push(data);
			});

			this.oDataSrcEditHardware = new $YU.LocalDataSource(dataSrcEditHardware);
			this.oDataSrcEditHardware.responseSchema = {
					fields: ["name"]
			};
		},
		editHardwareListener: function(){

			this.searchHardwareInput = $D.getElementsByClassName("search-item-input-hardware",null,this.elBase)[0];
			this.eventEditHardwareManager.addListener($D.getElementsByClassName("search-go-button-hardware"), function(params){
				this.selectEditHardwareItem(this.searchHardwareInput.value);
			}, null, this);

			var enterEditHardwareKeyListener = new $YU.KeyListener(this.searchHardwareInput, {
				"keys": 13
			}, {
				"fn": function(){
					this.selectEditHardwareItem(this.searchHardwareInput.value);
				},
				"scope": this,
				"correctScope": true
			});
			enterEditHardwareKeyListener.enable();
			$U.addDefaultInputText(this.searchHardwareInput, this.searchHardwareInput.value);
			/**
			 * Avoiding possible memory leaks;
			 */
			enterEditHardwareKeyListener = null;
		},
		/*Defining callbacks*/
		onHardwareSelected: function(event, args) {
			var elList = $D.getAncestorByClassName(args[1].target, "hardware-sel");
			$D.getElementsByClassName("list-item", null, elList, function(elTarget) {
				if (elTarget === args[1].target) {
					if (!$D.hasClass(elTarget, "selected")) {
						$D.addClass(elTarget, "selected");
					}
				}
				else {
					if ($D.hasClass(elTarget, "selected")) {
						$D.removeClass(elTarget, "selected");
					}
				}
			});
			this.set($W.EditEntity.ATTR_SELECTED_ENTITY, $D.getAttribute(args[1].target, "value"));
		},
		addListeners: function() {
			var oListEventManager = new $U.DOMEventManager($D.getElementsByClassName("hardware-sel", null, this.elBase)[0], "click");
			oListEventManager.addBehavior("hardware-sel-item", this.onHardwareSelected, null, this);
			/*
			 * Preventing possible memory leaks
			 */
			oListEventManager = null;

			this.searchHardwareInput = $D.getElementsByClassName("search-item-input-hardware",null,this.elBase)[0];
		},
		/**
		 * Selects an item (trip) in the sidebar with the given item name (object)
		 */
		selectEditHardwareItem: function(objectName){
			/*Retaining the current scope*/

			$D.getElementsByClassName("hardware-sel-item", null, this.elBase, function(target){
				var keyname;

				keyname = target.innerHTML;

				if (objectName != keyname) {
					/*Change the Selected trip attribute value*/
					$D.addClass(target, "disabled");
				}
				else {
					if($D.hasClass(target,"disabled")){
						$D.removeClass(target,"disabled");
					}
				}
			});
			/*Avioding possible memory leaks*/
			objectName = null;
		},
		/*Defining utilities*/
		hardwareComparator: function(el1, el2) {
			var result = -1;
			if ($L.isObject(el1) && $L.isObject(el2)) {
				var attrEl1 = $L.trim(el1.innerHTML);
				var attrEl2 = $L.trim(el2.innerHTML);
				if (attrEl1 == attrEl2) {
					result = 0;
				}
				else if (attrEl1 > attrEl2) {
					result = 1;
				}
				else {
					result = -1;
				}
			}
			else if ($L.isObject(el1)) {
				result = 1;
			}
			else if ($L.isObject(el2)) {
				result = -1;
			}
			else {
				result = 0;
			}
			return result;
		}
	});
})();
