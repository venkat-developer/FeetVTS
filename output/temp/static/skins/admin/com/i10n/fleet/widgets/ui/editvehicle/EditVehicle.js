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
     * Edit Vehicle UI Widget
     * @author sabarish
     */
    $W.EditVehicle = function(el, oArgs) {
        $W.EditVehicle.superclass.constructor.call(this, el, oArgs);
        var oDataSource=[];
        
        this.elTsSideBarContainer = $D.getElementsByClassName("veh-sel",null,this.elBase)[0];

        this.initEditVehicle(el, oArgs);
        
    };
    $L.extend($W.EditVehicle, $W.EditEntity, {
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
                    if (!oData.adminmanage.vehiclemanage.Error) {
                        $D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Please Note:</span> All the fields are mandatory.";
                        this.processData(oData.adminmanage.vehiclemanage);
                        this.showEditSheet();
                    }
                    else if (oData.adminmanage.vehiclemanage.Error.name == "ResourceNotFoundError") {
                        $D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Error:</span> The vehicle does not exist. The list has been reloaded with available vehicles";
                        this.reloadVehicleList(oData.adminmanage.vehiclemanage.vehicles);
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
            $U.Connect.asyncRequest("GET", "@APP_CONTEXT@/view/controlpanel/?markup=AdminManage&debug=true&module=/blocks/json&data=view&subpage=vehicle&vehicleID=" + oKey, this.oCallBacks.oGetEntityCallBack, null);
        },
        getSaveURL: function() {
            /*
             * TODO: Change this to the correct URL once the data providers are implemented
             */
            this.removeOption();
            return "@APP_CONTEXT@/form/admin/";
        },
        /**
         * Function to validate the vehicle details being updated.
         */
        validateForm: function(oInput) {
            var displayName = oInput['name'];
            var make = oInput['make'];
            var model = oInput['model'];
            var year = oInput['year'];
            var imei = oInput['imei'];
            // Validating the type of the year 
            var isYear = /^-?\d+$/.test(year);
					                       
            // Check for null entries 
            if(displayName == "" || make == "" || model == "" || year == "" || imei == "" ){
				this.editVehiclePopUp("empty");
				return false;
			}
			
            // Check for length and type validation 
            if(displayName.length > 20 || make.length > 20 || model.length > 20 || isYear == false || year.length != 4){
				if(displayName.length > 20){
					this.editVehiclePopUp("displayname");
				}
				if(make.length > 20){
                	this.editVehiclePopUp("make");
    			}
                if(model.length > 20){
                	this.editVehiclePopUp("model");
    			}
                if(isYear == false || year.length != 4){
    				this.editVehiclePopUp("year");
    			}
                return false ;
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
    $L.augmentObject($W.EditVehicle.prototype, {
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