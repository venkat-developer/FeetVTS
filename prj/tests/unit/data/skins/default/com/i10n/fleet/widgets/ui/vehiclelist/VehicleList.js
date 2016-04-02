( function() {
    var $B = YAHOO.Bubbling;
    var $L = YAHOO.lang;
    var $YU = YAHOO.util;
    var $E = YAHOO.util.Event;
    var $D = YAHOO.util.Dom;
    var $YW = YAHOO.widget;
    var $W = getPackageForName("com.i10n.fleet.widget.ui");
    var $WU = getPackageForName("com.i10n.fleet.widget.util");

    $W.VehicleList = function(el, params) {
        this.init = function(el, params) {
            if ($D.hasClass(el, "group-disabled")) {
                this.isGrouped = false;
            }
            else {
                this.isGrouped = true;
            }
            if ($D.hasClass(el, "pinning-disabled")) {
                this.pinningEnabled = false;
            }
            else {
                this.pinningEnabled = true;
            }
            $E.addListener($D.getElementsByClassName('vehicle-group-item',
                    null, el), "click", function(params) {
                var target = params.currentTarget;
                if ($L.isObject(target)) {
                    var group = target.getAttribute("value");
                    this.selectGroup(group);
                }
            }, null, this);
            $E.addListener($D.getElementsByClassName('list-group', null, el),
                    "click", function(params) {
                        this.toggleGrouping();
                    }, null, this);
            $E.addListener($D.getElementsByClassName('pin', null, el), "click",
                    function(params) {
                        var target = params.currentTarget;
                        if ($L.isObject(target)) {
                            this.togglePin($D.getAncestorByClassName(target,
                                    "vehicle-item"));
                        }
                    }, null, this);
            $E.addListener($D.getElementsByClassName('list-pin', null, el),
                    "click", function(params) {
                        this.togglePinning();
                    }, null, this);
            this.generateDataSource();
            this.setAutoComplete();
        }

        this.setAutoComplete = function() {
            var autoComplete = new $YW.AutoComplete($D
                    .get("search-vehicle-string"), $D.getElementsByClassName(
                    "search-autocomplete", null, this.baseElement)[0],
                    this.dataSource);
            autoComplete.prehighlightClassName = "yui-ac-prehighlight";
            autoComplete.useShadow = true;
        }

        this.generateDataSource = function() {
            var dataSource = [];
            $D.getElementsByClassName("vehicle-item", null, this.baseElement,
                    function(target) {
                        var vehicleID = target.getAttribute("vehicle");
                        var vehicleName = target.getAttribute("vehiclename");
                        var data = {
                            "name" : vehicleName,
                            "id" : vehicleID
                        };
                        dataSource.push(data);
                    });
            this.dataSource = new $YU.LocalDataSource(dataSource);
            this.dataSource.responseSchema = {
                fields : [ "name" ]
            }
        }

        this.selectGroup = function(group) {
            if ($L.isString(group)) {
                $D.getElementsByClassName("list-item-type", null, el, function(
                        target) {
                    $D.addClass(target, "disabled");
                });
                var filterFunction = function(target) {
                    var result = false;
                    if (group === target.getAttribute("group")) {
                        result = true;
                    }
                    return result;
                }
                $D.getElementsBy(filterFunction, null, el, function(target) {
                    $D.removeClass(target, "disabled");
                });
            }
            else {
                $D.getElementsByClassName("list-item-type", null, el, function(
                        target) {
                    $D.removeClass(target, "disabled");
                });
            }
        }

        this.sort = function() {
            if (this.isGrouped) {
                this.sortByGroup();
            }
            else {
                this.sortByVehicle();
            }
        }

        this.sortByVehicle = function() {
            if (this.pinningEnabled) {
                this.sortableList.sort($D.getElementsByClassName(
                        "vehicle-item", null, this.baseElement),
                        $W.VehicleList.listPinComparator);
            }
            else {
                this.sortableList.sort($D.getElementsByClassName(
                        "vehicle-item", null, this.baseElement),
                        $W.VehicleList.listComparator);
            }
        }

        this.sortByGroup = function() {
            if (this.pinningEnabled) {
                this.sortableList.sort($D.getElementsByClassName(
                        "list-item-type", null, this.baseElement),
                        $W.VehicleList.groupPinComparator);
            }
            else {
                this.sortableList.sort($D.getElementsByClassName(
                        "list-item-type", null, this.baseElement),
                        $W.VehicleList.groupComparator);
            }
        }
        this.togglePin = function(target) {
            if ($L.isObject(target)) {
                $D.hasClass(target, "pin-enabled") ? this._removePin(target)
                        : this._addPin(target);
                if (this.pinningEnabled) {
                    this.sort();
                }
            }
        }

        this._addPin = function(target) {
            $D.addClass(target, "pin-enabled");
        }

        this._removePin = function(target) {
            $D.removeClass(target, "pin-enabled");
        }

        this.togglePinning = function() {
            this.pinningEnabled ? this.disablePinning() : this.enablePinning();
        }

        this.disablePinning = function() {
            if (this.pinningEnabled) {
                this.pinningEnabled = false;
                $D.addClass(this.baseElement, "pinning-disabled");
                this.sort();
            }
        }

        this.enablePinning = function() {
            if (!this.pinningEnabled) {
                this.pinningEnabled = true;
                $D.removeClass(this.baseElement, "pinning-disabled");
                this.sort();
            }
        }

        this.toggleGrouping = function() {
            this.isGrouped ? this.disableGrouping() : this.enableGrouping();
        }

        this.disableGrouping = function() {
            if (this.isGrouped) {
                this.isGrouped = false;
                this.selectGroup();
                $D.addClass(this.baseElement, "group-disabled");
                this.sortByVehicle();
            }
        }

        this.enableGrouping = function() {
            if (!this.isGrouped) {
                this.isGrouped = true;
                $D.removeClass(this.baseElement, "group-disabled");
                this.sortByGroup();
            }
        }

        this.dataSource = null;
        this.baseElement = $D.getElementsByClassName("vehicle-list", null, el)[0];
        this.initParams = params;
        this.isGrouped = true;
        this.sortableList = new $WU.SortableList(el);
        this.pinningEnabled = false;
        this.init(this.baseElement, params);
        this.sortByGroup();

    }

    $W.VehicleList.listPinComparator = function(el1, el2) {
        var isEl1Pinned = $D.hasClass(el1, "pin-enabled");
        var isEl2Pinned = $D.hasClass(el2, "pin-enabled");
        var result = 0;
        if (isEl1Pinned === isEl2Pinned) {
            result = $W.VehicleList.listComparator(el1, el2);
        }
        else if (isEl1Pinned) {
            result = -1;
        }
        else if (isEl2Pinned) {
            result = 1;
        }
        return result;
    }

    $W.VehicleList.groupPinComparator = function(el1, el2) {
        var group1 = el1.getAttribute("group");
        var group2 = el2.getAttribute("group");
        var result = 0;
        if ($L.isString(group1) && $L.isString(group2)) {
            if (group1 === group2) {
                var isGroup1Head = $D.hasClass(el1, "group-item");
                var isGroup2Head = $D.hasClass(el2, "group-item");
                if (isGroup1Head && isGroup2Head) {
                    result = 0;
                }
                else if (isGroup1Head) {
                    result = -1;
                }
                else if (isGroup2Head) {
                    result = 1;
                }
                else if ($D.hasClass(el1, "vehicle-item")
                        && $D.hasClass(el2, "vehicle-item")) {
                    result = $W.VehicleList.listPinComparator(el1, el2)
                }
            }
            else if (group1 > group2) {
                result = 1;
            }
            else {
                result = -1;
            }

        }
        else if ($L.isString(group1)) {
            result = 1;
        }
        else if ($L.isString(group2)) {
            result = -1;
        }

        return result;
    }

    $W.VehicleList.groupComparator = function(el1, el2) {
        var group1 = el1.getAttribute("group");
        var group2 = el2.getAttribute("group");
        var result = 0;
        if ($L.isString(group1) && $L.isString(group2)) {
            if (group1 === group2) {
                var isGroup1Head = $D.hasClass(el1, "group-item");
                var isGroup2Head = $D.hasClass(el2, "group-item");
                if (isGroup1Head && isGroup2Head) {
                    result = 0;
                }
                else if (isGroup1Head) {
                    result = -1;
                }
                else if (isGroup2Head) {
                    result = 1;
                }
                else if ($D.hasClass(el1, "vehicle-item")
                        && $D.hasClass(el2, "vehicle-item")) {
                    result = $W.VehicleList.listComparator(el1, el2);
                }
            }
            else if (group1 > group2) {
                result = 1;
            }
            else {
                result = -1;
            }
        }
        else if ($L.isString(group1)) {
            result = 1;
        }
        else if ($L.isString(group2)) {
            result = -1;
        }

        return result;
    }

    $W.VehicleList.listComparator = function(el1, el2) {
        var vehicle1 = el1.getAttribute("vehicle");
        var vehicle2 = el2.getAttribute("vehicle");
        var result = 0;
        if ($L.isString(vehicle1) && $L.isString(vehicle2)) {
            if (vehicle1 === vehicle2) {
                result = 0;
            }
            else if (vehicle1 > vehicle2) {
                result = 1;
            }
            else {
                result = -1;
            }
        }
        else if ($L.isString(vehicle1)) {
            result = 1;
        }
        else if ($L.isString(vehicle2)) {
            result = -1;
        }

        return result;
    }
    var proto = $W.VehicleList.prototype;
})();
