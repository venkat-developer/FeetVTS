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
	var winCal;
	var dtToday=new Date();
	var Cal;
	var docCal;
	var MonthName=["January", "February", "March", "April", "May", "June","July", 
	               "August", "September", "October", "November", "December"];
	var WeekDayName=["Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"];	
	var exDateTime;//Existing Date and Time

	//Configurable parameters
	var cnTop="200";//top coordinate of calendar window.
	var cnLeft="500";//left coordinate of calendar window
	var WindowTitle ="DateTime Picker";//Date Time Picker title.
	var WeekChar=2;//number of character for week day. if 2 then Mo,Tu,We. if 3 then Mon,Tue,Wed.
	var CellWidth=20;//Width of day cell.
	var DateSeparator="-";//Date Separator, you can change it to "/" if you want.
	var TimeMode=24;//default TimeMode value. 12 or 24

	var ShowLongMonth=true;//Show long month name in Calendar header. example: "January".
	var ShowMonthYear=true;//Show Month and Year in Calendar header.
	var MonthYearColor="#cc0033";//Font Color of Month and Year in Calendar header.
	var WeekHeadColor="#0099CC";//Background Color in Week header.
	var SundayColor="#6699FF";//Background color of Sunday.
	var SaturdayColor="#CCCCFF";//Background color of Saturday.
	var WeekDayColor="white";//Background color of weekdays.
	var FontColor="blue";//color of font in Calendar day cell.
	var TodayColor="#FFFF33";//Background color of today.
	var SelDateColor="#FFFF99";//Backgrond color of selected date in textbox.
	var YrSelColor="#cc0033";//color of font of Year selector.
	var ThemeBg="";//Background image of Calendar window.

	/**
	 * Edit Vehicle UI Widget
	 * @author sabarish
	 */
	$W.VehiclehistoryManager = function(el, oArgs) {
		$W.VehiclehistoryManager.superclass.constructor.call(this, el, oArgs);
		var oDataSource=[];

		this.elTsSideBarContainer = $D.getElementsByClassName("veh-sel",null,this.elBase)[0];

		this.initEditVehicle(el, oArgs);

	};
	$L.extend($W.VehiclehistoryManager, $W.EditEntity, {
		/*Defining necessary callbacks*/
		oSaveCallback: {
			success: function(o) {
				$D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Success:</span> Vehicle successfully edited.";
				$D.getElementsByClassName(this.oConfiguration.formClass, null, this.elBase)[0].reset();
				this.hideInputForm();
			},
			failure: function(o) {
				$D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Failure:</span> Editing vehicle information failed.";
			}
		},
		oCallBacks: {
			oGetEntityCallBack: {
				success: function(o) {
					var oData = JSON.parse(o.responseText);
					if (!oData.adminmanage.vehiclehistorymanager.Error) {
						$D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Please Note:</span> All the fields are mandatory.";
						this.processData(oData.adminmanage.vehiclehistorymanager);
						this.showEditSheet();
					}
					else if (oData.adminmanage.vehiclehistorymanager.Error.name == "ResourceNotFoundError") {
						$D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Error:</span> The vehicle does not exist. The list has been reloaded with available vehicles";
						this.reloadVehicleList(oData.adminmanage.vehiclehistorymanager.vehicles);
						this.showMessage();
					}
				},
				failure: function(o) {
					$D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Failure:</span> Failed to retrive vehicle information.";
				}
			}
		},
		/*Over-riding necessary methods*/
		getEntity: function(oKey) {
			/*Correcting the scope of the call back*/
			this.oCallBacks.oGetEntityCallBack.scope = this;
			$U.Connect.asyncRequest("GET", "/fleet/view/controlpanel/?markup=AdminManage&debug=true&module=/blocks/json&data=view&subpage=vehiclehistory&vehicleID=" + oKey, this.oCallBacks.oGetEntityCallBack, null);
		},
		getSaveURL: function() {
			/*
			 * TODO: Change this to the correct URL once the data providers are implemented
			 */
			this.removeOption();
			return "/fleet/form/admin/";
		},
		/**
		 * Function to validate the vehicle details being updated.
		 */
		validateForm: function(oInput) {
			var aRadioEl = $D.getElementsByClassName("batterychangedvalue");
			for (var i = 0; i < aRadioEl.length; i++) {
				if (aRadioEl[i].checked) {
					oTimeFrameData.timeframe = aRadioEl[i].value;
				}
			}        	
			return true;
		},

		/**
		 * Function to provide a customized alert popup.
		 */
		editVehiclePopUp : function (type) {
			var popUpType = "edit-vehicle-"+type+"-popup"
			this.oPopUp = new $W.PopUp($D.getElementsByClassName(popUpType, null, this.elBase,function(elTarget){ 
				if($D.hasClass(elTarget,"disabled")){
					$D.removeClass(elTarget,"disabled");
				}
			})[0]);
			this.oPopUp.render();
			this.oPopUp.show();
		}
	}, true);
	$L.augmentObject($W.VehiclehistoryManager.prototype, {
		/**
		 * The initialization function
		 * @param {Object} el The base element
		 * @param {Object} oArgs The optional configuration params
		 */
		initEditVehicle: function(el, oArgs) {
			this.elBase = el;
			this.eventManager = new $U.DOMEventHandler(this.elBase, {
				type: "click"
			});
			/*Sorting the list of vehicles*/
			this.sortVehicleList();
			this.addListeners();
			this._generateDataSource();
			this.setAutoComplete();
		},
		/*Declaring the data members*/
		elBase: null,

		/**
		 * Default Search String
		 */
		DEFAULT_SEARCH_STRING: "Search...",

		/*Defining methods*/
		reloadVehicleList: function(oList) {
			var elList = $D.getElementsByClassName("veh-sel", null, this.elBase)[0];
			/*Removing all the available contents*/
			$D.getElementsByClassName("veh-sel-item", null, elList, function(elTarget) {
				elTarget.parentNode.removeChild(elTarget);
			});
			/*Writing new Data*/
			var sTemplate = $D.getElementsByClassName("template-vehicle-list-item", null, this.elBase)[0].innerHTML;
			//  $U.Element.DivElement.appendTemplatizedChildren(elList, sTemplate, $U.Arrays.mapToArray(oList));

			var newList = $U.Arrays.mapToArray(oList);

			var childstring="";
			for(var i=0;i<newList.length;i++)
			{
				var template=$U.processTemplate(sTemplate,newList[i]);
				childstring+=template;
			}


			elList.innerHTML=childstring;

			this.sortVehicleList();
		},

		sortVehicleList: function() {
			var elList = new $WU.SortableList($D.getElementsByClassName("veh-sel", null, this.elBase)[0]);
			elList.setComparator(this.vehicleComparator);
			elList.sort();
			/*Preventing possible memory leaks*/
			elList = null;
		},
		showDropdown : function(){
			var imei=$D.get("imeino");

			if (!$D.hasClass(imei, "disabled")) {
				$D.addClass(imei, "disabled");
			}else {
				$D.removeClass(imei, "disabled");

			}
			var el = $D.getElementsByClassName("input-element list");
			$D.getElementsByClassName("input-element list", null, this.elBase, function(elTarget) {
				if ($D.hasClass(elTarget, "disabled")) {

					$D.removeClass(elTarget, "disabled");
					$D.setAttribute(el,"status","active");
				}else {
					$D.addClass(elTarget, "disabled");
					$D.setAttribute(el,"status","inactive");
				}
			});
		},
		hideDropdown : function(){
			var imei=$D.get("imeino");
			if ($D.hasClass(imei, "disabled")) {
				$D.removeClass(imei, "disabled");
			}


			$D.getElementsByClassName("input-element list", null, this.elBase, function(elTarget) {
				if (!$D.hasClass(elTarget, "disabled")) {
					$D.addClass(elTarget, "disabled");
				}
			});
		},
		removeOption:function(){
			var selectbox=$D.getElementsByClassName("input-element list", null, this.elBase)[0];
			selectbox.remove(selectbox.selectedIndex);


		},
		/**
		 * Setups for showing autocomplete
		 */
		setAutoComplete: function(){
			this.el=$D.getElementsByClassName("editvehicle")[0];
			var autoComplete = new $YW.AutoComplete(this.searchInput, $D.getElementsByClassName("search-autocomplete", null, this.el)[0], this.oDataSource);
			autoComplete.prehighlightClassName = "yui-ac-prehighlight";
			autoComplete.useShadow = true;
			this.editVehicleListener();
		},

		/**
		 * Generates Datasource for autocomplete
		 */
		_generateDataSource: function(){
			var dataSource = [];
			$D.getElementsByClassName("veh-sel-item", null, this.elTsSideBarContainer, function(target){
				var itemID = target.getAttribute("value");
				var itemName = target.innerHTML;
				var data = {
						"name": itemName,
						"id": itemID
				};

				dataSource.push(data);
			});
			this.oDataSource = new $YU.LocalDataSource(dataSource);
			this.oDataSource.responseSchema = {
					fields: ["name"]
			};
		},
		/*Defining callbacks*/
		onVehicleSelected: function(event, args) {
			/*Changing the selected item*/
			var elList = $D.getAncestorByClassName(args[1].target, "veh-sel");
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
			this.hideDropdown();

			this.set($W.EditEntity.ATTR_SELECTED_ENTITY, $D.getAttribute(args[1].target, "value"));

		},
		addListeners: function() {
			var oListEventManager = new $U.DOMEventManager($D.getElementsByClassName("veh-sel", null, this.elBase)[0], "click");
			oListEventManager.addBehavior("veh-sel-item", this.onVehicleSelected, null, this);
			/*
			 * Preventing possible memory leaks
			 */
			$W.Buttons.addDefaultHandler($D.getElementsByClassName("edit-button",null,this.elBase)[0],this.showDropdown,null, this);

			oListEventManager = null;


			this.searchInput = $D.getElementsByClassName("search-item-input",null,this.elBase)[0];



		},

		editVehicleListener: function(){
			this.searchInput = $D.getElementsByClassName("search-item-input",null,this.elBase)[0];

			this.eventManager.addListener($D.getElementsByClassName("search-go-button"), function(params){
				this.selectItem(this.searchInput.value);
			}, null, this);

			var enterKeyListener = new $YU.KeyListener(this.searchInput, {
				"keys": 13
			}, {
				"fn": function(){
					this.selectItem(this.searchInput.value);
				},
				"scope": this,
				"correctScope": true
			});
			enterKeyListener.enable();
			$U.addDefaultInputText(this.searchInput, this.searchInput.value);
			/**
			 * Avoiding possible memory leaks;
			 */
			enterKeyListener = null;
		},

		/**
		 * Selects an item (trip) in the sidebar with the given item name (object)
		 */
		selectItem: function(objectName){
			/*Retaining the current scope*/
			var originalScope = this;
			$D.getElementsByClassName("veh-sel-item", null, this.elBase, function(target){
				var key;
				key = target.innerHTML;

				if (objectName != key) {
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
		vehicleComparator: function(el1, el2) {
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