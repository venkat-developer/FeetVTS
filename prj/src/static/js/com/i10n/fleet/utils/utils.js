(function() {
    var $B = YAHOO.Bubbling;
    var $L = YAHOO.lang;
    var $YU = YAHOO.util;
    var $E = YAHOO.util.Event;
    var $D = YAHOO.util.Dom;
    var $YW = YAHOO.widget;
    var $U = getPackageForName("com.i10n.fleet.Utils");
    /**
     * Processes templatized markup to obtain a detemplatized markup based on the map given.
     * Support only single level detemplatizion of maps
     * @param {Object} sTemplate
     * @param {Object} oMap
     */
    $U.processTemplate = function(sTemplate, oMap) {
        var sResult = sTemplate;
        for (var sKey in oMap) {
            var sValue = oMap[sKey];
            var oExp = new RegExp("{" + sKey + "}", "g");
            sResult = sResult.replace(oExp, sValue);
        }
        return sResult;
    };
    /**
     * Clones the object to give a new Object. To be used judiciously
     * Does not do a deep cloning. Does only a shallow cloning
     */
    $U.cloneObject = function(oCloneable) {
        var oClonedObj = {};
        for (var key in oCloneable) {
            oClonedObj[key] = oCloneable[key];
        }
        return oClonedObj;
    };
    /**
     * Since putting innerHTML = "" has issues accross browser's
     * this utility will help in emptying an element.
     * @param {Object} el
     */
    $U.removeChildNodes = function(el) {
        while (el.hasChildNodes()) {
            el.removeChild(el.firstChild);
        }
    };
    /**
     * Opens a Popup will the specified title, body's innerHTML, stysheet/script elements and options
     * @param {Object} sTitle
     * @param {Object} sBody
     * @param {Object} aStyleSheets
     * @param {Object} aScripts
     * @param {Object} sOptions
     */
    $U.openPrintPreviewPopup = function(sTitle, sBody, aStyleSheets, aScripts, sOptions) {
        var popup = window.open("", "popup", sOptions);
        var doc = popup.document;
        try {
            doc.write("<html><head><title>" + sTitle + "</title>");
            if ($L.isArray(aStyleSheets)) {
                for (var i = 0; i < aStyleSheets.length; i++) {
                    if (aStyleSheets[i].indexOf("inline:") === 0) {
                        doc.write("<style type='text/css'>" + aStyleSheets[i].substring(7) + "</style>");
                    }
                    else {
                        doc.write("<link href='" + aStyleSheets[i] + "' type='text/css' rel='stylesheet'/>");
                    }
                }
            }
            doc.write("</head><body class='print-body'>");
            doc.write("<div class='print popup'>" + sBody + "</div><div class='overlay'></div></body>");
            if ($L.isArray(aScripts)) {
                for (var j = 0; j < aScripts.length; j++) {
                    if (aScripts[j].indexOf("inline:") === 0) {
                        doc.write("<script>" + aScripts[j].substring(7) + "</script>");
                    }
                    else {
                        doc.write("<script src='" + aScripts[j] + "'></script>");
                    }
                }
            }
            doc.write("</html>");
        } 
        catch (e) {
            /**
             * TODO : Handling Error Condition
             */
        }
        doc.close();
    };
    /**
     * Shows a loading overlay when a page is loaded.
     */
    $U.showLayerOverlay = function() {
        var elOverlay = $D.get("overlay");
        if (!$U._nLyrOverlayCount) {
            $U._nLyrOverlayCount = 0;
        }
        $U._nLyrOverlayCount++;
        if ("block" !== $D.getStyle(elOverlay, "display")) {
            $D.setStyle(elOverlay, "display", "block");
        }
    };
    /**
     * Hides the loading overlay.
     */
    $U.hideLayerOverlay = function() {
        var elOverlay = $D.get("overlay");
        if (!$U._nLyrOverlayCount) {
            $U._nLyrOverlayCount = 1;
        }
        $U._nLyrOverlayCount--;
        if ("none" !== $D.getStyle(elOverlay, "display") && $U._nLyrOverlayCount <= 0) {
            $U._nLyrOverlayCount = 0;
            $D.setStyle(elOverlay, "display", "none");
        }
    };
    /**
     * hides the overlay popups have opened.
     */
    $U.hidePopupOverlay = function() {
        var elOverlay = $D.get("overlay");
        if ($D.hasClass(elOverlay, "popup")) {
            $D.removeClass(elOverlay, "popup");
        }
        if ("none" !== $D.getStyle(elOverlay, "display")) {
            $D.setStyle(elOverlay, "display", "none");
        }
    };
    /**
     * Shows overlay for popups
     */
    $U.showPopupOverlay = function() {
        var elOverlay = $D.get("overlay");
        if (!$D.hasClass(elOverlay, "popup")) {
            $D.addClass(elOverlay, "popup");
        }
        if ("block" !== $D.getStyle(elOverlay, "display")) {
            $D.setStyle(elOverlay, "display", "block");
        }
    };
    /**
     * Array Utilities
     */
    $U.Arrays = {};
    /**
     * Checks whether the object obj is present in the array.
     * @param {Object} arr
     * @param {Object} obj
     */
    $U.Arrays.contains = function(arr, obj) {
        var result = false;
        for (var i = 0; i < arr.length; i++) {
            if (arr[i] === obj) {
                result = true;
                break;
            }
        }
        return result;
    };
    /**
     * Utility function to remove an object from the array
     * @param {Object} arr
     * @param {Object} obj
     */
    $U.Arrays.remove = function(arr, obj) {
        for (var index = 0; index < arr.length; index++) {
            if (arr[index] === obj) {
                arr.splice(index, 1);
            }
        }
    };
    /**
     *
     * @param {Object} oData
     * @param {Object} oFilter
     * @param {Object} bPreserveKey should the key be preserved
     */
    $U.Arrays.mapToArray = function(oData, oFilter, bPreserveKey) {
        var aData = [];
        if (!bPreserveKey) {
            bPreserveKey = false;
        }
        if (oData && $L.isObject(oData)) {
            for (var id in oData) {
                if (oData[id]) {
                    if (oFilter && oFilter.content) {
                        var isMember = true;
                        for (var key in oFilter.content) {
                            if (oFilter.content[key] || oData[id][key]) {
                                if (oFilter.content[key] != oData[id][key]) {
                                    isMember = false;
                                    break;
                                }
                            }
                        }
                        if (isMember) {
                            if (!bPreserveKey) {
                                aData.push(oData[id]);
                            }
                            else {
                                var tempFiltered = {};
                                tempFiltered.id = oData[id];
                                aData.push(tempFiltered);
                            }
                        }
                    }
                    else {
                        if (!bPreserveKey) {
                            aData.push(oData[id]);
                        }
                        else {
                            var tempUnFiltered = {};
                            tempUnFiltered.id = oData[id];
                            aData.push(tempUnFiltered);
                        }
                    }
                }
            }
        }
        return aData;
    };
    /**
     * Holds the utility functions for sorted arrays.
     *
     * Every utility function must have a comparator object
     * as an argument. This comparator function must model
     * the criteria upon which the array is sorted
     *
     * IMPORTANT: ALL THE FUNCTIONS ASSUME THAT THE INPUT ARRAY IS SORTED ACCORDING
     *            TO THE COMPARATOR!!!!
     */
    $U.Arrays.SortedArrays = {};
    /**
     * Finds the index in the sorted array where the new key will be inserted
     * based upon the comparator.
     *
     * It is NOT the responsibility of this function to insert the key at the
     * insertion position.
     *
     * @param {Object} aInput The SORTED array
     * @param {Function} fComparator The criteria upon which aInput is sorted
     * @param {Object} oKey The new object for the insertion index must be found
     * @param {Object} iStartIndex The starting index for the search. Inclusive. Defaults to 0
     * @param {Object} iEndIndex The ending index of the search. Inclusive. Defaults to (length of aInput)-1
     *
     *
     * @return the index where the new element should be inserted
     */
    $U.Arrays.SortedArrays.getInsertionPosition = function(aInput, fComparator, oKey, iStartIndex, iEndIndex) {
        /*Setting defaults*/
        if (!iStartIndex) {
            iStartIndex = 0;
        }
        if (!iEndIndex) {
            iEndIndex = aInput.length - 1;
        }
        /*Performing the search*/
        if (fComparator(oKey, aInput[iStartIndex]) <= 0) {
            return iStartIndex;
        }
        else if (fComparator(oKey, aInput[iEndIndex]) >= 0) {
            return iEndIndex + 1;
        }
        else {
            var iMiddleIndex = Math.floor((iStartIndex + iEndIndex) / 2);
            if (fComparator(oKey, aInput[iMiddleIndex]) === 0) {
                return iMiddleIndex;
            }
            else if (fComparator(oKey, aInput[iMiddleIndex]) == -1) {
                return $U.Arrays.SortedArrays.getInsertionPosition(aInput, fComparator, oKey, iStartIndex, iMiddleIndex);
            }
            else {
                return $U.Arrays.SortedArrays.getInsertionPosition(aInput, fComparator, oKey, iMiddleIndex + 1, iEndIndex);
            }
        }
    };
    /**
     * Used for putting one listener for the whole parent and then applying the
     * the proper handler based on the target of the event.
     * @param {Object} el
     * @param {Object} oArgs
     */
    $U.DOMEventHandler = function(el, oArgs) {
        if ($L.isArray(el)) {
            this.aElBase = el;
        }
        else {
            this.aElBase = [el];
        }
        this.oInitArgs = oArgs;
        $E.addListener(el, oArgs.type, this._handleEvent, this, true);
    };
    $L.augmentObject($U.DOMEventHandler.prototype, {
        _oEvtConfig: {},
        _nextId: 0,
        aElBase: null,
        oInitArgs: null,
        /**
         * Adds listener supposed to handle vent on el to the list.
         * @param {Object} el
         * @param {Object} fHandler
         * @param {Object} oArgs
         * @param {Object} oContext
         */
        addListener: function(el, fHandler, oArgs, oContext) {
            var aElems = [];
            if ($L.isArray(el)) {
                aElems = el;
            }
            if ($L.isString(el)) {
                aElems.push($D.get(el));
            }
            for (var i = 0; i < aElems.length; i++) {
                var elItem = aElems[i];
                var aConfigEl = this._oEvtConfig[this._getIdForElement(elItem)];
                if (!aConfigEl) {
                    aConfigEl = [];
                }
                aConfigEl.push({
                    el: elItem,
                    handler: fHandler,
                    args: oArgs,
                    context: oContext
                });
                this._oEvtConfig[this._getIdForElement(elItem)] = aConfigEl;
            }
        },
        /**
         * Removes the listeners attached to the element el.
         * @param {Object} el
         * @param {Object} fHandler
         */
        removeListener: function(el, fHandler) {
            if (el) {
                var sId = this._getIdForElement(el);
                if (this._oEvtConfig[sId]) {
                    var aConfig = this._oEvtConfig[sId];
                    for (var i = 0; i < aConfig.length; i++) {
                        if (aConfig[i].el === el) {
                            if (!(fHandler && aConfig[i].handler !== fHandler)) {
                                aConfig[i] = null;
                            }
                        }
                    }
                }
            }
        },
        /**
         * removes all the listeners.
         */
        purge: function() {
            this._oEvtConfig = {};
            this.stopListening();
        },
        /**
         * Stops listening to event on the base element.
         */
        stopListening: function() {
            $E.removeListener(this.elBase, this.oInitArgs.type, this._handleEvent);
        },
        /**
         * handler function for event in any of the child of the form element
         * @param {Object} oArgs
         * @param {Object} oSelf
         */
        _handleEvent: function(oArgs, oSelf) {
            var elTarget = $E.getTarget(oArgs);
            while (elTarget && !$U.Arrays.contains(this.aElBase, elTarget)) {
                var sId = this._getIdForElement(elTarget);
                if (this._oEvtConfig[sId]) {
                    var aElConfigs = this._oEvtConfig[sId];
                    for (var i = 0; i < aElConfigs.length; i++) {
                        if (elTarget === aElConfigs[i].el) {
                            var oContext = aElConfigs[i].context;
                            if ($L.isBoolean(oContext)) {
                                oContext = aElConfigs[i].args;
                            }
                            aElConfigs[i].handler.call(oContext, oArgs, aElConfigs[i].args);
                            if (oArgs.cancelBubble) {
                                break;
                            }
                        }
                    }
                }
                if (oArgs.cancelBubble) {
                    break;
                }
                elTarget = elTarget.parentNode;
            }
        },
        _getIdForElement: function(elId) {
            var sId = "~";
            var el = elId;
            if ($L.isString(elId)) {
                el = $D.get(elId);
            }
            if (el) {
                if (el.id) {
                    sId = sId + el.id;
                }
                if (el.className) {
                    sId = sId + el.className;
                }
            }
            return sId;
        }
    });
    /**
     * 1. Utility to handle the DOMEvents using the Bubbling pattern.
     * 2. This is a supplementary to the $U.DOMEventHandler utility.
     * 3. This is advantageous compared to $U.DOMEventHandler in that this uses only
     *    class names as opposed to the Element references used by $U.DOMEventHandle
     * @param {Object} el - The Element which acts as the new behaviour layer
     * @param {String} type - The type of events to which this layer responds to
     * @param {Object} oArgs - Optional additional arguments
     * @author N.Balaji
     */
    $U.DOMEventManager = function(el, sType, oArgs) {
        this.initDOMEventManager(el, sType, oArgs);
    };
    $L.augmentObject($U.DOMEventManager.prototype, {
        /**
         * The initialization function
         */
        initDOMEventManager: function(el, sType, oArgs) {
            /*Validating the arguments*/
            if (!$L.isString(sType)) {
                throw new Error("Invalid argument passed for the type argument");
            }
            this._oClassToHandlerMap = {};
            this._elBehaviorLayer = el;
            /*Installing event listener*/
            $E.addListener(el, sType, this._eventHandler, null, this);
        },
        /*Defining properties*/
        _elBehaviorLayer: null,
        _oClassToHandlerMap: null,
        /*Defining Methods*/
        /**
         * This functions informs the EventManager how an element with a
         * particular class will behave to a certain kind of event
         *
         * A "Behavior" is considered to be a set which consists of
         *   1. The handler
         *   2. The argument that is to be passed to the handler - defaults to null
         *   3. The scope of execution - defaults to window
         * @param {Object} className
         * @param {Object} fnHandler
         * @param {Object} argumentObject
         * @param {Object} scopeOfExecution
         */
        addBehavior: function(className, fnHandler, argumentObject, scopeOfExecution) {
            /*Defining defaults*/
            if (!$L.isObject(scopeOfExecution)) {
                scopeOfExecution = window;
            }
            var behavior = {
                callBack: fnHandler,
                argument: argumentObject,
                scope: scopeOfExecution
            };
            /*Augmenting this behavior to the previously defined behaviors*/
            var listOfBehaviors;
            if (!$L.isArray(this._oClassToHandlerMap[className])) {
                this._oClassToHandlerMap[className] = new Array();
            }
            listOfBehaviors = this._oClassToHandlerMap[className];
            listOfBehaviors.push(behavior);
        },
        /**
         * Removes a behavior of a class of elements by matching
         * 1. The handler funtion
         * 2. The Scope of execution
         * If any of the above two parameters  are not specified, they will be ignored
         * @param {Object} className
         * @param {Object} fnHandler
         * @param {Object} scopeOfExecution
         */
        removeBehavior: function(className, fnHandler, scopeOfExecution) {
            /*Removing the behavior if it exists*/
            if ($L.isArray(this._oClassToHandlerMap[className])) {
                var listOfBehaviors = this._oClassToHandlerMap[className];
                for (var index = 0; index < listOfBehaviors.length; index++) {
                    var behavior = listOfBehaviors[index];
                    var targetHandler = fnHandler;
                    var targetScope = scopeOfExecution;
                    /*Implementing the removeBy scenarios*/
                    if (!$L.isFunction(targetHandler)) {
                        /*If a specific handler is not specified, don't bother about handlers*/
                        targetHandler = behavior.callBack;
                    }
                    if (!$L.isObject(targetScope)) {
                        /*If a specific scope is not specified, don't bother about scopes*/
                        targetScope = behavior.scope;
                    }
                    if (behavior.callBack == targetHandler && behavior.scope == targetScope) {
                        listOfBehaviors.splice(index, 1);
                        index--;
                    }
                }
            }
        },
        /**
         * Destroys the behavior layer
         */
        destroy: function() {
            $E.removeListener(this._elBehaviorLayer, this._eventHandler);
            /*Preventing memory leaks*/
            this._elBehaviorLayer = null;
            this._oClassToHandlerMap = null;
        },
        /*Defining Callbacks*/
        /**
         * The function that is called when the respective event gets
         * bubbled up to the behavior layer
         *
         * This function will replicate the bubbling up but it will be using
         * classes for matching the elements instead of the elements themselves
         * @param {Object} event
         */
        _eventHandler: function(event) {
            /*Implementing the bubbling up logic*/
            var elementUnderFocus = $E.getTarget(event);
            var contextObj = {
                consumed: false
            };
            contextObj.target = $E.getTarget(event);
            /*Listing over all the classes available*/
            for (var className in this._oClassToHandlerMap) {
                /*Verifying whether the target or any of its ancestors has the class*/
                var flag = false;
                var ancestorOfTarget = null;
                if ($D.hasClass(elementUnderFocus, className)) {
                    flag = true;
                }
                else {
                    ancestorOfTarget = $D.getAncestorByClassName(elementUnderFocus, className);
                }
                if (flag || (ancestorOfTarget && !$D.isAncestor(ancestorOfTarget, this._elBehaviorLayer))) {
                    /*Firing the corresponding behaviors*/
                    var listOfBehaviors = this._oClassToHandlerMap[className];
                    for (var i = 0; i < listOfBehaviors.length; i++) {
                        var behavior = listOfBehaviors[i];
                        var args = new Array();
                        args.push(behavior.argument);
                        args.push(contextObj);
                        behavior.callBack.call(behavior.scope, event, args);
                        args = null;
                    }
                }
                ancestorOfTarget = null;
                flag = null;
            }
        }
    });
    /**
     *
     * @param {Object} el
     * @param {Object} sDefaultText
     */
    $U.addDefaultInputText = function(el, sDefaultText) {
        $E.addFocusListener(el, function(oArgs) {
            var elInput = $E.getTarget(oArgs);
            if (sDefaultText === elInput.value) {
                elInput.value = "";
            }
        });
        $E.addBlurListener(el, function(oArgs) {
            var elInput = $E.getTarget(oArgs);
            if ("" === elInput.value) {
                elInput.value = sDefaultText;
            }
        });
        el = null;
    };
    $U.Forms = {};
    /**
     * Returns a Map of form element's name and value.
     * Searches all the children of passed param element el for class
     * input-element and creates a map of such element's name and value
     * @param {Object} el
     */
    $U.Forms.getFormValue = function(el) {
    	var aInputs ;
    	var aResult = {};
    	var imeiList = YAHOO.util.Dom.getElementsByClassName("input-element list")[0];
    	if(YAHOO.util.Dom.hasClass(imeiList ,"disabled")){
    		aInputs= $D.getElementsByClassName("input-element txt", null, el);
    		for (var i = 0; i < aInputs.length; i++) {
                var elInput = aInputs[i];
                var sTagName = elInput.type;
                var fHandler = $U.Forms.getFormValue.handlers["defaultedit"];
                if ($U.Forms.getFormValue.handlers[sTagName]) {
                    fHandler = $U.Forms.getFormValue.handlers[sTagName];
                }
                fHandler.call(this, elInput, aResult);
            }
    	}else {
    		aInputs= $D.getElementsByClassName("input-element", null, el);
    		
    		for (var i = 0; i < aInputs.length; i++) {
    			var elInput = aInputs[i];
    			var sTagName = elInput.type;
    			var fHandler = $U.Forms.getFormValue.handlers["default"];
    			if ($U.Forms.getFormValue.handlers[sTagName]) {
    				fHandler = $U.Forms.getFormValue.handlers[sTagName];
    			}
    			fHandler.call(this, elInput, aResult);
    		}
    	}
        return aResult;
    };
    /**
     * Map of handlers for various form elements.
     */
    $U.Forms.getFormValue.handlers = {
        "checkbox": function(el, oMap) {
            oMap[el.name] = el.checked;
        },
        "radio": function(el, oMap) {
            if (el.checked) {
                oMap[el.name] = el.value;
            }
        },
        "default": function(el, oMap) {
            oMap[el.name] = el.value;
        },
        "defaultedit": function(el, oMap) {
        	if(el.name == "imeiid"){
        		oMap["imei"] = el.value;	
        	}else{
        		oMap[el.name] = el.value;
        	}
        }
    };
    /**
     * Accepts a map and sets the elements in the supplied form to specific values.
     * The elements should have a class named input-element
     * Sets the value by name and tag
     * @param {Object} oMap The map of input
     * @param {Object} el
     */
    $U.Forms.setFormValue = function(el, oMap) {
        var aInputs = $D.getElementsByClassName("input-element", null, el);
        
        for (var i = 0; i < aInputs.length; i++) {
            var elInput = aInputs[i];
            
            var sName = $D.getAttribute(elInput, "name");
            
            var sValue = oMap[sName];
            
            /*Is there a value associated with this element?*/
            if (sValue) {
                var sTagName = elInput.type;
                
                if ($U.Forms.setFormValue.handlers[sTagName]) {
                    $U.Forms.setFormValue.handlers[sTagName].call(this, elInput, sValue);
                    
                }
            }
        }
    };
    /**
     * Map of handlers for various form elements.
     */
    $U.Forms.setFormValue.handlers = {
        "checkbox": function(el, sValue) {
            if (sValue === "true") {
                el.checked = true;
            }
            else if (sValue === "false") {
                el.checked = false;
            }
        },
        "text": function(el, sValue) {
            el.value = sValue;
        },
        "password": function(el, sValue) {
            el.value = sValue;
        },
        "select-one": function(el, sValue) {
            /*
             * Getting all the options
             */
            var options = $D.getElementsBy(function() {
                return true;
            }, "option", el);
            for (var i = 0; i < options.length; i++) {
                if (sValue.toLowerCase() === $D.getAttribute(options[i], "value").toLowerCase()) {
                    options[i].selected = true;
                }
            }
        },
        "radio": function(el, sValue) {
        	if(el.value == sValue){
        		el.checked = true ;
        	}
        	else {
        		el.checked = false;
        	}
        }
    };
    /**
     * Intended to be used with input elements that are not inside a form element
     *
     * For input elements inside a form element, use the reset method of the form object
     * @param {Object} el The form element
     */
    $U.Forms.refreshForm = function(el) {
        var aInputs = $D.getElementsByClassName("input-element", null, el);
        for (var i = 0; i < aInputs.length; i++) {
            var elInput = aInputs[i];
            var sTagName = elInput.type;
            var fHandler = $U.Forms.refreshForm.handlers["default"];
            if ($U.Forms.refreshForm.handlers[sTagName]) {
                fHandler = $U.Forms.refreshForm.handlers[sTagName];
            }
            fHandler.call(this, elInput);
        }
    };
    /**
     * Map of handlers for various form elements.
     */
    $U.Forms.refreshForm.handlers = {
        "checkbox": function(el) {
            el.checked = false;
        },
        "radio": function(el) {
            el.checked = false;
        },
        "select-one": function(el) {
            el.selectedIndex = 0;
        },
        "default": function(el) {
            el.value = "";
        }
    };
    /**
     * Object Utilities
     */
    $U.Objects = {};
    /**
     * checks the equality in terms of values between two values
     * @param {Object} obj1
     * @param {Object} obj2
     */
    $U.Objects.equals = function(obj1, obj2) {
        var bresult = true;
        if (obj1 && obj2) {
            if ((!$L.isObject(obj1)) && (!$L.isObject(obj2))) {
                bresult = obj1 === obj2;
            }
            else if ($L.isObject(obj1) && $L.isObject(obj2)) {
                var lenobj1 = $U.Objects.length(obj1);
                var lenobj2 = $U.Objects.length(obj2);
                if (lenobj1 == lenobj2) {
                    for (var key in obj1) {
                        if (!$U.Objects.equals(obj1[key], obj2[key])) {
                            bresult = false;
                            break;
                        }
                    }
                }
                else {
                    bresult = false;
                }
            }
            else {
                bresult = false;
            }
        }
        else {
            if ((obj1 !== null) || (obj2 !== null)) {
                bresult = false;
            }
        }
        return bresult;
    };
    /**
     * returns the length of the object
     * @param {Object} obj
     */
    $U.Objects.length = function(obj) {
        var count = 0;
        if (obj) {
            for (var keys in obj) {
                count++;
            }
        }
        return count;
    };
    /**
     * returns the current time
     */
    $U.getLocalTime = function(){
    	
    	var currTime;
    	 var d = new Date();
       	 currTime = $YU.Date.format(d,{format: "%m/%d/%Y  %H:%M:%S"},"en-US");
    	 return currTime;
    };

    /**
     *
     * Customized Connection Utils for Fleet
     */
    $U.Connect = {
        /**
         * Customized asyncRequest for Fleet checks whether a person has been logged out.
         * If yes it will redirect the page to login page. Uses YUI Connect internally
         * @param {Object} method
         * @param {Object} url
         * @param {Object} oCallback
         * @param {Object} postData
         */
        asyncRequest: function(method, url, oCallback, postData) {
            var oWrappedCallBack = {
                success: function(o) {
                    if (o.responseText.indexOf("@Page:Login@") >= 0) {
                        window.location = "@APP_CONTEXT@/view/login/";
                    }
                    else {
                    	
                        oCallback.success.call(this, o);
                    }
                    oCallback = null;
                }
            };
          
            $L.augmentObject(oWrappedCallBack, oCallback);
            $YU.Connect.asyncRequest(method, url, oWrappedCallBack, postData);
          
            postData = null;
            url = null;
            method = null;
        }
    };
    /**
     * A custom alert for fleet. Right now just shows the window.alert
     * But for a change in alerting mechanism we just need to change here.
     * Hence all the widgets must alert through this.
     * 
     * @param {Object} oArgs
     */
    $U.alert = function(oArgs) {
        window.alert(oArgs.message);
    };
    $U.Date = {};
    /**
     * A Utility for getting Date with milliseconds from 01/01/1970 04:00:00.
     * JS recognizes Milliseconds from 01/01/1970 00:00:00 and the input is from  01/01/1970 04:00:00
     * @param {Object} nMSec
     */
    $U.Date.getDate = function(nMSec) {
        if (!(nMSec instanceof Number)) {
            nMSec = parseInt(nMSec);
        }
        var nMilliSeconds = nMSec + 14400000; //
        var oDate = new Date();
        oDate.setTime(nMilliSeconds);
        return oDate;
    };

    $U.Date.getReportDate = function(nMSec) {
        if (!(nMSec instanceof Number)) {
            nMSec = parseInt(nMSec);
        }
        var nMilliSeconds = nMSec; //
        var oDate = new Date();
        oDate.setTime(nMilliSeconds);
        return oDate;
    };
    /**
     * The Sigleton manager of the EditableDiv utility widgets
     */
    $U.EditableDivBuilder = function() {
    };
    $L.augmentObject($U.EditableDivBuilder, {
        /**
         * The initialization Function
         */
        initEditableDiv: function() {
            $U.EditableDivBuilder.addListeners();
        },
        INPUT_ELEMENT_SPEED: "<input type='text' class='editablediv input' id='speed-edit' maxlength='10'/>",
        INPUT_ELEMENT_IDLE: "<input type='text' class='editablediv input' id='idle-edit' maxlength='10'/>",        
        SUBMIT_STATE: 0,
        EDIT_STATE: 1,
        SUBMIT_STATE_CLASS: "submit",
        EDIT_STATE_CLASS: "edit",
        /*Methods*/
        load: function() {
            $U.EditableDivBuilder.initEditableDiv();
        },
        /*Defining the handlers for editing Speed*/
        onTriggerSpeed: function(layer, args) {
            /*To be executed in the context of this object*/
            var triggerArea = args[1].target;
            var state;
            if ($D.hasClass(triggerArea, $U.EditableDivBuilder.SUBMIT_STATE_CLASS)) {
                state = $U.EditableDivBuilder.EDIT_STATE;
                $U.EditableDivBuilder.prepareEngineStateSpeed($D.getAncestorByClassName(args[1].target, 'editableDiv'), state);
            }
            else {
                /*Consider the default to be editstate*/
                state = $U.EditableDivBuilder.SUBMIT_STATE;
                $U.EditableDivBuilder.prepareEngineStateSpeed($D.getAncestorByClassName(args[1].target, 'editableDiv'), state);
            }
        },
        /*Defining the handlers for editing idle point limit*/
        onTriggerIdle: function(layer, args) {
            /*To be executed in the context of this object*/
            var triggerArea = args[1].target;
            var state;
            if ($D.hasClass(triggerArea, $U.EditableDivBuilder.SUBMIT_STATE_CLASS)) {
                state = $U.EditableDivBuilder.EDIT_STATE;
                $U.EditableDivBuilder.prepareEngineStateIdle($D.getAncestorByClassName(args[1].target, 'editableDiv'), state);
            }
            else {
                /*Consider the default to be editstate*/
                state = $U.EditableDivBuilder.SUBMIT_STATE;
                $U.EditableDivBuilder.prepareEngineStateIdle($D.getAncestorByClassName(args[1].target, 'editableDiv'), state);
            }
        },
        /*installing listeners*/
        addListeners: function() {
            $B.addDefaultAction("editableDiv-trigger-speed", $U.EditableDivBuilder.onTriggerSpeed, $U.EditableDivBuilder);
            $B.addDefaultAction("editableDiv-trigger-idle", $U.EditableDivBuilder.onTriggerIdle, $U.EditableDivBuilder);
        },
        /*Defining utility functions*/
        /**
         * Utility function to put the supplied Object into the supplied state
         *
         * @param {Object} targetEl The targetElement
         * @param {Object} triggerEl The triggerElement
         * @param {Object} state the supplied state
         */
        prepareEngineStateSpeed: function(elEditableDiv, state) {
            switch (state) {
                case $U.EditableDivBuilder.SUBMIT_STATE:
                    $D.removeClass($D.getElementsByClassName($U.EditableDivBuilder.SUBMIT_STATE_CLASS, null, elEditableDiv), "disabled");
                    $D.addClass($D.getElementsByClassName($U.EditableDivBuilder.EDIT_STATE_CLASS, null, elEditableDiv), "disabled");
                    break;
                case $U.EditableDivBuilder.EDIT_STATE:
                    $D.removeClass($D.getElementsByClassName($U.EditableDivBuilder.EDIT_STATE_CLASS, null, elEditableDiv), "disabled");
                    $D.addClass($D.getElementsByClassName($U.EditableDivBuilder.SUBMIT_STATE_CLASS, null, elEditableDiv), "disabled");
                    break;
                default:
                    throw new Error("Invalid state reqested");
            }
            /*Preparing the target element*/
            $U.EditableDivBuilder.prepareTargetElementSpeed($D.getElementsByClassName("editableDiv-target-speed", null, elEditableDiv)[0], state);
        },
        /*Defining utility functions*/
        /**
         * Utility function to put the supplied Object into the supplied state
         *
         * @param {Object} targetEl The targetElement
         * @param {Object} triggerEl The triggerElement
         * @param {Object} state the supplied state
         */
        prepareEngineStateIdle: function(elEditableDiv, state) {
            switch (state) {
                case $U.EditableDivBuilder.SUBMIT_STATE:
                    $D.removeClass($D.getElementsByClassName($U.EditableDivBuilder.SUBMIT_STATE_CLASS, null, elEditableDiv), "disabled");
                    $D.addClass($D.getElementsByClassName($U.EditableDivBuilder.EDIT_STATE_CLASS, null, elEditableDiv), "disabled");
                    break;
                case $U.EditableDivBuilder.EDIT_STATE:
                    $D.removeClass($D.getElementsByClassName($U.EditableDivBuilder.EDIT_STATE_CLASS, null, elEditableDiv), "disabled");
                    $D.addClass($D.getElementsByClassName($U.EditableDivBuilder.SUBMIT_STATE_CLASS, null, elEditableDiv), "disabled");
                    break;
                default:
                    throw new Error("Invalid state reqested");
            }
            /*Preparing the target element*/
            $U.EditableDivBuilder.prepareTargetElementIdle($D.getElementsByClassName("editableDiv-target-idle", null, elEditableDiv)[0], state);
        },
        /**
         * Utility function to prepare the target Element
         * @param {Object} targetEl the targetElement
         * @param {Object} state the state into which to put the target element
         */
        prepareTargetElementSpeed: function(targetEl, state) {
            switch (state) {
                case $U.EditableDivBuilder.SUBMIT_STATE:
                    var content = targetEl.innerHTML;
                    targetEl.innerHTML = $U.EditableDivBuilder.INPUT_ELEMENT_SPEED;
                    $D.getElementsByClassName("input", null, targetEl)[0].value = content;
                    break;
                case $U.EditableDivBuilder.EDIT_STATE:
                    if ($D.getElementsByClassName("input", null, targetEl)[0] !== null) {
                        var input = $D.getElementsByClassName("input", null, targetEl)[0].value;
                        targetEl.innerHTML = input;
                        this.oCallBacks.oEditSpeedCallBack.scope = this;
                        var node = YAHOO.util.Dom.getElementsByClassName('slist-item list-item-type item selected')[0];
                        var tripId = YAHOO.util.Dom.getAttribute(node,'item');
                        var speed = targetEl.innerHTML;
                        $U.Connect.asyncRequest('GET', "@APP_CONTEXT@/form/tripsettings/?command_type=trip_settings_edit_speed&tripId="+
                        		tripId+"&speed="+speed, this.oCallBacks.oEditSpeedCallBack,null);
                    }
                    break;
                default:
                    throw new Error("Invalid state reqested");
            }
        },
        /**
         * Utility function to prepare the target Element
         * @param {Object} targetEl the targetElement
         * @param {Object} state the state into which to put the target element
         */
        prepareTargetElementIdle: function(targetEl, state) {
            switch (state) {
                case $U.EditableDivBuilder.SUBMIT_STATE:
                    var content = targetEl.innerHTML;
                    targetEl.innerHTML = $U.EditableDivBuilder.INPUT_ELEMENT_IDLE;
                    $D.getElementsByClassName("input", null, targetEl)[0].value = content;
                    break;
                case $U.EditableDivBuilder.EDIT_STATE:
                    if ($D.getElementsByClassName("input", null, targetEl)[0] !== null) {
                        var input = $D.getElementsByClassName("input", null, targetEl)[0].value;
                        targetEl.innerHTML = input;
                        this.oCallBacks.oEditIdleCallBack.scope = this;
                        var node = YAHOO.util.Dom.getElementsByClassName('slist-item list-item-type item selected')[0];
                        var tripId = YAHOO.util.Dom.getAttribute(node,'item');
                        var idle = targetEl.innerHTML;
                        $U.Connect.asyncRequest('GET', "@APP_CONTEXT@/form/tripsettings/?command_type=trip_settings_edit_idle&tripId="+
                        		tripId+"&idle="+idle, this.oCallBacks.oEditIdleCallBack,null);
                    }
                    break;
                default:
                    throw new Error("Invalid state reqested");
            }
        },
        
        oCallBacks: {
            oEditSpeedCallBack: {
                success: function(o) {
        		},
        		failure: function(o) {
        		}
        	},
        	oEditIdleCallBack: {
        		success: function(o) {
    			},
    			failure: function(o) {
    			}
        	}
        }
    });
    $E.onDOMReady($U.EditableDivBuilder.load);
    /**
     * Convenience method to covert a map to URL params
     *
     * If an attribute has a array of values, a comma seperated list
     * will be generated
     * @param {Object} oMap
     */
    $U.MapToParams = function(oMap) {
        var sOutput = "";
        for (var attribute in oMap) {
            var aValues = $L.isArray(oMap[attribute]) ? oMap[attribute] : [oMap[attribute]];
            var sValue = "";
            for (var i = 0; i < aValues.length; i++) {
                sValue += "," + aValues[i];
            }
            sValue = sValue.substring(1);
    
            sOutput += "&" + attribute + "=" + sValue;
        }
        sOutput = sOutput.substring(1);
       
        return sOutput;
    };
    /**
     * Element utilities
     */
    $U.Element = {};
    /**
     * Utilities specific to the Div element
     */
    $U.Element.DivElement = {};
    /**
     * Uses $U.processTemplate and appends a new  element as a child of
     * the parent element
     * @param {Object} elParent The parent div to which the new element will be appended to
     * @param {Object} sTemplate The template string
     * @param {Object} oTemplateSubstitutions substitutions
     */
    $U.Element.DivElement.appendTemplatizedChild = function(elParent, sTemplate, oTemplateSubstitutions) {
        elParent.innerHTML += $U.processTemplate(sTemplate, oTemplateSubstitutions);
       
    };
    /**
     * Append multiple child elements to the parent div
     * @param {Object} elParent The parent div to which the new element will be appended to
     * @param {Object} sTemplate The template string
     * @param {Object} aTemplateSubstitutions array of substitutions. One for each child
     *
     * The number of children appended = the length of aTemplateSubstitutions
     */
    $U.Element.DivElement.appendTemplatizedChildren = function(elParent, sTemplate, aTemplateSubstitutions) {
        
    	
    	for (var i = 0; i <aTemplateSubstitutions.length; i++) {
            $U.Element.DivElement.appendTemplatizedChild(elParent, sTemplate, aTemplateSubstitutions[i]);
            
        }
    };
    /**
     * DOM utilities
     */
    $U.Dom = {};
    /**
     * 1.Over comes a problem with YUI YAHOO.Dom.hasClass
     *
     * when checking for elements through multiple class names
     *
     * 2. The problem is that the YUI hasClass utility expects that the multiple
     *
     * classes in the DOM be in the same order as they are given in the test string
     *
     *
     * @param {Object} elTarget
     * @param {Object} sClass
     */
    $U.Dom.hasClass = function(elTarget, sClass) {
        var aClassNames = sClass.split(" ");
        var result = true;
        for (var i = 0; i < aClassNames.length; i++) {
            result &= $D.hasClass(aClassNames[i]);
        }
        return result;
    };
})();
