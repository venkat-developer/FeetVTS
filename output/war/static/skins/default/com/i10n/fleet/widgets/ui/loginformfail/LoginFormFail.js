(function() {
    var $B = YAHOO.Bubbling;
    var $L = YAHOO.lang;
    var $YU = YAHOO.util;
    var $E = YAHOO.util.Event;
    var $D = YAHOO.util.Dom;
    var $YW = YAHOO.widget;
    var $W = getPackageForName("com.i10n.fleet.widget.ui");
    var $V = getPackageForName("com.i10n.fleet.widget.view");
    var $U = getPackageForName("com.i10n.fleet.Utils");

    /**
     * LoginFormFail Widget for Fleetcheck project
     *
     * @author sabarish
     */
    $W.LoginFormFail = function(el, params) {
        this.init = function(el, params) {
        	
            /**
             * Adding handler to the login button to submit the form
             */
            var loginButton = $D.getElementsByClassName("login-button", null,el);
            //local time calculation
            var localTime = $D.getElementsByClassName("localTime",null,el);
            var time = $U.getLocalTime();
            var element = new YAHOO.util.Element($D.get("login.localTime"));
            element.set('value',time);
            //local time zone calculation
            /*var currentTime = new Date();
            var currentTimezone = currentTime.getTimezoneOffset();
            
            currentTimezone = (currentTimezone/60) * -1;
            var gmt = 'GMT';
            if (currentTimezone !== 0) {
              gmt += currentTimezone > 0 ? '+' : ' ';
              gmt += currentTimezone;
            }*/
            var timezone = this.getTimezoneName();
           
            var element1 = new YAHOO.util.Element($D.get("login.localTimeZone"));
            element1.set('value',timezone);
            
            var userName = $D.get("login.user");
            var passWord = $D.get("login.password");
            var res=false;
            if ($L.isArray(loginButton)) {
                $W.Buttons.addDefaultHandler(loginButton, function() {
                	
                    var formElement = $D.get("login-form");
              
                    if ($L.isObject(formElement)) {
                      
                        if (userName.value == "" && passWord.value == "") {
                            var elSheet = $D.getElementsByClassName("user-message");
                            $D.removeClass(elSheet, "disabled");
                            var elSheet1 = $D.getElementsByClassName("password-message");
                            $D.removeClass(elSheet1, "disabled");
          
                        } 
                        else if (userName.value == "") {
                            var elSheet = $D.getElementsByClassName("user-message");
                            $D.removeClass(elSheet, "disabled");
                            var elSheet1=$D.getElementsByClassName("password-message");
                            $D.addClass(elSheet1,"disabled");
                        	
                        	
                        }  else if (passWord.value == "") {
                        	var elSheet1 = $D.getElementsByClassName("password-message");
                        	$D.removeClass(elSheet1, "disabled");
                        	 var elSheet = $D.getElementsByClassName("user-message");
                                 $D.addClass(elSheet, "disabled");
                        }else{
                        formElement.submit();}
                    }
                });
            }
            var enterKeyListener = new $YU.KeyListener(document, {
                "keys" : 13
            }, {
                "fn" : function() {
                    this.login();
                },
                "scope" : this,
                "correctScope" : true
            });
            enterKeyListener.enable();
            /**
             * Avoiding possible memory leaks.
             */
            loginButton = null;
           // this.getTimeUrl();
              
        },
        
        this.getTimezoneName = function() {
            tmSummer = new Date(Date.UTC(new Date().getFullYear(), 6, 30, 0, 0, 0, 0));
            so = -1 * tmSummer.getTimezoneOffset();
            tmWinter = new Date(Date.UTC(new Date().getFullYear(), 12, 30, 0, 0, 0, 0));
            wo = -1 * tmWinter.getTimezoneOffset();

            if (-660 == so && -660 == wo) return 'Pacific/Midway';
            if (-600 == so && -600 == wo) return 'Pacific/Tahiti';
            if (-570 == so && -570 == wo) return 'Pacific/Marquesas';
            if (-540 == so && -600 == wo) return 'America/Adak';
            if (-540 == so && -540 == wo) return 'Pacific/Gambier';
            if (-480 == so && -540 == wo) return 'US/Alaska';
            if (-480 == so && -480 == wo) return 'Pacific/Pitcairn';
            if (-420 == so && -480 == wo) return 'US/Pacific';
            if (-420 == so && -420 == wo) return 'US/Arizona';
            if (-360 == so && -420 == wo) return 'US/Mountain';
            if (-360 == so && -360 == wo) return 'America/Guatemala';
            if (-360 == so && -300 == wo) return 'Pacific/Easter';
            if (-300 == so && -360 == wo) return 'US/Central';
            if (-300 == so && -300 == wo) return 'America/Bogota';
            if (-240 == so && -300 == wo) return 'US/Eastern';
            if (-240 == so && -240 == wo) return 'America/Caracas';
            if (-240 == so && -180 == wo) return 'America/Santiago';
            if (-180 == so && -240 == wo) return 'Canada/Atlantic';
            if (-180 == so && -180 == wo) return 'America/Montevideo';
            if (-180 == so && -120 == wo) return 'America/Sao_Paulo';
            if (-150 == so && -210 == wo) return 'America/St_Johns';
            if (-120 == so && -180 == wo) return 'America/Godthab';
            if (-120 == so && -120 == wo) return 'America/Noronha';
            if (-60 == so && -60 == wo) return 'Atlantic/Cape_Verde';
            if (0 == so && -60 == wo) return 'Atlantic/Azores';
            if (0 == so && 0 == wo) return 'Africa/Casablanca';
            if (60 == so && 0 == wo) return 'Europe/London';
            if (60 == so && 60 == wo) return 'Africa/Algiers';
            if (60 == so && 120 == wo) return 'Africa/Windhoek';
            if (120 == so && 60 == wo) return 'Europe/Amsterdam';
            if (120 == so && 120 == wo) return 'Africa/Harare';
            if (180 == so && 120 == wo) return 'Europe/Athens';
            if (180 == so && 180 == wo) return 'Africa/Nairobi';
            if (240 == so && 180 == wo) return 'Europe/Moscow';
            if (240 == so && 240 == wo) return 'Asia/Dubai';
            if (270 == so && 210 == wo) return 'Asia/Tehran';
            if (270 == so && 270 == wo) return 'Asia/Kabul';
            if (300 == so && 240 == wo) return 'Asia/Baku';
            if (300 == so && 300 == wo) return 'Asia/Karachi';
            if (330 == so && 330 == wo) return 'Asia/Calcutta';
            if (345 == so && 345 == wo) return 'Asia/Katmandu';
            if (360 == so && 300 == wo) return 'Asia/Yekaterinburg';
            if (360 == so && 360 == wo) return 'Asia/Colombo';
            if (390 == so && 390 == wo) return 'Asia/Rangoon';
            if (420 == so && 360 == wo) return 'Asia/Almaty';
            if (420 == so && 420 == wo) return 'Asia/Bangkok';
            if (480 == so && 420 == wo) return 'Asia/Krasnoyarsk';
            if (480 == so && 480 == wo) return 'Australia/Perth';
            if (540 == so && 480 == wo) return 'Asia/Irkutsk';
            if (540 == so && 540 == wo) return 'Asia/Tokyo';
            if (570 == so && 570 == wo) return 'Australia/Darwin';
            if (570 == so && 630 == wo) return 'Australia/Adelaide';
            if (600 == so && 540 == wo) return 'Asia/Yakutsk';
            if (600 == so && 600 == wo) return 'Australia/Brisbane';
            if (600 == so && 660 == wo) return 'Australia/Sydney';
            if (630 == so && 660 == wo) return 'Australia/Lord_Howe';
            if (660 == so && 600 == wo) return 'Asia/Vladivostok';
            if (660 == so && 660 == wo) return 'Pacific/Guadalcanal';
            if (690 == so && 690 == wo) return 'Pacific/Norfolk';
            if (720 == so && 660 == wo) return 'Asia/Magadan';
            if (720 == so && 720 == wo) return 'Pacific/Fiji';
            if (720 == so && 780 == wo) return 'Pacific/Auckland';
            if (765 == so && 825 == wo) return 'Pacific/Chatham';
            if (780 == so && 780 == wo) return 'Pacific/Enderbury'
            if (840 == so && 840 == wo) return 'Pacific/Kiritimati';
            return 'US/Pacific';
        },
        
        /*this.getTimeUrl: function(){
        	 $U.Connect.asyncRequest("GET", "/fleet/form/login/?localTime="+ $U.getLocalTime(), this.oCallBacks, null);
        },
        this.oCallBacks: {
        	success: function(o){
        },
        failure: function(o){
        }
        },*/

        this.login = function() {

            var formElement = $D.get("login-form");
            if ($L.isObject(formElement)) {
                if(this.validation()){
                formElement.submit();}
            }
        },
        /**
         * Displays message has username is empty when username field is empty
         */
        this.showUserComment=function(){
               var elSheet = $D.getElementsByClassName("user-message");
               if ($D.hasClass(elSheet, "disabled")) {
                   $D.removeClass(elSheet, "disabled");
               }
               
               elSheet = null;
          },
          /**
           * Displays message has password is empty when password field is empty
           */
          this.showPasswordComment=function(){
                var elSheet = $D.getElementsByClassName("password-message");
                if ($D.hasClass(elSheet, "disabled")) {
                    $D.removeClass(elSheet, "disabled");
                }
               
                elSheet = null;
           },
           /**
            * Hides the message username is empty
            */
           this.hideUserComment=function(){
               var elSheet = $D.getElementsByClassName("user-message");
                   $D.addClass(elSheet, "disabled");
               elSheet=null;
           },
           /**
            * Hides the message password is empty
            */
           this.hidePasswordComment=function(){
               var elSheet = $D.getElementsByClassName("password-message");
                   $D.addClass(elSheet, "disabled");
               elSheet=null;
           },
          
        this.validation = function() {
            var userName = $D.get("login.user");
            var passWord = $D.get("login.password");
           var result=false;
            if (userName.value == "" && passWord.value == "") {
               
                this.showUserComment();
                this.showPasswordComment();
                return result;
            } else if (userName.value == "") {
                this.showUserComment();
                this.hidePasswordComment();
                return result;
            } else if (passWord.value == "") {
                   this.showPasswordComment();
                this.hideUserComment();
                return result;
            }
            return true;
        };
        this.baseElement = el;
        this.initParams = params;
        this.init(this.baseElement, params);
    };
})();
