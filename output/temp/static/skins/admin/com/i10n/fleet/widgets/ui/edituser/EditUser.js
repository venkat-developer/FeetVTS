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
    $W.EditUser = function(el, oArgs) {
        $W.EditUser.superclass.constructor.call(this, el, oArgs);
		var oDataSrc=[];
		this.elTsSideContainer = $D.getElementsByClassName("user-sel",null,this.elBase)[0];
        this.initEditUser(el, oArgs);
    };
    $L.extend($W.EditUser, $W.EditEntity, {
        /*Defining necessary callbacks*/
        oSaveCallback: {
            success: function(o) {
                $D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Success:</span> User successfully edited.";
                $D.getElementsByClassName(this.oConfiguration.formClass, null, this.elBase)[0].reset();
                this.hideInputForm();
            },
            failure: function(o) {
                $D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Failure:</span> Editing user information failed.";
            }
        },
        oCallBacks: {
            oGetEntityCallBack: {
                success: function(o) {
                    var oData = JSON.parse(o.responseText);
                    if (!oData.adminmanage.usermanage.Error) {
                    	
                        this.processData(oData.adminmanage.usermanage);
                        this.processSpecificData(oData.adminmanage.usermanage);
                        $D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Please Note:</span> All the fields are mandatory.";
                        this.showEditSheet();
                    }
                    else if (oData.adminmanage.usermanage.Error.name == "ResourceNotFoundError") {
                        $D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Error:</span> The user does not exist. The list has been reloaded with available users";
                        this.reloadUserList(oData.adminmanage.usermanage.users);
                        this.showMessage();
                    }
                },
                failure: function(o) {
                    $D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Failure:</span> Failed to retrive user information.";
                }
            }
        },
        /*Over-riding necessary methods*/
        processSpecificData: function(oData) {
            var elForm = $D.getElementsByClassName("input-form", null, this.elBase)[0];
            $D.getElementsByClassName("input-element", null, this.elBase, function(target) {
                if ("passwd-confirm" === $D.getAttribute(target, "name")) {
                    target.value = oData.passwd;
                }
            });
            /*Preventing possible memory leaks*/
            elForm = null;
        },
        getEntity: function(oKey) {
            /*Correcting the scope of the call back*/
            this.oCallBacks.oGetEntityCallBack.scope = this;
            $U.Connect.asyncRequest("GET", "@APP_CONTEXT@/view/controlpanel/?markup=AdminManage&debug=true&module=/blocks/json&data=view&subpage=user&userID=" + oKey, this.oCallBacks.oGetEntityCallBack, null);
        },
        getSaveURL: function() {
            /*
             * TODO: Change this to the correct URL once the data providers are implemented
             */
            return "@APP_CONTEXT@/form/admin/";
        },
        
        /**
         * Function to validate details of the user being updated.
         */
		validateForm: function(oInput) {
			var firstName = oInput['firstname'];
			var lastName = oInput['lastname'];
			var password = oInput['passwd'];
			var confirm_password = oInput['passwd-confirm'];
			
			// Check for null entries for the mandatory fields.
			if(firstName == "" || lastName == "" || password == "" || confirm_password == ""){
				this.editUserPopUp("empty");
				return false;
			}
			
			// Check for Length validation.
			if(firstName.length > 20 || lastName.length > 20 || password != confirm_password){
				if(firstName.length > 20) {
					this.editUserPopUp("firstname");
				}
                if(lastName.length > 20){
                	this.editUserPopUp("lastname");
    			}
                // Check for mismatch of password and confirm password entries.
                if(password != confirm_password){
                	this.editUserPopUp("password");
    			}
                return false ;
			}
            return true;
        },
        
        /**
         * Function to provide a customized alert popup.
         */
        editUserPopUp : function (type) {
        	var popUpType = "edit-user-"+type+"-popup";
        	this.oPopUp = new $W.PopUp($D.getElementsByClassName(popUpType, null, this.elBase,function(elTarget){ 
				if($D.hasClass(elTarget,"disabled")){
					$D.removeClass(elTarget,"disabled");
				}
			})[0]);
			this.oPopUp.render();
        	this.oPopUp.show();
        }
    }, true);
    $L.augmentObject($W.EditUser.prototype, {
        /**
         * The initialization function
         * @param {Object} el The base element
         * @param {Object} oArgs The optional configuration params
         */
        initEditUser: function(el, oArgs) {
		
            this.elBase = el;
            this.userEventManager = new $U.DOMEventHandler(this.elBase, {
                type: "click"
            });
            this.sortUserList();
            this.addListeners();
			this.generateDataSourceForEditUser();
			 this.setAutoCompleteForEditUser();
        },
        /*Declaring the data members*/
        elBase: null,
        /*Defining methods*/
		DEFAULT_SEARCH_STRING: "Search...",
		
        reloadUserList: function(oList) {
            var elList = $D.getElementsByClassName("user-sel", null, this.elBase)[0];
            /*Removing all the available contents*/
            $D.getElementsByClassName("user-sel-item", null, elList, function(elTarget) {
                elTarget.parentNode.removeChild(elTarget);
            });
            /*Writing new Data*/
            var sTemplate = $D.getElementsByClassName("template-user-list-item", null, this.elBase)[0].innerHTML;
            $U.Element.DivElement.appendTemplatizedChildren(elList, sTemplate, $U.Arrays.mapToArray(oList));
            this.sortUserList();
        },
        sortUserList: function() {
            var elList = new $WU.SortableList($D.getElementsByClassName("user-sel", null, this.elBase)[0]);
            elList.setComparator(this.userComparator);
            elList.sort();
            /*Preventing possible memory leaks*/
            elList = null;
        },
		  /**
         * Generates Datasource for autocomplete
         */
        generateDataSourceForEditUser: function(){
        	  var dataSrc = [];
            $D.getElementsByClassName("user-sel-item", null, this.elTsSideContainer, function(target){
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
		  setAutoCompleteForEditUser: function(){
            var autoCompleteUser = new $YW.AutoComplete(this.searchInputString, $D.getElementsByClassName("search-autocomplete-user", null, this.elBase)[0], this.oDataSrc);
            autoCompleteUser.prehighlightClassName = "yui-ac-prehighlight";
            autoCompleteUser.useShadow = true;
            this.editUserListener();
        },
		editUserListener: function(){        	
            this.searchInputString = $D.getElementsByClassName("search-item-input-user",null,this.elBase)[0];
            this.userEventManager.addListener($D.getElementsByClassName("search-go-button-user"), function(params){
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
        onUserSelected: function(event, args) {
            var elList = $D.getAncestorByClassName(args[1].target, "user-sel");
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
            var oListEventManager = new $U.DOMEventManager($D.getElementsByClassName("user-sel", null, this.elBase)[0], "click");
            oListEventManager.addBehavior("user-sel-item", this.onUserSelected, null, this);
            /*
             * Preventing possible memory leaks
             */
            oListEventManager = null;
		  this.searchInputString = $D.getElementsByClassName("search-item-input-user",null,this.elBase)[0];

        },
		/**
         * Selects an item (trip) in the sidebar with the given item name (object)
         */
        selectItem: function(objectName){
            /*Retaining the current scope*/

           $D.getElementsByClassName("user-sel-item", null, this.elBase, function(target){
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
        userComparator: function(el1, el2) {
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
