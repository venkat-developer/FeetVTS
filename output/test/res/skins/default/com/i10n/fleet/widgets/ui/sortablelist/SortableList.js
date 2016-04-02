( function() {
    var $B = YAHOO.Bubbling;
    var $L = YAHOO.lang;
    var $E = YAHOO.util.Event;
    var $D = YAHOO.util.Dom;
    var $YW = YAHOO.widget;
    var $W = getPackageForName("com.i10n.fleet.widget.util");

    $W.SortableList = function(el, params) {
        this.init = function(el, params) {

        }

        this.sort = function(elements, comparator, firstElement) {
            if (!$L.isObject(elements) && !$L.isArray(elements)) {
                elements = $D.getElementsByClassName("slist-item", null,
                        this.baseElement);
            }
            if (elements.length > 1) {
                if (!$L.isFunction(comparator)) {
                    comparator = this.comparator;
                }
                var parent = elements[0].parentNode;
                if (!$L.isObject(firstElement)) {
                    firstElement = $D.getFirstChildBy(parent, function(target) {
                        var result = false;
                        if ($D.hasClass(target, "slist-item")) {
                            result = true;
                        }
                        return result;
                    });
                }
                if ($L.isObject(firstElement)) {
                    elements.sort(comparator);
                    var firstElementEncountered = false;
                    var lastElement = null;
                    for ( var i = 0; i < elements.length; i++) {
                        var element = elements[i];
                        if (!firstElementEncountered) {
                            if (element !== firstElement) {
                                parent.removeChild(element);
                                $D.insertBefore(element, firstElement);
                            }
                            else {
                                lastElement = element;
                                firstElementEncountered = true;
                            }
                        }
                        else {
                            if (element.previousSibling !== lastElement) {
                                parent.removeChild(element);
                                $D.insertAfter(element, lastElement);
                            }
                            lastElement = element;
                        }
                    }
                }
            }
        }

        this.setComparator = function(comparator) {
            if ($L.isFunction(comparator)) {
                this.comparator = comparator;
            }
        }

        this.comparator = function(el1, el2) {
            var result = -1;
            if ($L.isObject(el1) && $L.isObject(el2)) {
                var attrEl1 = el1.getAttribute("value");
                var attrEl2 = el2.getAttribute("value");
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

        this.baseElement = el;
        this.initParams = params;

        this.list

        this.init(el, params);
    }

})();
