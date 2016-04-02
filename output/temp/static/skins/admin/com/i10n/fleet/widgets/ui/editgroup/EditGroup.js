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
     * Edit Group UI Widget
     * @author dharma
     */
    $W.EditGroup = function(el, oArgs) {
        $W.EditGroup.superclass.constructor.call(this, el, oArgs);
        var oDataSource=[];
       
        this.elTsSideBarContainer = $D.getElementsByClassName("grp-sel",null,this.elBase)[0];
        this.initEditGroup(el, oArgs);
    };
    $L.extend($W.EditGroup, $W.EditEntity, {
        /*Defining necessary callbacks*/
        oSaveCallback: {
            success: function(o) {
                $D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Success:</span> Group successfully edited.";
                $D.getElementsByClassName(this.oConfiguration.formClass, null, this.elBase)[0].reset();
                this.hideInputForm();
            },
            failure: function(o) {
                $D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Failure:</span> Editing Group information failed. Groupname might be duplicate one.";
            }
        },
        oCallBacks: {
            oGetEntityCallBack: {
                success: function(o) {
                    var oData = JSON.parse(o.responseText);
                    if (!oData.adminmanage.groupmanage.Error) {
                        $D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Please Note:</span> All the fields are mandatory.";
                        this.processData(oData.adminmanage.groupmanage);
                        this.showEditSheet();
                    }
                    else if (oData.adminmanage.groupmanage.Error.name == "ResourceNotFoundError") {
                        $D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Error:</span> The Group does not exist. The list has been reloaded with available groups";
                        this.reloadGroupList(oData.adminmanage.groupmanage.groups);
                        this.showMessage();
                    }
                },
                failure: function(o) {
                    $D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Failure:</span> Failed to retrive group information.";
                }
            }
        },
        /*Over-riding necessary methods*/
        getEntity: function(oKey) {
            /*Correcting the scope of the call back*/
            this.oCallBacks.oGetEntityCallBack.scope = this;
            $U.Connect.asyncRequest("GET", "@APP_CONTEXT@/view/controlpanel/?markup=AdminManage&debug=true&module=/blocks/json&data=view&subpage=group&groupID=" + oKey, this.oCallBacks.oGetEntityCallBack, null);
        },
        getSaveURL: function() {
            /*
             * TODO: Change this to the correct URL once the data providers are implemented
             */
            return "@APP_CONTEXT@/form/admin/";
        },
        
        /**
         * Function to validate details of the group being updated.
         */
        validateForm: function(oInput) {
        	var groupName=oInput['name'];
        	// groupname has to be a string.
			var isValid = /[a-zA-Z]+/.test(groupName);
                    
			// Check for null entries for the mandatory fields.
			if(groupName == "" ){
				this.editGroupPopUp("empty");
				return false;
			}
			
			// Check for length and type validation.
			if(isValid == false || groupName.length > 20){
				this.editGroupPopUp("groupname");	
				return false;
			}
						
		    return true;
        },
        
        /**
         * Function to provide a customized alert popup.
         */
        editGroupPopUp : function (type) {
        	var popUpType = "edit-group-"+type+"-popup";
        	this.oPopUp = new $W.PopUp($D.getElementsByClassName(popUpType, null, this.elBase,function(elTarget){ 
				if($D.hasClass(elTarget,"disabled")){
					$D.removeClass(elTarget,"disabled");
				}
			})[0]);
			this.oPopUp.render();
        	this.oPopUp.show();
        }
    }, true);
    $L.augmentObject($W.EditGroup.prototype, {
        /**
         * The initialization function
         * @param {Object} el The base element
         * @param {Object} oArgs The optional configuration params
         */
        initEditGroup: function(el, oArgs) {
            this.elBase = el;
            this.eventManager = new $U.DOMEventHandler(this.elBase, {
                type: "click"
            });
            /*Sorting the list of groups*/
            this.sortGroupList();
            this.addListeners();
            this.generateDataSourceForEditGroup();
			 this.setAutoCompleteForEditGroup();
        },
        /*Declaring the data members*/
        elBase: null,
        
      
        /*Defining methods*/
        reloadGroupList: function(oList) {
            var elList = $D.getElementsByClassName("grp-sel", null, this.elBase)[0];
            /*Removing all the available contents*/
            $D.getElementsByClassName("grp-sel-item", null, elList, function(elTarget) {
                elTarget.parentNode.removeChild(elTarget);
            });
            /*Writing new Data*/
            var sTemplate = $D.getElementsByClassName("template-group-list-item", null, this.elBase)[0].innerHTML;
            $U.Element.DivElement.appendTemplatizedChildren(elList, sTemplate, $U.Arrays.mapToArray(oList));
            this.sortGroupList();
        },
       
        sortGroupList: function() {
            var elList = new $WU.SortableList($D.getElementsByClassName("grp-sel", null, this.elBase)[0]);
            elList.setComparator(this.groupComparator);
            elList.sort();
            /*Preventing possible memory leaks*/
            elList = null;
        },
        
        /**
         * Generates Datasource for autocomplete
         */
        generateDataSourceForEditGroup: function(){
        	  var dataSrc = [];
            $D.getElementsByClassName("grp-sel-item", null, this.elTsSideBarContainer, function(target){
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
		
		 setAutoCompleteForEditGroup: function(){
            var autoCompleteGroup = new $YW.AutoComplete(this.searchInputString, $D.getElementsByClassName("search-autocomplete-group", null, this.elBase)[0], this.oDataSrc);
         
			autoCompleteGroup.prehighlightClassName = "yui-ac-prehighlight";
            autoCompleteGroup.useShadow = true;
            this.editGroupListener();
        },
		
		editGroupListener: function(){  
   	            this.searchInputString = $D.getElementsByClassName("search-item-input-group",null,this.elBase)[0];
            this.eventManager.addListener($D.getElementsByClassName("search-go-button-group"), function(params){
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
        onGroupSelected: function(event, args) {
            /*Changing the selected item*/
            var elList = $D.getAncestorByClassName(args[1].target, "grp-sel");
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
		
            var oListEventManager = new $U.DOMEventManager($D.getElementsByClassName("grp-sel", null, this.elBase)[0], "click");
            oListEventManager.addBehavior("grp-sel-item", this.onGroupSelected, null, this);
            /*
             * Preventing possible memory leaks
             */
            
            
            oListEventManager = null;
			  this.searchInputString = $D.getElementsByClassName("search-item-input-group",null,this.elBase)[0];
            
        },
		/**
         * Selects an item (trip) in the sidebar with the given item name (object)
         */
        selectItem: function(objectName){
            /*Retaining the current scope*/
           $D.getElementsByClassName("grp-sel-item", null, this.elBase, function(target){
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
        onFocus: function(){
        		$D.getElementsByClassName("grp-sel-item",null,this.elBase,function(target){
        		if($D.hasClass(target,"disabled")){
        			$D.removeClass(target,"disabled");
        		}
        	});
        },

        /*Defining utilities*/
        groupComparator: function(el1, el2) {
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
