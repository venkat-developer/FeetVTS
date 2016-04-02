
(function(){
    var $L = YAHOO.lang;
    var $E = YAHOO.util.Event;
    var $D = YAHOO.util.Dom;
    var $YW = YAHOO.widget;
    var $YU = YAHOO.util;
    var $U = getPackageForName("com.i10n.fleet.Utils");
    var $W = getPackageForName("com.i10n.fleet.widget.ui");
    var $V = getPackageForName("com.i10n.fleet.widget.view");
    /**
     * Manage Logs UI Widget
     * @author sabarish, irk
     */
    $W.LogManager = function(el, oArgs){
        this.elBase = el;
        this.init(oArgs);
    };
   
    $L.augmentObject($W.LogManager, {
        USERS: "users",
        IPS: "ips",
        LOGS: "logs"
    });
   
    $L.augmentObject($W.LogManager.prototype, {
        /**
         * Calendar object of the widget, used for selecting start & end dates
         */
        _calendar: null,
       
        /**
         * Dialog box holding the calendar object
         */
        _dialog: null,
       
        /**
         * Initialize the widget
         */
        init: function(oArgs){
            this.updateUsers();
            this.addCalander();
            this.addListeners();
          
        },
       
        /**
         * Add a calendar using which the user can select start & end dates for logs
         */
        addCalander: function(){
            var dialogEl = $D.getElementsByClassName('cal-dialog', null, this.elBase)[0];
            this._dialog = new YAHOO.widget.Dialog(dialogEl, {
                visible: false,
                draggable: false,
                close: true,
                modal: true,
                x: 650,
                y: 250
            });
            this._dialog.render();
            var calContainer = $D.getElementsByClassName('cal', null, this.elBase)[0];
            this._calendar = new $YW.Calendar(calContainer, {
                mindate: "01/01/2000",
                maxdate: "12/31/2030"
            });
            this._calendar.render();
            this._calendar.selectEvent.subscribe(this._onSelect, this, true);
            /**
             * Set initial end-date as tommorow, start-date as 1st of current month
             */
            var _date = $D.getElementsByClassName('sel-dt end', null, this.elBase)[0];
            var _mon = $D.getElementsByClassName('sel-mon end', null, this.elBase)[0];
            var _yr = $D.getElementsByClassName('sel-yr end', null, this.elBase)[0];
            var today = new Date();
            _date.selectedIndex = today.getDate();
            _mon.selectedIndex = today.getMonth();
            _yr.selectedIndex = today.getFullYear() - 2000;
            _yr = $D.getElementsByClassName('sel-yr st', null, this.elBase)[0];
            _yr.selectedIndex = today.getFullYear() - 2000;
            _mon = $D.getElementsByClassName('sel-mon st', null, this.elBase)[0];
            _mon.selectedIndex = today.getMonth();
           
        },
       
        /**
         * Event Handler function for selection of dates
         * Sets the date on the dropdown box based on the selected date in the cal
         */
        _onSelect: function(type, oArgs, obj){
            var dates = oArgs[0];
            var date = dates[0];
            var year = date[0], month = date[1], day = date[2];
            var selectedDate = $D.getElementsBy(function(target){
                return (($D.hasClass(target, 'selected') && $D.hasClass(target, 'cal-icon')));
            }, null, this.elBase)[0];
            var _date, _mon, _yr;
            if ($D.hasClass(selectedDate, 'st-dt')) {
                _date = $D.getElementsByClassName('sel-dt st', null, this.elBase)[0];
                _mon = $D.getElementsByClassName('sel-mon st', null, this.elBase)[0];
                _yr = $D.getElementsByClassName('sel-yr st', null, this.elBase)[0];
                _date.selectedIndex = day - 1;
                _mon.selectedIndex = month - 1;
                _yr.selectedIndex = year - 2000;
            }
            else if ($D.hasClass(selectedDate, 'end-dt')) {
                _date = $D.getElementsByClassName('sel-dt end', null, this.elBase)[0];
                _mon = $D.getElementsByClassName('sel-mon end', null, this.elBase)[0];
                _yr = $D.getElementsByClassName('sel-yr end', null, this.elBase)[0];
                _date.selectedIndex = day - 1;
                _mon.selectedIndex = month - 1;
                _yr.selectedIndex = year - 2000;
            }
        },
       
        /**
         * Add  listeners for the events
         */
        addListeners: function(){
            var oListBehaviorLayer = new $U.DOMEventManager($D.getElementsByClassName('frm-bd', null, this.elBase), 'click');
            oListBehaviorLayer.addBehavior('user', this.updateIPs, null, this);
            oListBehaviorLayer.addBehavior('ip', function(event, args){
                var target = $E.getTarget(event);
                var elChkbox;
                if ($D.hasClass(target, 'list-item')) {
                    elChkbox = $D.getChildrenBy(target, function(el){
                        return $D.hasClass(el, 'input-element');
                    })[0];
                }
                else if ($D.hasClass(target, 'ip-name')) {
                    elChkbox = $D.getPreviousSiblingBy(target, function(el){
                        return ($D.hasClass(el, 'input-element'));
                    });
                }
                if (elChkbox) {
                    elChkbox.checked ? elChkbox.checked = false : elChkbox.checked = true;
                }
            }, null, this);
            oListBehaviorLayer.addBehavior('cal-icon', function(event, args){
                var target = $E.getTarget(event);
                var prevTarget = $D.getElementsBy(function(el){
                    return (($D.hasClass(el, 'selected') && $D.hasClass(el, 'cal-icon')));
                }, null, this.elBase)[0];
                $D.removeClass(prevTarget, 'selected');
                $D.addClass(target, 'selected');
                this._dialog.show();
            }, null, this);
            $W.Buttons.addDefaultHandler($D.getElementsByClassName("show-but", null, this.elBase), this.updateLogs, null, this);
            $W.Buttons.addDefaultHandler($D.getElementsByClassName("dload-but", null, this.elBase), this.downloadLogs, null, this);
            
        },
       
        /**
         *  Handle download logs event
         */
        downloadLogs: function(oArgs){
       
        },
       
        /**
         * Function that sends an async request to update the User list
         */
        updateUsers: function(){
            var userListURL = "/fleet/view/controlpanel/?markup=adminmanage&subpage=logs&debug=true&module=/blocks/json&data=view";
            this.getData(userListURL, "users");
        },
        /**
         * Function that sends an async request to update IP list
         * @param {Object} oArgs, newly selected user
         */
        updateIPs: function(event, oArgs){
            var targetEl = $E.getTarget(event);
            var prevUser = $D.getElementsByClassName('selectedUser', null, this.elBase)[0];
            if (prevUser)
                $D.removeClass(prevUser, 'selectedUser');
            $D.addClass(targetEl, 'selectedUser');
            var userID = $D.getAttribute(targetEl, 'user');
            var IPsURL = "/fleet/view/controlpanel/?markup=adminmanage&subpage=logs&debug=true&module=/blocks/json&data=view&log=ips&userID=";
            IPsURL = IPsURL + userID;
            this.getData(IPsURL, "ips");
        },
       
        /**
         * Function that retrieves the logs
         */
        updateLogs: function(){
            var logsURL = "/fleet/view/controlpanel/?markup=adminmanage&subpage=logs&debug=true&module=/blocks/json&data=view&ip=logs&localTime="+$U.getLocalTime()+"&userID=";
            
            var userEl = $D.getElementsByClassName('selectedUser', null, this.elBase)[0];
            var userID = $D.getAttribute(userEl, 'user');
            var _date = $D.getElementsByClassName('sel-dt st', null, this.elBase)[0];
            var _mon = $D.getElementsByClassName('sel-mon st', null, this.elBase)[0];
            var _yr = $D.getElementsByClassName('sel-yr st', null, this.elBase)[0];
            var startDate = (_mon.selectedIndex + 1) + "/" + (_date.selectedIndex + 1) + "/" + (_yr.selectedIndex + 2000);
            _date = $D.getElementsByClassName('sel-dt end', null, this.elBase)[0];
            _mon = $D.getElementsByClassName('sel-mon end', null, this.elBase)[0];
            _yr = $D.getElementsByClassName('sel-yr end', null, this.elBase)[0];
            var endDate = (_mon.selectedIndex + 1) + "/" + (_date.selectedIndex + 1) + "/" + (_yr.selectedIndex + 2000);
            var ips = $D.getElementsByClassName('input-element', null, this.elBase);
            var ipaddr = "";
            if (ips) {
                for (var ip in ips) {
                    if (ips[ip].checked) {
                        ipaddr += ips[ip].value + '|';
                    }
                }
            }
            if (ipaddr) {
                ipaddr = ipaddr.substring(0, ipaddr.length - 1);
            }
            logsURL += userID + "&startdate=" + startDate + "&enddate=" + endDate + "&ipaddr=" + ipaddr;
            if (ipaddr) {
                this.getData(logsURL, "logs");
                
            }
        },
       
        /**
         * Generic function to get User/IP/Logs data
         * @param {Object} url
         * @param {Object} args
         */
        getData: function(url, args){
            var oCallback = {
                success: function(o){
                    $U.hideLayerOverlay();
                    try {
                        var data = JSON.parse(o.responseText);
                        if (args == $W.LogManager.USERS)
                             this.populateUsers(data);
                        else if (args == $W.LogManager.IPS)
                            this.populateIPs(data);
                        else if (args == $W.LogManager.LOGS)
                            this.populateLogs(data);
                    }
                    catch (ex) {
                        $U.alert({
                            message: "There is no data present for the selected date!"
                        });
                    }
                },
                failure: function(o){
                    $U.alert({
                        message: "There was a failure in loading the data !"
                    });
                },
                argument: args,
                scope: this
            };
            if (url) {
                $U.showLayerOverlay();
                $U.Connect.asyncRequest('GET', url, oCallback);
            }
        },
       
        /**
         * Data handling function for updating users
         */
        populateUsers: function(data){
            var userList = $D.getElementsByClassName('user-list', null, this.elBase)[0];
            var template = $D.getElementsByClassName('markup-template', null, this.elBase)[0];
            var oldUsers = $D.getElementsBy(function(target){
                return ($D.hasClass(target, 'user') && !($D.isAncestor(template, target)));
            }, null, this.elBase, function(target){
                userList.removeChild(target);
            });
            var users = data.adminmanage.logs;
            if (users) {
                for (var user in users) {
                    var elUserTemplate = $D.getElementsByClassName('markup-user-template', null, this.elBase)[0];
                    var sMarkupTemplate = $U.processTemplate(elUserTemplate.innerHTML, {
                        "user": user,
                        "firstName": users[user].firstname.replace(/\s/g, ""),
                        "lastName": users[user].lastname.replace(/\s/g, "")
                    });
                    userList.innerHTML += sMarkupTemplate;
                }
            }
        },
       
        /**
         * Data handling function for updating IPs
         */
        populateIPs: function(data){
            var timeOverlay = $D.getElementsByClassName('time-sel-overlay', null, this.elBase);
            $D.addClass(timeOverlay, 'disabled');
            var ipOverlay = $D.getElementsByClassName('ip-list-overlay', null, this.elBase);
            $D.addClass(ipOverlay, 'disabled');
            var logsOverlay = $D.getElementsByClassName('log-list-overlay', null, this.elBase);
            $D.removeClass(logsOverlay, 'disabled');
            var selectedUser = $D.getElementsByClassName('selectedUser', null, this.elBase)[0];
            var userID = $D.getAttribute(selectedUser, 'user');
            var IPList = $D.getElementsByClassName('ip-list', null, this.elBase)[0];
            var template = $D.getElementsByClassName('markup-template', null, this.elBase)[0];
            var oldIPs = $D.getElementsBy(function(target){
                return ($D.hasClass(target, 'ip') && !($D.isAncestor(template, target)));
            }, null, this.elBase, function(target){
                IPList.removeChild(target);
            });
            var logsTable = $D.getElementsByClassName('tlist', null, this.elBase)[0];
            for (var j = logsTable.rows.length - 1; j > 0; j--)
                logsTable.deleteRow(j);
            var ips = data.adminmanage.logs[userID].ips;
            if (ips) {
                for (var ip in ips) {
                    var elIPTemplate = $D.getElementsByClassName('markup-ip-template', null, this.elBase)[0];
                    var sMarkupTemplate = $U.processTemplate(elIPTemplate.innerHTML, {
                        "ip": ips[ip].ipaddr.replace(/\s/g, "")
                    });
                    IPList.innerHTML += sMarkupTemplate;
                }
            }
        },
       
        /**
         * Data handling function for updating Logs
         */
        populateLogs: function(data){
            var logsTable = $D.getElementsByClassName('tlist', null, this.elBase)[0];
            for (var j = logsTable.rows.length - 1; j > 0; j--) {
                logsTable.deleteRow(j);
            }
            var logsTableBody = $D.getFirstChild(logsTable);
            var logsOverlay = $D.getElementsByClassName('log-list-overlay', null, this.elBase);
            $D.addClass(logsOverlay, 'disabled');
            var selectedUser = $D.getElementsByClassName('selectedUser', null, this.elBase)[0];
            var userID = $D.getAttribute(selectedUser, 'user');
            var logsData = data.adminmanage.logs[userID].ips;
            for (var ips in logsData) {
                var ipaddr = logsData[ips].ipaddr;
                var logs = logsData[ips].logs;
                for (var i = 0; i < logs.length; i++) {
                    var date = new Date(parseInt(logs[i].date));
                    var action = logs[i].action;
                    var mins = date.getMinutes();
                    var hrs = date.getHours();
                    var ampm;
                    (hrs >= 12) ? ampm = 'pm' : ampm = 'am';
                    (hrs >= 12) ? hrs -= 12 : hrs;
                    var time = (hrs >= 10) ? hrs : '0' + hrs;
                    time += ' : ' + ((mins > 10) ? (mins) : ('0' + mins)) + ' ' + ampm;
                    var elLogsTemplate = $D.getElementsByClassName('logs-table-template', null, this.elBase)[0];
                    var newNode = elLogsTemplate.tBodies[0].rows[0].cloneNode(true);
                    newNode.children[0].innerHTML = (date.getDate() > 9 ? date.getDate() : '0' + date.getDate()) + "/" + (date.getMonth()+1) + "/" + date.getFullYear();
                    newNode.children[1].innerHTML = time;
                    newNode.children[2].innerHTML = action;
                    newNode.children[3].innerHTML = ipaddr;
                    logsTableBody.appendChild(newNode);
                }
            }
        }
    });
})();

