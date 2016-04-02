
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
     * Edit Driver UI Widget
     * @author sabarish
     */
    $W.EditDriver = function(el, oArgs) {
        $W.EditDriver.superclass.constructor.call(this, el, oArgs);
        var oDataSrc=[];     
        this.elTsSideContainer = $D.getElementsByClassName("drv-sel",null,this.elBase)[0];
        this.initEditDriver(el, oArgs);
    };
    $L.extend($W.EditDriver, $W.EditEntity, {
        /*Defining necessary callbacks*/
        oSaveCallback: {
            success: function(o) {
                $D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Success:</span> Driver successfully edited.";
                $D.getElementsByClassName(this.oConfiguration.formClass, null, this.elBase)[0].reset();
                this.hideInputForm();
            },
            failure: function(o) {
                $D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Failure:</span> Editing driver information failed.";
            },
            upload:function(o){
            	$D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Success:</span> Driver successfully edited.";
                $D.getElementsByClassName(this.oConfiguration.formClass, null, this.elBase)[0].reset();
                this.hideInputForm();
            }
        },
        oCallBacks: {
            oGetEntityCallBack: {
                success: function(o) {
                    var oData = JSON.parse(o.responseText);
                    if (!oData.adminmanage.drivermanage.Error) {
                        this.processData(oData.adminmanage.drivermanage);
                        this.processSpecificData(oData.adminmanage.drivermanage);
                        $D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Please Note:</span> All the fields are mandatory.";
                       this.showPhoto();
                        this.showEditSheet();
                    }
                    else if (oData.adminmanage.drivermanage.Error.name == "ResourceNotFoundError") {
                        $D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Error:</span> The driver does not exist. The list has been reloaded with available drivers";
                        this.reloadDriverList(oData.adminmanage.drivermanage.drivers);
                        this.showMessage();
                    }
                },
                failure: function(o) {
                    $D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Failure:</span> Failed to retrive driver information.";
                }
            }
        },
        /*Over-riding necessary methods*/
        processSpecificData: function(oData) {
        },
        getEntity: function(oKey) {
            /*Correcting the scope of the call back*/
            this.oCallBacks.oGetEntityCallBack.scope = this;
            $U.Connect.asyncRequest("GET", "/fleet/view/controlpanel/?markup=AdminManage&debug=true&module=/blocks/json&data=view&subpage=driver&driverID=" + oKey, this.oCallBacks.oGetEntityCallBack, null);
        },
        getSaveURL: function() {
            /*
             * TODO: Change this to the correct URL once the data providers are implemented
             */
            return "/fleet/form/admin/";
        },
        
        /**
         * Function to validate details of the driver being updated.
         */
        validateForm: function(oInput){
        	var firstName=oInput['firstname'];
        	var lastName=oInput['lastname'];
        	var licenseNo=oInput['license'];
        	// License number has to a valid number.
        	var isValidLicenseNumber = /^-?\d+$/.test(licenseNo);
        	
        	// Check for null entries for the mandatory fields.
        	if(firstName == "" || lastName == "" || licenseNo == "" ){
        		this.editDriverPopUp("empty");
				return false;
        	}
        	
        	// Check for length and type validation.
        	if(firstName.length > 50 || lastName.length > 50 || isValidLicenseNumber == false || licenseNo.length > 20){
				if(firstName.length > 50) {
					this.editDriverPopUp("firstname");
				}
                if(lastName.length > 50){
                	this.editDriverPopUp("lastname");
    			}
                if(isValidLicenseNumber == false || licenseNo.length > 20){
                	this.editDriverPopUp("licenseno");
    			}
                return false ;
			}
        	return  true;
        },
        
        /**
         * Function to provide a customized alert popup.
         */
        editDriverPopUp : function (type) {
        	var popUpType = "edit-driver-"+type+"-popup";
        	this.oPopUp = new $W.PopUp($D.getElementsByClassName(popUpType, null, this.elBase,function(elTarget){ 
				if($D.hasClass(elTarget,"disabled")){
					$D.removeClass(elTarget,"disabled");
				}
			})[0]);
			this.oPopUp.render();
        	this.oPopUp.show();
        }
        
    }, true);
    $L.augmentObject($W.EditDriver.prototype, {
        /**
         * The initialization function
         * @param {Object} el The base element
         * @param {Object} oArgs The optional configuration params
         */
        initEditDriver: function(el, oArgs) {
            this.elBase = el;
            this.driverEventManager = new $U.DOMEventHandler(this.elBase, {
                type: "click"
            });
            this.sortDriverList();
            this.addListeners();
            this.generateDataSourceForEditDriver();
            this.setAutoCompleteForEditDriver();
        },
        /*Declaring the data members*/
        elBase: null,
        /**
         * Default Search String
         */
        DEFAULT_SEARCH_STRING: "Search...",
        
        /*Defining methods*/
        reloadDriverList: function(oList) {
        	
            var elList = $D.getElementsByClassName("drv-sel", null, this.elBase)[0];
            /*Removing all the available contents*/
            $D.getElementsByClassName("drv-sel-item", null, elList, function(elTarget) {
                elTarget.parentNode.removeChild(elTarget);
            });
            /*Writing new Data*/
            var sTemplate = $D.getElementsByClassName("template-driver-list-item", null, this.elBase)[0].innerHTML;
            
           // $U.Element.DivElement.appendTemplatizedChildren(elList, sTemplate, $U.Arrays.mapToArray(oList));
            var newList = $U.Arrays.mapToArray(oList);
            
            var childstring="";
            for(var i=0;i<newList.length;i++)
            {
                var template=$U.processTemplate(sTemplate,newList[i]);
                childstring+=template;
            }
            this.sortDriverList();
        },
        sortDriverList: function() {
            var elList = new $WU.SortableList($D.getElementsByClassName("drv-sel", null, this.elBase)[0]);
            elList.setComparator(this.driverComparator);
            elList.sort();
            /*Preventing possible memory leaks*/
            elList = null;
        },
        /**
         * Setups for showing autocomplete
         */
        setAutoCompleteForEditDriver: function(){
            var autoCompleteDriver = new $YW.AutoComplete(this.searchInputString, $D.getElementsByClassName("search-autocomplete-driver", null, this.elBase)[0], this.oDataSrc);
            autoCompleteDriver.prehighlightClassName = "yui-ac-prehighlight";
            autoCompleteDriver.useShadow = true;
            this.editDriverListener();
        },
        /**
         * Generates Datasource for autocomplete
         */
        generateDataSourceForEditDriver: function(){
        	  var dataSrc = [];
            $D.getElementsByClassName("drv-sel-item", null, this.elTsSideContainer, function(target){
                var itemID = target.getAttribute("value");
                var itemName = target.innerHTML;
                var data = {
                    "name": itemName,
                    "id": itemID
                };

                dataSrc.push(data);
            });

            this.oDataSrc = new $YU.LocalDataSource(dataSrc);
            this.oDataSrc.responseSchema = {
                fields: ["name"]
            };
        },
        editDriverListener: function(){
        	
            this.searchInputString = $D.getElementsByClassName("search-item-input-driver",null,this.elBase)[0];
            this.driverEventManager.addListener($D.getElementsByClassName("search-go-button-driver"), function(params){
                this.selectItem(this.searchInputString.value);
            }, null, this);
           
            var enterKeyListener = new $YU.KeyListener(this.searchInputString, {
                "keys": 13
            }, {
                "fn": function(){
                    this.selectItem(this.searchInputString.value);
                },
                "scope": this,
                "correctScope": true
            });
            enterKeyListener.enable();
            $U.addDefaultInputText(this.searchInputString, this.searchInputString.value);
            /**
             * Avoiding possible memory leaks;
             */
            enterKeyListener = null;
        },
        /*Defining callbacks*/
        onDriverSelected: function(event, args) {
            var elList = $D.getAncestorByClassName(args[1].target, "drv-sel");
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
        	 var oListener = new $U.DOMEventHandler(this.elBase, {
                 type: "click"
             });
        	  
            var oListEventManager = new $U.DOMEventManager($D.getElementsByClassName("drv-sel", null, this.elBase)[0], "click");
            oListEventManager.addBehavior("drv-sel-item", this.onDriverSelected, null, this);
            /*
             * Preventing possible memory leaks
             */
            oListEventManager = null;
            
            this.searchInputString = $D.getElementsByClassName("search-item-input-driver",null,this.elBase)[0];
           

        },
       
        showPhoto:function(){
        	 var photo=$D.getElementsByClassName('pic-cur-img');
             $D.removeClass(photo,"yui-hidden");
        	
        },
        /**
         * Selects an item (trip) in the sidebar with the given item name (object)
         */
        selectItem: function(objectName){
            /*Retaining the current scope*/

           $D.getElementsByClassName("drv-sel-item", null, this.elBase, function(target){
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
        driverComparator: function(el1, el2) {
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

