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
    var hardwareid;
    /**
     * Edit User UI Widget
     * @author sabarish
     */
    $W.AddFuelCalibration = function(el, oArgs) {
        $W.AddFuelCalibration.superclass.constructor.call(this, el, oArgs);
        var oDataSrcEditHardware=[];     
        this.elTEditHardwareContainer = $D.getElementsByClassName("hardware-sel",null,this.elBase)[0];
        this.initAddFuelCalibration(el, oArgs);
    };
    $L.extend($W.AddFuelCalibration, $W.EditEntity, {
        /*Defining necessary callbacks*/
        oSaveCallback: {
            success: function(o) {
                $D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Success:</span> Hardware successfully edited.";
                $D.getElementsByClassName(this.oConfiguration.formClass, null, this.elBase)[0].reset();
                $D.getElementsByClassName("selected", null, this.elBase, function(elTarget){
           
                $D.addClass(elTarget,"disabled");
         
                },null,this); 

               this.hideInputForm();
            },
            failure: function(o) {
                $D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Failure:</span> Editing Hardware information failed. IMEI might be duplicate one.";
            }
        },
        oCallBacks: {
            oGetEntityCallBack: {
                success: function(o) {
                    var oData = JSON.parse(o.responseText);

                   
                    if (!oData.adminmanage.fuelcalibrationmanage.Error) {
                        this.processData(oData.adminmanage.fuelcalibrationmanage);
                       
                        this.processSpecificData(oData.adminmanage.fuelcalibrationmanage);
                        $D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Please Note:</span> add the Fuel samples.";
                        this.hideEditSheet();
			            this.showSampleSheet();
                        
                    }
                    else if (oData.adminmanage.fuelcalibrationmanage.Error.name == "ResourceNotFoundError") {
                        $D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Error:</span> The Hardware does not exist. The list has been reloaded with available hardwares";
                        this.reloadHardwareList(oData.adminmanage.fuelcalibration.hardwares);
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
	    showSampleSheet: function() {
             
            var elSheet = $D.getElementsByClassName("sample-sheet", null, this.elBase)[0];
            
            if ($D.hasClass(elSheet, "disabled")) {
                $D.removeClass(elSheet, "disabled");
            }
            
            
            /*
             * Preventing possible memory leaks
             */
            elSheet = null;
        },

       hideSampleSheet: function() {
            var elSheet = $D.getElementsByClassName("sample-sheet", null, this.elBase)[0];
            if (!$D.hasClass(elSheet, "disabled")) {
                $D.addClass(elSheet, "disabled");
            }
           
            /*
             * Preventing possible memory leaks
             */
            elSheet = null;
        },


        getEntity: function(oKey) {
            hardwareid=oKey;

            /*Correcting the scope of the call back*/
            this.oCallBacks.oGetEntityCallBack.scope = this;
            $U.Connect.asyncRequest("GET", "/fleet/view/controlpanel/?markup=AdminManage&debug=true&module=/blocks/json&data=view&subpage=fuelcalibration&hardwareID=" + oKey, this.oCallBacks.oGetEntityCallBack, null);
        },
        getSaveURL: function() {
            /*
             * TODO: Change this to the correct URL once the data providers are implemented
             */
            
                     
            return "/fleet/form/admin/";
        },
        
        /**
         * Function to Validate the Hardware details being updated.
         */ 
        validateSampleForm: function(oInput) {
             var isSamples = /^-?\d+$/.test(oInput.numberofsamples);
   
             if(isSamples == false){
				this.editHardwarePopUp("samples");
                                return false;

				}
             return true;
        },
        validateForm:function(oInput){
    
              for(var i=1;i<=oInput['numberofsamples'];i++){
               var isFuel = /^-?\d+$/.test(oInput['fuel'+i]);
                if(isFuel == false){
				this.editHardwarePopUp("fuel");
                                return false;

				}
                var isAd = /^-?\d+$/.test(oInput['ad'+i]);
                if(isAd == false){
				this.editHardwarePopUp("ad");
                                return false;

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
    $L.augmentObject($W.AddFuelCalibration.prototype, {
        /**
         * The initialization function
         * @param {Object} el The base element
         * @param {Object} oArgs The optional configuration params
         */
        initAddFuelCalibration: function(el, oArgs) {
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
            $W.Buttons.addDefaultHandler($D.getElementsByClassName("ok-button", null, this.elBase)[0], function(oEvent, oArgs) {

              var oInputsamples = $U.Forms.getFormValue($D.getElementsByClassName("input-form1", null, this.elBase)[0]);
	      var nosamples=oInputsamples.numberofsamples;
            
             if(this.validateSampleForm(oInputsamples)){
           
               this.hideSampleSheet();
		
               var str="<input class='input-element txt' name='numberofsamples' value="+nosamples+" type='hidden'/>";
                 str+="<input class='input-element txt' name='hardwareid' value="+hardwareid+" type='hidden'/>";

                for(var i=1;i<=nosamples;i++){

               str+="<div class='frm-item-cnt'>&nbsp&nbsp&nbsp&nbsp Fuel "+ i+"&nbsp ";
               str+="<input class='input-element txt' name='fuel"+i+"'+ type='text'></input>";
               str+="&nbsp&nbsp&nbsp&nbsp AD "+ i+"&nbsp";
               str+=" <input class='input-element txt' name='ad"+i+"'+ type='text'></input></div>";

		}
               $D.getElementsByClassName("frm-item-cnt", null, this.elBase)[0].innerHTML=str;
               this.showEditSheet();
             }
               
            }, null, this);
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
